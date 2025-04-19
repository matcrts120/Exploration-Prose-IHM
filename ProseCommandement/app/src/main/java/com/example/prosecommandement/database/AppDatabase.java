package com.example.prosecommandement.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {MediaItem.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // DAO abstrait que Room implémentera
    public abstract MediaDao mediaDao();

    // Instance singleton pour éviter d'ouvrir trop de connexions
    private static volatile AppDatabase INSTANCE;

    // Pool de threads pour les opérations de base de données
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Méthode pour obtenir l'instance de la base de données (pattern Singleton)
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_database")
                            .fallbackToDestructiveMigration() // Réinitialise la BD si le schéma change
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}