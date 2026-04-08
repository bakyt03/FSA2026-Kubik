package sk.posam.fsa.statstracker.jpa;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.PlayerRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaPlayerRepositoryAdapter implements PlayerRepository {

    private final PlayerSpringDataRepository springDataRepository;

    public JpaPlayerRepositoryAdapter(PlayerSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<Player> get(long id) {
        return springDataRepository.findById(id);
    }

    @Override
    public Optional<Player> get(String nickname) {
        return springDataRepository.findByNickname(nickname);
    }

    @Override
    public List<Player> getAll() {
        return springDataRepository.findAll();
    }

    @Override
    public List<Player> getByIds(List<Long> ids) {
        return springDataRepository.findAllById(ids);
    }

    @Override
    @Transactional
    public void create(Player player) {
        springDataRepository.save(player);
    }
}
