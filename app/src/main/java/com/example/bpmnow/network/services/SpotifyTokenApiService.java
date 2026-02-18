package com.example.bpmnow.network.services;

import com.example.bpmnow.models.ResponseRefreshToken;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface SpotifyTokenApiService {
    @FormUrlEncoded
    @POST("api/token")
    Call<ResponseRefreshToken> refreshToken(
            @Field("grant_type") String grantType,
            @Field("refresh_token") String refreshToken,
            @Header("Authorization") String authorization
    );
}
