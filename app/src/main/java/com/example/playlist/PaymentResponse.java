package com.example.playlist;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {

    @SerializedName("tid")
    private String tid;

    @SerializedName("next_redirect_pc_url")
    private String next_redirect_pc_url;;

    @SerializedName("created_at")
    private String created_at;

    public String getTid() {
        return tid;
    };

    public String getNext_redirect_pc_url() {
        return next_redirect_pc_url;
    }

    public String getCreated_at() {
        return created_at;
    }
} // PaymentResponse
