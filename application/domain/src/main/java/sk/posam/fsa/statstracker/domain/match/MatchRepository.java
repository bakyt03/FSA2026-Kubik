package sk.posam.fsa.statstracker.domain.match;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {
    Optional<Match> get(long id);

    List<Match> getAll();

    List<Match> getAll(int page, int size);

    List<Match> getAllByIds(List<Long> ids);

    Match create(Match match);

    void delete(long matchId);
}
