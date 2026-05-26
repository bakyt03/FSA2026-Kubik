package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.KeycloakUserInfo;
import sk.posam.fsa.statstracker.domain.UserManagementPort;

import java.util.List;

public class UserManagementService implements UserManagementFacade {

    private final UserManagementPort port;

    public UserManagementService(UserManagementPort port) {
        this.port = port;
    }

    @Override
    public void createUser(String email, String password) {
        port.createUser(email, password);
    }

    @Override
    public List<KeycloakUserInfo> listUsers() {
        return port.listUsers();
    }
}
