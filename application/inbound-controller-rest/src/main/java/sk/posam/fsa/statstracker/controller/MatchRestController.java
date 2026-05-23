package sk.posam.fsa.statstracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.statstracker.rest.api.MatchesApi;
import sk.posam.fsa.statstracker.rest.dto.CreateMatchRequestDto;
import sk.posam.fsa.statstracker.rest.dto.MatchDetailDto;
import sk.posam.fsa.statstracker.rest.dto.MatchSummaryDto;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.service.MatchFacade;
import sk.posam.fsa.statstracker.mapper.MatchMapper;

import java.util.List;

@RestController
public class MatchRestController implements MatchesApi {

    private final MatchFacade matchFacade;
    private final MatchMapper matchMapper;

    public MatchRestController(MatchFacade matchFacade, MatchMapper matchMapper) {
        this.matchFacade = matchFacade;
        this.matchMapper = matchMapper;
    }

    @Override
    public ResponseEntity<List<MatchSummaryDto>> listMatches() {
        return ResponseEntity.ok(
                matchFacade.getAll().stream()
                        .map(matchMapper::toDto)
                        .toList());
    }

    @Override
    public ResponseEntity<MatchDetailDto> getMatch(Long id) {
        try {
            return ResponseEntity.ok(matchMapper.toDetailDto(matchFacade.getById(id)));
        } catch (StatsTrackerException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> createMatch(CreateMatchRequestDto createMatchRequestDto) {
        matchFacade.create(
                matchMapper.toEntity(createMatchRequestDto),
                matchMapper.toTeam1Stats(createMatchRequestDto),
                matchMapper.toTeam2Stats(createMatchRequestDto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
