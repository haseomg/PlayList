package com.example.playlist;

import com.google.gson.annotations.SerializedName;

public class ChatListModel {
    @SerializedName("the_other")
    private String the_other;

    @SerializedName("message")
    private String message;

    @SerializedName("date_time")
    private String date_time;

    @SerializedName("is_read")
    private int is_read;

    public ChatListModel(String name, String msg, String time, int is_read) {
        this.the_other = name;
        this.message = msg;
        this.date_time = time;
        this.is_read = is_read;
    } // Constructor END


    public String getThe_other() {
        return the_other;
    }

    public String getMessage() {
        return message;
    }


    public  String getDate_time() {
        return date_time;
    }

    public int getIs_read() {
        return is_read;
    }

    ChatListModel() {

    } // Default Constructor END
}
