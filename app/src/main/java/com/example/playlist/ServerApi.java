package com.example.playlist;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ServerApi {

    @Multipart // 요청이 멀티파트 형식 = 여러 파트로 구성된 메시지를 전송할 때 사용되는 표준
//    @Headers("Content-Type: multipart/form-data") // 멀티파트 요청 명시
    @POST("/upload_audio.php")
        // 엔드포인트에 요청을 보낸다
        // 호출 시 파일을 서버에 업로드하는 역할을 수행, 업로드 완료 시 응답 본문 반환
    Call<ResponseBody> uploadAudio(
            @Part MultipartBody.Part file,
            // TODO 데이터랑 파일 같이 보내고 어떤 게 누락되는지 확인
            @Part("text") RequestBody textRequestBody);

//    @FormUrlEncoded
//    @POST("get_comments.php")
//    Call<CommentModel> getComments(
//            @Field("song_name") String song_name,
//            @Field("selected_time") String selected_time
//    );

    @FormUrlEncoded
    @POST("update_likes.php")
    Call<List<UpdateLikedModel>> updateLikes(
            @Field("user_id") String user_id,
            @Field("song_name") String song_name
    );

    @FormUrlEncoded
    @POST("get_liked_users.php")
    Call<List<LikedModel>> getLikedUsers(
            @Field("selected_song") String selected_song
    );

    @FormUrlEncoded
    @POST("get_uuid.php")
    Call<ResponseModel> getUUID(
            @Field("me") String me
    );

    @GET("load_chat_data.php")
    Call<List<ResponseModel>> loadChat(
            @Query("uuid") String uuid
    );

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
    Call<List<ChatListModel>> getChatMessages(
            @Query("me") String me);

    @GET("get_comments.php")
    Call<List<CommentModel>> getComments(
            @Query("song_name") String song_name);

    // TODO
    @FormUrlEncoded
    @POST("get_played_songs.php")
    Call<List<PlayedModel>> getPlayedSongs(
            @Field("user_name") String user_name
    );

    @GET("get_songs.php")
    Call<List<AllSongsModel>> getAllSongs();

    @FormUrlEncoded
    @POST("select_likes.php")
    Call<List<UpdateLikedModel>> selectLikes(
            @Field("user_id") String user_id
    );


    @GET("top_views.php")
    Call<List<TopViewsModel>> getTopSongs();

    @FormUrlEncoded
    @POST("increment_song_views.php")
        // 서버의 PHP 파일 경로
    Call<Void> incrementSongViews(
            @Field("song_id") String songId);

    @FormUrlEncoded
    @POST("upload_comment.php")
    Call<Void> loadComment(
            @Field("user") String user,
            @Field("song") String song,
            @Field("selected_time") String selected_time,
            @Field("msg") String msg
    );

    @FormUrlEncoded
    @POST("insert_played.php")
    Call<Void> loadPlayedRecord(
            @Field("user") String user,
            @Field("playedSongs") String playedSongs
    );

    @FormUrlEncoded
    @POST("delete_played.php")
    Call<Void> deletePlayedRecord(
            @Field("song_name") String song_name,
            @Field("user_name") String user_name
    );

} // ServerApi Interface END
