package com.example.playlist;

import static com.google.android.gms.common.util.ClientLibraryUtils.getPackageInfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Intro extends AppCompatActivity {

    Button start;

    String nickNameFromShared;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        Log.i("[MainActivity]", "acct : " + acct);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
        }

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        nickNameFromShared = shared.getString("nickName", "LOG IN");

        start = findViewById(R.id.introStartButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("[Intro]", "nickNamFromShared String 값 확인 : " + nickNameFromShared);


                if (acct != null || !nickNameFromShared.equals("LOG IN")) {
                    Log.i("[Intro]", "nickNameFromShared가 default값이 아닐 때");

                    Intent intent = new Intent(Intro.this, MainActivity.class);
                    startActivity(intent);
                } else if (nickNameFromShared.equals("LOG IN")) {
                    Log.i("[Intro]", "nickNameFromShared가 default값일 때");
                    Intent intent = new Intent(Intro.this, SignUp.class);

                    startActivity(intent);
                }
            }
        });
    }

}