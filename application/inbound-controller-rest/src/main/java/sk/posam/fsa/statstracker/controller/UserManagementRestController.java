package sk.posam.fsa.statstracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.statstracker.domain.KeycloakUserInfo;
import sk.posam.fsa.statstracker.domain.service.UserManagementFacade;
import sk.posam.fsa.statstracker.rest.api.UsersApi;
import sk.posam.fsa.statstracker.rest.dto.KeycloakUserDtoDto;
import sk.posam.fsa.statstracker.rest.dto.UserCreateRequestDto;

import java.util.List;

@RestController
public class UserManagementRestController implements UsersApi {

    private final UserManagementFacade userManagementFacade;

    public UserManagementRestController(UserManagementFacade userManagementFacade) {
        this.userManagementFacade = userManagementFacade;
    }

    @Override
    public ResponseEntity<Void> createUser(UserCreateRequestDto request) {
        userManagementFacade.createUser(request.getEmail(), request.getPassword());
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
        return dto;
    }
}
