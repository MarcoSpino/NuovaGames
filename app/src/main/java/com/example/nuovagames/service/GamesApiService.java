package com.example.nuovagames.service;

import com.example.nuovagames.model.GamesApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Interface for Service to get news from the Web Service.
 */
public interface GamesApiService {

    @GET("api/games/?format=json")
    Call<GamesApiResponse> getApiGames(
            @Query("api_key") String key,
            @Query("limit") int limit,
            @Query("offset") int offset);
}
