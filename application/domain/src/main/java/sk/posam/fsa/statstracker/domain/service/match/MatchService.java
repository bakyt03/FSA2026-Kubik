package sk.posam.fsa.statstracker.domain.service.match;

import sk.posam.fsa.statstracker.domain.match.Match;
import sk.posam.fsa.statstracker.domain.match.MatchRepository;
import sk.posam.fsa.statstracker.domain.match.MatchWithStats;
import sk.posam.fsa.statstracker.domain.player.Player;
import sk.posam.fsa.statstracker.domain.player.PlayerInMatch;
import sk.posam.fsa.statstracker.domain.player.PlayerMatchStats;
import sk.posam.fsa.statstracker.domain.player.PlayerRepository;
import sk.posam.fsa.statstracker.domain.player.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.predicate.IsValidCsScorePredicate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchService implements MatchFacade {

    private final MatchRepository matchRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final PlayerRepository playerRepository;

    public MatchService(MatchRepository matchRepository, PlayerStatsRepository playerStatsRepository,
            PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Match> getAll() {
        return matchRepository.getAll();
    }

    @Override
    public List<Match> getAll(int page, int size) {
        return matchRepository.getAll(page, size);
    }

    @Override
    public MatchWithStats getById(long id) throws StatsTrackerException {
        Match match = matchRepository.get(id)
                .orElseThrow(() -> new StatsTrackerException(StatsTrackerException.Type.NOT_FOUND,
                        "Match not found: " + id));
        List<PlayerMatchStats> allStats = playerStatsRepository.getForMatch(id);
        List<Long> playerIds = allStats.stream().map(PlayerMatchStats::getPlayerId).distinct()
                .collect(Collectors.toList());
        Map<Long, Player> playerMap = playerRepository.getByIds(playerIds).stream()
                .collect(Collectors.toMap(Player::getId, p -> p));
        List<PlayerInMatch> team1 = allStats.stream()
                .filter(s -> "TEAM1".equals(s.getTeam()))
                .map(s -> toPlayerInMatch(s, playerMap))
                .collect(Collectors.toList());
        List<PlayerInMatch> team2 = allStats.stream()
                .filter(s -> "TEAM2".equals(s.getTeam()))
                .map(s -> toPlayerInMatch(s, playerMap))
                .collect(Collectors.toList());
        return new MatchWithStats(match, team1, team2);
    }

    private PlayerInMatch toPlayerInMatch(PlayerMatchStats s, Map<Long, Player> playerMap) {
        Player player = playerMap.get(s.getPlayerId());
        String nickname = player != null ? player.getNickname() : "Unknown";
        return new PlayerInMatch(s.getPlayerId(), nickname, s.getKills(), s.getDeaths(), s.getDamage(), s.getAdr());
    }

    @Override
    public void create(Match match, List<PlayerMatchStats> team1Stats, List<PlayerMatchStats> team2Stats)
            throws StatsTrackerException {
        validateScore(match.getTeam1Score(), match.getTeam2Score());
        int totalRounds = match.getTeam1Score() + match.getTeam2Score();
        Match created = matchRepository.create(match);
        team1Stats.forEach(s -> {
            s.setMatchId(created.getId());
            s.setTeam("TEAM1");
            computeAdr(s, totalRounds);
            playerStatsRepository.create(s);
        });
        team2Stats.forEach(s -> {
            s.setMatchId(created.getId());
            s.setTeam("TEAM2");
            computeAdr(s, totalRounds);
            playerStatsRepository.create(s);
        });
    }

    @Override
    public void deleteMatch(long matchId) throws StatsTrackerException {
        matchRepository.get(matchId)
                .orElseThrow(() -> new StatsTrackerException(StatsTrackerException.Type.NOT_FOUND,
                        "Match not found: " + matchId));
        playerStatsRepository.deleteByMatchId(matchId);
        matchRepository.delete(matchId);
    }

    private void computeAdr(PlayerMatchStats s, int totalRounds) {
        if (totalRounds > 0) {
            s.setAdr((double) s.getDamage() / totalRounds);
        }
    }

    private void validateScore(int s1, int s2) throws StatsTrackerException {
        require(IsValidCsScorePredicate.INSTANCE.test(s1, s2),
                StatsTrackerException.Type.VALIDATION,
                "Invalid CS2 score: " + s1 + "-" + s2 +
                        ". Regulation: first to 13 with 2+ round lead. OT: first to 16/19/22/... with 2+ round lead.");
    }

    private void require(boolean valid, StatsTrackerException.Type type, String message) {
        if (!valid) {
            throw new StatsTrackerException(type, message);
        }
    }
}
