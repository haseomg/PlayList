package com.example.playlist;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    EditText id, pw, pwCheck, nickName;
    Button submit, goHome;
    String nickNameToMain;

    TextView pwInfo, pwCheckInfo;


    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    ImageView googleBtn;
    Button googleBtnMent;

    ImageView kakaoBtn;
    Button kakaoBtnMent;

    public final String TAG = "[Sign Up Activity]";

    String fromEditId, fromEditPw, fromEditPwCheck, fromEditNickName;

    String idShared, pwShared, pwCheckShared, nickNameShared;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    static Context signCtx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Log.i(TAG, "onCreate()");


        // final static context
        signCtx = this;

//        ????????? ??? ?????? ?????? (????????? getKeyHash() ????????? ??????)
//        Log.d("KeyHash", getKeyHash());


        // ????????? ?????????
        kakaoBtn = findViewById(R.id.kakaoBtn);
        kakaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "????????? ????????? ???????????? ??????");

                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(SignUp.this)) {
                    kakaoLogin();
                } else {
                    kakaoAccountLogin();
                }
            }
        });

        // ????????? ?????????
        kakaoBtnMent = findViewById(R.id.kakaoBtnMent);
        kakaoBtnMent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "????????? ????????? ???????????? ??????");

                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(SignUp.this)) {
                    kakaoLogin();
                } else {
                    kakaoAccountLogin();
                }
            }
        });


        googleBtn = findViewById(R.id.googleBtn);
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        // ?????? ?????????
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "?????? ????????? ???????????? ??????");

                signIn();
            }
        });

        // ?????? ?????????
        googleBtnMent = findViewById(R.id.googleBtnMent);
        googleBtnMent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "?????? ????????? ???????????? ??????");

                signIn();
            }
        });

        // ?????????
        shared = getSharedPreferences("signUp", Activity.MODE_PRIVATE);
        editor = shared.edit();

        id = findViewById(R.id.signUpIdEditText);
        pw = findViewById(R.id.signUpPwEditText);
        pwCheck = findViewById(R.id.signUpPwCheckEditText);
        nickName = findViewById(R.id.signUpNickNameEditText);

        pwInfo = findViewById(R.id.pwInfoTextView);
        pwInfo.setVisibility(View.GONE);
        pwCheckInfo = findViewById(R.id.pwCheckInfoTextView);
        pwCheckInfo.setVisibility(View.GONE);

//        pw.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG, "pw.onClick");
//                pwInfo.setVisibility(View.VISIBLE);
//
//                if (pw == null) {
//                    Log.i(TAG, "pw == null");
//
//                } else {
//                    Log.i(TAG, "pw != null");
//
//                    int pwNum = Integer.parseInt(pw.getText().toString());
//                    Log.i(TAG, "pwNum Check : " + pwNum);
//                    if (pwNum <= 9999 && pwNum >= 0000) {
//                        Log.i(TAG, "pwNum <= 9999 ??? ???");
//                        pwInfo.setText("?????????????????????.");
//                    }
//                    if (pw == null) {
//                        Log.i(TAG, "pw == null");
//
//                    } else {
//                        pwInfo.setText("??????????????? ?????? 4?????? ???????????????.");
//                    }
//                }
//
//            }
//        });

        submit = findViewById(R.id.signUpSubmitButton);
        goHome = findViewById(R.id.goHomeButton);

        fromEditId = id.getText().toString();
        fromEditPw = pw.getText().toString();
        fromEditPwCheck = pwCheck.getText().toString();
        fromEditNickName = nickName.getText().toString();

        // ALREAD HAVE AN ID ??????
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "goHome ?????? ??????");

                Intent intent = new Intent(SignUp.this, MainActivity.class);

                startActivity(intent);
            }
        });

        // NOW PAGE ?????? ??????
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "?????? ????????? ???????????? ?????? ?????? ??? ?????? ?????? ??????");

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    // EditText??? ????????????
                    if (id.getText().toString().trim().length() >= 4 &&
                            pw.getText().toString().trim().length() == 4 &&
                            pwCheck.getText().toString().trim().length() == 4 &&
                            nickName.getText().toString().trim().length() >= 2) {

                        if (pw.getText().toString().equals(pwCheck.getText().toString())) {
                            // get ?????? ???????????? ??????
                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.123.224/signUp.php").newBuilder();
                            urlBuilder.addQueryParameter("ver", "1.0"); // ??????
                            String url = urlBuilder.build().toString();
                            Log.i("[SignUp Activity]", "String url ?????? : " + url);

                            // POST ???????????? ??????
                            RequestBody formBody = new FormBody.Builder()
                                    .add("id", id.getText().toString().trim())
                                    .add("pw", pw.getText().toString().trim())
                                    .add("pwCheck", pwCheck.getText().toString().trim())
                                    .add("nickname", nickName.getText().toString().trim())
                                    .build();

                            // ?????? ?????????
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(formBody)
                                    .build();

                            // ?????? ??????
                            client.newCall(request).enqueue(new Callback() {


                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                    Log.i("[SignUp Activity]", "" + e);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {


                                    Log.i("[SignUp Activity]", "onResponse ????????? ??????");

                                    // ?????? ????????? Ui ?????? ??? ?????? ??????
                                    // ??????????????? Ui ??????
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // ?????????????????? ???????????? ??????
//                                            findViewById(R.id.cpb).setVisibility(View.GONE);

                                                if (!response.isSuccessful()) {
                                                    // ?????? ??????
                                                    Log.i("[SignUp Activity]", "?????? ?????? : " + response);
                                                    Toast.makeText(getApplicationContext(), "???????????? ?????? ??????", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    // ?????? ??????
                                                    Log.i("[SignUp Activity]", "?????? ?????? : " + response);
                                                    final String responseData = response.body().string();
                                                    Log.i("[SignUp Activity]", "?????? ?????? responseData : " + responseData);

                                                    if (responseData.equals("1")) {

                                                        if (pw.getText().toString().equals(pwCheck.getText().toString())) {
                                                            if (id.getText().toString().length() >= 4 &&
                                                                    pw.getText().toString().length() == 4 &&
                                                                    pwCheck.getText().toString().length() == 4 &&
                                                                    nickName.getText().toString().length() >= 2) {

                                                                idShared = id.getText().toString();
                                                                pwShared = pw.getText().toString();
                                                                pwCheckShared = pwCheck.getText().toString();
                                                                nickNameShared = nickName.getText().toString();

                                                                editor.putString("id", idShared);
                                                                editor.putString("pw", pwShared);
                                                                editor.putString("pwCheck", pwCheckShared);
                                                                editor.putString("id", nickNameShared);
                                                                editor.commit();


                                                                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                                                                startActivityflag(MainActivity.class);
                                                            }

                                                        } else {
                                                            Log.i("[SignUp Activity]", "responseData.equals(\"0\") else : " + responseData);

                                                            Toast.makeText(getApplicationContext(), "Password is difference", Toast.LENGTH_SHORT).show();
                                                        }

                                                    } else {

                                                        Log.i("[SignUp Activity]", "responseData.equals(\"0\") else : " + responseData);

                                                        Toast.makeText(getApplicationContext(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
                                                    }

//                                                    if (id.getText().toString().length() >= 4 &&
//                                                            pw.getText().toString().length() == 4 &&
//                                                            pwCheck.getText().toString().length() == 4 &&
//                                                            nickName.getText().toString().length() >= 2) {

//                                                    else {
//                                                        Toast.makeText(SignUp.this, "Password is different", Toast.LENGTH_LONG).show();
//                                                    }

                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });


                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(), "Check your information", Toast.LENGTH_SHORT).show();
                        }

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    // ?????? ?????????
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
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        startActivity(intent);
    }


    // ????????? ???????????? ????????????
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // ???????????? ??????????????? ?????????
        overridePendingTransition(0, 0);
    }

    // ????????? ???????????? ?????? ??????
    // FLAG_ACTIVITY_CLEAR_TOP = ????????? ???????????? ?????? ?????? ???????????? ?????????.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // ???????????? ??????????????? ?????????
        overridePendingTransition(0, 0);
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

    // ????????? ????????? ?????? callback ??????
    void kakaoLogin() {
        String TAG = "kakaoLogin()";
        UserApiClient.getInstance().loginWithKakaoAccount(SignUp.this, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "????????? ??????", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "????????? ??????(??????) : " + oAuthToken.getAccessToken());
                getUserInfo();

                // ???????????? ???????????? ????????? ??????
//                registerMe();
            }
            return null;
        });


//        Log.i("[SignUp]","kakao login");
//        UserApiClient.getInstance().loginWithKakaoTalk(SignUp.this, (oAuthToken, error) -> {
//            if (error != null) {
//                Log.e("[SignUp KAKAO LOG IN]", "????????? ??????", error);
//            } else if (oAuthToken != null) {
//                Log.i("[SignUp KAKAO LOG IN]", "????????? ??????(??????) : " + oAuthToken.getAccessToken());
//            }
//            return null;
//        });

    }

    public void kakaoAccountLogin() {
        String TAG = "kakaoAccountLogin()";
        UserApiClient.getInstance().loginWithKakaoAccount(SignUp.this, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "????????? ??????", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "????????? ??????(??????) : " + oAuthToken.getAccessToken());
                getUserInfo();

                // ???????????? ???????????? ????????? ??????
//                registerMe();
            }
            return null;
        });
    }

//    public void registerMe() {
//        final String id = userEmailCut[0];
//        final String nickname = userEmailCut[0];
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(RegisterInterface.REGIST_URL)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();
//
//        RegisterInterface api = retrofit.create(RegisterInterface.class);
//        retrofit2.Call<String> call = api.getUserRegist(id, nickname);
//        call.enqueue(new Callback<String>() {
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//
//                if (response.isSuccessful() && response.body() != null) {
//                    Log.e("onSuccess", response.body());
//
//                    String jsonResponse = response.body();
//                    try {
//                        parseRegData(jsonResponse);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(@NonNull retrofit2.Call<String> call, @NonNull Throwable t) {
//                Log.e(TAG, "?????? = " + t.getMessage());
//            }
//        });
//    }
//
//    private void parseRegData(String response) throws JSONException {
//        JSONObject jsonObject = new JSONObject(response);
//        if (jsonObject.optString("status").equals("true")) {
//            saveInfo(response);
//            Toast.makeText(SignUp.this, "???????????? ??????", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(SignUp.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void saveInfo(String response) {
//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            if (jsonObject.getString("status").equals("true")) {
//                JSONArray dataArray = jsonObject.getJSONArray("data");
//                for (int i = 0; i < dataArray.length(); i++) {
//                    JSONObject dataobj = dataArray.getJSONObject(i);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }


    public void getUserInfo() {
        String TAG = "getUserInfo()";
        UserApiClient.getInstance().me((user, meError) -> {


            if (meError != null) {
                Log.e(TAG, "????????? ?????? ?????? ??????", meError);
            } else {
                System.out.println("????????? ??????");
                Log.i(TAG, user.toString());
                {
                    Log.i(TAG, "????????? ?????? ?????? ??????" +
                            "\n????????????: " + user.getId() +
                            "\n?????????: " + user.getKakaoAccount().getEmail());

                }
                Account user1 = user.getKakaoAccount();
                System.out.println("????????? ?????? : " + user1);
                System.out.println("????????? ??????.toString : " + user1.toString());
                Log.i("[KAKAO userEmail]", "" + user1.getEmail());

                String userID = user1.getEmail();
                String[] userEmailCut = userID.split("@");


                // kakao_api DB??? ???????????? ???!
                final String id = userEmailCut[0];
                final String nickname = userEmailCut[0];
                Log.i(TAG, "String id : " + id);
                Log.i(TAG, "String nickname : " + nickname);


                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    // get ?????? ???????????? ??????
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.123.224/kakaoLogin.php").newBuilder();
                    urlBuilder.addQueryParameter("ver", "1.0"); // ??????
                    String url = urlBuilder.build().toString();
                    Log.i("[kakao]", "String url ?????? : " + url);



                    // POST ???????????? ??????
                    RequestBody formBody = new FormBody.Builder()
                            .add("id", id.trim())
                            .add("nickname", nickname.trim())
                            .build();



                    // ?????? ?????????
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();



                    // ?????? ??????
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                            Log.i("[kakao]", "" + e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            Log.i("[kakao]", "onResponse ????????? ??????");

                            // ?????? ????????? Ui ?????? ??? ?????? ??????
                            // ??????????????? Ui ??????
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {


                                        if (!response.isSuccessful()) {
                                            // ?????? ??????
                                            Log.i("[kakao]", "?????? ?????? : " + response);
                                            Toast.makeText(getApplicationContext(), "???????????? ?????? ??????"
                                                    , Toast.LENGTH_SHORT).show();
                                        } else {
                                            // ?????? ??????
                                            final String responseData = response.body().string();
                                            Log.i("[kakao]", "?????? ?????? (responseData) : " + responseData);

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
                // id??? DB??? ????????? ??????
                Log.i("[KAKAO userID]", "" + id);


//                Intent intent = new Intent(SignUp.this, MainActivity.class);
//                startActivityForResult(intent, 1000);
                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                startActivityflag(MainActivity.class);

            }


            return null;
        });
    }


    public String getKeyHash() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo == null) return null;
            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    Log.w("getKeyHash", "Unable to get MessageDigest. signature=" + signature, e);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("getPackageInfo", "Unable to getPackageInfo");
        }
        return null;
    }


}