package com.example.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Feed extends AppCompatActivity {

    TextView feedLogo, profileMusic;
    Button close;

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