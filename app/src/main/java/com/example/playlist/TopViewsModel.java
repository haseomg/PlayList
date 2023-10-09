package com.example.playlist;

import com.google.gson.annotations.SerializedName;

public class TopViewsModel {
    @SerializedName("name")
    private String name;

    @SerializedName("artist")
    private String artist;

    @SerializedName("views")
    private String views;

    public TopViewsModel(String name, String artist, String views) {
        this.name = name;
        this.artist = artist;
        this.views = views;
    } // Constructor

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getViews() {
        return views;
    }

} // Class
