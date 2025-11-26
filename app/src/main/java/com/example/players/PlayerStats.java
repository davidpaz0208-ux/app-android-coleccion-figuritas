package com.example.players;

public class PlayerStats {
    private int goals;
    private int assists;
    private int matchesPlayed;
    private int yellowCards;
    private int redCards;

    public PlayerStats(int goals, int assists, int matchesPlayed, int yellowCards, int redCards) {
        this.goals = goals;
        this.assists = assists;
        this.matchesPlayed = matchesPlayed;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
    }

    public int getGoals() {
        return goals;
    }

    public int getAssists() {
        return assists;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }
}
