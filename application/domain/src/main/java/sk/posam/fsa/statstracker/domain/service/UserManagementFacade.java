package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.KeycloakUserInfo;

import java.util.List;

public interface UserManagementFacade {
    void createUser(String email, String password);

    List<KeycloakUserInfo> listUsers();
}
