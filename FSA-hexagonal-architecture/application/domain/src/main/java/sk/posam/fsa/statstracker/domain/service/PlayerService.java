package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.PlayerRepository;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.predicate.IsNotNullPredicate;
import sk.posam.fsa.statstracker.domain.predicate.IsUniqueNicknamePredicate;

import java.util.List;

public class PlayerService implements PlayerFacade {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public void createPlayer(Player player) throws StatsTrackerException {
        require(IsNotNullPredicate.<Player>getInstance().test(player),
                StatsTrackerException.Type.VALIDATION, "Player must not be null");
        player.validateForCreation();

        Player existingPlayer = playerRepository.get(player.getNickname()).orElse(null);
        require(IsUniqueNicknamePredicate.INSTANCE.test(player, existingPlayer),
                StatsTrackerException.Type.CONFLICT, "Player with nickname '" + player.getNickname() + "' already exists");

        playerRepository.create(player);
    }

    @Override
    public List<Player> findAll() {
        return playerRepository.getAll();
    }

    private void require(boolean valid, StatsTrackerException.Type type, String message) {
        if (!valid) {
            throw new StatsTrackerException(type, message);
        }
    }
}
