package com.example.playlist;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FeedCommentModel {
    ArrayList<FeedCommentModel> feedCommentList;

    @SerializedName("user")
    private String user;

    @SerializedName("song")
    private String song;

    @SerializedName("selected_time")
    private String selected_time;

    @SerializedName("msg")
    private String msg;

    public String getUser() {
        return user;
    } // getUser

    public String getSong() {
        return song;
    } // getSong_name

    public String getSelected_time() {
        return selected_time;
    } // getSong_time

    public String getMsg() {
        return msg;
    } // getMsg

} // FeedCommentModel
