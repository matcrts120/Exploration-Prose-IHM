package com.example.prosecommandement.database;

import android.app.Application;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaRepository {

    private final MediaDao mediaDao;
    private final ExecutorService executorService;

    // Constructeur recevant l'Application
    public MediaRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mediaDao = db.mediaDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    // Obtenir tous les médias (opération asynchrone)
    public CompletableFuture<List<MediaItem>> getAllMedia() {
        return CompletableFuture.supplyAsync(() -> mediaDao.getAll(), executorService);
    }

    // Obtenir un média par ID
    public CompletableFuture<MediaItem> getMediaById(int id) {
        return CompletableFuture.supplyAsync(() -> mediaDao.getById(id), executorService);
    }

    // Obtenir tous les médias d'un type spécifique
    public CompletableFuture<List<MediaItem>> getAllByType(String type) {
        return CompletableFuture.supplyAsync(() -> mediaDao.getAllByType(type), executorService);
    }

    // Obtenir tous les médias d'une catégorie spécifique
    public CompletableFuture<List<MediaItem>> getAllByCategory(String category) {
        return CompletableFuture.supplyAsync(() -> mediaDao.getAllByCategory(category), executorService);
    }

    // Obtenir les médias filtrés par type et catégorie
    public CompletableFuture<List<MediaItem>> getFilteredMedia(String type, String category) {
        return CompletableFuture.supplyAsync(() -> mediaDao.getFilteredMedia(type, category), executorService);
    }

    // Obtenir un média aléatoire d'une catégorie
    public CompletableFuture<MediaItem> getRandomByCategory(String category) {
        return CompletableFuture.supplyAsync(() -> mediaDao.getRandomByCategory(category), executorService);
    }

    // Insérer un nouveau média
    public void insert(MediaItem media) {
        executorService.execute(() -> mediaDao.insert(media));
    }

    // Insérer plusieurs médias
    public void insertAll(List<MediaItem> mediaList) {
        executorService.execute(() -> {
            MediaItem[] mediaArray = mediaList.toArray(new MediaItem[0]);
            mediaDao.insert(mediaArray);
        });
    }

    // Mettre à jour un média
    public void update(MediaItem media) {
        executorService.execute(() -> mediaDao.update(media));
    }

    // Supprimer un média
    public void delete(MediaItem media) {
        executorService.execute(() -> mediaDao.delete(media));
    }

    // Supprimer tous les médias
    public void deleteAll() {
        executorService.execute(mediaDao::deleteAll);
    }
}