package sk.posam.fsa.statstracker.domain;

import java.util.List;

public class PlayerMeDetail {

    private final Player player;
    private final PlayerStatsSnapshot stats;
    private final List<PlayerMatchHistoryEntry> recentMatches;
    private final int rank;

    public PlayerMeDetail(Player player, PlayerStatsSnapshot stats, List<PlayerMatchHistoryEntry> recentMatches,
            int rank) {
        this.player = player;
        this.stats = stats;
        this.recentMatches = recentMatches;
        this.rank = rank;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerStatsSnapshot getStats() {
        return stats;
    }

    public List<PlayerMatchHistoryEntry> getRecentMatches() {
        return recentMatches;
    }

    public int getRank() {
        return rank;
    }
}
