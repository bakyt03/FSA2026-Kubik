package sk.posam.fsa.statstracker.domain;

import java.util.List;

public interface UserManagementPort {
    void createUser(String email, String password, String firstName, String lastName);

    List<KeycloakUserInfo> listUsers();

    void deleteUser(String userId);
}
