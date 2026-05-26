package sk.posam.fsa.statstracker.domain;

public class KeycloakUserInfo {

    private final String id;
    private final String username;
    private final String email;

    public KeycloakUserInfo(String id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
