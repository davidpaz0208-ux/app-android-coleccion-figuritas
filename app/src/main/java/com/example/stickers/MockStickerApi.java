package com.example.stickers;

import java.util.ArrayList;
import java.util.List;

public class MockStickerApi {

    private static List<Sticker> stickers;

    public MockStickerApi() {

        if (stickers == null) {

            stickers = new ArrayList<>();

            stickers.add(new Sticker(
                    1,
                    10,
                    "Messi",
                    "Argentina",
                    "Legend",
                    false
            ));

            stickers.add(new Sticker(
                    2,
                    7,
                    "Cristiano Ronaldo",
                    "Portugal",
                    "Rare",
                    false
            ));

            stickers.add(new Sticker(
                    3,
                    9,
                    "Haaland",
                    "Noruega",
                    "Epic",
                    false
            ));

            stickers.add(new Sticker(
                    4,
                    11,
                    "Neymar",
                    "Brasil",
                    "Rare",
                    false
            ));

            stickers.add(new Sticker(
                    5,
                    8,
                    "De Bruyne",
                    "Bélgica",
                    "Epic",
                    false
            ));

            stickers.add(new Sticker(
                    6,
                    21,
                    "Dybala",
                    "Argentina",
                    "Common",
                    false
            ));

            stickers.add(new Sticker(
                    7,
                    4,
                    "Van Dijk",
                    "Países Bajos",
                    "Rare",
                    false
            ));
        }
    }

    public List<Sticker> getStickers() {
        return new ArrayList<>(stickers);
    }

    public static void updateSticker(Sticker updatedSticker) {

        for (int i = 0; i < stickers.size(); i++) {

            if (stickers.get(i).getId() == updatedSticker.getId()) {

                stickers.set(i, updatedSticker);
                break;
            }
        }
    }
}