package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.Match;
import sk.posam.fsa.statstracker.domain.MatchRepository;
import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.PlayerDetail;
import sk.posam.fsa.statstracker.domain.PlayerMatchHistoryEntry;
import sk.posam.fsa.statstracker.domain.PlayerMatchStats;
import sk.posam.fsa.statstracker.domain.PlayerMeDetail;
import sk.posam.fsa.statstracker.domain.PlayerRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsSnapshot;
import sk.posam.fsa.statstracker.domain.PlayerWithStats;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.predicate.IsNotNullPredicate;
import sk.posam.fsa.statstracker.domain.predicate.IsUniqueNicknamePredicate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerService implements PlayerFacade {

    private final PlayerRepository playerRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final MatchRepository matchRepository;

    public PlayerService(PlayerRepository playerRepository, PlayerStatsRepository playerStatsRepository,
            MatchRepository matchRepository) {
        this.playerRepository = playerRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public void createPlayer(Player player) throws StatsTrackerException {
        require(IsNotNullPredicate.<Player>getInstance().test(player),
                StatsTrackerException.Type.VALIDATION, "Player must not be null");
        player.validateForCreation();

        Player existingPlayer = playerRepository.get(player.getNickname()).orElse(null);
        require(IsUniqueNicknamePredicate.INSTANCE.test(player, existingPlayer),
                StatsTrackerException.Type.CONFLICT,
                "Player with nickname '" + player.getNickname() + "' already exists");

        playerRepository.create(player);
    }

    @Override
    public List<Player> findAll() {
        return playerRepository.getAll();
    }

    @Override
    public List<PlayerWithStats> findAllWithStats() {
        List<Player> players = playerRepository.getAll();
        if (players.isEmpty())
            return List.of();
        List<Long> ids = players.stream().map(Player::getId).collect(Collectors.toList());
        Map<Long, PlayerStatsSnapshot> snapMap = playerStatsRepository.getForPlayers(ids).stream()
                .collect(Collectors.toMap(PlayerStatsSnapshot::getPlayerId, s -> s));
        return players.stream()
                .map(p -> new PlayerWithStats(p, snapMap.get(p.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public PlayerDetail getById(long id) throws StatsTrackerException {
        Player player = playerRepository.get(id)
                .orElseThrow(() -> new StatsTrackerException(StatsTrackerException.Type.NOT_FOUND,
                        "Player not found: " + id));
        return buildDetail(player);
    }

    @Override
    public void linkToUser(long playerId, String keycloakId) throws StatsTrackerException {
        Player player = playerRepository.get(playerId)
                .orElseThrow(() -> new StatsTrackerException(StatsTrackerException.Type.NOT_FOUND,
                        "Player not found: " + playerId));
        playerRepository.getByKeycloakId(keycloakId).ifPresent(existing -> {
            if (existing.getId() != playerId) {
                throw new StatsTrackerException(StatsTrackerException.Type.CONFLICT,
                        "Keycloak account is already linked to another player");
            }
        });
        player.setKeycloakId(keycloakId);
        playerRepository.update(player);
    }

    @Override
    public void unlinkUser(long playerId) throws StatsTrackerException {
        Player player = playerRepository.get(playerId)
                .orElseThrow(() -> new StatsTrackerException(StatsTrackerException.Type.NOT_FOUND,
                        "Player not found: " + playerId));
        player.setKeycloakId(null);
        playerRepository.update(player);
    }

    @Override
    public PlayerMeDetail getMe(String keycloakSub) throws StatsTrackerException {
        Player player = playerRepository.getByKeycloakId(keycloakSub)
                .orElseThrow(() -> new StatsTrackerException(StatsTrackerException.Type.NOT_FOUND,
                        "No player linked to this account"));

        PlayerDetail detail = buildDetail(player);

        // Compute rank: count players with higher avgAdr (among those with ≥1 match)
        List<Player> allPlayers = playerRepository.getAll();
        List<Long> allIds = allPlayers.stream().map(Player::getId).collect(Collectors.toList());
        Map<Long, PlayerStatsSnapshot> allSnapshots = playerStatsRepository.getForPlayers(allIds).stream()
                .collect(Collectors.toMap(PlayerStatsSnapshot::getPlayerId, s -> s));

        double myAdr = detail.getStats() != null ? detail.getStats().getAvgAdr() : 0.0;
        long playersAhead = allSnapshots.values().stream()
                .filter(s -> s.getMatchesPlayed() > 0)
                .filter(s -> s.getAvgAdr() > myAdr)
                .count();
        int rank = (int) playersAhead + 1;

        return new PlayerMeDetail(detail.getPlayer(), detail.getStats(), detail.getRecentMatches(), rank);
    }

    private PlayerDetail buildDetail(Player player) {
        long id = player.getId();
        List<PlayerStatsSnapshot> snapshots = playerStatsRepository.getForPlayers(List.of(id));
        PlayerStatsSnapshot snapshot = snapshots.isEmpty() ? null : snapshots.get(0);
        List<PlayerMatchStats> recentStats = playerStatsRepository.getForPlayer(id);
        List<Long> matchIds = recentStats.stream()
                .map(PlayerMatchStats::getMatchId)
                .filter(mid -> mid != null)
                .collect(Collectors.toList());
        Map<Long, Match> matchMap = matchRepository.getAllByIds(matchIds).stream()
                .collect(Collectors.toMap(Match::getId, m -> m));
        List<PlayerMatchHistoryEntry> history = recentStats.stream()
                .filter(s -> matchMap.containsKey(s.getMatchId()))
                .map(s -> new PlayerMatchHistoryEntry(matchMap.get(s.getMatchId()), s))
                .collect(Collectors.toList());
        return new PlayerDetail(player, snapshot, history);
    }

    private void require(boolean valid, StatsTrackerException.Type type, String message) {
        if (!valid) {
            throw new StatsTrackerException(type, message);
        }
    }
}
