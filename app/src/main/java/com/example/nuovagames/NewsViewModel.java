package com.example.nuovagames;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.nuovagames.model.Games;
import com.example.nuovagames.model.Result;
import com.example.nuovagames.repository.IGamesRepository;


/**
 * ViewModel to manage the list of News and the list of favorite News.
 */
public class NewsViewModel extends ViewModel {

    private static final String TAG = NewsViewModel.class.getSimpleName();

    private final IGamesRepository newsRepositoryWithLiveData;
    private int page;
    private int currentResults;
    private int totalResults;
    private boolean isLoading;
    private boolean firstLoading;
    private MutableLiveData<Result> newsListLiveData;
    private MutableLiveData<Result> favoriteNewsListLiveData;

    public NewsViewModel(IGamesRepository iNewsRepositoryWithLiveData) {
        this.newsRepositoryWithLiveData = iNewsRepositoryWithLiveData;
        this.page = 0;
        this.totalResults = 0;
        this.firstLoading = true;
    }

    /**
     * Returns the LiveData object associated with the
     * news list to the Fragment/Activity.
     * @return The LiveData object associated with the news list.
     */
    public MutableLiveData<Result> getNews(long lastUpdate) {
        if (newsListLiveData == null) {
            fetchNews(lastUpdate);
        }
        return newsListLiveData;
    }

    /**
     * Returns the LiveData object associated with the
     * list of favorite news to the Fragment/Activity.
     * @return The LiveData object associated with the list of favorite news.
     */
    public MutableLiveData<Result> getFavoriteNewsLiveData() {
        if (favoriteNewsListLiveData == null) {
            getFavoriteNews();
        }
        return favoriteNewsListLiveData;
    }

    /**
     * Updates the news status.
     * @param news The news to be updated.
     */
    public void updateNews(Games news) {
        newsRepositoryWithLiveData.updateNews(news);
    }

    public void fetchNews() {
        newsRepositoryWithLiveData.fetchNews(page);
    }

    /**
     * It uses the Repository to download the news list
     * and to associate it with the LiveData object.
     */
    private void fetchNews(long lastUpdate) {
        newsListLiveData = newsRepositoryWithLiveData.fetchNews(page, lastUpdate);
    }

    /**
     * It uses the Repository to get the list of favorite news
     * and to associate it with the LiveData object.
     */
    private void getFavoriteNews() {
        favoriteNewsListLiveData = newsRepositoryWithLiveData.getFavoriteNews();
    }

    /**
     * Removes the news from the list of favorite news.
     * @param news The news to be removed from the list of favorite news.
     */
    public void removeFromFavorite(Games news) {
        newsRepositoryWithLiveData.updateNews(news);
    }

    /**
     * Clears the list of favorite news.
     */
    public void deleteAllFavoriteNews() {
        newsRepositoryWithLiveData.deleteFavoriteNews();
    }

    public int getOffset() {
        return page;
    }

    public void setOffset(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getCurrentResults() {
        return currentResults;
    }

    public void setCurrentResults(int currentResults) {
        this.currentResults = currentResults;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isFirstLoading() {
        return firstLoading;
    }

    public void setFirstLoading(boolean firstLoading) {
        this.firstLoading = firstLoading;
    }

    public MutableLiveData<Result> getNewsResponseLiveData() {
        return newsListLiveData;
    }
}
