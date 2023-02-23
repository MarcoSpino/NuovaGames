package com.example.nuovagames.source;

import static com.example.nuovagames.Constanti.UNEXPECTED_ERROR;

import androidx.room.RoomDatabase;

import com.example.nuovagames.GamesRoomDatabase;
import com.example.nuovagames.database.GamesDao;

import com.example.nuovagames.model.Games;

import java.util.List;


/**
 * Class to get news from local database using Room.
 */
public class NewsLocalDataSource extends BaseNewsLocalDataSource {

    private final GamesDao newsDao;

    public NewsLocalDataSource(GamesRoomDatabase newsRoomDatabase) {
        this.newsDao = newsRoomDatabase.newsDao();

    }

    /**
     * Gets the news from the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     */
    @Override
    public void getNews() {
        GamesRoomDatabase.databaseWriteExecutor.execute(() -> {
            newsCallback.onSuccessFromLocal(newsDao.getAll());
        });
    }

    @Override
    public void getFavoriteNews() {
        GamesRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Games> favoriteNews = newsDao.getFavoriteNews();
            newsCallback.onNewsFavoriteStatusChanged(favoriteNews);
        });
    }

    @Override
    public void updateNews(Games news) {
        GamesRoomDatabase.databaseWriteExecutor.execute(() -> {
            int rowUpdatedCounter = newsDao.updateSingleFavoriteNews(news);

            // It means that the update succeeded because only one row had to be updated
            if (rowUpdatedCounter == 1) {
                Games updatedNews = newsDao.getNews(news.getId());
                newsCallback.onNewsFavoriteStatusChanged(updatedNews, newsDao.getFavoriteNews());
            } else {
                newsCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    @Override
    public void deleteFavoriteNews() {
        GamesRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Games> favoriteNews = newsDao.getFavoriteNews();
            for (Games news : favoriteNews) {
                news.setFavorite(false);
            }
            int updatedRowsNumber = newsDao.updateListFavoriteNews(favoriteNews);

            // It means that the update succeeded because the number of updated rows is
            // equal to the number of the original favorite news
            if (updatedRowsNumber == favoriteNews.size()) {
                newsCallback.onDeleteFavoriteNewsSuccess(favoriteNews);
            } else {
                newsCallback.onFailureFromLocal(new Exception(UNEXPECTED_ERROR));
            }
        });
    }

    /**
     * Saves the news in the local database.
     * The method is executed with an ExecutorService defined in NewsRoomDatabase class
     * because the database access cannot been executed in the main thread.
     * @param newsList the list of news to be written in the local database.
     */
    @Override
    public void insertNews(List<Games> newsList) {
        GamesRoomDatabase.databaseWriteExecutor.execute(() -> {
            // Reads the news from the database
            List<Games> allNews = newsDao.getAll();

            if (newsList != null) {

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
                newsCallback.onSuccessFromLocal(newsList);
            }
        });
    }
}
