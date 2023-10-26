package com.example.playlist;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MusicClickListener {

    private static final String BASE_URL = "http://54.180.152.109/";
    String TAG = "[MusicClickListener]";

    public void onClick(String songId) {
        Log.i(TAG, "onClick");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);

        Call<Void> call = serverApi.incrementSongViews(songId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "incrementSongViews onResponse : " + response);

                if (response.isSuccessful()) {
                    try {
                        Log.i(TAG, "incrementSongViews Response isSuccessful : " + response.body());

                    } catch (NullPointerException e) {
                        // TODO - response.body null
                        Log.i(TAG, "incrementSongViews Null Error : " + e);
                    } // catch

                } else {
                    // 조회수 증가 요청이 실패했을 때의 처리
                    Log.e(TAG, "incrementSongViews Response not successful! Status code: " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 조회수 증가 요청이 실패했을 때의 처리
                Log.i(TAG, "incrementSongViews response onFailure! : " + t.getMessage());
            } // onFailure
        }); // call.enqueque
    } // onClick
} // Class
