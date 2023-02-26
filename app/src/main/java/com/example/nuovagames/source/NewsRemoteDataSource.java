package com.example.nuovagames.source;


import static com.example.nuovagames.Constanti.API_KEY_ERROR;
import static com.example.nuovagames.Constanti.RETROFIT_ERROR;
import static com.example.nuovagames.Constanti.TOP_HEADLINES_PAGE_SIZE_VALUE;

import androidx.annotation.NonNull;

import com.example.nuovagames.ServiceLocator;
import com.example.nuovagames.model.GamesApiResponse;
import com.example.nuovagames.service.GamesApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class to get news from a remote source using Retrofit.
 */
public class NewsRemoteDataSource extends BaseNewsRemoteDataSource {

    private final GamesApiService newsApiService;
    private final String apiKey;

    public NewsRemoteDataSource(String apiKey) {
        this.apiKey = apiKey;
        this.newsApiService = ServiceLocator.getInstance().getNewsApiService();
    }

    @Override
    public void getNews(int offset) {
        Call<GamesApiResponse> newsResponseCall = newsApiService.getApiGames(apiKey,
                TOP_HEADLINES_PAGE_SIZE_VALUE, offset);

        newsResponseCall.enqueue(new Callback<GamesApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<GamesApiResponse> call,
                                   @NonNull Response<GamesApiResponse> response) {

                if (response.body() != null && response.isSuccessful() &&
                        !response.body().getError().equals("error")) {
                    newsCallback.onSuccessFromRemote(response.body(), System.currentTimeMillis());
                    long temp = System.currentTimeMillis();

                } else {
                    newsCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
                }
            }

            @Override
            public void onFailure(@NonNull Call<GamesApiResponse> call, @NonNull Throwable t) {
                newsCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }
}