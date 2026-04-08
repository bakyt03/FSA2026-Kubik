package sk.posam.fsa.statstracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.statstracker.rest.api.PlayersApi;
import sk.posam.fsa.statstracker.rest.dto.CreatePlayerRequestDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerSummaryDto;
import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.service.PlayerFacade;
import sk.posam.fsa.statstracker.mapper.PlayerMapper;

import java.util.List;

@RestController
public class PlayerRestController implements PlayersApi {

    private final PlayerFacade playerFacade;
    private final PlayerMapper playerMapper;

    public PlayerRestController(PlayerFacade playerFacade, PlayerMapper playerMapper) {
        this.playerFacade = playerFacade;
        this.playerMapper = playerMapper;
    }

    @Override
    public ResponseEntity<List<PlayerSummaryDto>> listPlayers() {
        return ResponseEntity.ok(
                playerFacade.findAll().stream()
                        .map(playerMapper::toDto)
                        .toList()
        );
    }

    @Override
    public ResponseEntity<Void> createPlayer(CreatePlayerRequestDto createPlayerRequestDto) {
        Player player = playerMapper.toEntity(createPlayerRequestDto);
        playerFacade.createPlayer(player);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
