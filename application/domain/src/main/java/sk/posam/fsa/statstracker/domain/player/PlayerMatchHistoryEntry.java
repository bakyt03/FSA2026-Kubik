package sk.posam.fsa.statstracker.domain.player;

import sk.posam.fsa.statstracker.domain.match.Match;

public class PlayerMatchHistoryEntry {

    private final Match match;
    private final PlayerMatchStats stats;

    public PlayerMatchHistoryEntry(Match match, PlayerMatchStats stats) {
        this.match = match;
        this.stats = stats;
    }

    public Match getMatch() { return match; }
    public PlayerMatchStats getStats() { return stats; }
}
