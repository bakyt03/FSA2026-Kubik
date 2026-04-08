package sk.posam.fsa.statstracker.domain;

import java.util.List;

public class Team {
    private String side;
    private List<Player> players;

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
