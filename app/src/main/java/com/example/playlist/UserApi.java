package com.example.playlist;

import com.kakao.sdk.user.model.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UserApi {

    @FormUrlEncoded
    @POST("user_info.php")
    Call<User> getUserById(@Field("id") String id);

    @FormUrlEncoded
    @POST("user_info.php")
    Call<com.example.playlist.User> getUserByNickname(@Field("nickname") String nickname);

    @FormUrlEncoded
    @POST("update_user.php")
    Call<String> updateUserNickname(
            @Field("id") String id,
            @Field("nickname") String nickname);


}
