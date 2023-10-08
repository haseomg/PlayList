package com.example.playlist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

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

} // CLASS