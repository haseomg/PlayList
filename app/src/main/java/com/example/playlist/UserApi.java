package com.example.playlist;

import com.kakao.sdk.user.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserApi {

    @GET("user_info.php")
    Call<User> getUserByNum(@Query("num") int num);

    @GET("user_info.php")
    Call<com.example.playlist.User> getUserByNickname(@Query("nickname") String nickname);

    @GET("update_user.php")
    Call<String> updateUserNickname(@Query("num")int num,
    @Query("nickname")String nickname);


}
