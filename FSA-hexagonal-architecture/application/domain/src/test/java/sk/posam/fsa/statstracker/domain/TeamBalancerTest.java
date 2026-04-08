package sk.posam.fsa.statstracker.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamBalancerTest {

    private final TeamBalancer balancer = new TeamBalancer();

    @Test
    void throwsValidationWhenPlayerCountIsNot10() {
        List<Player> tooFew = players(9);

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> balancer.generateSuggestions(tooFew, Map.of()));

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
    }

    @Test
    void throwsValidationWhenPlayerCountExceeds10() {
        List<Player> tooMany = players(11);

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> balancer.generateSuggestions(tooMany, Map.of()));

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
    }

    @Test
    void generates126SuggestionsFor10Players() {
        // C(9, 4) = 126 — player[0] always in team A to avoid mirrored duplicates
        List<Player> players = players(10);

        List<TeamSuggestion> suggestions = balancer.generateSuggestions(players, uniformStats(players, 1.0));

        assertEquals(126, suggestions.size());
    }

    @Test
    void allTeamsContainExactly5Players() {
        List<Player> players = players(10);

        List<TeamSuggestion> suggestions = balancer.generateSuggestions(players, uniformStats(players, 1.0));

        for (TeamSuggestion s : suggestions) {
            assertEquals(5, s.getTeamA().getPlayers().size());
            assertEquals(5, s.getTeamB().getPlayers().size());
        }
    }

    @Test
    void player0IsAlwaysAssignedToTeamA() {
        List<Player> players = players(10);
        Player first = players.get(0);

        List<TeamSuggestion> suggestions = balancer.generateSuggestions(players, uniformStats(players, 1.0));

        for (TeamSuggestion s : suggestions) {
            assertTrue(s.getTeamA().getPlayers().contains(first),
                    "Player at index 0 must always be in team A to prevent mirrored duplicates");
        }
    }

    @Test
    void equalRatingsProduceMaximumBalanceScore() {
        List<Player> players = players(10);

        List<TeamSuggestion> suggestions = balancer.generateSuggestions(players, uniformStats(players, 1.5));

        for (TeamSuggestion s : suggestions) {
            assertEquals(1.0, s.getBalanceScore(), 1e-9,
                    "All teams should be perfectly balanced when ratings are equal");
        }
    }

    @Test
    void unequalRatingsProduceLowerBalanceScoreForImbalancedSplit() {
        List<Player> players = players(10);
        // players 0–4 have rating 2.0, players 5–9 have rating 1.0
        Map<Long, PlayerStatsSnapshot> statsMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            PlayerStatsSnapshot snap = new PlayerStatsSnapshot();
            snap.setPlayerId(players.get(i).getId());
            snap.setAvgHltvRating(i < 5 ? 2.0 : 1.0);
            snap.setMatchesPlayed(5);
            statsMap.put(players.get(i).getId(), snap);
        }

        List<TeamSuggestion> suggestions = balancer.generateSuggestions(players, statsMap);

        boolean hasImbalanced = suggestions.stream().anyMatch(s -> s.getBalanceScore() < 1.0 - 1e-9);
        assertTrue(hasImbalanced, "Expected at least one imbalanced suggestion when ratings differ");
    }

    @Test
    void missingStatsEntryDefaultsToRating1AndProducesPerfectBalance() {
        List<Player> players = players(10);
        // empty stats map — all players resolve to default rating 1.0

        List<TeamSuggestion> suggestions = balancer.generateSuggestions(players, Map.of());

        for (TeamSuggestion s : suggestions) {
            assertEquals(1.0, s.getBalanceScore(), 1e-9,
                    "Missing stats entry should default to rating 1.0, producing perfect balance");
        }
    }

    // --- helpers ---

    private List<Player> players(int count) {
        List<Player> result = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Player p = new Player();
            p.setId((long) i);
            p.setName("Player" + i);
            p.setNickname("nick" + i);
            result.add(p);
        }
        return result;
    }

    private Map<Long, PlayerStatsSnapshot> uniformStats(List<Player> players, double hltvRating) {
        Map<Long, PlayerStatsSnapshot> map = new HashMap<>();
        for (Player p : players) {
            PlayerStatsSnapshot snap = new PlayerStatsSnapshot();
            snap.setPlayerId(p.getId());
            snap.setAvgHltvRating(hltvRating);
            snap.setMatchesPlayed(5);
            map.put(p.getId(), snap);
        }
        return map;
    }
}
