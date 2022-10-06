package com.example.playlist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    EditText id, pw, pwCheck, nickName;
    Button submit;
    String nickNameToMain;

    String fromEditId, fromEditPw, fromEditPwCheck, fromEditNickName;

    String idShared, pwShared, pwCheckShared, nickNameShared;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Log.e("회원가입 [Sign Up]", "onCreate");


        shared = getSharedPreferences("signUp", Activity.MODE_PRIVATE);
        editor = shared.edit();

        id = findViewById(R.id.signUpIdEditText);
        pw = findViewById(R.id.signUpPwEditText);
        pwCheck = findViewById(R.id.signUpPwCheckEditText);
        nickName = findViewById(R.id.signUpNickNameEditText);
        submit = findViewById(R.id.signUpSubmitButton);

        fromEditId = id.getText().toString();
        fromEditPw = pw.getText().toString();
        fromEditPwCheck = pwCheck.getText().toString();
        fromEditNickName = nickName.getText().toString();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (id.length() < 1) {
                    Log.i("[SignUp]", "fromEditID String 길이가 1보다 작을 떄 : " + fromEditId);
                    Toast.makeText(SignUp.this, "Enter your ID", Toast.LENGTH_LONG).show();
                } else if (pw.length() < 1) {
                    Log.i("[SignUp]", "fromEditPw String 길이가 1보다 작을 떄 : " + fromEditPw);
                    Toast.makeText(SignUp.this, "Enter your Password", Toast.LENGTH_LONG).show();
                } else if (pwCheck.length() < 1) {
                    Log.i("[SignUp]", "fromEditPwCheck String 길이가 1보다 작을 떄 : " + fromEditPwCheck);
                    Toast.makeText(SignUp.this, "Enter your Password Check", Toast.LENGTH_LONG).show();
                } else if (nickName.length() < 1) {
                    Log.i("[SignUp]", "fromEditNickName String 길이가 1보다 작을 떄 : " + fromEditNickName);
                    Toast.makeText(SignUp.this, "Enter your NickName", Toast.LENGTH_LONG).show();
                } else if (!pw.getText().toString().equals(pwCheck.getText().toString())) {
                    Log.i("[SignUp]", "Pw != PwCheck : " + pw + ", " + pwCheck);
                    Toast.makeText(SignUp.this, "Password is different", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(SignUp.this, MainActivity.class);


                    nickNameToMain = nickName.getText().toString();

                    intent.putExtra("nickname", nickNameToMain);

                    if (id.getText().toString().equals("")) {
                        // 다이얼로그 띄워줄 예정
                        Log.i("회원가입 - 아이디 입력값이 비어있다.", "");
                    }

                    idShared = id.getText().toString();
                    pwShared = pw.getText().toString();
                    pwCheckShared = pwCheck.getText().toString();
                    nickNameShared = nickName.getText().toString();

                    editor.putString("id", idShared);
                    editor.putString("pw", pwShared);
                    editor.putString("pwCheck", pwCheckShared);
                    editor.putString("nickName", nickNameShared);
                    editor.commit();

                    startActivity(intent);
                }
            }
        });
    }

    protected void onStart() {
        super.onStart();
        Log.e("회원가입 [Sign Up]", "onStart");
    }

    protected void onResume() {
        super.onResume();
        Log.e("회원가입 [Sign Up]", "onResume");
    }

    protected void onPause() {
        super.onPause();
        Log.e("회원가입 [Sign Up]", "onPause");
    }

    protected void onStop() {
        super.onStop();
        Log.e("회원가입 [Sign Up]", "onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.e("회원가입 [Sign Up]", "onDestroy");
    }

}