package com.example.playlist;

//import androidx.appcompat.app.AlertDialog;

import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LogIn extends Activity {

    static Context ctx;

    Button back;
    Button submit;

    EditText idEdit, pwEdit;

    String idStr, pwStr;

//    private RetrofitClient retrofitClient;
//    private pinni_api pinni_api;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_log_in);

        ctx = this;

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        idEdit = findViewById(R.id.idEditText);
        pwEdit = findViewById(R.id.passwordEditText);

        back = findViewById(R.id.logInBackButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit = findViewById(R.id.logInSubmitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                idStr = idEdit.getText().toString();
                pwStr = pwEdit.getText().toString();
                hideKeyboard();

                editor.putString("id", idStr);
                editor.putString("pw", pwStr);
                editor.commit();

                finish();
            }
        });
    }


    //키보드 숨기기
    private void hideKeyboard() {

        Log.i("[LogIn]", "hideKeyboard 메서드 작동");


        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(idEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pwEdit.getWindowToken(), 0);
    }

    //화면 터치 시 키보드 내려감
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        Log.i("[LogIn]", "dispatchTouchEvent 메서드 작동");


        View focusView = getCurrentFocus();
        if (focusView != null) {

            Log.i("[LogIn]", "focusView != null일 때");

            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {

                Log.i("[LogIn]", "!rect.contains(x, y)일 때");

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)

                    Log.i("[LogIn]", "imm != null일 때");


                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

//    // 자동 로그인 유저
//    public void checkAutoLogin(String id) {
//
//        //Toast.makeText(this, id + "님 환영합니다.", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//
//    }

    // 뒤로 가기 버튼 2번 클릭시 종료
//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//        backPressCloseHandler.onBackPressed();
//    }
}

