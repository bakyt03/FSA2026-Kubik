package sk.posam.fsa.statstracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import sk.posam.fsa.statstracker.rest.api.TeamSuggestionsApi;
import sk.posam.fsa.statstracker.rest.dto.TeamSuggestionRequestDto;
import sk.posam.fsa.statstracker.rest.dto.TeamSuggestionResponseDto;
import sk.posam.fsa.statstracker.domain.TeamSuggestionResult;
import sk.posam.fsa.statstracker.domain.service.TeamSuggestionFacade;
import sk.posam.fsa.statstracker.mapper.TeamSuggestionMapper;

@RestController
public class TeamSuggestionRestController implements TeamSuggestionsApi {

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
        TeamSuggestionResult result = teamSuggestionFacade.generateSuggestions(
                teamSuggestionRequestDto.getPlayerIds());
        TeamSuggestionResponseDto responseDto = teamSuggestionMapper.toDto(result);
        return ResponseEntity.ok(responseDto);
    }
}
