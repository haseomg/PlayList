package com.example.playlist;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PlayedModel {
    ArrayList<PlayedModel> playedList;

    @SerializedName("song_name")
    private String song_name;

    @SerializedName("artist_name")
    private String artist_name;

    @SerializedName("song_time")
    private String song_time;

    @SerializedName("album_image")
    private String album_image;

    @SerializedName("views")
    private String views;

    public PlayedModel(String song_name, String artist_name, String song_time, String album_image, String views) {
        this.song_name = song_name;
        this.artist_name = artist_name;
        this.song_time = song_time;
        this.album_image = album_image;
        this.views = views;
    } // Constructor END

    public PlayedModel() {

    } // Default Constructor

    public String getSong_name() {
        return song_name;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public String getSong_time() {
        return song_time;
    }

    public String getViews() {
        return views;
    }

    public String getAlbum_image() {
        return album_image;
    }

    public void setSong_name() {
        this.song_name = song_name;
    } // setName

    public void setArtist_name() {
        this.artist_name = artist_name;
    } // setArtist

    public void setSong_time() {
        this.song_time = song_time;
    } // setTime

    public void setViews() {
        this.views = views;
    } // setViews

    public void setAlbum_image() {
        this.album_image = album_image;
    } // setImage

    public void setPlayedList(ArrayList<PlayedModel> playedList) {
        this.playedList = playedList;
    } // setPlayedList
} // PlayedModel END
