package sk.posam.fsa.statstracker.mapper;

import org.springframework.stereotype.Component;
import sk.posam.fsa.statstracker.rest.dto.CreatePlayerRequestDto;
import sk.posam.fsa.statstracker.rest.dto.PlayerSummaryDto;
import sk.posam.fsa.statstracker.domain.Player;

@Component
public class PlayerMapper {

    public Player toEntity(CreatePlayerRequestDto dto) {
        if(dto == null) {
            return null;
        }
        Player player = new Player();
        player.setName(dto.getName());
        player.setNickname(dto.getNickname());

        return player;
    }

    public PlayerSummaryDto toDto(Player player) {
        if(player == null) {
            return null;
        }
        PlayerSummaryDto dto = new PlayerSummaryDto();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setNickname(player.getNickname());

        return dto;
    }
}
