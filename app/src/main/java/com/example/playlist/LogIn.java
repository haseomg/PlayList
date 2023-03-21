package com.example.playlist;

//import androidx.appcompat.app.AlertDialog;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogIn extends Activity {

    static Context ctx;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView google_Btn;
    ImageView kakao_Btn;

    Button back;
    Button submit;
    Button signUp;
    Button google;
    Button kakao;

    EditText idEdit, pwEdit;

    String idStr, pwStr;

//    private RetrofitClient retrofitClient;
//    private pinni_api pinni_api;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    public final String TAG = "[LogIn Activity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_log_in);

        Log.i(TAG, "onCreate()");


        ctx = this;

        kakao_Btn = findViewById(R.id.kakaoBtn);
        kakao_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               goKakao();
            }
        });


        kakao = findViewById(R.id.kakaoBtnMent);
        kakao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goKakao();
            }
        });

        google_Btn = findViewById(R.id.googleBtn);
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        google_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        google = findViewById(R.id.googleBtnMent);
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        idEdit = findViewById(R.id.idEditText);
        pwEdit = findViewById(R.id.passwordEditText);

        signUp = findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, SignUp.class);

                startActivity(intent);
            }
        });

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

                        // get 방식 파라미터 추가
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/logIn.php").newBuilder();
                        urlBuilder.addQueryParameter("v", "1.0"); // 예시
                        String url = urlBuilder.build().toString();

                        // POST 파라미터 추가
                        RequestBody formBody = new FormBody.Builder()
                                .add("id", idEdit.getText().toString().trim())
                                .add("pw", pwEdit.getText().toString().trim())
                                .build();

                        // 요청
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
//                                             findViewById(R.id.cpb).setVisibility(View.GONE);
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
                                                        editor.putString("id", responseData);
                                                        ((MainActivity) MainActivity.mainCtx).logIn.setText(responseData);
                                                        Log.i(TAG, "LogIn.setText Check One : " + responseData);
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

                    if (!idStr.equals("") || !pwStr.equals("")) {

                        editor.putString("id", idStr);
                        editor.putString("pw", pwStr);
                        editor.commit();

                        finish();

                        Log.i(TAG, "LogIn.setText Check Two : " + idStr);
                        ((MainActivity) MainActivity.mainCtx).logIn.setText(idStr + "'S");

                        Toast.makeText(getApplicationContext(), idStr + " 님 반갑습니다 !",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "아이디 및 비밀번호 입력이 필요합니다.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
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


    // 자동 로그인 유저
    public void checkAutoLogin(String id) {

        //Toast.makeText(this, id + "님 환영합니다.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    // 카카오 로그인 공통 callback 구성
    void kakaoLogin() {
        String TAG = "kakaoLogin()";
        UserApiClient.getInstance().loginWithKakaoAccount(LogIn.this, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                getUserInfo();

                // 성공하면 테이블에 데이터 삽입
//                registerMe();
            }
            return null;
        });


//        Log.i("[SignUp]","kakao login");
//        UserApiClient.getInstance().loginWithKakaoTalk(SignUp.this, (oAuthToken, error) -> {
//            if (error != null) {
//                Log.e("[SignUp KAKAO LOG IN]", "로그인 실패", error);
//            } else if (oAuthToken != null) {
//                Log.i("[SignUp KAKAO LOG IN]", "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
//            }
//            return null;
//        });

    }

    public void getUserInfo() {
        String TAG = "getUserInfo()";
        UserApiClient.getInstance().me((user, meError) -> {


            if (meError != null) {
                Log.e(TAG, "사용자 정보 요청 실패", meError);
            } else {
                System.out.println("로그인 완료");
                Log.i(TAG, user.toString());
                {
                    Log.i(TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: " + user.getId() +
                            "\n이메일: " + user.getKakaoAccount().getEmail());

                }
                Account user1 = user.getKakaoAccount();
                System.out.println("사용자 계정 : " + user1);
                System.out.println("사용자 계정.toString : " + user1.toString());
                Log.i("[KAKAO userEmail]", "" + user1.getEmail());

                String userID = user1.getEmail();
                String[] userEmailCut = userID.split("@");


                // kakao_api DB에 넘겨줘야 해!
                final String id = userEmailCut[0];
                final String nickname = userEmailCut[0];
                Log.i(TAG, "String id : " + id);
                Log.i(TAG, "String nickname : " + nickname);


                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    // get 방식 파라미터 추가
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/kakaoLogin.php").newBuilder();
                    urlBuilder.addQueryParameter("ver", "1.0"); // 예시
                    String url = urlBuilder.build().toString();
                    Log.i("[kakao]", "String url 확인 : " + url);


                    // POST 파라미터 추가
                    RequestBody formBody = new FormBody.Builder()
                            .add("id", id.trim())
                            .add("nickname", nickname.trim())
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
                            e.printStackTrace();
                            Log.i("[kakao]", "" + e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            Log.i("[kakao]", "onResponse 메서드 작동");

                            // 서브 스레드 Ui 변경 할 경우 에러
                            // 메인스레드 Ui 설정
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {


                                        if (!response.isSuccessful()) {
                                            // 응답 실패
                                            Log.i("[kakao]", "응답 실패 : " + response);
                                            Toast.makeText(getApplicationContext(), "네트워크 문제 발생"
                                                    , Toast.LENGTH_SHORT).show();
                                        } else {
                                            // 응답 성공
                                            final String responseData = response.body().string();
                                            Log.i("[kakao]", "응답 성공 (responseData) : " + responseData);

                                            if (responseData.equals("1")) {
                                                Log.i("[SignUp Activity]", "responseData.equals(\"1\") else : " + responseData);
//                                                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
//                                                startActivityflag(MainActivity.class);
                                            } else {
                                                Log.i("[SignUp Activity]", "responseData.equals(\"0\") else : " + responseData);
//                                                Toast.makeText(getApplicationContext(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
                                            }


                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        }
                    });
                }

                Log.i("[KAKAO userID]", "" + user1.getEmail());
                // id를 DB에 넘겨줄 거야
                Log.i("[KAKAO userID]", "" + id);


//                Intent intent = new Intent(SignUp.this, MainActivity.class);
//                startActivityForResult(intent, 1000);
                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                startActivityflag(MainActivity.class);

            }


            return null;
        });
    }

    // 인텐트 화면전환 하는 함수
    // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    public void kakaoAccountLogin() {
        String TAG = "kakaoAccountLogin()";
        UserApiClient.getInstance().loginWithKakaoAccount(LogIn.this, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                getUserInfo();

                // 성공하면 테이블에 데이터 삽입
//                registerMe();
            }
            return null;
        });
    }

    void goKakao() {
        if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(LogIn.this)) {
            kakaoLogin();
        } else {
            kakaoAccountLogin();
        }
    }

    // 뒤로 가기 버튼 2번 클릭시 종료
//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//        backPressCloseHandler.onBackPressed();
//    }

}

