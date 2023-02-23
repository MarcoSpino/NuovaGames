package com.example.nuovagames.repository;

import com.example.nuovagames.model.Games;

import java.util.List;


/**
 * Interface to send data from Repositories that implement
 * INewsRepository interface to Activity/Fragment.
 */
public interface ResponseCallback {
    void onSuccess(List<Games> newsList, long lastUpdate);
    void onFailure(String errorMessage);
    void onNewsFavoriteStatusChanged(Games news);
}
