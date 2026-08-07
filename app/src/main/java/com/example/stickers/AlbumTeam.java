package com.example.stickers;

public class AlbumTeam {

    private String team;
    private int completed;
    private int total;

    public AlbumTeam(
            String team,
            int completed,
            int total
    ) {
        this.team = team;
        this.completed = completed;
        this.total = total;
    }

    public String getTeam() {
        return team;
    }

    public int getCompleted() {
        return completed;
    }

    public int getTotal() {
        return total;
    }
}