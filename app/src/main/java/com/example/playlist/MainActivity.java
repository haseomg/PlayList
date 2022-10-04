package com.example.playlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button select;
    Button comment;
    Button logIn;
    Button play;

    String fromSignUpNickName;

    public static Context mainCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        mainCtx = this;

        select = findViewById(R.id.selectableButton);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Selectable.class);

                startActivity(intent);
            }
        });

        comment = findViewById(R.id.commentButton);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // comment
                Intent intent = new Intent(MainActivity.this, Comment.class);

                startActivity(intent);
            }
        });

        fromSignUpNickName = intent.getStringExtra("nickname");

        logIn = findViewById(R.id.logInButton);
        logIn.setText(intent.getStringExtra("nickname") + "'S");
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (logIn.getText().toString() != fromSignUpNickName) {

                    Intent intent = new Intent(MainActivity.this, LogIn.class);

                    startActivity(intent);

                } else {
                    Log.i("로그인 버튼의 문장이 회원가입으로부터 받은 문장과 같다.","");
                    // 로그아웃 하시겠습니까?
                }
            }
        });



        play = findViewById(R.id.mainPlayButton);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String playState = play.getText().toString();

                if (!playState.equals("❚❚")) {
                    play.setText("❚❚");
                    play.setTextSize(45);
                    Log.i("메인 플레이 버튼 클릭", "일시정지가 아닐 때");
                } else if (playState.equals("❚❚")) {
                    play.setText("▶");
                    play.setTextSize(60);

                    Log.i("메인 플레이 버튼 클릭", "재생이 아닐 때");
                }


            }
        });

    }
}