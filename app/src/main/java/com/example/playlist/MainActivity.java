package com.example.playlist;

import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    String fromSharedNickName;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    public static Context mainCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        fromSharedNickName = shared.getString("nickName","LOG IN");

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

        if (fromSharedNickName.equals("LOG IN")) {
            logIn.setText(intent.getStringExtra("nickname") + "'S");
            Log.i("[MainActivity]","fromSharedNickName String 값이 default값일 때");
        } else {
            logIn.setText(fromSharedNickName + "'S");
            Log.i("[MainActivity]","fromSharedNickName String 값을 쉐어드에서 가져왔을 때 : " + fromSharedNickName);
        }

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (logIn.getText().toString().equals("LOG IN")) {

                    Intent intent = new Intent(MainActivity.this, LogIn.class);

                    startActivity(intent);

                } else {
                    // Dialog "로그아웃 하시겠습니까?"
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(mainCtx);

                    builder.setTitle("로그아웃 하시겠습니까?");

                    builder.setPositiveButton("아니오",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i("[MainActivity]","LogOut Dialog No");

                                }
                            });

                    builder.setNegativeButton("네",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i("[MainActivity]","LogOut Dialog Yes");
                                    logIn.setText("LOG IN");
                                    editor.clear();
                                    editor.commit();
                                }
                            });
                    builder.show();
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