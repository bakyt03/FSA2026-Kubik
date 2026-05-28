package sk.posam.fsa.statstracker.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.statstracker.rest.dto.CreatePlayerRequestDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerDetailDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerMatchHistoryEntryDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerMeDetailDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerSummaryDto;
import sk.posam.fsa.statstracker.domain.match.Match;
import sk.posam.fsa.statstracker.domain.player.Player;
import sk.posam.fsa.statstracker.domain.player.PlayerDetail;
import sk.posam.fsa.statstracker.domain.player.PlayerMatchHistoryEntry;
import sk.posam.fsa.statstracker.domain.player.PlayerMeDetail;
import sk.posam.fsa.statstracker.domain.player.PlayerStatsSnapshot;
import sk.posam.fsa.statstracker.domain.player.PlayerWithStats;

@Component
public class PlayerMapper {

    public Player toEntity(CreatePlayerRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Player player = new Player();
        player.setName(dto.getName());
        player.setNickname(dto.getNickname());

        return player;
    }

    public PlayerSummaryDto toDto(Player player) {
        if (player == null) {
            return null;
        }
        PlayerSummaryDto dto = new PlayerSummaryDto();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setNickname(player.getNickname());
        dto.setKeycloakId(player.getKeycloakId());

        return dto;
    }

    public PlayerSummaryDto toDto(PlayerWithStats pws) {
        if (pws == null)
            return null;
        PlayerSummaryDto dto = toDto(pws.getPlayer());
        PlayerStatsSnapshot s = pws.getStats();
        if (s != null) {
            dto.setMatchesPlayed(s.getMatchesPlayed());
            dto.setAvgKills(s.getAvgKills());
            dto.setAvgDeaths(s.getAvgDeaths());
            dto.setAvgAdr(s.getAvgAdr());
            dto.setAvgKdRatio(s.getAvgKdRatio());
            dto.setWinRate(s.getWinRate());
            dto.setAvgKillsPerRound(s.getAvgKillsPerRound());
        }
        return dto;
    }

    public PlayerDetailDto toDetailDto(PlayerDetail pd) {
        if (pd == null)
            return null;
        PlayerDetailDto dto = new PlayerDetailDto();
        Player player = pd.getPlayer();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setNickname(player.getNickname());
        PlayerStatsSnapshot s = pd.getStats();
        if (s != null) {
            dto.setMatchesPlayed(s.getMatchesPlayed());
            dto.setAvgKills(s.getAvgKills());
            dto.setAvgDeaths(s.getAvgDeaths());
            dto.setAvgAdr(s.getAvgAdr());
        }
        dto.setRecentMatches(pd.getRecentMatches().stream().map(this::toHistoryEntryDto).toList());
        dto.setKeycloakId(player.getKeycloakId());
        return dto;
    }

    public PlayerMeDetailDto toMeDetailDto(PlayerMeDetail pmd) {
        if (pmd == null)
            return null;
        PlayerMeDetailDto dto = new PlayerMeDetailDto();
        Player player = pmd.getPlayer();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setNickname(player.getNickname());
        PlayerStatsSnapshot s = pmd.getStats();
        if (s != null) {
            dto.setMatchesPlayed(s.getMatchesPlayed());
            dto.setAvgKills(s.getAvgKills());
            dto.setAvgDeaths(s.getAvgDeaths());
            dto.setAvgAdr(s.getAvgAdr());
        }
        dto.setRank(pmd.getRank());
        dto.setRecentMatches(pmd.getRecentMatches().stream().map(this::toHistoryEntryDto).toList());
        return dto;
    }

    public PlayerMatchHistoryEntryDto toHistoryEntryDto(PlayerMatchHistoryEntry entry) {
        PlayerMatchHistoryEntryDto dto = new PlayerMatchHistoryEntryDto();
        Match match = entry.getMatch();
        dto.setMatchId(match.getId());
        dto.setMap(match.getMap() != null ? match.getMap().name() : null);
        dto.setPlayedAt(match.getPlayedAt());
        dto.setTeam1Score(match.getTeam1Score());
        dto.setTeam2Score(match.getTeam2Score());
        dto.setPlayerTeam(entry.getStats().getTeam());
        dto.setKills(entry.getStats().getKills());
        dto.setDeaths(entry.getStats().getDeaths());
        dto.setDamage(entry.getStats().getDamage());
        dto.setAdr(entry.getStats().getAdr());
        return dto;
    }
}
