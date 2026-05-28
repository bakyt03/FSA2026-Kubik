package sk.posam.fsa.statstracker.domain.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.posam.fsa.statstracker.domain.user.KeycloakUserInfo;
import sk.posam.fsa.statstracker.domain.player.Player;
import sk.posam.fsa.statstracker.domain.player.PlayerRepository;
import sk.posam.fsa.statstracker.domain.user.UserManagementPort;
import sk.posam.fsa.statstracker.domain.service.userManagement.UserManagementService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserManagementPort port;

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private UserManagementService service;

    @Test
    void createUserDelegatesToPort() {
        service.createUser("alice@example.com", "secret", "Alice", "Smith");

        verify(port).createUser("alice@example.com", "secret", "Alice", "Smith");
    }

    @Test
    void listUsersDelegatesToPort() {
        KeycloakUserInfo user = new KeycloakUserInfo("kc-1", "alice", "alice@example.com", "Alice", "Smith");
        when(port.listUsers()).thenReturn(List.of(user));

        List<KeycloakUserInfo> result = service.listUsers();

        assertEquals(1, result.size());
        assertEquals("alice", result.get(0).getUsername());
        verify(port).listUsers();
    }

    @Test
    void deleteUserUnlinksLinkedPlayerThenDeletesFromPort() {
        Player player = new Player();
        player.setId(1L);
        player.setKeycloakId("kc-123");
        when(playerRepository.getByKeycloakId("kc-123")).thenReturn(Optional.of(player));

        service.deleteUser("kc-123");

        assertNull(player.getKeycloakId());
        var order = inOrder(playerRepository, port);
        order.verify(playerRepository).update(player);
        order.verify(port).deleteUser("kc-123");
    }

    @Test
    void deleteUserStillDeletesFromPortWhenNoPlayerIsLinked() {
        when(playerRepository.getByKeycloakId("kc-orphan")).thenReturn(Optional.empty());

        service.deleteUser("kc-orphan");

        verify(playerRepository, never()).update(any());
        verify(port).deleteUser("kc-orphan");
    }
}
