package sk.posam.fsa.statstracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.statstracker.rest.api.TeamSuggestionsApi;
import sk.posam.fsa.statstracker.rest.dto.TeamSuggestionRequestDto;
import sk.posam.fsa.statstracker.rest.dto.TeamSuggestionResponseDto;
import sk.posam.fsa.statstracker.domain.team.TeamSuggestionResult;
import sk.posam.fsa.statstracker.domain.service.teamSuggestion.TeamSuggestionFacade;
import sk.posam.fsa.statstracker.mapper.TeamSuggestionMapper;

@RestController
public class TeamSuggestionRestController implements TeamSuggestionsApi {

    private static final Logger log = LoggerFactory.getLogger(TeamSuggestionRestController.class);

    private final TeamSuggestionFacade teamSuggestionFacade;
    private final TeamSuggestionMapper teamSuggestionMapper;

    public TeamSuggestionRestController(TeamSuggestionFacade teamSuggestionFacade,
            TeamSuggestionMapper teamSuggestionMapper) {
        this.teamSuggestionFacade = teamSuggestionFacade;
        this.teamSuggestionMapper = teamSuggestionMapper;
    }

    @Override
    public ResponseEntity<TeamSuggestionResponseDto> generateTeamSuggestions(
            TeamSuggestionRequestDto teamSuggestionRequestDto) {
        log.info("Generating team suggestions: players={}, maxAdrDifference={}",
                teamSuggestionRequestDto.getPlayerIds().size(),
                teamSuggestionRequestDto.getMaxAdrDifference());
        TeamSuggestionResult result = teamSuggestionFacade.generateSuggestions(
                teamSuggestionRequestDto.getPlayerIds(),
                teamSuggestionRequestDto.getMaxAdrDifference());
        TeamSuggestionResponseDto responseDto = teamSuggestionMapper.toDto(result);
        return ResponseEntity.ok(responseDto);
    }
}
