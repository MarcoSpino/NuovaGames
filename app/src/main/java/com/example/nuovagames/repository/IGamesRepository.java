package com.example.nuovagames.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.nuovagames.model.Games;
import com.example.nuovagames.model.Result;

public interface IGamesRepository {

    MutableLiveData<Result> fetchNews(int offset, long lastUpdate);

    void fetchNews(int offset);

    MutableLiveData<Result> getFavoriteNews();

    void updateNews(Games news);

    void deleteFavoriteNews();
}
