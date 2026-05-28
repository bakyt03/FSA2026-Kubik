package sk.posam.fsa.statstracker.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sk.posam.fsa.statstracker.domain.match.Match;

import java.util.List;

public interface MatchSpringDataRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByOrderByPlayedAtDesc();

    Page<Match> findAllByOrderByPlayedAtDesc(Pageable pageable);

    List<Match> findAllByIdIn(List<Long> ids);
}
