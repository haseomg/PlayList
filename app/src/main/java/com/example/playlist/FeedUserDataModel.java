package com.example.playlist;

import com.google.gson.annotations.SerializedName;

public class FeedUserDataModel {

    @SerializedName("user_name")
    private String user_name;

    @SerializedName("profile_music")
    private String profile_music;

    @SerializedName("profile_image")
    private String profile_image;

    @SerializedName("like_genre_first")
    private String like_genre_first;

    @SerializedName("like_genre_second")
    private String like_genre_second;

    @SerializedName("like_genre_third")
    private String like_genre_third;

    public String getUser_name() { return user_name; }
    public String getProfile_music() { return profile_music; }
    public String getProfile_image() { return profile_image; }
    public String getLike_genre_first() { return like_genre_first; }
    public String getLike_genre_second() { return like_genre_second; }
    public String getLike_genre_third() { return like_genre_third; }

} // FeedUserDataModel
