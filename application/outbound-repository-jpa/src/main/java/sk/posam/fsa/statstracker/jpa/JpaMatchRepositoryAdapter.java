package sk.posam.fsa.statstracker.jpa;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.posam.fsa.statstracker.domain.Match;
import sk.posam.fsa.statstracker.domain.MatchRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaMatchRepositoryAdapter implements MatchRepository {

    private final MatchSpringDataRepository matchSpringDataRepository;

    public JpaMatchRepositoryAdapter(MatchSpringDataRepository matchSpringDataRepository) {
        this.matchSpringDataRepository = matchSpringDataRepository;
    }

    @Override
    public Optional<Match> get(long id) {
        return matchSpringDataRepository.findById(id);
    }

    @Override
    public List<Match> getAll() {
        return matchSpringDataRepository.findAllByOrderByPlayedAtDesc();
    }

    @Override
    public List<Match> getAll(int page, int size) {
        return matchSpringDataRepository.findAllByOrderByPlayedAtDesc(PageRequest.of(page, size)).getContent();
    }

    @Override
    public List<Match> getAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty())
            return List.of();
        return matchSpringDataRepository.findAllByIdIn(ids);
    }

    @Override
    public Match create(Match match) {
        return matchSpringDataRepository.save(match);
    }

    @Override
    @Transactional
    public void delete(long matchId) {
        matchSpringDataRepository.deleteById(matchId);
    }
}
