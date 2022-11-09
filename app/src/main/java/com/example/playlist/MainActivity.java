package com.example.playlist;

import androidx.annotation.NonNull;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    Button select;
    Button comment;
    Button logIn;
    Button play;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    String personName;
    String personEmail;

    String fromSignUpNickName;
    String fromSharedNickName;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    public static Context mainCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("메인 [MainActivity]","onCreate");


        logIn = findViewById(R.id.logInButton);
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        Log.i("[MainActivity]","acct : " + acct);
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            Log.i("[MainActivity]","personName : " + personName);
            Log.i("[MainActivity]","personEmail : " + personEmail);
            logIn.setText(personName + "'S");

            // personEmail.setText
        }


        Intent intent = getIntent();

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        fromSharedNickName = shared.getString("nickName", "LOG IN");

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


        if (!fromSharedNickName.equals("LOG IN")) {
            logIn.setText(fromSharedNickName);
        }

        if (fromSharedNickName.equals("LOG IN")) {
            logIn.setText(intent.getStringExtra("nickname") + "'S");
            Log.i("[MainActivity]", "fromSharedNickName String 값이 default값일 때");
        } else {
            logIn.setText(fromSharedNickName + "'S");
            Log.i("[MainActivity]", "fromSharedNickName String 값을 쉐어드에서 가져왔을 때 : " + fromSharedNickName);
        }
        Log.i("[Main]", "login.getText.toString() : " + logIn.getText().toString());
        if (logIn.getText().toString().equals("null'S")) {
            logIn.setText("LOG IN");
        }

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (logIn.getText().toString().equals("LOG IN")) {

                    Intent intent = new Intent(MainActivity.this, LogIn.class);

                    editor.remove("nickName");
                    editor.commit();

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
                                    Log.i("[MainActivity]", "로그아웃 취소");

                                }
                            });

                    builder.setNegativeButton("네",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i("[MainActivity]", "로그아웃 완료");

                                    signOut();

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

    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                // 괜찮을지 모르겠네
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
    }

    protected void onStart() {
        super.onStart();
        Log.e("메인 [MainActivity]", "onStart");

        logIn = findViewById(R.id.logInButton);
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            Log.i("[MainActivity]", "personName : " + personName);
            Log.i("[MainActivity]", "personEmail : " + personEmail);
            logIn.setText(personName);
            logIn.setTextSize(13);
        }

    }

    protected void onResume() {
        super.onResume();
        Log.e("메인 [MainActivity]", "onResume");
    }

    protected void onPause() {
        super.onPause();
        Log.e("메인 [MainActivity]", "onPause");
    }

    protected void onStop() {
        super.onStop();
        Log.e("메인 [MainActivity]", "onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.e("메인 [MainActivity]", "onDestroy");
    }



}