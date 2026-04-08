package sk.posam.fsa.statstracker.domain;

public class PlayerMatchStats {
    private Long id;
    private Long playerId;
    private int kills;
    private int deaths;
    private int assists;
    private double adr;
    private double hsPercentage;
    private double hltvRating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public double getAdr() {
        return adr;
    }

    public void setAdr(double adr) {
        this.adr = adr;
    }

    public double getHsPercentage() {
        return hsPercentage;
    }

    public void setHsPercentage(double hsPercentage) {
        this.hsPercentage = hsPercentage;
    }

    public double getHltvRating() {
        return hltvRating;
    }

    public void setHltvRating(double hltvRating) {
        this.hltvRating = hltvRating;
    }
}
