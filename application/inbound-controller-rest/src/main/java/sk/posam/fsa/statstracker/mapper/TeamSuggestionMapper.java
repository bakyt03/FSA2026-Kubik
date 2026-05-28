package sk.posam.fsa.statstracker.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.statstracker.domain.Team;
import sk.posam.fsa.statstracker.domain.TeamSuggestion;
import sk.posam.fsa.statstracker.rest.dto.TeamSuggestionResponseDto;
import sk.posam.fsa.statstracker.rest.dto.TeamDefinitionDto;
import sk.posam.fsa.statstracker.rest.dto.TeamSuggestionDto;
import sk.posam.fsa.statstracker.domain.TeamSuggestionResult;

import java.util.List;
import java.util.Map;

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
                : result.getSuggestions().stream()
                        .map(s -> toSuggestionDto(s, result.getPlayerAdrMap()))
                        .toList();

        List<String> warnings = result.getWarnings() == null ? List.of() : result.getWarnings();

        dto.setSuggestions(suggestions);
        dto.setWarnings(warnings);
        return dto;
    }

    private TeamSuggestionDto toSuggestionDto(TeamSuggestion suggestion, Map<Long, Double> playerAdrMap) {
        TeamSuggestionDto dto = new TeamSuggestionDto();
        if (suggestion == null) {
            dto.setTeamA(new TeamDefinitionDto().players(List.of()));
            dto.setTeamB(new TeamDefinitionDto().players(List.of()));
            dto.setAdrDifference(0.0);
            dto.setTeamAAdrAvg(0.0);
            dto.setTeamBAdrAvg(0.0);
            return dto;
        }

        Map<Long, Double> adrMap = playerAdrMap != null ? playerAdrMap : Map.of();
        dto.setTeamA(toTeamDto(suggestion.getTeamA(), adrMap));
        dto.setTeamB(toTeamDto(suggestion.getTeamB(), adrMap));
        dto.setAdrDifference(suggestion.getAdrDifference());
        dto.setTeamAAdrAvg(suggestion.getTeamAAdrAvg());
        dto.setTeamBAdrAvg(suggestion.getTeamBAdrAvg());
        return dto;
    }

    private TeamDefinitionDto toTeamDto(Team team, Map<Long, Double> playerAdrMap) {
        TeamDefinitionDto dto = new TeamDefinitionDto();
        if (team == null || team.getPlayers() == null) {
            dto.setPlayers(List.of());
            return dto;
        }
        dto.setPlayers(team.getPlayers().stream().map(p -> {
            var pd = playerMapper.toDto(p);
            pd.setAvgAdr(playerAdrMap.getOrDefault(p.getId(), 0.0));
            return pd;
        }).toList());
        return dto;
    }
}
