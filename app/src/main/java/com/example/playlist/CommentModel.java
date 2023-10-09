package com.example.playlist;

import com.google.gson.annotations.SerializedName;

public class CommentModel {
    @SerializedName("user")
    private String user_name;

    @SerializedName("song")
    private String song_name;

    @SerializedName("selected_time")
    private String selected_time;

    @SerializedName("msg")
    private String msg;

    public CommentModel(String user_name, String song_name, String selected_time, String msg) {
        this.user_name = user_name;
        this.song_name = song_name;
        this.selected_time = selected_time;
        this.msg = msg;
    } // constructor END

    CommentModel(String msg) {
        this.msg = msg;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getSong_name() {
        return song_name;
    }

    public String getSelected_time() {
        return selected_time;
    }

    public String getMsg() {
        return msg;
    }

} // Model END
