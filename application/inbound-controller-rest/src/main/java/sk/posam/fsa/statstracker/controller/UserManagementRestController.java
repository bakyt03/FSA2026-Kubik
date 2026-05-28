package sk.posam.fsa.statstracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.statstracker.domain.user.KeycloakUserInfo;
import sk.posam.fsa.statstracker.domain.service.userManagement.UserManagementFacade;
import sk.posam.fsa.statstracker.rest.api.UsersApi;
import sk.posam.fsa.statstracker.rest.dto.KeycloakUserDtoDto;
import sk.posam.fsa.statstracker.rest.dto.UserCreateRequestDto;

import java.util.List;

@RestController
public class UserManagementRestController implements UsersApi {

    private static final Logger log = LoggerFactory.getLogger(UserManagementRestController.class);

    private final UserManagementFacade userManagementFacade;

    public UserManagementRestController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    @Override
    public ResponseEntity<Void> createUser(UserCreateRequestDto request) {
        log.info("Creating user: email='{}'", request.getEmail());
        userManagementFacade.createUser(request.getEmail(), request.getPassword(),
                request.getFirstName(), request.getLastName());
        return ResponseEntity.status(201).build();
    }

    @Override
    public ResponseEntity<List<KeycloakUserDtoDto>> listUsers() {
        List<KeycloakUserDtoDto> users = userManagementFacade.listUsers().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    private KeycloakUserDtoDto toDto(KeycloakUserInfo info) {
        KeycloakUserDtoDto dto = new KeycloakUserDtoDto();
        dto.setId(info.getId());
        dto.setUsername(info.getUsername());
        dto.setEmail(info.getEmail());
        dto.setFirstName(info.getFirstName());
        dto.setLastName(info.getLastName());
        return dto;
    }

    @Override
    public ResponseEntity<Void> deleteUser(String id) {
        log.info("Deleting user id={}", id);
        userManagementFacade.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
