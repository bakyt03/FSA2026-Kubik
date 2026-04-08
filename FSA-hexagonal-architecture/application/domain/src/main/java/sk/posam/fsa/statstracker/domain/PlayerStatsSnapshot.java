package sk.posam.fsa.statstracker.domain;

/**
 * Agregované priemerné štatistiky hráča naprieč všetkými odohratými zápasmi.
 * Hodnotový objekt – nemá identitu, iba nesie vypočítané hodnoty.
 */
public class PlayerStatsSnapshot {

    private long playerId;
    private double avgHltvRating;
    private double avgAdr;
    private double avgKdRatio;
    private int matchesPlayed;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public double getAvgHltvRating() {
        return avgHltvRating;
    }

    public void setAvgHltvRating(double avgHltvRating) {
        this.avgHltvRating = avgHltvRating;
    }

    public double getAvgAdr() {
        return avgAdr;
    }

    public void setAvgAdr(double avgAdr) {
        this.avgAdr = avgAdr;
    }

    public double getAvgKdRatio() {
        return avgKdRatio;
    }

    public void setAvgKdRatio(double avgKdRatio) {
        this.avgKdRatio = avgKdRatio;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    /**
     * Nastaví neutrálne hodnoty pre hráčov bez histórie zápasov.
     * Použije sa, keď matchesPlayed == 0.
     */
    public void applyNeutralValues() {
        this.avgHltvRating = 1.0;
        this.avgAdr = 70.0;
        this.avgKdRatio = 1.0;
    }
}
