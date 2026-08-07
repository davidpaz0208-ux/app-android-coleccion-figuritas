package com.example.stickers;

public class AlbumItem {

    private Sticker sticker;
    private boolean owned;

    public AlbumItem(Sticker sticker, boolean owned) {
        this.sticker = sticker;
        this.owned = owned;
    }

    public Sticker getSticker() {
        return sticker;
    }

    public boolean isOwned() {
        return owned;
    }
}