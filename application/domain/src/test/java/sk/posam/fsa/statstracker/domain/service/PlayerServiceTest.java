package sk.posam.fsa.statstracker.domain.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.statstracker.domain.Match;
import sk.posam.fsa.statstracker.domain.MatchRepository;
import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.PlayerDetail;
import sk.posam.fsa.statstracker.domain.PlayerMeDetail;
import sk.posam.fsa.statstracker.domain.PlayerWithStats;
import sk.posam.fsa.statstracker.domain.PlayerMatchHistoryEntry;
import sk.posam.fsa.statstracker.domain.PlayerMatchStats;
import sk.posam.fsa.statstracker.domain.PlayerRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsRepository;
import sk.posam.fsa.statstracker.domain.PlayerStatsSnapshot;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PlayerStatsRepository playerStatsRepository;

    @Mock
    private MatchRepository matchRepository;

    @InjectMocks
    private PlayerService service;

    @Test
    void createPlayerPersistsWhenNicknameIsUnique() {
        Player player = player("Lukas", "l0ki");
        when(playerRepository.get("l0ki")).thenReturn(Optional.empty());

        service.createPlayer(player);

        verify(playerRepository).get("l0ki");
        verify(playerRepository).create(player);
    }

    @Test
    void createPlayerFailsWhenNicknameAlreadyExists() {
        Player existing = player("Jan", "l0ki");
        Player duplicate = player("Maros", "l0ki");
        when(playerRepository.get("l0ki")).thenReturn(Optional.of(existing));

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.createPlayer(duplicate));

        assertEquals(StatsTrackerException.Type.CONFLICT, ex.getType());
        verify(playerRepository, never()).create(duplicate);
    }

    @Test
    void createPlayerFailsWhenNameIsNull() {
        Player player = player(null, "l0ki");

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.createPlayer(player));

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
        verify(playerRepository, never()).create(player);
    }

    @Test
    void createPlayerFailsWhenNameIsEmpty() {
        Player player = player("", "l0ki");

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.createPlayer(player));

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
        verify(playerRepository, never()).create(player);
    }

    @Test
    void createPlayerFailsWhenNicknameIsNull() {
        Player player = player("Lukas", null);

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.createPlayer(player));

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
        verify(playerRepository, never()).create(player);
    }

    @Test
    void createPlayerFailsWhenNicknameIsEmpty() {
        Player player = player("Lukas", "");

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.createPlayer(player));

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
        verify(playerRepository, never()).create(player);
    }

    @Test
    void createPlayerFailsWhenPlayerIsNull() {
        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.createPlayer(null));

        assertEquals(StatsTrackerException.Type.VALIDATION, ex.getType());
        verify(playerRepository, never()).create(null);
    }

    @Test
    void findAllDelegatesToRepository() {
        Player p1 = player("Lukas", "l0ki");
        Player p2 = player("Maros", "m4ros");
        when(playerRepository.getAll()).thenReturn(List.of(p1, p2));

        List<Player> result = service.findAll();

        assertEquals(2, result.size());
        verify(playerRepository).getAll();
    }

    // ----------------------------------------------------------------
    // getById
    // ----------------------------------------------------------------

    @Test
    void getByIdThrowsNotFoundWhenPlayerDoesNotExist() {
        when(playerRepository.get(1L)).thenReturn(Optional.empty());

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.getById(1L));

        assertEquals(StatsTrackerException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void getByIdReturnsPlayerDetail() {
        Player p = playerWithId(1L, "Lukas", "l0ki");
        when(playerRepository.get(1L)).thenReturn(Optional.of(p));
        when(playerStatsRepository.getForPlayers(List.of(1L))).thenReturn(List.of());
        when(playerStatsRepository.getForPlayer(1L)).thenReturn(List.of());
        when(matchRepository.getAllByIds(List.of())).thenReturn(List.of());

        PlayerDetail detail = service.getById(1L);

        assertEquals(p, detail.getPlayer());
    }

    // ----------------------------------------------------------------
    // deletePlayer
    // ----------------------------------------------------------------

    @Test
    void deletePlayerThrowsNotFoundWhenPlayerDoesNotExist() {
        when(playerRepository.get(5L)).thenReturn(Optional.empty());

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.deletePlayer(5L));

        assertEquals(StatsTrackerException.Type.NOT_FOUND, ex.getType());
        verify(playerStatsRepository, never()).deleteByPlayerId(anyLong());
        verify(playerRepository, never()).delete(anyLong());
    }

    @Test
    void deletePlayerDeletesStatsBeforePlayer() {
        Player p = playerWithId(5L, "Lukas", "l0ki");
        when(playerRepository.get(5L)).thenReturn(Optional.of(p));

        service.deletePlayer(5L);

        var order = inOrder(playerStatsRepository, playerRepository);
        order.verify(playerStatsRepository).deleteByPlayerId(5L);
        order.verify(playerRepository).delete(5L);
    }

    // ----------------------------------------------------------------
    // getMatchHistory
    // ----------------------------------------------------------------

    @Test
    void getMatchHistoryThrowsNotFoundWhenPlayerDoesNotExist() {
        when(playerRepository.get(3L)).thenReturn(Optional.empty());

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.getMatchHistory(3L, 0, 10));

        assertEquals(StatsTrackerException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void getMatchHistoryReturnsEntriesForKnownMatches() {
        Player p = playerWithId(3L, "Bob", "bob");
        when(playerRepository.get(3L)).thenReturn(Optional.of(p));

        PlayerMatchStats stat = new PlayerMatchStats();
        stat.setPlayerId(3L);
        stat.setMatchId(10L);
        when(playerStatsRepository.getForPlayer(3L, 0, 10)).thenReturn(List.of(stat));

        Match match = new Match();
        match.setId(10L);
        match.setPlayedAt(LocalDate.now());
        match.setTeam1Score(13);
        match.setTeam2Score(5);
        when(matchRepository.getAllByIds(List.of(10L))).thenReturn(List.of(match));

        List<PlayerMatchHistoryEntry> history = service.getMatchHistory(3L, 0, 10);

        assertEquals(1, history.size());
        assertEquals(match, history.get(0).getMatch());
        assertEquals(stat, history.get(0).getStats());
    }

    // ----------------------------------------------------------------
    // linkToUser
    // ----------------------------------------------------------------

    @Test
    void linkToUserThrowsNotFoundWhenPlayerDoesNotExist() {
        when(playerRepository.get(7L)).thenReturn(Optional.empty());

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.linkToUser(7L, "kc-abc"));

        assertEquals(StatsTrackerException.Type.NOT_FOUND, ex.getType());
        verify(playerRepository, never()).update(any());
    }

    @Test
    void linkToUserThrowsConflictWhenKeycloakIdAlreadyLinkedToAnotherPlayer() {
        Player target = playerWithId(7L, "Alice", "alice");
        Player other = playerWithId(8L, "Bob", "bob");
        when(playerRepository.get(7L)).thenReturn(Optional.of(target));
        when(playerRepository.getByKeycloakId("kc-abc")).thenReturn(Optional.of(other));

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.linkToUser(7L, "kc-abc"));

        assertEquals(StatsTrackerException.Type.CONFLICT, ex.getType());
        verify(playerRepository, never()).update(any());
    }

    @Test
    void linkToUserSucceedsWhenSamePlayerRelinks() {
        Player player = playerWithId(7L, "Alice", "alice");
        when(playerRepository.get(7L)).thenReturn(Optional.of(player));
        // getByKeycloakId returns the same player (re-linking)
        when(playerRepository.getByKeycloakId("kc-abc")).thenReturn(Optional.of(player));

        service.linkToUser(7L, "kc-abc");

        verify(playerRepository).update(player);
        assertEquals("kc-abc", player.getKeycloakId());
    }

    @Test
    void linkToUserSetsKeycloakIdAndPersists() {
        Player player = playerWithId(7L, "Alice", "alice");
        when(playerRepository.get(7L)).thenReturn(Optional.of(player));
        when(playerRepository.getByKeycloakId("kc-abc")).thenReturn(Optional.empty());

        service.linkToUser(7L, "kc-abc");

        assertEquals("kc-abc", player.getKeycloakId());
        verify(playerRepository).update(player);
    }

    // ----------------------------------------------------------------
    // unlinkUser
    // ----------------------------------------------------------------

    @Test
    void unlinkUserThrowsNotFoundWhenPlayerDoesNotExist() {
        when(playerRepository.get(9L)).thenReturn(Optional.empty());

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.unlinkUser(9L));

        assertEquals(StatsTrackerException.Type.NOT_FOUND, ex.getType());
        verify(playerRepository, never()).update(any());
    }

    @Test
    void unlinkUserClearsKeycloakIdAndPersists() {
        Player player = playerWithId(9L, "Carol", "carol");
        player.setKeycloakId("kc-old");
        when(playerRepository.get(9L)).thenReturn(Optional.of(player));

        service.unlinkUser(9L);

        assertNull(player.getKeycloakId());
        verify(playerRepository).update(player);
    }

    private Player player(String name, String nickname) {
        Player p = new Player();
        p.setName(name);
        p.setNickname(nickname);
        return p;
    }

    // ----------------------------------------------------------------
    // findAllWithStats
    // ----------------------------------------------------------------

    @Test
    void findAllWithStatsReturnsEmptyListWhenNoPlayersExist() {
        when(playerRepository.getAll()).thenReturn(List.of());

        List<PlayerWithStats> result = service.findAllWithStats();

        assertEquals(0, result.size());
    }

    @Test
    void findAllWithStatsZipsPlayersWithMatchingSnapshots() {
        Player p1 = playerWithId(1L, "Alice", "alice");
        Player p2 = playerWithId(2L, "Bob", "bob");
        when(playerRepository.getAll()).thenReturn(List.of(p1, p2));

        PlayerStatsSnapshot snap1 = snapshot(1L, 80.0, 5);
        PlayerStatsSnapshot snap2 = snapshot(2L, 60.0, 3);
        when(playerStatsRepository.getForPlayers(List.of(1L, 2L))).thenReturn(List.of(snap1, snap2));

        List<PlayerWithStats> result = service.findAllWithStats();

        assertEquals(2, result.size());
        assertEquals(p1, result.get(0).getPlayer());
        assertEquals(snap1, result.get(0).getStats());
        assertEquals(p2, result.get(1).getPlayer());
        assertEquals(snap2, result.get(1).getStats());
    }

    @Test
    void findAllWithStatsReturnsNullSnapshotForPlayerWithNoStats() {
        Player p1 = playerWithId(1L, "Alice", "alice");
        Player p2 = playerWithId(2L, "Bob", "bob");
        when(playerRepository.getAll()).thenReturn(List.of(p1, p2));
        // only p1 has a snapshot
        when(playerStatsRepository.getForPlayers(List.of(1L, 2L)))
                .thenReturn(List.of(snapshot(1L, 80.0, 5)));

        List<PlayerWithStats> result = service.findAllWithStats();

        assertEquals(80.0, result.get(0).getStats().getAvgAdr(), 1e-9);
        assertNull(result.get(1).getStats());
    }

    // ----------------------------------------------------------------
    // getMe
    // ----------------------------------------------------------------

    @Test
    void getMeThrowsNotFoundWhenNoPlayerLinkedToAccount() {
        when(playerRepository.getByKeycloakId("kc-x")).thenReturn(Optional.empty());

        StatsTrackerException ex = assertThrows(StatsTrackerException.class,
                () -> service.getMe("kc-x"));

        assertEquals(StatsTrackerException.Type.NOT_FOUND, ex.getType());
    }

    @Test
    void getMeReturnsRank1WhenNoOtherPlayerHasHigherAdr() {
        Player alice = playerWithId(1L, "Alice", "alice");
        when(playerRepository.getByKeycloakId("kc-alice")).thenReturn(Optional.of(alice));

        // buildDetail stubs
        PlayerStatsSnapshot aliceSnap = snapshot(1L, 80.0, 5);
        when(playerStatsRepository.getForPlayers(List.of(1L))).thenReturn(List.of(aliceSnap));
        when(playerStatsRepository.getForPlayer(1L)).thenReturn(List.of());
        when(matchRepository.getAllByIds(List.of())).thenReturn(List.of());

        // rank computation stubs — two other players with lower ADR
        Player bob = playerWithId(2L, "Bob", "bob");
        Player carol = playerWithId(3L, "Carol", "carol");
        when(playerRepository.getAll()).thenReturn(List.of(alice, bob, carol));
        PlayerStatsSnapshot bobSnap = snapshot(2L, 70.0, 3);
        PlayerStatsSnapshot carolSnap = snapshot(3L, 50.0, 2);
        when(playerStatsRepository.getForPlayers(List.of(1L, 2L, 3L)))
                .thenReturn(List.of(aliceSnap, bobSnap, carolSnap));

        PlayerMeDetail me = service.getMe("kc-alice");

        assertEquals(1, me.getRank());
    }

    @Test
    void getMeReturnsCorrectRankWhenOtherPlayersRankHigher() {
        Player alice = playerWithId(1L, "Alice", "alice");
        when(playerRepository.getByKeycloakId("kc-alice")).thenReturn(Optional.of(alice));

        // Alice has ADR 60
        PlayerStatsSnapshot aliceSnap = snapshot(1L, 60.0, 5);
        when(playerStatsRepository.getForPlayers(List.of(1L))).thenReturn(List.of(aliceSnap));
        when(playerStatsRepository.getForPlayer(1L)).thenReturn(List.of());
        when(matchRepository.getAllByIds(List.of())).thenReturn(List.of());

        // Bob has ADR 80 (ranked ahead), Carol has ADR 50 (ranked behind)
        Player bob = playerWithId(2L, "Bob", "bob");
        Player carol = playerWithId(3L, "Carol", "carol");
        when(playerRepository.getAll()).thenReturn(List.of(alice, bob, carol));
        PlayerStatsSnapshot bobSnap = snapshot(2L, 80.0, 3);
        PlayerStatsSnapshot carolSnap = snapshot(3L, 50.0, 2);
        when(playerStatsRepository.getForPlayers(List.of(1L, 2L, 3L)))
                .thenReturn(List.of(aliceSnap, bobSnap, carolSnap));

        PlayerMeDetail me = service.getMe("kc-alice");

        assertEquals(2, me.getRank()); // one player (Bob) has higher ADR
    }

    @Test
    void getMeIgnoresPlayersWithNoMatchHistoryInRankComputation() {
        Player alice = playerWithId(1L, "Alice", "alice");
        when(playerRepository.getByKeycloakId("kc-alice")).thenReturn(Optional.of(alice));

        PlayerStatsSnapshot aliceSnap = snapshot(1L, 60.0, 5);
        when(playerStatsRepository.getForPlayers(List.of(1L))).thenReturn(List.of(aliceSnap));
        when(playerStatsRepository.getForPlayer(1L)).thenReturn(List.of());
        when(matchRepository.getAllByIds(List.of())).thenReturn(List.of());

        // Dave has higher ADR (90) but 0 matches — must not count towards rank
        Player dave = playerWithId(2L, "Dave", "dave");
        when(playerRepository.getAll()).thenReturn(List.of(alice, dave));
        PlayerStatsSnapshot daveSnap = snapshot(2L, 90.0, 0); // 0 matches
        when(playerStatsRepository.getForPlayers(List.of(1L, 2L)))
                .thenReturn(List.of(aliceSnap, daveSnap));

        PlayerMeDetail me = service.getMe("kc-alice");

        assertEquals(1, me.getRank()); // Dave's ADR is ignored (no matches)
    }

    private PlayerStatsSnapshot snapshot(long playerId, double avgAdr, int matchesPlayed) {
        PlayerStatsSnapshot s = new PlayerStatsSnapshot();
        s.setPlayerId(playerId);
        s.setAvgAdr(avgAdr);
        s.setMatchesPlayed(matchesPlayed);
        return s;
    }

    private Player playerWithId(long id, String name, String nickname) {
        Player p = player(name, nickname);
        p.setId(id);
        return p;
    }
}
