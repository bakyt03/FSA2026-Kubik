package sk.posam.fsa.statstracker.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.statstracker.domain.Team;
import sk.posam.fsa.statstracker.domain.TeamSuggestion;
import sk.posam.fsa.statstracker.rest.dto.TeamSuggestionResponseDto;
import sk.posam.fsa.statstracker.rest.dto.TeamDefinitionDto;
import sk.posam.fsa.statstracker.rest.dto.TeamSuggestionDto;
import sk.posam.fsa.statstracker.domain.TeamSuggestionResult;

import java.util.List;

@Component
public class TeamSuggestionMapper {

    private final PlayerMapper playerMapper;

    public TeamSuggestionMapper(PlayerMapper playerMapper) {
        this.playerMapper = playerMapper;
    }

    public TeamSuggestionResponseDto toDto(TeamSuggestionResult result) {
        TeamSuggestionResponseDto dto = new TeamSuggestionResponseDto();
        if (result == null) {
            dto.setSuggestions(List.of());
            dto.setWarnings(List.of());
            return dto;
        }

        List<TeamSuggestionDto> suggestions = result.getSuggestions() == null
                ? List.of()
                : result.getSuggestions().stream().map(this::toSuggestionDto).toList();

        List<String> warnings = result.getWarnings() == null ? List.of() : result.getWarnings();

        dto.setSuggestions(suggestions);
        dto.setWarnings(warnings);
        return dto;
    }

    private TeamSuggestionDto toSuggestionDto(TeamSuggestion suggestion) {
        TeamSuggestionDto dto = new TeamSuggestionDto();
        if (suggestion == null) {
            dto.setTeamA(new TeamDefinitionDto().players(List.of()));
            dto.setTeamB(new TeamDefinitionDto().players(List.of()));
            dto.setBalanceScore(0.0);
            return dto;
        }

        dto.setTeamA(toTeamDto(suggestion.getTeamA()));
        dto.setTeamB(toTeamDto(suggestion.getTeamB()));
        dto.setBalanceScore(suggestion.getBalanceScore());
        return dto;
    }

    private TeamDefinitionDto toTeamDto(Team team) {
        TeamDefinitionDto dto = new TeamDefinitionDto();
        if (team == null || team.getPlayers() == null) {
            dto.setPlayers(List.of());
            return dto;
        }
        dto.setPlayers(team.getPlayers().stream().map(playerMapper::toDto).toList());
        return dto;
    }
}
