package com.example.playlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class Intro extends AppCompatActivity {

    Button start;

    String nickNameFromShared;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    public final String TAG = "[Intro Activity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Log.i(TAG, "onCreate()");


        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        Log.i("[Main Activity]", "acct : " + acct);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
        }

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        nickNameFromShared = shared.getString("id", "LOG IN");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (acct != null || !nickNameFromShared.equals("LOG IN")) {
                    Log.i("[Intro]", "nickNameFromShared가 default값이 아닐 때");

                    Intent intent = new Intent(Intro.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else if (nickNameFromShared.equals("LOG IN")) {
                    Log.i("[Intro]", "nickNameFromShared가 default값일 때");

                    Intent intent = new Intent(Intro.this, SignUp.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);


//        start = findViewById(R.id.introStartButton);
//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.i("[Intro]", "nickNamFromShared String 값 확인 : " + nickNameFromShared);
//
//
//                if (acct != null || !nickNameFromShared.equals("LOG IN")) {
//                    Log.i("[Intro]", "nickNameFromShared가 default값이 아닐 때");
//
//                    Intent intent = new Intent(Intro.this, MainActivity.class);
//                    startActivity(intent);
//                } else if (nickNameFromShared.equals("LOG IN")) {
//                    Log.i("[Intro]", "nickNameFromShared가 default값일 때");
//                    Intent intent = new Intent(Intro.this, SignUp.class);
//
//                    startActivity(intent);
//                }
//            }
//        });
    }

    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }

}