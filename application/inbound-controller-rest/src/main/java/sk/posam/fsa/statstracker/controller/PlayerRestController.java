package sk.posam.fsa.statstracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.statstracker.rest.api.PlayersApi;
import sk.posam.fsa.statstracker.rest.dto.CreatePlayerRequestDto;
import sk.posam.fsa.statstracker.rest.dto.LinkUserRequestDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerDetailDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerMatchHistoryEntryDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerMeDetailDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerSummaryDto;
import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
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
                playerFacade.findAllWithStats().stream()
                        .map(playerMapper::toDto)
                        .toList());
    }

    @Override
    public ResponseEntity<PlayerDetailDto> getPlayer(Long id) {
        try {
            return ResponseEntity.ok(playerMapper.toDetailDto(playerFacade.getById(id)));
        } catch (StatsTrackerException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> createPlayer(CreatePlayerRequestDto createPlayerRequestDto) {
        Player player = playerMapper.toEntity(createPlayerRequestDto);
        playerFacade.createPlayer(player);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<PlayerMeDetailDto> getMyPlayer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String keycloakSub = (String) auth.getPrincipal();
        try {
            return ResponseEntity.ok(playerMapper.toMeDetailDto(playerFacade.getMe(keycloakSub)));
        } catch (StatsTrackerException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> linkPlayerToUser(Long id, LinkUserRequestDto linkUserRequestDto) {
        playerFacade.linkToUser(id, linkUserRequestDto.getKeycloakId());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> unlinkPlayerFromUser(Long id) {
        playerFacade.unlinkUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deletePlayer(Long id) {
        try {
            playerFacade.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (StatsTrackerException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<List<PlayerMatchHistoryEntryDto>> getPlayerMatches(Long id, Integer page, Integer size) {
        try {
            return ResponseEntity.ok(
                    playerFacade.getMatchHistory(id, page, size).stream()
                            .map(playerMapper::toHistoryEntryDto)
                            .toList());
        } catch (StatsTrackerException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
