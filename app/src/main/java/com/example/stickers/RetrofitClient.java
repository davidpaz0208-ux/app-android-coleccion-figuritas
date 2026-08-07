package com.example.stickers;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getInstance(Context context) {

        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new MockInterceptor(context))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://localhost/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
