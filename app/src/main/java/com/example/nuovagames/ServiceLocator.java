package com.example.nuovagames;

import android.app.Application;

import com.example.nuovagames.repository.GamesRepository;
import com.example.nuovagames.repository.IGamesRepository;
import com.example.nuovagames.service.GamesApiService;
import com.example.nuovagames.source.BaseNewsLocalDataSource;
import com.example.nuovagames.source.BaseNewsRemoteDataSource;
import com.example.nuovagames.source.NewsLocalDataSource;
import com.example.nuovagames.source.NewsRemoteDataSource;
import com.example.nuovagames.GamesRoomDatabase;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Registry to provide the dependencies for the classes
 * used in the application.
 */
public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {
    }

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized (ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * It creates an instance of NewsApiService using Retrofit.
     *
     * @return an instance of NewsApiService.
     */
    public GamesApiService getNewsApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constanti.Base_url).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(GamesApiService.class);
    }

    public GamesRoomDatabase getNewsDao(Application application) {
        return GamesRoomDatabase.getDatabase(application);
    }

    public IGamesRepository getNewsRepository(Application application, boolean debugMode) {
        BaseNewsRemoteDataSource newsRemoteDataSource;
        BaseNewsLocalDataSource newsLocalDataSource;

        newsRemoteDataSource =
                new NewsRemoteDataSource("4c7dbf77229b59e4317ed026ad2d4db550540fbf");
        newsLocalDataSource = new NewsLocalDataSource(getNewsDao(application));

        return new GamesRepository(newsRemoteDataSource, newsLocalDataSource);
    }
}
