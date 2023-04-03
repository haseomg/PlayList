package com.example.playlist;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitService {

    // Http Method 'GET / POST / PUT / DELETE / HEAD 중에서 무슨 작업인지
    @GET("posts/{post}")
    // TODO - posts = 전체 URL에서 URL을 제외한 End Point (URI)
    // TODO - {post} = @Path 변수 대입, 중괄호 {} 안에 감싸진 이름으로 매치
    Call<PostResult> getPosts(@Path("post") String post);
}
