package sk.posam.fsa.statstracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import sk.posam.fsa.statstracker.domain.player.Player;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.service.player.PlayerFacade;
import sk.posam.fsa.statstracker.mapper.PlayerMapper;

import java.util.List;

@RestController
public class PlayerRestController implements PlayersApi {

    private static final Logger log = LoggerFactory.getLogger(PlayerRestController.class);

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
            log.warn("Player not found: id={}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> createPlayer(CreatePlayerRequestDto createPlayerRequestDto) {
        log.info("Creating player: nickname='{}'", createPlayerRequestDto.getNickname());
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
            log.warn("No player linked to user: sub={}", keycloakSub);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> linkPlayerToUser(Long id, LinkUserRequestDto linkUserRequestDto) {
        log.info("Linking player id={} to keycloak user id={}", id, linkUserRequestDto.getKeycloakId());
        playerFacade.linkToUser(id, linkUserRequestDto.getKeycloakId());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> unlinkPlayerFromUser(Long id) {
        log.info("Unlinking user from player id={}", id);
        playerFacade.unlinkUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deletePlayer(Long id) {
        try {
            log.info("Deleting player id={}", id);
            playerFacade.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (StatsTrackerException e) {
            log.warn("Player not found for deletion: id={}", id);
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
            log.warn("Player not found for match history: id={}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
