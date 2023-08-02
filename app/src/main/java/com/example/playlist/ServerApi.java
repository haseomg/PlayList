package com.example.playlist;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ServerApi {

//    @Multipart // 요청이 멀티파트 형식 = 여러 파트로 구성된 메시지를 전송할 때 사용되는 표준
//    @Headers("Content-Type: multipart/form-data") // 멀티파트 요청 명시
//    @POST("/upload_audio.php") // 엔드포인트에 요청을 보낸다.
//    // 호출 시 파일을 서버에 업로드하는 역할을 수행, 업로드 완료 시 응답 본문 반환
//    Call<ResponseBody> uploadAudio(
////            @Part MultipartBody.Part file
//            @Part("file\"; filename=\"uploaded_file_name.extension\"") RequestBody file
//            // file_name, file_type ex) song, mp3
//    );

    @Multipart
    @Headers("Content-Type: multipart/form-data")
    @POST("/upload_audio.php")
    Call<ResponseBody> uploadAudio(@Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("get_uuid.php")
    Call<ResponseModel> getUUID(
            @Field("me") String me
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
    @POST("chatting_data_delete.php")
    Call<ResponseModel> deleteData(
            @Field("me") String me,
            @Field("you") String you
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
