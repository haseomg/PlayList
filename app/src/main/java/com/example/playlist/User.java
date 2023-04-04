package com.example.playlist;

public class User {
    private int num;
    private  String nickname;

    public User(int num, String nickname) {
        this.num = num;
        this.nickname = nickname;
    }

    public int getNum() {
        return num;
    }

    public String getNickname() {
        return  nickname;
    }

}
