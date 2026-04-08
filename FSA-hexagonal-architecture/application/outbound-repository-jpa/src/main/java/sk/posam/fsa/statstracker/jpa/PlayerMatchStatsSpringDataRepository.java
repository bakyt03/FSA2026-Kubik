package sk.posam.fsa.statstracker.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.posam.fsa.statstracker.domain.PlayerMatchStats;

import java.util.List;

public interface PlayerMatchStatsSpringDataRepository extends JpaRepository<PlayerMatchStats, Long> {

    /**
     * Agreguje priemerné štatistiky pre zoznam hráčov (všetky zápasy).
     */
    @Query("SELECT s.playerId, AVG(s.hltvRating), AVG(s.adr), " +
            "AVG(CASE WHEN s.deaths = 0 THEN s.kills ELSE (s.kills * 1.0 / s.deaths) END), COUNT(s) " +
            "FROM PlayerMatchStats s WHERE s.playerId IN :playerIds GROUP BY s.playerId")
    List<Object[]> findAggregatedStatsForPlayers(@Param("playerIds") List<Long> playerIds);

    /**
     * Vráti všetky záznamy pre zadaných hráčov zoradené zostupne podľa ID.
     * Limitovanie na posledných N záznamov prebieha v adaptéri (in-memory
     * grouping).
     */
    @Query("SELECT s FROM PlayerMatchStats s WHERE s.playerId IN :playerIds ORDER BY s.playerId ASC, s.id DESC")
    List<PlayerMatchStats> findAllByPlayerIdInOrderByIdDesc(@Param("playerIds") List<Long> playerIds);
}
