package com.example.playlist;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterInterface {

    String REGIST_URL = "http://43.201.105.106/";

    @FormUrlEncoded
    @POST("kakaoLogin.php")
    Call<String> getUserRegist(
            @Field("id") String id,
            @Field("nickname") String nickname
    );
}
