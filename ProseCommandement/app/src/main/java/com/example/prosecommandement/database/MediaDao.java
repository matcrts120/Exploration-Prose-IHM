package com.example.prosecommandement.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MediaDao {

    // Requête pour obtenir tous les médias
    @Query("SELECT * FROM media_items")
    List<MediaItem> getAll();

    // Requête pour obtenir un média par son ID
    @Query("SELECT * FROM media_items WHERE id = :mediaId")
    MediaItem getById(int mediaId);

    // Requête pour obtenir tous les médias d'un type spécifique (photo/vidéo)
    @Query("SELECT * FROM media_items WHERE type = :type")
    List<MediaItem> getAllByType(String type);

    // Requête pour obtenir tous les médias d'une catégorie spécifique
    @Query("SELECT * FROM media_items WHERE category = :category")
    List<MediaItem> getAllByCategory(String category);

    // Requête pour obtenir les médias filtrés par type et catégorie
    @Query("SELECT * FROM media_items WHERE type = :type AND category = :category")
    List<MediaItem> getFilteredMedia(String type, String category);

    // Requête pour obtenir un média aléatoire d'une catégorie
    @Query("SELECT * FROM media_items WHERE category = :category ORDER BY RANDOM() LIMIT 1")
    MediaItem getRandomByCategory(String category);

    // Insérer un ou plusieurs médias
    @Insert
    void insert(MediaItem... media);

    // Mettre à jour un média
    @Update
    void update(MediaItem media);

    // Supprimer un média
    @Delete
    void delete(MediaItem media);

    // Supprimer tous les médias
    @Query("DELETE FROM media_items")
    void deleteAll();
}