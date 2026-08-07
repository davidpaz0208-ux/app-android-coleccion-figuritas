package com.example.stickers;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface StickerApi {

    @GET("stickers")
    Call<List<Sticker>> getStickers();

    @GET("stickers/{id}")
    Call<Sticker> getStickerById(@Path("id") int id);

    @POST("stickers")
    Call<Sticker> createSticker(@Body Sticker sticker);

    @PUT("stickers/{id}")
    Call<Sticker> updateSticker(@Path("id") int id, @Body Sticker sticker);

    @DELETE("stickers/{id}")
    Call<Void> deleteSticker(@Path("id") int id);
}
