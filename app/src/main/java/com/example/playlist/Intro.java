package com.example.playlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Intro extends AppCompatActivity {

    Button start;

    String nickNameFromShared;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        nickNameFromShared = shared.getString("nickName","LOG IN");

        start = findViewById(R.id.introStartButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("[Intro]","nickNamFromShared String 값 확인 : " + nickNameFromShared);

                if (nickNameFromShared.equals("LOG IN")) {
                    Log.i("[Intro]","nickNameFromShared가 default값일 때");
                    Intent intent = new Intent(Intro.this, SignUp.class);

                    startActivity(intent);
                } else {
                    Log.i("[Intro]","nickNameFromShared가 default값이 아닐 때");

                    Intent intent = new Intent(Intro.this, MainActivity.class);

                    startActivity(intent);
                }
            }
        });

    }
}