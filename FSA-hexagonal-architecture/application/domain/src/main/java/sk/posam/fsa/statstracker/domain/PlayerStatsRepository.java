package sk.posam.fsa.statstracker.domain;

import java.util.List;

/**
 * Outbound port – získanie agregovaných štatistík hráčov z perzistentnej
 * vrstvy.
 * Implementovaný v module outbound-repository-jpa.
 */
public interface PlayerStatsRepository {

    /**
     * Vráti snapshot priemerných štatistík pre každého z požadovaných hráčov.
     * Ak pre hráča neexistujú žiadne záznamy, vráti snapshot s
     * {@code matchesPlayed = 0}.
     */
    List<PlayerStatsSnapshot> getForPlayers(List<Long> playerIds);

    /**
     * Rovnaké ako {@link #getForPlayers(List)}, ale berie do úvahy iba
     * posledných {@code recentMatchesLimit} zápasov každého hráča.
     */
    List<PlayerStatsSnapshot> getForPlayers(List<Long> playerIds, int recentMatchesLimit);
}
