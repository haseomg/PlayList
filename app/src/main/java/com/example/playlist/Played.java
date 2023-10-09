package com.example.playlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Played extends Activity {

    String TAG = "[SongList Activity]";
    LinearLayoutManager songListLayoutManager;
    ArrayList<PlayedModel> playedList = new ArrayList<>();
    PlayedAdapter playedAdapter;
    PlayedModel playedModel;
    androidx.recyclerview.widget.RecyclerView PlayedRecyclerView;

    androidx.constraintlayout.widget.ConstraintLayout topBar;
    TextView title;
    Button close;
    String userName, played, getPlayedListFromShared;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    static Context playedContext;

    private static final String BASE_URL = "http://54.180.155.66/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_played);

        initial();
        setPlayedSongs(userName);
    } // onCreate

    private void initial() {
        playedContext = Played.this;
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        Log.i(TAG, "setPlayed : " + userName);

        shared = getSharedPreferences("PlayedList", MODE_PRIVATE);
        editor = shared.edit();
        try {
            if (getPlayedListFromShared == "" | getPlayedListFromShared == null) {
                // TODO Insert Into Shared PlayedList
                for (int i = 0; i < playedList.size(); i++) {
//                    played =
                } // for
                editor.putString(userName, played);
                editor.commit();
                Log.i(TAG, "getPlayedListFromShared check : " + shared.getString(userName, "default"));

            } else {
                // TODO getPlayedList From Shared
            } // else
        } catch (NullPointerException e) {
            Log.e(TAG, "getPlayedListFromShared Null : " + e);
        } // catch END

        PlayedRecyclerView = findViewById(R.id.songListRecyclerView);
        topBar = findViewById(R.id.songListTopBar);
        title = findViewById(R.id.songListTitle);
        close = findViewById(R.id.songListClose);

        setClose();

        PlayedRecyclerView = findViewById(R.id.songListRecyclerView);
        songListLayoutManager = new LinearLayoutManager(this);
        songListLayoutManager.setReverseLayout(true);
        songListLayoutManager.setStackFromEnd(true);
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

    // TODO 월요일에 끝낸다!
    void setPlayedSongs(String userName) {
        Log.i(TAG, "setPlayed");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        ServerApi playedFetchApi = retrofit.create(ServerApi.class);

        // TODO. 유저 이름 기준으로 played_list 테이블에서 유저가 들었던 곡들 가져옴
        Call<List<PlayedModel>> call = playedFetchApi.getPlayedSongs(userName);

        call.enqueue(new Callback<List<PlayedModel>>() {
            @Override
            public void onResponse(Call<List<PlayedModel>> call, Response<List<PlayedModel>> response) {
                Log.i(TAG, "setPlayed onResponse");

                if (response.isSuccessful()) {
                    Log.i(TAG, "setPlayed response success");
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "setPlayed responseBody : " + responseBody);
//                    Log.i(TAG, "setPlayed onResponse : " + response.code());
//                    Log.i(TAG, "setPlayed Server ResponseBody: " + responseBody);
//                    Log.i(TAG, "setPlayed Server Response: " + response);
//                    Log.i(TAG, "setPlayed Server Response.message : " + response.message());
//                    Log.i(TAG, "setPlayed response.isSuccessful");
                    List<PlayedModel> played = response.body();
                    Log.i(TAG, "setPlayed response.body : " + played);

                    playedAdapter.clearItems();

                    for (PlayedModel playedLists : played) {
                        Log.i(TAG, "setPlayed onResponse get PlayedList");

                        String song_name = playedLists.getSong_name();
                        Log.i(TAG, "setPlayed song_name : " + song_name);

                        Log.i(TAG, "setPlayed recyclerview add");

                        playedList.add(playedLists);
                    } // for

                    for (int i = 0; i < playedList.size(); i++) {
                        Log.i(TAG, "playedList check> " + playedList.get(i).getSong_name());
                    } // for
                    playedAdapter.notifyDataSetChanged();

                } else {
                    Log.i(TAG, "setPlayed response failed : " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<List<PlayedModel>> call, Throwable t) {
                Log.i(TAG, "setPlayed onFailure : " + t.getMessage());

            } // onFailure
        }); // call
    } // setPlayedSongs

} // CLASS