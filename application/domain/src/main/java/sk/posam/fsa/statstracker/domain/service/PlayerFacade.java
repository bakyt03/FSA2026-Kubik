package sk.posam.fsa.statstracker.domain.service;

import sk.posam.fsa.statstracker.domain.Player;
import sk.posam.fsa.statstracker.domain.PlayerDetail;
import sk.posam.fsa.statstracker.domain.PlayerMatchHistoryEntry;
import sk.posam.fsa.statstracker.domain.PlayerMeDetail;
import sk.posam.fsa.statstracker.domain.PlayerWithStats;
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

    /**
     * Vráti všetkých hráčov spolu s ich agregovanými štatistikami.
     */
    List<PlayerWithStats> findAllWithStats();

    PlayerDetail getById(long id) throws StatsTrackerException;

    List<PlayerMatchHistoryEntry> getMatchHistory(long playerId, int page, int size) throws StatsTrackerException;

    /**
     * Prepojí hráča s Keycloak účtom.
     *
     * @throws StatsTrackerException type=NOT_FOUND ak hráč neexistuje
     * @throws StatsTrackerException type=CONFLICT ak keycloakId je už prepojené s
     *                               iným hráčom
     */
    void linkToUser(long playerId, String keycloakId) throws StatsTrackerException;

    /**
     * Zruší prepojenie hráča s Keycloak účtom.
     *
     * @throws StatsTrackerException type=NOT_FOUND ak hráč neexistuje
     */
    void unlinkUser(long playerId) throws StatsTrackerException;

    /**
     * Vráti detail hráča prepojeného s daným Keycloak sub.
     *
     * @throws StatsTrackerException type=NOT_FOUND ak žiadny hráč nie je prepojený
     *                               s týmto keycloakId
     */
    PlayerMeDetail getMe(String keycloakSub) throws StatsTrackerException;

    /**
     * Zmaže hráča. Štatistiky z odohraných zápasov zostanú zachované
     * (anonymizácia).
     *
     * @throws StatsTrackerException type=NOT_FOUND ak hráč neexistuje
     */
    void deletePlayer(long playerId) throws StatsTrackerException;
}
