package sk.posam.fsa.statstracker.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import sk.posam.fsa.statstracker.domain.Match;

import java.util.List;

public interface MatchSpringDataRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByOrderByPlayedAtDesc();

    List<Match> findAllByIdIn(List<Long> ids);
}
