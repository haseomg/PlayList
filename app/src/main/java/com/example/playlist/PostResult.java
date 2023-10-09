package com.example.playlist;

import com.google.gson.annotations.SerializedName;

public class PostResult {

    @SerializedName("nickname")
    private String nickname;
    // @SerializedName으로 일치시켜 주지않을  경우, 클래스 변수명이 일치해야 한다.

    @SerializedName("id")
    private String id;

    private String title;

    @SerializedName("body")
    private String bodyValue;

    // toString()을 Override 해주지 않으면 객체 주소값을 출력한다.
    @Override
    public String toString() {
        return "PostResult {" +
                "nickname = " + nickname +
                ", id = " + id +
                ", title = ' " + title +
                ", bodyValue = ' " + bodyValue +
                " } ";
    }
}
