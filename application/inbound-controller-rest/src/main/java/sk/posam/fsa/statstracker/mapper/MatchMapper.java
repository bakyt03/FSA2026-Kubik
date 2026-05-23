package sk.posam.fsa.statstracker.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.statstracker.domain.Match;
import sk.posam.fsa.statstracker.domain.MatchWithStats;
import sk.posam.fsa.statstracker.domain.PlayerInMatch;
import sk.posam.fsa.statstracker.domain.PlayerMatchStats;
import sk.posam.fsa.statstracker.rest.dto.CreateMatchRequestDto;
import sk.posam.fsa.statstracker.rest.dto.MatchDetailDto;
import sk.posam.fsa.statstracker.rest.dto.MatchPlayerStatsDto;
import sk.posam.fsa.statstracker.rest.dto.MatchSummaryDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerStatsRequestDto;

import java.util.List;

@Component
public class MatchMapper {

    public Match toEntity(CreateMatchRequestDto dto) {
        if (dto == null)
            return null;
        Match match = new Match();
        match.setMap(sk.posam.fsa.statstracker.domain.Map.valueOf(dto.getMap().getValue()));
        match.setPlayedAt(dto.getPlayedAt());
        match.setTeam1Score(dto.getTeam1Score());
        match.setTeam2Score(dto.getTeam2Score());
        return match;
    }

    public List<PlayerMatchStats> toTeam1Stats(CreateMatchRequestDto dto) {
        return toStatsList(dto.getTeam1Players());
    }

    public List<PlayerMatchStats> toTeam2Stats(CreateMatchRequestDto dto) {
        return toStatsList(dto.getTeam2Players());
    }

    private List<PlayerMatchStats> toStatsList(List<PlayerStatsRequestDto> dtos) {
        if (dtos == null)
            return List.of();
        return dtos.stream().map(this::toPlayerStats).toList();
    }

    private PlayerMatchStats toPlayerStats(PlayerStatsRequestDto dto) {
        PlayerMatchStats stats = new PlayerMatchStats();
        stats.setPlayerId(dto.getPlayerId());
        stats.setKills(dto.getKills());
        stats.setDeaths(dto.getDeaths());
        stats.setDamage(dto.getDamage());
        return stats;
    }

    public MatchSummaryDto toDto(Match match) {
        if (match == null)
            return null;
        MatchSummaryDto dto = new MatchSummaryDto();
        dto.setId(match.getId());
        dto.setMap(match.getMap() != null ? match.getMap().name() : null);
        dto.setPlayedAt(match.getPlayedAt());
        dto.setTeam1Score(match.getTeam1Score());
        dto.setTeam2Score(match.getTeam2Score());
        return dto;
    }

    public MatchDetailDto toDetailDto(MatchWithStats mws) {
        if (mws == null)
            return null;
        Match match = mws.getMatch();
        MatchDetailDto dto = new MatchDetailDto();
        dto.setId(match.getId());
        dto.setMap(match.getMap() != null ? match.getMap().name() : null);
        dto.setPlayedAt(match.getPlayedAt());
        dto.setTeam1Score(match.getTeam1Score());
        dto.setTeam2Score(match.getTeam2Score());
        dto.setTeam1Players(mws.getTeam1Players().stream().map(this::toPlayerStatsDto).toList());
        dto.setTeam2Players(mws.getTeam2Players().stream().map(this::toPlayerStatsDto).toList());
        return dto;
    }

    private MatchPlayerStatsDto toPlayerStatsDto(PlayerInMatch p) {
        MatchPlayerStatsDto dto = new MatchPlayerStatsDto();
        dto.setPlayerId(p.getPlayerId());
        dto.setPlayerNickname(p.getPlayerNickname());
        dto.setKills(p.getKills());
        dto.setDeaths(p.getDeaths());
        dto.setDamage(p.getDamage());
        dto.setAdr(p.getAdr());
        return dto;
    }
}
