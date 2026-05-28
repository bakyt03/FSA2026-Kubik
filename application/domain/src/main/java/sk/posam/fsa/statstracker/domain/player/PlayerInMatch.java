package sk.posam.fsa.statstracker.domain.player;

public class PlayerInMatch {

    private final long playerId;
    private final String playerNickname;
    private final int kills;
    private final int deaths;
    private final int damage;
    private final double adr;

    public PlayerInMatch(long playerId, String playerNickname, int kills, int deaths, int damage, double adr) {
        this.playerId = playerId;
        this.playerNickname = playerNickname;
        this.kills = kills;
        this.deaths = deaths;
        this.damage = damage;
        this.adr = adr;
    }

    public long getPlayerId() { return playerId; }
    public String getPlayerNickname() { return playerNickname; }
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getDamage() { return damage; }
    public double getAdr() { return adr; }
}
