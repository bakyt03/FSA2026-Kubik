package sk.posam.fsa.statstracker.domain.player;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {
    Optional<Player> get(long id);

    Optional<Player> get(String nickname);

    Optional<Player> getByKeycloakId(String keycloakId);

    List<Player> getAll();

    List<Player> getByIds(List<Long> ids);

    void create(Player player);

    void update(Player player);

    void delete(long playerId);
}
