package sk.posam.fsa.statstracker.domain.service.match;

import sk.posam.fsa.statstracker.domain.match.Match;
import sk.posam.fsa.statstracker.domain.match.MatchWithStats;
import sk.posam.fsa.statstracker.domain.player.PlayerMatchStats;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;

import java.util.List;

/**
 * Inbound port pre operácie so zápasmi.
 * Implementovaný doménovou triedou {@link MatchService}.
 */
public interface MatchFacade {

    List<Match> getAll();

    List<Match> getAll(int page, int size);

    MatchWithStats getById(long id) throws StatsTrackerException;

    void create(Match match, List<PlayerMatchStats> team1Stats, List<PlayerMatchStats> team2Stats)
            throws StatsTrackerException;

    /**
     * Zmaže zápas vrátane všetkých štatistík hráčov.
     *
     * @throws StatsTrackerException type=NOT_FOUND ak zápas neexistuje
     */
    void deleteMatch(long matchId) throws StatsTrackerException;
}
