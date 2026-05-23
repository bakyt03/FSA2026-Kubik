package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.Match;
import sk.posam.fsa.statstracker.domain.MatchWithStats;
import sk.posam.fsa.statstracker.domain.PlayerMatchStats;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;

import java.util.List;

/**
 * Inbound port pre operácie so zápasmi.
 * Implementovaný doménovou triedou {@link MatchService}.
 */
public interface MatchFacade {

    List<Match> getAll();

    MatchWithStats getById(long id) throws StatsTrackerException;

    void create(Match match, List<PlayerMatchStats> team1Stats, List<PlayerMatchStats> team2Stats)
            throws StatsTrackerException;
}
