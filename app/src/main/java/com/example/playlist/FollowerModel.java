package com.example.playlist;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FollowerModel {
    ArrayList<FollowerModel> followerList;

    @SerializedName("follower_profile_image")
    private String profile_image;

    @SerializedName("me")
    private String user_name;

    public String getProfile_image() {
        return profile_image;
    } // getProfile_image

    public String getUser_name() {
        return user_name;
    } // getUser_name

} // FollowerModel
