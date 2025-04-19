package com.example.prosecommandement.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "media_items")
public class MediaItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String type;      // "video" ou "photo"
    private String name;      // Nom descriptif du média
    private String path;      // Chemin vers la ressource
    private String category;  // "target", "entity", "ally", etc.

    // Constructeur par défaut requis par Room
    public MediaItem() {
    }

    // Constructeur pratique pour créer des instances
    public MediaItem(String type, String name, String path, String category) {
        this.type = type;
        this.name = name;
        this.path = path;
        this.category = category;
    }

    // Getters et setters (nécessaires pour Room)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}