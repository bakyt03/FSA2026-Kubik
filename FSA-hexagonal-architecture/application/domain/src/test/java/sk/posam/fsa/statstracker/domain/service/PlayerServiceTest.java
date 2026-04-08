package sk.posam.fsa.statstracker.domain.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.PlayerRepository;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

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

    private Player player(String name, String nickname) {
        Player p = new Player();
        p.setName(name);
        p.setNickname(nickname);
        return p;
    }
}
