package com.example.nuovagames.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.nuovagames.model.Games;

import java.util.List;


/**
 * Data Access Object (DAO) that provides methods that can be used to query,
 * update, insert, and delete data in the database.
 * https://developer.android.com/training/data-storage/room/accessing-data
 */
@Dao
public interface GamesDao {
    @Query("SELECT * FROM games ")
    List<Games> getAll();

    @Query("SELECT * FROM games WHERE id = :id")
    Games getNews(long id);

    @Query("SELECT * FROM games WHERE is_favorite = 1")
    List<Games> getFavoriteNews();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertNewsList(List<Games> newsList);

    @Insert
    void insertAll(Games... games);

    @Delete
    void delete(Games games);

    @Query("DELETE FROM games")
    void deleteAll();

    @Query("DELETE FROM games WHERE is_favorite = 0")
    void deleteNotFavoriteNews();

    @Delete
    void deleteAllWithoutQuery(Games... games);

    @Update
    void updateSingleFavoriteNews(Games games);

    @Update
    void updateListFavoriteNews(List<Games> games);
}
