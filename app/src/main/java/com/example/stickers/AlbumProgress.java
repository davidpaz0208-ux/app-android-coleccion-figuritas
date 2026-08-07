package com.example.stickers;

public class AlbumProgress {

    private String team;
    private int owned;
    private int total;

    public AlbumProgress(
            String team,
            int owned,
            int total
    ) {
        this.team = team;
        this.owned = owned;
        this.total = total;
    }

    public String getTeam() {
        return team;
    }

    public int getOwned() {
        return owned;
    }

    public int getTotal() {
        return total;
    }
}