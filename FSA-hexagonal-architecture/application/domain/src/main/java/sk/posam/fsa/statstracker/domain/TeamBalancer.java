package sk.posam.fsa.statstracker.domain;

import sk.posam.fsa.statstracker.domain.predicate.HasExactSizePredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamBalancer {

    /**
     * Vygeneruje návrhy vyrovnaných tímov.
     *
     * @param players  10 hráčov vybraných používateľom
     * @param statsMap agregované štatistiky keyed by playerId
     * @return zoznam návrhov zoradený zostupne podľa balanceScore
     */
    public List<TeamSuggestion> generateSuggestions(List<Player> players,
            Map<Long, PlayerStatsSnapshot> statsMap) {
        require(HasExactSizePredicate.ofSize(10).test(players),
                "Exactly 10 players are required");

        List<TeamSuggestion> suggestions = new ArrayList<>();
        int totalPlayers = players.size();
        int teamSize = totalPlayers / 2;

        // Keep player at index 0 always in team A to avoid mirrored duplicates (A/B
        // swapped).
        for (int mask = 0; mask < (1 << totalPlayers); mask++) {
            if ((mask & 1) == 0) {
                continue;
            }
            if (Integer.bitCount(mask) != teamSize) {
                continue;
            }

            List<Player> teamAPlayers = new ArrayList<>();
            List<Player> teamBPlayers = new ArrayList<>();
            for (int i = 0; i < totalPlayers; i++) {
                if ((mask & (1 << i)) != 0) {
                    teamAPlayers.add(players.get(i));
                } else {
                    teamBPlayers.add(players.get(i));
                }
            }

            Team teamA = new Team();
            teamA.setSide("A");
            teamA.setPlayers(teamAPlayers);

            Team teamB = new Team();
            teamB.setSide("B");
            teamB.setPlayers(teamBPlayers);

            TeamSuggestion suggestion = new TeamSuggestion();
            suggestion.setTeamA(teamA);
            suggestion.setTeamB(teamB);
            suggestion.setBalanceScore(calculateBalance(teamA, teamB, statsMap));
            suggestions.add(suggestion);
        }

        return suggestions;
    }

    /**
     * Vypočíta skóre vyrovnanosti dvoch tímov.
     * Vyššie skóre = vyrovnanejšie tímy.
     *
     * @return hodnota v rozsahu (0, 1]
     */
    private double calculateBalance(Team t1, Team t2, Map<Long, PlayerStatsSnapshot> statsMap) {
        double avgT1 = t1.getPlayers().stream()
                .mapToDouble(player -> resolveHltvRating(player, statsMap))
                .average()
                .orElse(1.0);

        double avgT2 = t2.getPlayers().stream()
                .mapToDouble(player -> resolveHltvRating(player, statsMap))
                .average()
                .orElse(1.0);

        return 1.0 / (1.0 + Math.abs(avgT1 - avgT2));
    }

    private double resolveHltvRating(Player player, Map<Long, PlayerStatsSnapshot> statsMap) {
        PlayerStatsSnapshot snapshot = statsMap.get(player.getId());
        if (snapshot == null) {
            return 1.0;
        }
        return snapshot.getAvgHltvRating();
    }

    private void require(boolean valid, String message) {
        if (!valid) {
            throw new StatsTrackerException(StatsTrackerException.Type.VALIDATION, message);
        }
    }
}
