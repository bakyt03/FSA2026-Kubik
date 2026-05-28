package sk.posam.fsa.statstracker.domain.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.statstracker.domain.match.Match;
import sk.posam.fsa.statstracker.domain.match.MatchRepository;
import sk.posam.fsa.statstracker.domain.match.MatchWithStats;
import sk.posam.fsa.statstracker.domain.player.Player;
import sk.posam.fsa.statstracker.domain.player.PlayerInMatch;
import sk.posam.fsa.statstracker.domain.player.PlayerMatchStats;
import sk.posam.fsa.statstracker.domain.player.PlayerRepository;
import sk.posam.fsa.statstracker.domain.player.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.service.match.MatchService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private PlayerStatsRepository playerStatsRepository;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private MatchService service;

    // ----------------------------------------------------------------
    // Score validation — valid regulation scores
    // ----------------------------------------------------------------

    @ParameterizedTest(name = "valid regulation score {0}-{1}")
    @CsvSource({
            "13, 0",
            "13, 5",
            "13, 11",
            "0,  13",
            "5,  13",
            "11, 13"
    })
    void createSucceedsForValidRegulationScore(int t1, int t2) {
        Match match = match(t1, t2);
        when(matchRepository.create(match)).thenReturn(matchWithId(match, 1L));

        service.create(match, List.of(), List.of());

        verify(matchRepository).create(match);
    }

    // ----------------------------------------------------------------
    // Score validation — valid overtime scores
    // ----------------------------------------------------------------

    @ParameterizedTest(name = "valid overtime score {0}-{1}")
    @CsvSource({
            "16, 12",
            "16, 13",
            "16, 14",
            "12, 16",
            "14, 16",
            "19, 15",
            "19, 17",
            "22, 18",
            "22, 20"
    })
    void createSucceedsForValidOvertimeScore(int t1, int t2) {
        Match match = match(t1, t2);
        when(matchRepository.create(match)).thenReturn(matchWithId(match, 1L));

        service.create(match, List.of(), List.of());

        verify(matchRepository).create(match);
    }

    // ----------------------------------------------------------------
    // Score validation — invalid scores
    // ----------------------------------------------------------------

    @ParameterizedTest(name = "invalid score {0}-{1}")
    @CsvSource({
            "13, 12", // regulation win but loser has 12 (must be ≤11)
            "12, 13",
            "10,  5", // not a valid win score
            "0,   0",
            "16, 11", // OT win but loser has 11 (min is 12)
            "15, 13", // 15 is not a valid win score
            "7,   7"
    })
    void createFailsForInvalidScore(int t1, int t2) {
        Match match = match(t1, t2);

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.create(match, List.of(), List.of()));

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
        verify(matchRepository, never()).create(any());
    }

    // ----------------------------------------------------------------
    // ADR computation
    // ----------------------------------------------------------------

    @Test
    void createComputesAdrFromDamageAndTotalRounds() {
        Match match = match(13, 7); // 20 rounds total
        Match created = matchWithId(match, 1L);
        when(matchRepository.create(match)).thenReturn(created);

        PlayerMatchStats stats = stats(1L, 400); // 400 damage → ADR = 400/20 = 20.0

        service.create(match, List.of(stats), List.of());

        assertEquals(20.0, stats.getAdr(), 1e-9);
    }

    @Test
    void createComputesAdrIndependentlyPerPlayer() {
        Match match = match(13, 5); // 18 rounds total
        when(matchRepository.create(match)).thenReturn(matchWithId(match, 1L));

        PlayerMatchStats t1 = stats(1L, 90); // 90 / 18 = 5.0
        PlayerMatchStats t2 = stats(2L, 180); // 180 / 18 = 10.0

        service.create(match, List.of(t1), List.of(t2));

        assertEquals(5.0, t1.getAdr(), 1e-9);
        assertEquals(10.0, t2.getAdr(), 1e-9);
    }

    // ----------------------------------------------------------------
    // create — team assignment and match ID propagation
    // ----------------------------------------------------------------

    @Test
    void createSetsTeam1OnFirstTeamStats() {
        Match match = match(13, 0);
        long matchId = 99L;
        when(matchRepository.create(match)).thenReturn(matchWithId(match, matchId));

        PlayerMatchStats t1 = stats(1L, 0);
        service.create(match, List.of(t1), List.of());

        assertEquals("TEAM1", t1.getTeam());
        assertEquals(matchId, t1.getMatchId());
    }

    @Test
    void createSetsTeam2OnSecondTeamStats() {
        Match match = match(13, 0);
        long matchId = 99L;
        when(matchRepository.create(match)).thenReturn(matchWithId(match, matchId));

        PlayerMatchStats t2 = stats(2L, 0);
        service.create(match, List.of(), List.of(t2));

        assertEquals("TEAM2", t2.getTeam());
        assertEquals(matchId, t2.getMatchId());
    }

    @Test
    void createPersistsAllPlayerStats() {
        Match match = match(13, 5);
        when(matchRepository.create(match)).thenReturn(matchWithId(match, 1L));

        PlayerMatchStats t1a = stats(1L, 100);
        PlayerMatchStats t1b = stats(2L, 200);
        PlayerMatchStats t2a = stats(3L, 150);

        service.create(match, List.of(t1a, t1b), List.of(t2a));

        verify(playerStatsRepository).create(t1a);
        verify(playerStatsRepository).create(t1b);
        verify(playerStatsRepository).create(t2a);
    }

    // ----------------------------------------------------------------
    // getById
    // ----------------------------------------------------------------

    @Test
    void getByIdThrowsNotFoundWhenMatchDoesNotExist() {
        when(matchRepository.get(42L)).thenReturn(Optional.empty());

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.getById(42L));

        assertEquals(StatsTrackerException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void getByIdAssemblesTeamsFromStats() {
        Match match = matchWithId(match(13, 5), 7L);
        when(matchRepository.get(7L)).thenReturn(Optional.of(match));

        Player p1 = player(1L, "Alice");
        Player p2 = player(2L, "Bob");

        PlayerMatchStats s1 = statsWithTeam(1L, "TEAM1", 10, 2, 100);
        PlayerMatchStats s2 = statsWithTeam(2L, "TEAM2", 5, 8, 200);

        when(playerStatsRepository.getForMatch(7L)).thenReturn(List.of(s1, s2));
        when(playerRepository.getByIds(any())).thenReturn(List.of(p1, p2));

        MatchWithStats result = service.getById(7L);

        assertEquals(match, result.getMatch());
        assertEquals(1, result.getTeam1Players().size());
        assertEquals(1, result.getTeam2Players().size());

        PlayerInMatch inTeam1 = result.getTeam1Players().get(0);
        assertEquals("Alice", inTeam1.getPlayerNickname());
        assertEquals(10, inTeam1.getKills());
        assertEquals(2, inTeam1.getDeaths());

        PlayerInMatch inTeam2 = result.getTeam2Players().get(0);
        assertEquals("Bob", inTeam2.getPlayerNickname());
    }

    @Test
    void getByIdFallsBackToUnknownNicknameWhenPlayerNotInMap() {
        Match match = matchWithId(match(13, 5), 8L);
        when(matchRepository.get(8L)).thenReturn(Optional.of(match));

        PlayerMatchStats s1 = statsWithTeam(99L, "TEAM1", 0, 0, 0);
        when(playerStatsRepository.getForMatch(8L)).thenReturn(List.of(s1));
        // repository returns no matching player
        when(playerRepository.getByIds(any())).thenReturn(List.of());

        MatchWithStats result = service.getById(8L);

        assertEquals("Unknown", result.getTeam1Players().get(0).getPlayerNickname());
    }

    // ----------------------------------------------------------------
    // deleteMatch
    // ----------------------------------------------------------------

    @Test
    void deleteMatchThrowsNotFoundWhenMatchDoesNotExist() {
        when(matchRepository.get(5L)).thenReturn(Optional.empty());

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.deleteMatch(5L));

        assertEquals(StatsTrackerException.Type.NOT_FOUND, ex.getType());
        verify(playerStatsRepository, never()).deleteByMatchId(anyLong());
        verify(matchRepository, never()).delete(anyLong());
    }

    @Test
    void deleteMatchDeletesStatsBeforeMatch() {
        long id = 3L;
        when(matchRepository.get(id)).thenReturn(Optional.of(matchWithId(match(13, 0), id)));

        service.deleteMatch(id);

        var order = inOrder(playerStatsRepository, matchRepository);
        order.verify(playerStatsRepository).deleteByMatchId(id);
        order.verify(matchRepository).delete(id);
    }

    // ----------------------------------------------------------------
    // helpers
    // ----------------------------------------------------------------

    private Match match(int team1Score, int team2Score) {
        Match m = new Match();
        m.setTeam1Score(team1Score);
        m.setTeam2Score(team2Score);
        return m;
    }

    private Match matchWithId(Match base, long id) {
        base.setId(id);
        return base;
    }

    private PlayerMatchStats stats(long playerId, int damage) {
        PlayerMatchStats s = new PlayerMatchStats();
        s.setPlayerId(playerId);
        s.setDamage(damage);
        return s;
    }

    private PlayerMatchStats statsWithTeam(long playerId, String team, int kills, int deaths, int damage) {
        PlayerMatchStats s = stats(playerId, damage);
        s.setKills(kills);
        s.setDeaths(deaths);
        s.setTeam(team);
        return s;
    }

    private Player player(long id, String nickname) {
        Player p = new Player();
        p.setId(id);
        p.setNickname(nickname);
        p.setName(nickname);
        return p;
    }
}
