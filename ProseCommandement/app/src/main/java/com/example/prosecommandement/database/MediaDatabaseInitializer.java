package com.example.prosecommandement.database;

import android.content.Context;
import android.content.res.Resources;

import com.example.prosecommandement.R;

import java.util.ArrayList;
import java.util.List;

public class MediaDatabaseInitializer {

    // Méthode pour préparer les données initiales
    public static List<MediaItem> getInitialMediaData(Context context) {
        List<MediaItem> mediaItems = new ArrayList<>();
        Resources resources = context.getResources();

        // Format pour les ressources : "android.resource://[package]/[resource_id]"
        String packageName = context.getPackageName();

        // Ajouter les vidéos
        // Exemple: Le fichier vidéo se trouve dans res/raw/video_thales.mp4
        mediaItems.add(new MediaItem(
                "video",
                "combat",
                "android.resource://" + packageName + "/" + R.raw.video_thales,
                "combat"));

        // Si vous avez d'autres vidéos:
        // mediaItems.add(new MediaItem("video", "drone", "android.resource://" + packageName + "/" + R.raw.video_drone, "surveillance"));

        // Ajouter les photos pour les popups
        // Exemple: Les images se trouvent dans res/drawable/
        mediaItems.add(new MediaItem(
                "photo",
                "cible1",
                "android.resource://" + packageName + "/" + R.drawable.target_photo,
                "target"));

        // Ajouter d'autres médias au besoin...

        return mediaItems;
    }

    // Méthode pour initialiser la base de données
    public static void initializeDatabase(final Context context) {
        MediaRepository repository = new MediaRepository(
                (android.app.Application) context.getApplicationContext());

        // Vérifier si la BD contient déjà des données
        repository.getAllMedia().thenAccept(existingMedia -> {
            if (existingMedia == null || existingMedia.isEmpty()) {
                // Si vide, ajouter les données initiales
                repository.insertAll(getInitialMediaData(context));
            }
        });
    }
}