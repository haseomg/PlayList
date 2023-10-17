package com.example.playlist;

import com.google.gson.annotations.SerializedName;

public class LikedModel {
    @SerializedName("user_id")
    private String user;

    @SerializedName("selected_song")
    private String selected_song;

    public LikedModel(String user) {
        this.user = user;
    } // Constructor

    public String getUser() {
        return user;
    } // getUser\

    public String getSelected_song() {
        return selected_song;
    }
} // CLASS
