package sk.posam.fsa.statstracker.domain.team;

public class TeamSuggestion {
    private Team teamA;
    private Team teamB;
    private double adrDifference;
    private double teamAAdrAvg;
    private double teamBAdrAvg;

    public Team getTeamA() {
        return teamA;
    }

    public void setTeamA(Team teamA) {
        this.teamA = teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public void setTeamB(Team teamB) {
        this.teamB = teamB;
    }

    public double getAdrDifference() {
        return adrDifference;
    }

    public void setAdrDifference(double adrDifference) {
        this.adrDifference = adrDifference;
    }

    public double getTeamAAdrAvg() {
        return teamAAdrAvg;
    }

    public void setTeamAAdrAvg(double teamAAdrAvg) {
        this.teamAAdrAvg = teamAAdrAvg;
    }

    public double getTeamBAdrAvg() {
        return teamBAdrAvg;
    }

    public void setTeamBAdrAvg(double teamBAdrAvg) {
        this.teamBAdrAvg = teamBAdrAvg;
    }
}
