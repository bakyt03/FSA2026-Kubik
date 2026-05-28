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
            double avgA = calculateTeamAvg(teamA, statsMap);
            double avgB = calculateTeamAvg(teamB, statsMap);
            suggestion.setTeamAAdrAvg(avgA);
            suggestion.setTeamBAdrAvg(avgB);
            suggestion.setAdrDifference(Math.abs(avgA - avgB));
            suggestions.add(suggestion);
        }

        return suggestions;
    }

    private double calculateTeamAvg(Team team, Map<Long, PlayerStatsSnapshot> statsMap) {
        return team.getPlayers().stream()
                .mapToDouble(player -> resolveAdr(player, statsMap))
                .average()
                .orElse(0.0);
    }

    private double resolveAdr(Player player, Map<Long, PlayerStatsSnapshot> statsMap) {
        PlayerStatsSnapshot snapshot = statsMap.get(player.getId());
        if (snapshot == null) {
            return 0.0;
        }
        return snapshot.getAvgAdr();
    }

    private void require(boolean valid, String message) {
        if (!valid) {
            throw new StatsTrackerException(StatsTrackerException.Type.VALIDATION, message);
        }
    }
}
