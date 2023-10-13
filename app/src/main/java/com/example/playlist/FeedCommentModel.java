package com.example.playlist;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FeedCommentModel {
    ArrayList<FeedCommentModel> feedCommentList;

    @SerializedName("song_name")
    private String song_name;

    @SerializedName("song_time")
    private String song_time;

    @SerializedName("comment")
    private String comment;

    public String getSong_name() {
        return song_name;
    } // getSong_name

    public String getSong_time() {
        return song_time;
    } // getSong_time

    public String getComment() {
        return comment;
    } // getComment

} // FeedCommentModel
