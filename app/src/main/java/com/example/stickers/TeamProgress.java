package com.example.stickers;

public class TeamProgress {

    private String team;
    private int count;

    public TeamProgress(String team, int count) {
        this.team = team;
        this.count = count;
    }

    public String getTeam() {
        return team;
    }

    public int getCount() {
        return count;
    }
}