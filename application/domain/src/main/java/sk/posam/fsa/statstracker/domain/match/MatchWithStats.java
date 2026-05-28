package sk.posam.fsa.statstracker.domain.match;

import sk.posam.fsa.statstracker.domain.player.PlayerInMatch;

import java.util.List;

public class MatchWithStats {

    private final Match match;
    private final List<PlayerInMatch> team1Players;
    private final List<PlayerInMatch> team2Players;

    public MatchWithStats(Match match, List<PlayerInMatch> team1Players, List<PlayerInMatch> team2Players) {
        this.match = match;
        this.team1Players = team1Players;
        this.team2Players = team2Players;
    }

    public Match getMatch() { return match; }
    public List<PlayerInMatch> getTeam1Players() { return team1Players; }
    public List<PlayerInMatch> getTeam2Players() { return team2Players; }
}
