package sk.posam.fsa.statstracker.domain.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.PlayerRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsSnapshot;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.Team;
import sk.posam.fsa.statstracker.domain.TeamBalancer;
import sk.posam.fsa.statstracker.domain.TeamSuggestion;
import sk.posam.fsa.statstracker.domain.TeamSuggestionResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamSuggestionServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerStatsRepository playerStatsRepository;

    @Mock
    private TeamBalancer teamBalancer;

    @InjectMocks
    private TeamSuggestionService service;

    @Test
    void generateSuggestionsFailsWhenPlayerCountIsNot10() {
        List<Long> ids = List.of(1L, 2L, 3L);

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.generateSuggestions(ids));

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
        verify(playerRepository, never()).getByIds(any());
    }

    @Test
    void generateSuggestionsFailsOnDuplicateIds() {
        // 10 IDs but first one is duplicated
        List<Long> ids = List.of(1L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.generateSuggestions(ids));

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
        verify(playerRepository, never()).getByIds(any());
    }

    @Test
    void generateSuggestionsFailsWhenPlayerNotFound() {
        List<Long> ids = playerIds(10);
        // repository only returns 9 of the 10 requested players
        when(playerRepository.getByIds(ids)).thenReturn(players(9));

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.generateSuggestions(ids));

        assertEquals(StatsTrackerException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void generateSuggestionsReturnsSortedTop3() {
        List<Long> ids = playerIds(10);
        List<Player> players = players(10);
        List<PlayerStatsSnapshot> snapshots = snapshotsWithHistory(players);

        when(playerRepository.getByIds(ids)).thenReturn(players);
        when(playerStatsRepository.getForPlayers(eq(ids), eq(15))).thenReturn(snapshots);
        when(teamBalancer.generateSuggestions(any(), any())).thenReturn(
                List.of(suggestion(0.5), suggestion(0.9), suggestion(0.7), suggestion(0.3)));

        TeamSuggestionResult result = service.generateSuggestions(ids);

        assertEquals(3, result.getSuggestions().size());
        assertEquals(0.9, result.getSuggestions().get(0).getBalanceScore());
        assertEquals(0.7, result.getSuggestions().get(1).getBalanceScore());
        assertEquals(0.5, result.getSuggestions().get(2).getBalanceScore());
        assertTrue(result.getWarnings().isEmpty());
    }

    @Test
    void generateSuggestionsAddsWarningForPlayerWithNoMatchHistory() {
        List<Long> ids = playerIds(10);
        List<Player> players = players(10);

        // first player has no match history
        List<PlayerStatsSnapshot> snapshots = new ArrayList<>(snapshotsWithHistory(players));
        snapshots.get(0).setMatchesPlayed(0);
        snapshots.get(0).setAvgHltvRating(0.0);

        when(playerRepository.getByIds(ids)).thenReturn(players);
        when(playerStatsRepository.getForPlayers(eq(ids), eq(15))).thenReturn(snapshots);
        when(teamBalancer.generateSuggestions(any(), any())).thenReturn(
                List.of(suggestion(1.0), suggestion(0.9), suggestion(0.8)));

        TeamSuggestionResult result = service.generateSuggestions(ids);

        assertEquals(1, result.getWarnings().size());
        assertTrue(result.getWarnings().get(0).contains("nick1"));
        assertTrue(result.getWarnings().get(0).contains("neutral values"));
    }

    @Test
    void generateSuggestionsAppliesNeutralValuesToPlayerWithNoHistory() {
        List<Long> ids = playerIds(10);
        List<Player> players = players(10);

        List<PlayerStatsSnapshot> snapshots = new ArrayList<>(snapshotsWithHistory(players));
        PlayerStatsSnapshot noHistorySnapshot = snapshots.get(0);
        noHistorySnapshot.setMatchesPlayed(0);
        noHistorySnapshot.setAvgHltvRating(0.0);

        when(playerRepository.getByIds(ids)).thenReturn(players);
        when(playerStatsRepository.getForPlayers(eq(ids), eq(15))).thenReturn(snapshots);
        when(teamBalancer.generateSuggestions(any(), any())).thenReturn(
                List.of(suggestion(1.0), suggestion(0.9), suggestion(0.8)));

        service.generateSuggestions(ids);

        // applyNeutralValues() sets avgHltvRating to 1.0
        assertEquals(1.0, noHistorySnapshot.getAvgHltvRating());
    }

    // --- helpers ---

    private List<Long> playerIds(int count) {
        return LongStream.rangeClosed(1, count).boxed().toList();
    }

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

    private List<PlayerStatsSnapshot> snapshotsWithHistory(List<Player> players) {
        List<PlayerStatsSnapshot> snapshots = new ArrayList<>();
        for (Player p : players) {
            PlayerStatsSnapshot s = new PlayerStatsSnapshot();
            s.setPlayerId(p.getId());
            s.setAvgHltvRating(1.0);
            s.setMatchesPlayed(5);
            snapshots.add(s);
        }
        return snapshots;
    }

    private TeamSuggestion suggestion(double balanceScore) {
        Team teamA = new Team();
        teamA.setSide("A");
        teamA.setPlayers(List.of());

        Team teamB = new Team();
        teamB.setSide("B");
        teamB.setPlayers(List.of());

        TeamSuggestion s = new TeamSuggestion();
        s.setTeamA(teamA);
        s.setTeamB(teamB);
        s.setBalanceScore(balanceScore);
        return s;
    }
}
