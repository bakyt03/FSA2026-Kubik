package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.StatsTrackerException;
import sk.posam.fsa.statstracker.domain.TeamSuggestionResult;

import java.util.List;

/**
 * Inbound port pre generovanie návrhov tímov (UC4).
 * Implementovaný doménovou triedou {@link TeamSuggestionService}.
 */
public interface TeamSuggestionFacade {

    /**
     * Vygeneruje min. 3 návrhy vyrovnaných tímov pre presne 10 hráčov.
     *
     * @param playerIds        presne 10 unikátnych ID hráčov
     * @param maxAdrDifference maximálny povolený rozdiel ADR medzi tímami
     * @return výsledok s návrhmi v rámci prahu a varovaniami pre hráčov bez
     *         histórie
     * @throws StatsTrackerException type=VALIDATION ak počet alebo unikátnosť
     *                               podmienky nie sú splnené
     * @throws StatsTrackerException type=NOT_FOUND ak niektorý hráč neexistuje
     */
    TeamSuggestionResult generateSuggestions(List<Long> playerIds, double maxAdrDifference)
            throws StatsTrackerException;
}
