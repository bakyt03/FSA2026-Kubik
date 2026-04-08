package sk.posam.fsa.statstracker.domain;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository {
    Optional<Player> get(long id);

    Optional<Player> get(String nickname);

    List<Player> getAll();

    List<Player> getByIds(List<Long> ids);

    void create(Player player);
}
