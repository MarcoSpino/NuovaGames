package com.example.nuovagames.repository;

import com.example.nuovagames.model.Games;

public interface IGamesRepository {

    void fetchNews(long lastUpdate);

    void updateNews(Games news);

    void getFavoriteNews();

    void deleteFavoriteNews();
}
