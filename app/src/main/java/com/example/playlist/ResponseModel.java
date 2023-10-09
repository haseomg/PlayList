package com.example.playlist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseModel {
    private String uuid;
    public String me;
    private String you;
    private String from_idx;
    private String msg;
    private int msg_idx;
    private String date_time;
    private int is_read;
    private String image_idx;

    private boolean isDateSeparator; // 날짜 구분선 표시 여부

    @SerializedName("uuids")
    private List<String> uuids;

    @SerializedName("the_other")
    private String the_other;

    @SerializedName("message")
    private String message;

    public List<String> getUUIDs() {
        return uuids;
    }

    public void setUUIDs(List<String> uuids) {
        this.uuids = uuids;
    }

    public String getThe_other() {
        return the_other;
    }

    public String getMessage() {
        return message;
    }

    public ResponseModel(String uuid, String me, String you, String from_idx, String msg, int msg_idx, String date_time, int is_read, String image_idx) {
        this.uuid = uuid;
        this.me = me;
        this.you = you;
        this.from_idx = from_idx;
        this.msg = msg;
        this.msg_idx = msg_idx;
        this.date_time = date_time;
        this.is_read = is_read;
        this.image_idx = image_idx;
        this.isDateSeparator = false;
    }

    public ResponseModel(String me, String msg, String date_time, int is_read, String image_idx) {
        this.me = me;
        this.msg = msg;
        this.date_time = date_time;
        this.is_read = is_read;
        this.image_idx = image_idx;
        this.isDateSeparator = false;
    }

    public ResponseModel(String me) {
        this.me = me;
    }

    public ResponseModel(String me, String you) {
        this.me = me;
        this.you = you;
    }

    public String getUUID() { return uuid; }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getMyName() {
        return me;
    }

    public String getYou() {return  you;}

    public String getMsg() {
        return msg;
    }

    public String getImage() {
        return image_idx;
    }

    public String getTimestamp() {
        return date_time;
    }

    public void setTimestamp(String date_time) {
        date_time = date_time;
    }

    public int getReadCheck() {
        return is_read;
    }

    ResponseModel() {
        this.isDateSeparator = false;
    }

    public boolean isDateSeparator() {
        return isDateSeparator;
    }

    public void setDateSeparator(boolean dateSeparator) {
        isDateSeparator = dateSeparator;
    }
}
