package com.example.nuovagames.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.nuovagames.model.Games;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Main access point for the underlying connection to the local database.
 * https://developer.android.com/reference/kotlin/androidx/room/Database
 */
@Database(entities = {Games.class}, version = 1)
public abstract class GamesRoomDatabase extends RoomDatabase {

    public abstract GamesDao newsDao();

    private static volatile GamesRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static GamesRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GamesRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GamesRoomDatabase.class, "db_game").build();
                }
            }
        }
        return INSTANCE;
    }
}
