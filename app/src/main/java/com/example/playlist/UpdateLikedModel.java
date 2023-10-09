package com.example.playlist;

import com.google.gson.annotations.SerializedName;

public class UpdateLikedModel {
    @SerializedName("user_id")
    private String user_id;

    @SerializedName("song_name")
    private String song_name;

    public UpdateLikedModel(String user_id, String song_name) {
        this.user_id = user_id;
        this.song_name = song_name;
    } // Constructor

    public String getUser_id() {
        return user_id;
    }

    public String getSong_name() {
        return song_name;
    }
} // Model
