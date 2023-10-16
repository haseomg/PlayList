package com.example.playlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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

    private ServerApi serverApi;
    private UUIDDatabase uuidDatabase;

    int num;

    public final String TAG = "[Intro Activity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Log.i(TAG, "onCreate()");

        // TODO. intro에서 uuid 키 값 서버로부터 가져와서 쉐어드에 저장해서 뽑아쓰자!
        initial();

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

        shared = getSharedPreferences("nickname", MODE_PRIVATE);
        editor = shared.edit();

        nickNameFromShared = shared.getString("nickname", "LOG IN");

//        responseRandomNumbers();

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
        }, 3000);
    }

    private void initial() {
        serverApi = ApiClient.getApiClient().create(ServerApi.class);
        uuidDatabase = Room.databaseBuilder(getApplicationContext(), UUIDDatabase.class, "uuid_db")
                .allowMainThreadQueries()
                .build();
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