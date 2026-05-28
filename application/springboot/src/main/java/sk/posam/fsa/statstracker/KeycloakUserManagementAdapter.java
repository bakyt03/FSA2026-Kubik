package sk.posam.fsa.statstracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import sk.posam.fsa.statstracker.domain.user.KeycloakUserInfo;
import sk.posam.fsa.statstracker.domain.user.UserManagementPort;

import java.util.List;
import java.util.Map;

@Component
public class KeycloakUserManagementAdapter implements UserManagementPort {

    private final RestClient restClient;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    public KeycloakUserManagementAdapter(
            @Value("${keycloak.admin.url:http://localhost:8081}") String adminUrl,
            @Value("${keycloak.admin.realm:FSA}") String realm,
            @Value("${keycloak.admin.client-id:fsa-backend-client}") String clientId,
            @Value("${keycloak.admin.client-secret:fsa-backend-client-secret}") String clientSecret) {
        this.restClient = RestClient.builder().baseUrl(adminUrl).build();
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public void createUser(String email, String password, String firstName, String lastName) {
        String token = getAdminToken();
        String userId = createKeycloakUser(token, email, password, firstName, lastName);
        assignUserRole(token, userId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<KeycloakUserInfo> listUsers() {
        String token = getAdminToken();
        List<Map<String, Object>> users = restClient.get()
                .uri("/admin/realms/{realm}/users?max=500", realm)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                });
        if (users == null) {
            return List.of();
        }
        return users.stream()
                .map(u -> new KeycloakUserInfo(
                        (String) u.get("id"),
                        (String) u.getOrDefault("username", ""),
                        (String) u.getOrDefault("email", ""),
                        (String) u.getOrDefault("firstName", ""),
                        (String) u.getOrDefault("lastName", "")))
                .toList();
    }

    @Override
    public void deleteUser(String userId) {
        String token = getAdminToken();
        restClient.delete()
                .uri("/admin/realms/{realm}/users/{userId}", realm, userId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }

    @SuppressWarnings("unchecked")
    private String getAdminToken() {
        String formBody = "grant_type=client_credentials"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret;

        Map<String, Object> response = restClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formBody)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("access_token") == null) {
            throw new IllegalStateException("Failed to obtain Keycloak admin token");
        }
        return (String) response.get("access_token");
    }

    private String createKeycloakUser(String token, String email, String password, String firstName, String lastName) {
        Map<String, Object> credential = Map.of(
                "type", "password",
                "value", password,
                "temporary", false);
        Map<String, Object> user = Map.of(
                "username", email,
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "enabled", true,
                "emailVerified", true,
                "credentials", List.of(credential));

        try {
            var response = restClient.post()
                    .uri("/admin/realms/{realm}/users", realm)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(user)
                    .retrieve()
                    .toBodilessEntity();

            String location = response.getHeaders().getFirst("Location");
            if (location == null) {
                throw new IllegalStateException("Keycloak did not return Location header after user creation");
            }
            return location.substring(location.lastIndexOf('/') + 1);
        } catch (HttpClientErrorException.Conflict e) {
            throw new IllegalArgumentException("User with email '" + email + "' already exists in Keycloak");
        }
    }

    @SuppressWarnings("unchecked")
    private void assignUserRole(String token, String userId) {
        Map<String, Object> role = restClient.get()
                .uri("/admin/realms/{realm}/roles/{roleName}", realm, "USER")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(Map.class);

        try {
            restClient.post()
                    .uri("/admin/realms/{realm}/users/{id}/role-mappings/realm", realm, userId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of(role))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Forbidden e) {
            throw new IllegalStateException(
                    "User was created but role assignment failed (403). " +
                            "Grant the 'manage-users' and 'manage-roles' (or 'realm-admin') roles " +
                            "to the '" + clientId + "' service account in the realm-management client.",
                    e);
        }
    }
}
