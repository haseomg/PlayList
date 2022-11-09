package com.example.playlist;

//import androidx.appcompat.app.AlertDialog;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;

public class LogIn extends Activity {

    static Context ctx;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView google_Btn;

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


//        google_Btn = findViewById(R.id.google_Btn);
//        gso = new GoogleSignInOptions
//                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail().build();
//        gsc = GoogleSignIn.getClient(this, gso);
//
//        google_Btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signIn();
//            }
//        });



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

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    // EditText값 예외처리
                    if (idEdit.getText().toString().trim().length() > 0 ||
                            pwEdit.getText().toString().trim().length() > 0) {

                        // 프로그래스바 보이게 처리
//                        findViewById(R.id.cpb).setVisibility(View.VISIBLE);

                        // get방식 파라미터 추가
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://43.201.105.106/logIn.php").newBuilder();
                        urlBuilder.addQueryParameter("v", "1.0"); // 예시
                        String url = urlBuilder.build().toString();

                        // POST 파라미터 추가
                        RequestBody formBody = new FormBody.Builder()
                                .add("id", idEdit.getText().toString().trim())
                                .add("pw", pwEdit.getText().toString().trim())
                                .build();

                        // 요청 만들기
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(url)
                                .post(formBody)
                                .build();

                        // 응답 콜백
                        client.newCall(request).enqueue(new Callback() {

                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.e("[LogIn]", "" + e);
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                // 서브 스레드 Ui 변경 할 경우 에러
                                // 메인스레드 Ui 설정
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            // 프로그래스바 안보이게 처리
//                                                                      findViewById(R.id.cpb).setVisibility(View.GONE);
                                            if (!response.isSuccessful()) {
                                                // 응답 실패
                                                Log.i("tag", "응답 실패");
                                                Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // 응답 성공
                                                Log.i("tag", "응답 성공");
                                                final String responseData = response.body().string().trim();
                                                Log.i("tag", responseData);
                                                if (responseData.equals("1")) {
                                                    Log.i("[Main]", "responseData 가 1일 때 : " + responseData);
                                                    Toast.makeText(getApplicationContext(), "아이디 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.i("[Main]", "responseData 가 1이 아닐 때 : " + responseData);
                                                    startActivityString(MainActivity.class, "nickname", responseData);
                                                    if (!responseData.equals(0)) {
                                                        editor.putString("nickName", responseData);
                                                        ((MainActivity) MainActivity.mainCtx).logIn.setText(responseData);
                                                        editor.commit();
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "입력 안된 칸이 있습니다.", Toast.LENGTH_SHORT).show();
                    }


                    idStr = idEdit.getText().toString();
                    pwStr = pwEdit.getText().toString();
                    hideKeyboard();

                    editor.putString("id", idStr);
                    editor.putString("pw", pwStr);
                    editor.commit();

                    finish();
                }
            }
        });
    }

    // 액티비티 전환 함수
    // 인텐트 액티비티 전환함수
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 문자열 인텐트 전달 함수
    public void startActivityString(Class c, String name, String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
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


    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            navigateToSecondActivity();
            try {
                task.getResult(ApiException.class);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(LogIn.this, MainActivity.class);
        startActivity(intent);
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

