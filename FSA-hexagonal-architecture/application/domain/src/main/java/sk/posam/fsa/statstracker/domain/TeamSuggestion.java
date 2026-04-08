package sk.posam.fsa.statstracker.domain;

public class TeamSuggestion {
    private Team teamA;
    private Team teamB;
    private double balanceScore;

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

    public double getBalanceScore() {
        return balanceScore;
    }

    public void setBalanceScore(double balanceScore) {
        this.balanceScore = balanceScore;
    }
}
