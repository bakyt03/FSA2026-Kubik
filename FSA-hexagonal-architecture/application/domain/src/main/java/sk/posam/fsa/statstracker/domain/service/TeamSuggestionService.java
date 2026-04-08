package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.PlayerRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsSnapshot;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.TeamBalancer;
import sk.posam.fsa.statstracker.domain.TeamSuggestion;
import sk.posam.fsa.statstracker.domain.TeamSuggestionResult;
import sk.posam.fsa.statstracker.domain.predicate.HasExactSizePredicate;
import sk.posam.fsa.statstracker.domain.predicate.HasNoDuplicatesPredicate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamSuggestionService implements TeamSuggestionFacade {

    private final PlayerRepository playerRepository;
    private final PlayerStatsRepository playerStatsRepository;
    private final TeamBalancer teamBalancer;

    public TeamSuggestionService(PlayerRepository playerRepository,
            PlayerStatsRepository playerStatsRepository,
            TeamBalancer teamBalancer) {
        this.playerRepository = playerRepository;
        this.playerStatsRepository = playerStatsRepository;
        this.teamBalancer = teamBalancer;
    }

    @Override
    public TeamSuggestionResult generateSuggestions(List<Long> playerIds) throws StatsTrackerException {
        //exactly 10 players
        require(HasExactSizePredicate.ofSize(10).test(playerIds),
                StatsTrackerException.Type.VALIDATION, "Exactly 10 player IDs are required");

        //no duplicates
        require(HasNoDuplicatesPredicate.<Long>getInstance().test(playerIds),
                StatsTrackerException.Type.VALIDATION, "Player IDs must be unique");

        //load players, verify all exist – find the exact missing IDs
        List<Player> players = playerRepository.getByIds(playerIds);
        Set<Long> foundIds = players.stream().map(Player::getId).collect(Collectors.toSet());
        List<Long> missingIds = playerIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());
        require(missingIds.isEmpty(),
                StatsTrackerException.Type.NOT_FOUND, "Players not found with IDs: " + missingIds);

        //load stats – only last 15 matches per player
        List<PlayerStatsSnapshot> snapshots = playerStatsRepository.getForPlayers(playerIds, 15);

        //warn for players with no history and apply neutral values
        Map<Long, Player> playerById = players.stream()
                .collect(Collectors.toMap(Player::getId, p -> p));

        List<String> warnings = new ArrayList<>();
        for (PlayerStatsSnapshot snapshot : snapshots) {
            if (snapshot.getMatchesPlayed() == 0) {
                String nickname = playerById.get(snapshot.getPlayerId()).getNickname();
                warnings.add("Player '" + nickname + "' has no match history – using neutral values");
                snapshot.applyNeutralValues();
            }
        }

        //build statsMap and call teamBalancer
        Map<Long, PlayerStatsSnapshot> statsMap = snapshots.stream()
                .collect(Collectors.toMap(PlayerStatsSnapshot::getPlayerId, s -> s));

        List<TeamSuggestion> suggestions = teamBalancer.generateSuggestions(players, statsMap);

        //sort descending by balanceScore, take top 3
        List<TeamSuggestion> top3 = suggestions.stream()
                .sorted(Comparator.comparingDouble(TeamSuggestion::getBalanceScore).reversed())
                .limit(3)
                .collect(Collectors.toList());

        //wrap in result
        TeamSuggestionResult result = new TeamSuggestionResult();
        result.setSuggestions(top3);
        result.setWarnings(warnings);
        return result;
    }

    private void require(boolean valid, StatsTrackerException.Type type, String message) {
        if (!valid) {
            throw new StatsTrackerException(type, message);
        }
    }
}

