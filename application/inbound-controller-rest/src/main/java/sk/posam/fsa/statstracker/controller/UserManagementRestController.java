package sk.posam.fsa.statstracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.statstracker.domain.service.UserManagementFacade;
import sk.posam.fsa.statstracker.rest.api.UsersApi;
import sk.posam.fsa.statstracker.rest.dto.UserCreateRequestDto;

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
}
