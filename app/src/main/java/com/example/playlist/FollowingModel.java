package com.example.playlist;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FollowingModel {
    ArrayList<FollowingModel> followingList;

    @SerializedName("follower_profile_image")
    private String profile_image;

    @SerializedName("you")
    private String user_name;

    public String getProfile_image() {
        return profile_image;
    } // getProfile_image

    public String getUser_name() {
        return user_name;
    } // getUser_name

} // FollowingModel
