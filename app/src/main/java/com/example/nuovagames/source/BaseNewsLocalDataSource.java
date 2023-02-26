package com.example.nuovagames.source;

import com.example.nuovagames.model.Games;
import com.example.nuovagames.model.GamesApiResponse;


/**
 * Base class to get news from a local source.
 */
public abstract class BaseNewsLocalDataSource {

    protected GamesCallback newsCallback;

    public void setNewsCallback(GamesCallback newsCallback) {
        this.newsCallback = newsCallback;
    }

    public abstract void getNews();
    public abstract void getFavoriteNews();
    public abstract void updateNews(Games news);
    public abstract void deleteFavoriteNews();
    public abstract void insertNews(GamesApiResponse newsList);
}
