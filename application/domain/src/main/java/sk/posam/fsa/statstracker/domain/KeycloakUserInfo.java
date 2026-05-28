package sk.posam.fsa.statstracker.domain;

public class KeycloakUserInfo {

    private final String id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;

    public KeycloakUserInfo(String id, String username, String email, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
