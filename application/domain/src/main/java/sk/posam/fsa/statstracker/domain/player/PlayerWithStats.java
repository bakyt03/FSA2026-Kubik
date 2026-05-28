package sk.posam.fsa.statstracker.domain.player;

/**
 * Kombinuje základné údaje o hráčovi s jeho agregovanými štatistikami.
 */
public class PlayerWithStats {

    private final Player player;
    private final PlayerStatsSnapshot stats;

    public PlayerWithStats(Player player, PlayerStatsSnapshot stats) {
        this.player = player;
        this.stats = stats;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerStatsSnapshot getStats() {
        return stats;
    }
}
