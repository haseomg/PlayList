package com.example.playlist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Played extends Activity {

    String TAG = "[SongList Activity]";
    LinearLayoutManager songListLayoutManager;
    ArrayList<AllSongsModel> playedList = new ArrayList<>();
    PlayedAdapter playedAdapter;
    AllSongsModel allSongsModel;
    androidx.recyclerview.widget.RecyclerView PlayedRecyclerView;

    androidx.constraintlayout.widget.ConstraintLayout topBar;
    TextView title;
    Button close;

    private static final String BASE_URL = "http://54.180.155.66/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_played);

        initial();
    } // onCreate

    private void initial() {
        topBar = findViewById(R.id.songListTopBar);
        PlayedRecyclerView = findViewById(R.id.songListRecyclerView);
        title = findViewById(R.id.songListTitle);
        close = findViewById(R.id.songListClose);

        setClose();

        PlayedRecyclerView = findViewById(R.id.songListRecyclerView);
        songListLayoutManager = new LinearLayoutManager(this);
        PlayedRecyclerView.setLayoutManager(songListLayoutManager);
        PlayedRecyclerView.setHasFixedSize(true);
        playedAdapter = new PlayedAdapter(this, playedList);
        PlayedRecyclerView.setAdapter(playedAdapter);

        setPlayedList();

    } // initial

    private void setClose() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } // onClick
        }); // setOnClickListener
    } // setClose;

    private void setPlayedList() {
        Log.i(TAG, "setPlayedList");

    } // setPlayedList

    void setPlayedSongs(String userName) {
        Log.i(TAG, "setPlayed");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi playedFetchApi = retrofit.create(ServerApi.class);

        // TODO. 유저 이름 기준으로 played_list 테이블에서 유저가 들었던 곡들 가져옴
        Call<List<AllSongsModel>> call = playedFetchApi.getPlayedSongs(userName);
        call.enqueue(new Callback<List<AllSongsModel>>() {
            @Override
            public void onResponse(Call<List<AllSongsModel>> call, Response<List<AllSongsModel>> response) {
                Log.i(TAG, "setPlayed onResponse");

                if (response.isSuccessful()) {
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "setPlayed onResponse : " + response.code());
                    Log.i(TAG, "setPlayed Server ResponseBody: " + responseBody);
                    Log.i(TAG, "setPlayed Server Response: " + response);
                    Log.i(TAG, "setPlayed Server Response.message : " + response.message());
                    Log.i(TAG, "setPlayed response.isSuccessful");
                    List<AllSongsModel> playedList = response.body();

                    for (AllSongsModel played : playedList) {
                        Log.i(TAG, "setPlayed onResponse get PlayedList");
                        if (!playedList.contains(null)) {
//                            Log.i(TAG, "setPlayed contains !null : " + );
                        }
                    } // for

                } else {

                } // else
            } // onResponse

            @Override
            public void onFailure(Call<List<AllSongsModel>> call, Throwable t) {
                Log.i(TAG, "setPlayed onFailure : " + t.getMessage());

            } // onFailure
        }); // call
    } // setPlayedSongs

} // CLASS