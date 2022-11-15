package com.example.playlist;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends Activity {

    TextView profileLogo;
    EditText nickNameChange;
    Button nickNameChangeBtn, profileBackBtn;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    String fromSharedNickName;

    public final String TAG = "[Profile Activity]";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);
        Log.i(TAG, "onCreate()");

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        fromSharedNickName = shared.getString("nickName", "LOG IN");

        profileLogo = findViewById(R.id.profileLogo);
        nickNameChange = findViewById(R.id.nickNameChangeEditText);
        nickNameChangeBtn = findViewById(R.id.nickNameChangeButton);

        if (!fromSharedNickName.equals("LOG IN") && fromSharedNickName != null) {
            profileLogo.setText(fromSharedNickName + "'s profile");

            // DB에서도 닉네임 변경해줘야 해
            nickNameChange.setHint(fromSharedNickName);
        } else {

        }

        if (fromSharedNickName.length() > 12) {
            profileLogo.setTextSize(20);
            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(12);
        } else if (fromSharedNickName.length() > 5) {
            profileLogo.setTextSize(25);
        } else if (fromSharedNickName.length() > 20) {
            profileLogo.setTextSize(1);
            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(10);
        } else if (fromSharedNickName.length() > 25) {
            profileLogo.setTextSize(1);
            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(10);
        } else if (fromSharedNickName.length() < 5) {
            profileLogo.setTextSize(30);
            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(15);
        }

        nickNameChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("nickName", nickNameChange.getText().toString());
                editor.commit();
                Toast.makeText(getApplicationContext(), "닉네임이 " + nickNameChange.getText().toString() + "(으)로 변경되었습니다."
                        , Toast.LENGTH_SHORT).show();

                profileLogo.setText(nickNameChange.getText().toString() + "'s profile");


                // DB에서도 닉네임 변경해줘야 해
                nickNameChange.setHint(nickNameChange.getText().toString());

                ((MainActivity) MainActivity.mainCtx).logIn.setText(nickNameChange.getText().toString() + "'S");

                if (nickNameChange.getText().toString().length() > 12) {
                    profileLogo.setTextSize(20);
                    ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(12);
                    editor.putString("nickName", nickNameChange.getText().toString());
                    editor.commit();
                } else if (nickNameChange.getText().toString().length() > 5) {
                    profileLogo.setTextSize(25);
                    editor.putString("nickName", nickNameChange.getText().toString());
                    editor.commit();
                } else if (nickNameChange.getText().toString().length() > 20) {
                    profileLogo.setTextSize(8);
                    ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(9);
                    editor.putString("nickName", nickNameChange.getText().toString());
                    editor.commit();
                } else if (nickNameChange.getText().toString().length() > 25) {
                    profileLogo.setTextSize(1);
                    ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(10);
                    editor.putString("nickName", nickNameChange.getText().toString());
                    editor.commit();
                } else if (fromSharedNickName.length() < 5) {
                    profileLogo.setTextSize(30);
                    ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(15);
                    editor.putString("nickName", nickNameChange.getText().toString());
                    editor.commit();
                }

                nickNameChange.setText("");

            }
        });


        profileBackBtn = findViewById(R.id.profileBackButton);
        profileBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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