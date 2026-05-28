package sk.posam.fsa.statstracker.domain.service.userManagement;

import sk.posam.fsa.statstracker.domain.user.KeycloakUserInfo;
import sk.posam.fsa.statstracker.domain.player.PlayerRepository;
import sk.posam.fsa.statstracker.domain.user.UserManagementPort;

import java.util.List;

public class UserManagementService implements UserManagementFacade {

    private final UserManagementPort port;
    private final PlayerRepository playerRepository;

    public UserManagementService(UserManagementPort port, PlayerRepository playerRepository) {
        this.port = port;
        this.playerRepository = playerRepository;
    }

    @Override
    public void createUser(String email, String password, String firstName, String lastName) {
        port.createUser(email, password, firstName, lastName);
    }

    @Override
    public List<KeycloakUserInfo> listUsers() {
        return port.listUsers();
    }

    @Override
    public void deleteUser(String userId) {
        playerRepository.getByKeycloakId(userId).ifPresent(player -> {
            player.setKeycloakId(null);
            playerRepository.update(player);
        });
        port.deleteUser(userId);
    }
}
