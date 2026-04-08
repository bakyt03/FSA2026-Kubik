package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.StatsTrackerException;

import java.util.List;

/**
 * Inbound port pre operácie s hráčmi.
 * Implementovaný doménovou triedou {@link PlayerService}.
 */
public interface PlayerFacade {

    /**
     * Vytvorí nového hráča.
     *
     * @throws StatsTrackerException type=VALIDATION ak name alebo nickname chýbajú
     * @throws StatsTrackerException type=CONFLICT ak prezývka je už obsadená
     */
    void createPlayer(Player player) throws StatsTrackerException;

    /**
     * Vráti všetkých hráčov v systéme.
     */
    List<Player> findAll();
}
