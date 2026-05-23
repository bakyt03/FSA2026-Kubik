package sk.posam.fsa.statstracker.domain;

import java.util.List;

public class PlayerDetail {

    private final Player player;
    private final PlayerStatsSnapshot stats;
    private final List<PlayerMatchHistoryEntry> recentMatches;

    public PlayerDetail(Player player, PlayerStatsSnapshot stats, List<PlayerMatchHistoryEntry> recentMatches) {
        this.player = player;
        this.stats = stats;
        this.recentMatches = recentMatches;
    }

    public Player getPlayer() { return player; }
    public PlayerStatsSnapshot getStats() { return stats; }
    public List<PlayerMatchHistoryEntry> getRecentMatches() { return recentMatches; }
}
