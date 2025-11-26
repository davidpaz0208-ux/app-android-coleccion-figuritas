package com.example.players;

public class Player {

    private int id;
    private String name;
    private String position;
    private int goals;
    private int matchesPlayed;

    // Constructor completo
    public Player(int id, String name, String position, int goals, int matchesPlayed) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.goals = goals;
        this.matchesPlayed = matchesPlayed;
    }

    // Constructor simplificado (opcional, si lo usás)
    public Player(int id, String name, String position) {
        this(id, name, position, 0, 0);
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    @Override
    public String toString() {
        return name + " (" + position + ")";
    }
}

