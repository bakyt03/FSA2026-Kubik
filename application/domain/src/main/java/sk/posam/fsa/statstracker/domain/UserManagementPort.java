package sk.posam.fsa.statstracker.domain;

public interface UserManagementPort {
    void createUser(String email, String password);
}
