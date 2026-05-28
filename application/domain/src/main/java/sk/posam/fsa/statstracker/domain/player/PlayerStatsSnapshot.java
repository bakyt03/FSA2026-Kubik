package sk.posam.fsa.statstracker.domain.player;

/**
 * Agregované priemerné štatistiky hráča naprieč všetkými odohratými zápasmi.
 * Hodnotový objekt – nemá identitu, iba nesie vypočítané hodnoty.
 */
public class PlayerStatsSnapshot {

    private long playerId;
    private double avgHltvRating;
    private double avgAdr;
    private double avgKdRatio;
    private double avgKills;
    private double avgDeaths;
    private int matchesPlayed;
    private double winRate;
    private double avgKillsPerRound;

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

    public double getAvgKills() {
        return avgKills;
    }

    public void setAvgKills(double avgKills) {
        this.avgKills = avgKills;
    }

    public double getAvgDeaths() {
        return avgDeaths;
    }

    public void setAvgDeaths(double avgDeaths) {
        this.avgDeaths = avgDeaths;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public double getAvgKillsPerRound() {
        return avgKillsPerRound;
    }

    public void setAvgKillsPerRound(double avgKillsPerRound) {
        this.avgKillsPerRound = avgKillsPerRound;
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
