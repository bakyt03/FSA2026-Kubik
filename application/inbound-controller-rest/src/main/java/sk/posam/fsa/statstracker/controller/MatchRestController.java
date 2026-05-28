package sk.posam.fsa.statstracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.statstracker.rest.api.MatchesApi;
import sk.posam.fsa.statstracker.rest.dto.CreateMatchRequestDto;
import sk.posam.fsa.statstracker.rest.dto.MatchDetailDto;
import sk.posam.fsa.statstracker.rest.dto.MatchSummaryDto;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.service.match.MatchFacade;
import sk.posam.fsa.statstracker.mapper.MatchMapper;

import java.util.List;

@RestController
public class MatchRestController implements MatchesApi {

    private static final Logger log = LoggerFactory.getLogger(MatchRestController.class);

    private final MatchFacade matchFacade;
    private final MatchMapper matchMapper;

    public MatchRestController(MatchFacade matchFacade, MatchMapper matchMapper) {
        this.matchFacade = matchFacade;
        this.matchMapper = matchMapper;
    }

    @Override
    public ResponseEntity<List<MatchSummaryDto>> listMatches(Integer page, Integer size) {
        return ResponseEntity.ok(
                matchFacade.getAll(page, size).stream()
                        .map(matchMapper::toDto)
                        .toList());
    }

    @Override
    public ResponseEntity<MatchDetailDto> getMatch(Long id) {
        try {
            return ResponseEntity.ok(matchMapper.toDetailDto(matchFacade.getById(id)));
        } catch (StatsTrackerException e) {
            log.warn("Match not found: id={}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> createMatch(CreateMatchRequestDto createMatchRequestDto) {
        log.info("Creating match");
        matchFacade.create(
                matchMapper.toEntity(createMatchRequestDto),
                matchMapper.toTeam1Stats(createMatchRequestDto),
                matchMapper.toTeam2Stats(createMatchRequestDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> deleteMatch(Long id) {
        try {
            log.info("Deleting match id={}", id);
            matchFacade.deleteMatch(id);
            return ResponseEntity.noContent().build();
        } catch (StatsTrackerException e) {
            log.warn("Match not found for deletion: id={}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
