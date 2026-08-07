package com.example.stickers;

public class TeamStickerItem {

    private String name;
    private boolean obtained;

    public TeamStickerItem(
            String name,
            boolean obtained
    ) {
        this.name = name;
        this.obtained = obtained;
    }

    public String getName() {
        return name;
    }

    public boolean isObtained() {
        return obtained;
    }
}