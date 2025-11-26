package com.example.players;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PlayerApi {

    @GET("players")
    Call<List<Player>> getPlayers();

    @POST("players")
    Call<Player> addPlayer(@Body Player player);

    @PUT("players/{id}")
    Call<Player> updatePlayerName(@Path("id") int id, @Body Player player);

    @DELETE("players/{id}")
    Call<Player> deletePlayer(@Path("id") int id);
}
