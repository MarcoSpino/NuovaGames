package com.example.nuovagames.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.nuovagames.R;
import com.example.nuovagames.ResponseCallback;
import com.example.nuovagames.ServiceLocator;
import com.example.nuovagames.database.GamesDao;
import com.example.nuovagames.database.GamesRoomDatabase;
import com.example.nuovagames.model.Games;
import com.example.nuovagames.model.GamesApiResponse;
import com.example.nuovagames.service.GamesApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GamesRepository implements IGamesRepository{

    private static final String TAG = GamesRepository.class.getSimpleName();

    private final Application application;
    private final GamesApiService newsApiService;
    private final GamesDao newsDao;
    private final ResponseCallback responseCallback;

    public GamesRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.newsApiService = ServiceLocator.getInstance().getNewsApiService();
        GamesRoomDatabase newsRoomDatabase = ServiceLocator.getInstance().getNewsDao(application);
        this.newsDao = newsRoomDatabase.newsDao();
        this.responseCallback = responseCallback;
    }

    @Override
    public void fetchNews(long lastUpdate) {
        long currentTime = System.currentTimeMillis();

        // It gets the news from the Web Service if the last download
        // of the news has been performed more than FRESH_TIMEOUT value ago
        if (currentTime - lastUpdate > (100*60*60)) {
            Call<GamesApiResponse> newsResponseCall = newsApiService.getApiGames(application.getString(R.string.news_api_key));

            newsResponseCall.enqueue(new Callback<GamesApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<GamesApiResponse> call,
                                       @NonNull Response<GamesApiResponse> response) {

                    if (response.body() != null && response.isSuccessful() &&
                            !response.body().getError().equals("error")) {
                        List<Games> newsList = response.body().getResults();
                        saveDataInDatabase(newsList);
                    } else {
                        responseCallback.onFailure(application.getString(R.string.error_retrieving_news));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<GamesApiResponse> call, @NonNull Throwable t) {
                    responseCallback.onFailure(t.getMessage());
                }
            });
        } else {
            Log.d(TAG, application.getString(R.string.data_read_from_local_database));
            readDataFromDatabase(lastUpdate);
        }
    }

    @Override
    public void updateNews(Games news) {
        GamesRoomDatabase.databaseWriteExecutor.execute(() -> {
            newsDao.updateSingleFavoriteNews(news);
            responseCallback.onNewsFavoriteStatusChanged(news);
        });

    }

    @Override
    public void getFavoriteNews() {
        GamesRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(newsDao.getFavoriteNews(), System.currentTimeMillis());
        });
    }

    @Override
    public void deleteFavoriteNews() {
        GamesRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Games> favoriteNews = newsDao.getFavoriteNews();
            for (Games games : favoriteNews) {
                games.setFavorite(false);
            }
            newsDao.updateListFavoriteNews(favoriteNews);
            responseCallback.onSuccess(newsDao.getFavoriteNews(), System.currentTimeMillis());
        });
    }

    private void saveDataInDatabase(List<Games> newsList) {
        GamesRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Games> allNews = newsDao.getAll();

            // Checks if the news just downloaded has already been downloaded earlier
            // in order to preserve the news status (marked as favorite or not)
            for (Games news : allNews) {
                // This check works because News and NewsSource classes have their own
                // implementation of equals(Object) and hashCode() methods
                if (newsList.contains(news)) {
                    // The primary key and the favorite status is contained only in the News objects
                    // retrieved from the database, and not in the News objects downloaded from the
                    // Web Service. If the same news was already downloaded earlier, the following
                    // line of code replaces the the News object in newsList with the corresponding
                    // News object saved in the database, so that it has the primary key and the
                    // favorite status.
                    newsList.set(newsList.indexOf(news), news);
                }
            }

            // Writes the news in the database and gets the associated primary keys
            List<Long> insertedNewsIds = newsDao.insertNewsList(newsList);
            for (int i = 0; i < newsList.size(); i++) {
                // Adds the primary key to the corresponding object News just downloaded so that
                // if the user marks the news as favorite (and vice-versa), we can use its id
                // to know which news in the database must be marked as favorite/not favorite
                newsList.get(i).setId(insertedNewsIds.get(i));
            }

            responseCallback.onSuccess(newsList, System.currentTimeMillis());
        });
    }

    /**
     * Gets the news from the local database.
     * The method is executed in a Runnable because the database access
     * cannot been executed in the main thread.
     */
    private void readDataFromDatabase(long lastUpdate) {
        GamesRoomDatabase.databaseWriteExecutor.execute(() -> {
            responseCallback.onSuccess(newsDao.getAll(), lastUpdate);
        });
    }
}
