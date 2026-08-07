package com.example.stickers;

import java.util.ArrayList;
import java.util.List;

public class StickerRepository {

    private static final List<Sticker> stickers = new ArrayList<>();
    private static int nextId = 1;

    static {
        stickers.add(new Sticker(nextId++, 10, "Messi", "Argentina", "Legend", false));
        stickers.add(new Sticker(nextId++, 7, "Cristiano Ronaldo", "Portugal", "Rare", false));
        stickers.add(new Sticker(nextId++, 9, "Haaland", "Noruega", "Epic", false));
    }

    public static List<Sticker> getStickers() {
        return stickers;
    }

    public static void addSticker(Sticker sticker) {
        sticker.setId(nextId++);
        stickers.add(sticker);
    }
}