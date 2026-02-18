package com.example.bpmnow.network.retorfit;

import com.example.bpmnow.network.services.SpotifyTokenApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SpotifyRetrofitClientToken {
    private static final String BASE_URL = "https://accounts.spotify.com/";
    private static SpotifyRetrofitClientToken instance;
    private final Retrofit retrofit;

    private SpotifyRetrofitClientToken() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized SpotifyRetrofitClientToken getInstance() {
        if (instance == null) {
            instance = new SpotifyRetrofitClientToken();
        }
        return instance;
    }

    public SpotifyTokenApiService getApiService() {
        return retrofit.create(SpotifyTokenApiService.class);
    }
}
