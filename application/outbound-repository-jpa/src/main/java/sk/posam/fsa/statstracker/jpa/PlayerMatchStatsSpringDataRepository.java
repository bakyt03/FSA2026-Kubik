package sk.posam.fsa.statstracker.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.posam.fsa.statstracker.domain.PlayerMatchStats;

import java.util.List;

public interface PlayerMatchStatsSpringDataRepository extends JpaRepository<PlayerMatchStats, Long> {

    /**
     * Agreguje priemerné štatistiky pre zoznam hráčov (všetky zápasy).
     * JOIN-uje s Match entitou pre výpočet winRate a avgKillsPerRound.
     */
    @Query("SELECT s.playerId, AVG(s.hltvRating), AVG(s.adr), " +
            "AVG(CASE WHEN s.deaths = 0 THEN s.kills ELSE (s.kills * 1.0 / s.deaths) END), COUNT(DISTINCT s.matchId), "
            +
            "AVG(s.kills), AVG(s.deaths), " +
            "SUM(CASE WHEN (s.team = 'TEAM1' AND m.team1Score > m.team2Score) " +
            "         OR  (s.team = 'TEAM2' AND m.team2Score > m.team1Score) " +
            "    THEN 1.0 ELSE 0.0 END) * 100.0 / COUNT(s), " +
            "AVG(CASE WHEN (m.team1Score + m.team2Score) > 0 " +
            "    THEN (1.0 * s.kills / (m.team1Score + m.team2Score)) ELSE 0.0 END) " +
            "FROM PlayerMatchStats s, Match m WHERE s.matchId = m.id AND s.playerId IN :playerIds GROUP BY s.playerId")
    List<Object[]> findAggregatedStatsForPlayers(@Param("playerIds") List<Long> playerIds);

    /**
     * Vráti všetky záznamy pre zadaných hráčov zoradené zostupne podľa ID.
     * JOIN-uje s Match entitou (rovnaký filter ako findAggregatedStatsForPlayers)
     * aby sa vylúčili osirelé záznamy bez platného zápasu.
     * Limitovanie na posledných N záznamov prebieha v adaptéri (in-memory
     * grouping).
     */
    @Query("SELECT s FROM PlayerMatchStats s, Match m WHERE s.matchId = m.id AND s.playerId IN :playerIds ORDER BY s.playerId ASC, s.id DESC")
    List<PlayerMatchStats> findAllByPlayerIdInOrderByIdDesc(@Param("playerIds") List<Long> playerIds);

    List<PlayerMatchStats> findByMatchId(Long matchId);

    List<PlayerMatchStats> findByPlayerIdOrderByIdDesc(Long playerId, Pageable pageable);

    void deleteByMatchId(Long matchId);

    void deleteByPlayerId(Long playerId);
}
