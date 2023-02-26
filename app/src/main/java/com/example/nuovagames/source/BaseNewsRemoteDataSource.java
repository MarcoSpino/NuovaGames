package com.example.nuovagames.source;

/**
 * Base class to get news from a remote source.
 */
public abstract class BaseNewsRemoteDataSource {
    protected GamesCallback newsCallback;

    public void setNewsCallback(GamesCallback newsCallback) {
        this.newsCallback = newsCallback;
    }

    public abstract void getNews(int offset);
}
