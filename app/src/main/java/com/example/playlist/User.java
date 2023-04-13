package com.example.playlist;

public class User {
    private String  id;
    private String nickname;

    public User() {

    }

    public User(String id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

}
