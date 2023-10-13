package com.example.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

public class Feed extends AppCompatActivity {

    TextView feedLogo, profileMusic;
    Button close;
    ArrayList<FeedCommentModel> feedCommentList = new ArrayList<>();
    androidx.recyclerview.widget.RecyclerView feedCommentRecyclerVIew;
    FeedCommentModel feedCommentModel;
    FeedCommentAdapter feedCommentAdapter;
    LinearLayoutManager feedCommentLayoutManager;

    private static final String BASE_URL = "http://54.180.155.66/";
    private String song_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feed);

        initial();
    } // onCreate

    private void initial() {

        feedLogo = findViewById(R.id.feedLogo);
        profileMusic = findViewById(R.id.feedProfileMusic);
        close = findViewById(R.id.feedCloseButton);

        Intent intent = getIntent();
        String user = intent.getStringExtra("user");
        feedLogo.setText(user);
        profileMusic.setText(user + " - hello ☁︎");

        feedCommentRecyclerVIew = findViewById(R.id.feedCommentRecyclerView);
         feedCommentLayoutManager = new LinearLayoutManager(this);
         feedCommentRecyclerVIew.setLayoutManager(feedCommentLayoutManager);
         feedCommentRecyclerVIew.setHasFixedSize(true);
         feedCommentAdapter = new FeedCommentAdapter(this, feedCommentList);
         feedCommentRecyclerVIew.setAdapter(feedCommentAdapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    } // initial END

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    } // onTouchEvent

} // CLASS END