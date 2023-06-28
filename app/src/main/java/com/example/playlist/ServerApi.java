package com.example.playlist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServerApi {

    @FormUrlEncoded
    @POST("get_uuid.php")
    Call<ResponseModel> getUUID(
            @Field("me") String me,
            @Field("you") String you
    );

    @GET("load_chat_data.php")
    Call<List<ResponseModel>> loadChat(@Query("uuid") String uuid);

    @FormUrlEncoded
    @POST("chat_data.php")
    Call<ResponseModel> addData(
            @Field("uuid") String uuid,
            @Field("me") String me,
            @Field("you") String you,
            @Field("from_idx") String from_idx,
            @Field("msg") String msg,
            @Field("msg_idx") int msg_idx,
            @Field("date_time") String timestamp,
            @Field("is_read") int is_read,
            @Field("image_idx") String image
    );

    @FormUrlEncoded
    @POST("chat_data.php")
    Call<Void> insertData(
            @Field("uuid") String uuid,
            @Field("me") String me,
            @Field("you") String you,
            @Field("from_idx") String from_idx,
            @Field("msg") String msg,
            @Field("msg_idx") int msg_idx,
            @Field("date_time") String timestamp,
            @Field("is_read") int is_read,
            @Field("image_idx") String image
    );

    @GET("get_chat_room.php")
    Call<List<ChatListModel>> getChatMessages(@Query("me") String me);
}
