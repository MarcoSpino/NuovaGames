package com.example.nuovagames.repository;

import static com.example.nuovagames.Constanti.FRESH_TIMEOUT;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.nuovagames.GamesRoomDatabase;
import com.example.nuovagames.R;
import com.example.nuovagames.ResponseCallback;
import com.example.nuovagames.ServiceLocator;
import com.example.nuovagames.database.GamesDao;
import com.example.nuovagames.model.Games;
import com.example.nuovagames.model.GamesApiResponse;
import com.example.nuovagames.model.Result;
import com.example.nuovagames.service.GamesApiService;
import com.example.nuovagames.source.BaseNewsLocalDataSource;
import com.example.nuovagames.source.BaseNewsRemoteDataSource;
import com.example.nuovagames.source.GamesCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GamesRepository implements IGamesRepository, GamesCallback {

    private static final String TAG = GamesRepository.class.getSimpleName();

    private final MutableLiveData<Result> allNewsMutableLiveData;
    private final MutableLiveData<Result> favoriteNewsMutableLiveData;
    private final BaseNewsRemoteDataSource newsRemoteDataSource;
    private final BaseNewsLocalDataSource newsLocalDataSource;

    public GamesRepository(BaseNewsRemoteDataSource newsRemoteDataSource,
                                      BaseNewsLocalDataSource newsLocalDataSource) {

        allNewsMutableLiveData = new MutableLiveData<>();
        favoriteNewsMutableLiveData = new MutableLiveData<>();
        this.newsRemoteDataSource = newsRemoteDataSource;
        this.newsLocalDataSource = newsLocalDataSource;
        this.newsRemoteDataSource.setNewsCallback(this);
        this.newsLocalDataSource.setNewsCallback(this);
    }

    @Override
    public MutableLiveData<Result> fetchNews(int offset, long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate < FRESH_TIMEOUT) {
            newsRemoteDataSource.getNews(offset);
        } else {
            newsLocalDataSource.getNews();
        }
        return allNewsMutableLiveData;
    }

    public void fetchNews(int offset) {
        newsRemoteDataSource.getNews(offset);
    }

    @Override
    public MutableLiveData<Result> getFavoriteNews() {
        newsLocalDataSource.getFavoriteNews();
        return favoriteNewsMutableLiveData;
    }

    @Override
    public void updateNews(Games news) {
        newsLocalDataSource.updateNews(news);
    }

    @Override
    public void deleteFavoriteNews() {
        newsLocalDataSource.deleteFavoriteNews();
    }

    @Override
    public void onSuccessFromRemote(GamesApiResponse newsApiResponse, long lastUpdate) {
        newsLocalDataSource.insertNews(newsApiResponse);
        Log.e(TAG, String.valueOf(lastUpdate));
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        allNewsMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(GamesApiResponse newsApiResponse) {
        if (allNewsMutableLiveData.getValue() != null && allNewsMutableLiveData.getValue().isSuccess()) {
            List<Games> newsList = ((Result.Success)allNewsMutableLiveData.getValue()).getData().getResults();
            newsList.addAll(newsApiResponse.getResults());
            newsApiResponse.setResults(newsList);
            Result.Success result = new Result.Success(newsApiResponse);
            allNewsMutableLiveData.postValue(result);
        } else {
            Result.Success result = new Result.Success(newsApiResponse);
            allNewsMutableLiveData.postValue(result);
        }
    }

    @Override
    public void onFailureFromLocal(Exception exception) {
        Result.Error resultError = new Result.Error(exception.getMessage());
        allNewsMutableLiveData.postValue(resultError);
        favoriteNewsMutableLiveData.postValue(resultError);
    }

    @Override
    public void onNewsFavoriteStatusChanged(Games news, List<Games> favoriteNews) {
        Result allNewsResult = allNewsMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<Games> oldAllNews = ((Result.Success)allNewsResult).getData().getResults();
            if (oldAllNews.contains(news)) {
                oldAllNews.set(oldAllNews.indexOf(news), news);
                allNewsMutableLiveData.postValue(allNewsResult);
            }
        }
        favoriteNewsMutableLiveData.postValue(new Result.Success(new GamesApiResponse(favoriteNews)));
    }

    @Override
    public void onNewsFavoriteStatusChanged(List<Games> news) {
        favoriteNewsMutableLiveData.postValue(new Result.Success(new GamesApiResponse(news)));
    }

    @Override
    public void onDeleteFavoriteNewsSuccess(List<Games> favoriteNews) {
        Result allNewsResult = allNewsMutableLiveData.getValue();

        if (allNewsResult != null && allNewsResult.isSuccess()) {
            List<Games> oldAllNews = ((Result.Success)allNewsResult).getData().getResults();
            for (Games news : favoriteNews) {
                if (oldAllNews.contains(news)) {
                    oldAllNews.set(oldAllNews.indexOf(news), news);
                }
            }
            allNewsMutableLiveData.postValue(allNewsResult);
        }

        if (favoriteNewsMutableLiveData.getValue() != null &&
                favoriteNewsMutableLiveData.getValue().isSuccess()) {
            favoriteNews.clear();
            Result.Success result = new Result.Success(new GamesApiResponse(favoriteNews));
            favoriteNewsMutableLiveData.postValue(result);
        }
    }
}
