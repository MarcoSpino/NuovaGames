package com.example.nuovagames.source;

import com.example.nuovagames.model.Games;
import com.example.nuovagames.model.GamesApiResponse;

import java.util.List;

/**
 * Interface to send data from DataSource to Repositories
 * that implement INewsRepositoryWithLiveData interface.
 */
public interface GamesCallback {
    void onSuccessFromRemote(GamesApiResponse newsApiResponse, long lastUpdate);
    void onFailureFromRemote(Exception exception);
    void onSuccessFromLocal(List<Games> newsList);
    void onFailureFromLocal(Exception exception);
    void onNewsFavoriteStatusChanged(Games news, List<Games> favoriteNews);
    void onNewsFavoriteStatusChanged(List<Games> news);
    void onDeleteFavoriteNewsSuccess(List<Games> favoriteNews);
}
