package com.example.playlist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
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
import androidx.appcompat.app.AlertDialog;
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
import java.util.regex.Pattern;

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
    Button submit, goHome, idDuCheck;
    String nickNameToMain;

    TextView pwSame, pwDiff;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    AlertDialog.Builder builder;

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

    EditText idEdit, pwEdit, pwCheckEdit, nicknameEdit;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Log.i(TAG, "signUp onCreate()");

        // final static context
        signCtx = this;
        builder = new AlertDialog.Builder(this);

//        카카오 키 해시 받음 (아래에 getKeyHash() 메서드 존재)
//        Log.d("KeyHash", getKeyHash());

        // 카카오 로그인
        kakaoBtn = findViewById(R.id.kakaoBtn);
        kakaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "카카오 로그인 이미지뷰 선택");

                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(SignUp.this)) {
                    kakaoLogin();
                } else {
                    kakaoAccountLogin();
                } // else
            } // kakaoBtn.onClick
        }); // kakaoBtn.setOnClickListener

        // 카카오 로그인
        kakaoBtnMent = findViewById(R.id.kakaoBtnMent);
        kakaoBtnMent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "카카오 로그인 텍스트뷰 선택");

                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(SignUp.this)) {
                    kakaoLogin();
                } else {
                    kakaoAccountLogin();
                } // else
            } // onClick
        }); // kakaoBtnMent.setOnClickListener

        googleBtn = findViewById(R.id.googleBtn);
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        // 구글 로그인
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "구글 로그인 이미지뷰 선택");
                signIn();
            } // onClick
        }); // googleBtn.setOnClickListener

        // 구글 로그인
        googleBtnMent = findViewById(R.id.googleBtnMent);
        googleBtnMent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "구글 로그인 텍스트뷰 선택");
                signIn();
            } // onClick
        }); // googleBtnMent.setOnClickListener

        // 쉐어드
        shared = getSharedPreferences("nickname", Activity.MODE_PRIVATE);
        editor = shared.edit();

        id = findViewById(R.id.signUpIdEditText);
        id.setFilters(new InputFilter[]{filter});

        checkIdChange();

        idDuCheck = findViewById(R.id.idDuCheckButton);
        idDuCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "idDuplicate button onClick");
                idDuplicateCheck();
            } // onClick
        }); // idDuCheck.setOnClickListener

        pw = findViewById(R.id.signUpPwEditText);
        pwCheck = findViewById(R.id.signUpPwCheckEditText);
        nickName = findViewById(R.id.signUpNickNameEditText);

        // TODO ID DUPLICATION CHECK
        pwSame = findViewById(R.id.pwSameTextView);
        pwSame.setVisibility(View.INVISIBLE);
        pwDiff = findViewById(R.id.pwDiffTextView);
        pwDiff.setVisibility(View.INVISIBLE);

        pwCheck.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    // 포커스 상태일 때
                    Log.i(TAG, "pwCheck hasFocus");
//                    Toast.makeText(signCtx, "hasFocus", Toast.LENGTH_SHORT).show();

                    if (pwCheck.getText().toString().length() >= 4) {
                        Log.i(TAG, "pwCheck has focus check (if) " + pwCheck.getText().toString());
                        pwFocusCheck();

                    } else {
                        Log.i(TAG, "pwCheck has focus check (else) " + pwCheck.getText().toString());
                        pwDiff.setVisibility(View.INVISIBLE);
                        pwSame.setVisibility(View.INVISIBLE);
                    } // else

                } else {
                    // 포커스 상태가 아닐 때
                    Log.i(TAG, "pwCheck not hasFocus " + pwCheck.hasFocus());
//                    Toast.makeText(signCtx, "has not Focus", Toast.LENGTH_SHORT).show();

                    if (pwCheck.getText().toString().length() >= 4) {
                        Log.i(TAG, "pwCheck has not focus check (if) " + pwCheck.hasFocus());
                        pwFocusCheck();

                    } else {
                        Log.i(TAG, "has not focus check (else) " + pwCheck.hasFocus());
                        pwDiff.setVisibility(View.INVISIBLE);
                        pwSame.setVisibility(View.INVISIBLE);
                    } // else
                } // else
            } // onFocusChange
        }); // pwCheck.setOnFocusChangeListener

        pwCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "pwCheck beforeTextChanged");
            } // beforeTextChanged

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "pwCheck onTextChanged");
            } // onTextChanged

            @Override
            public void afterTextChanged(Editable s) {
                if (pwCheck.getText().toString().length() > 0) {
                    Log.i(TAG, "pwCheck has focus check (if)");
                    pwFocusCheck();

                } else if (pwCheck.getText().toString().length() == 0) {
                    Log.i(TAG, "has focus check (else)");
                    pwDiff.setVisibility(View.INVISIBLE);
                    pwSame.setVisibility(View.INVISIBLE);
                } // else if

//            } else {
//                // 포커스 상태가 아닐 때
//                Log.i(TAG, "pwCheck not hasFocus");
////                    Toast.makeText(signCtx, "has not Focus", Toast.LENGTH_SHORT).show();
//                if (pwCheck.getText().toString().length() >= 4) {
//                    Log.i(TAG, "has not focus check (if)");
//                    pwFocusCheck();
//                } else {
//                    Log.i(TAG, "has not focus check (else)");
//                    pwDiff.setVisibility(View.INVISIBLE);
//                    pwSame.setVisibility(View.INVISIBLE);
//                }
            } // afterTextChanged
        }); // pwCheck.addTextChangedListener

        submit = findViewById(R.id.signUpSubmitButton);
        goHome = findViewById(R.id.goHomeButton);

        fromEditId = id.getText().toString();
        fromEditPw = pw.getText().toString();
        fromEditPwCheck = pwCheck.getText().toString();
        fromEditNickName = nickName.getText().toString();

        // ALREADY HAVE AN ID 버튼
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("[TAG]", "goHome 버튼 클릭");
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
            } // onClick
        }); // goHome.setOnClickListener

        setSubmit();

    } // onCreate

    // 구글 로그인
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
                Toast.makeText(getApplicationContext(), "Something went wrong ⚠️", Toast.LENGTH_SHORT).show();

            }
        }
    }

    void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        startActivity(intent);
    }


    // 인텐트 액티비티 전환함수
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
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

    protected void onStart() {
        super.onStart();
        Log.i(TAG, "signUp onStart()");
    }

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "signUp onResume()");


        // 만약 pwCheckEdit에서 손을 뗐는데 pw랑 정보가 다르면
//
//        String pwString = pw.getText().toString();
//        String pwCheckString = pwCheck.getText().toString();
//        int pwLength = pwString.length();
//        int pwCheckLength = pwCheckString.length();
//
//        if (pwLength != 0 || pwCheckLength != 0) {
//            if (pwString.equals(pwCheckString)) {
//                pwDiff.setVisibility(View.VISIBLE);
//            }
//        }
    }

    public void pwFocusCheck() {
        // 만약 pwCheckEdit에서 손을 뗐는데 pw랑 정보가 다르면
        Log.i(TAG, "pwCheck pwFocusCheck");

        String pwString = pw.getText().toString();
        String pwCheckString = pwCheck.getText().toString();
        int pwLength = pwString.length();
        int pwCheckLength = pwCheckString.length();

        if (pwLength != 0 || pwCheckLength != 0) {
            if (pwLength == 4) {

                Log.i(TAG, "pwCheck visible check if 1 " + pwLength + " / " + pwCheckLength);
                if (pwString.equals(pwCheckString)) {
                    Log.i(TAG, "pwCheck visible check if 2 " + pwString + " / " + pwCheckString);
                    pwDiff.setVisibility(View.INVISIBLE);
                    pwSame.setVisibility(View.VISIBLE);
                } else {
                    Log.i(TAG, "pwCheck pwCheckVisible check else 2 " + pwString + " / " + pwCheckString);
                    pwDiff.setVisibility(View.VISIBLE);
                    pwSame.setVisibility(View.INVISIBLE);
                } // else
            }
        } else {
            Log.i(TAG, "pwCheck visible check else 1 " + pwLength + " / " + pwCheckLength);
        } // else
    }


    protected void onPause() {
        super.onPause();
        Log.i(TAG, "signUp onPause()");
    }

    protected void onStop() {
        super.onStop();
        Log.i(TAG, "signUp onStop()");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "signUp onDestroy()");
    }

    // 카카오 로그인 공통 callback 구성
    void kakaoLogin() {
        String TAG = "kakaoLogin()";
        UserApiClient.getInstance().loginWithKakaoAccount(SignUp.this, (oAuthToken, error) -> {

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

    public void kakaoAccountLogin() {
        String TAG = "kakaoAccountLogin()";
        UserApiClient.getInstance().loginWithKakaoAccount(SignUp.this, (oAuthToken, error) -> {
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
//                Log.e(TAG, "에러 = " + t.getMessage());
//            }
//        });
//    }
//
//    private void parseRegData(String response) throws JSONException {
//        JSONObject jsonObject = new JSONObject(response);
//        if (jsonObject.optString("status").equals("true")) {
//            saveInfo(response);
//            Toast.makeText(SignUp.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
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

                try {
                    String userID = user1.getEmail();
                    String[] userEmailCut = userID.split("@");

// kakao_api DB에 넘겨줘야 해!
                    final String kakaoId = userEmailCut[0];
                    final String nickname = userEmailCut[0];
                    Log.i(TAG, "String id : " + kakaoId);
                    Log.i(TAG, "String nickname : " + nickname);

                    int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                    if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                        // get 방식 파라미터 추가
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.239.85/kakaoLogin.php").newBuilder();
                        urlBuilder.addQueryParameter("ver", "1.0"); // 예시
                        String url = urlBuilder.build().toString();
                        Log.i("[kakao]", "String url 확인 : " + url);

                        // POST 파라미터 추가
                        RequestBody formBody = new FormBody.Builder()
                                .add("id", kakaoId.trim())
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
                                                builder.setTitle("NETWORK ERROR ⚠️");
                                                builder.setMessage("네트워크 문제가 발생하였습니다. ");
                                                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    } // onClcik
                                                });
                                                AlertDialog dialog = builder.create();
                                                dialog.show();

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
                                                } // else
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } // catch
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

                    idShared = id.getText().toString();
                    pwShared = pw.getText().toString();
                    pwCheckShared = pwCheck.getText().toString();
                    nickNameShared = nickName.getText().toString();
                    Log.i("submitCheck Insert Shared ID Check : ", idShared);
                    Log.i("submitCheck Insert Shared PW Check : ", pwShared);
                    Log.i("submitCheck Insert Shared PWCHECK Check : ", pwCheckShared);
                    Log.i("submitCheck Insert Shared NICKNAME Check : ", nickNameShared);

                    editor.putString("nickname", nickNameShared);
                    editor.apply();
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                    startActivityflag(MainActivity.class);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } // catch
            }

            return null;
        });
    }

    protected InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

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

    public void checkId() {
        Log.i(TAG, "idDuCheckButton OnClick()");
        String url = "http://13.124.239.85/check_id.php";
        String myId = id.getText().toString();
        new CheckIdTask() {
            @Override
            protected void onPostExecute(String status) {
                Log.i("SignUp", "idDuCheck onClick status Check : " + status);
                if (id.getText().toString().length() > 0) {
                    if (status.equals("ok")) {
                        Log.i(TAG, "idDuCheck onClick 아이디가 중복되지 않았을 때 statis check : " + status);
                        idDuCheck.setText("✔️");
                    } else if (status.equals("duplicated")) {
                        // 다이얼로그 아이디가 중복되었습니다
                        Log.i(TAG, "idDuCheck onClick 아이디가 중복됐을 때");
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                        builder.setTitle("아이디가 즁복되었습니다,");
                        builder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i(TAG, "아이디가 중복되었습니다. - YES Click");
                                    }
                                });
                        builder.show();
                    } else {
                        Log.i(TAG, "idDuCheck onClick() - status가 ok도 duplicated도 아닐 때 status Check : " + status);
                    }
                } else if (id.getText().toString().length() == 0 || id.getText().toString().equals("")) {
                    // 다이얼로그 값을 입력해주세요
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                    builder.setTitle("아이디의 값을 입력해 주세요.");
                    builder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "아이디가 중복되었습니다. - YES Click");
                                }
                            });
                    builder.show();
                }
            }
        }.execute(url, myId);
    }

    public void checkIdChange() {
        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.i(TAG, "id - beforeTextChanged Method");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i(TAG, "id - onTextChanged Method");
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "id - afterTextChanged Method");
                String url = "http://13.124.239.85/check_id.php";
                String myId = id.getText().toString();
                new CheckIdTask() {
                    @Override
                    protected void onPostExecute(String status) {
                        Log.i("SignUp", "id afterTextChanged status Check : " + status);
                        if (id.getText().toString().length() > 4) {
                            if (status.equals("ok")) {
                                Log.i(TAG, "id onTextChanged 아이디가 중복되지 않았을 때  statis check : " + status);
//                                idDuCheck.setText("✔️");

                            } else if (status.equals("duplicated")) {
                                // 다이얼로그 아이디가 중복되었습니다
                                Log.i(TAG, "id onTextChanged 아이디가 중복 됐을 때");
                            } else {
                                Log.i(TAG, "idDuCheck onClick() - status가 ok도 duplicated도 아닐 때 status Check : " + status);
                            }
                        } else if (id.getText().toString().length() < 4 || id.getText().toString().equals("")) {
                            // 다이얼로그 값을 입력해주세요
                            idDuCheck.setText("중복체크️");
                        }
                    }
                }.execute(url, myId);
            }
        });
    }

    public void idDuplicateCheck() {
        Log.i(TAG, "idDuplicate Check Method()");

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.239.85/check_id.php").newBuilder();
        urlBuilder.addQueryParameter("ver", "1.0"); // 예시
        String url = urlBuilder.build().toString();
        Log.i("[SignUp Activity]", "idDuplicate String url 확인 : " + url);

        String idStr = id.getText().toString();

        RequestBody formBody = new FormBody.Builder()
                .add("id", id.getText().toString().trim())
                .build();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i(TAG, "idDuplicate onFailure");
            } // onFailure

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //TODO copy
                Log.i(TAG, "idDuplicate onResponse");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            if (!response.isSuccessful()) {
                                Log.i(TAG, "idDuplicate !response.isSuccessful : " + response);

                            } else {
                                Log.i(TAG, "idDuplicate response.isSuccessful");
                                final String responseBodyStr = response.body().string().trim();

                                if (idStr.length() >= 4) {

                                    if (responseBodyStr.equals("duplicate")) {
                                        Log.i(TAG, "idDuplicate onClick 아이디가 중복됐을 때");
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                        builder.setTitle("아이디가 중복되었습니다.");
                                        builder.setPositiveButton("YES",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Log.i(TAG, "idDuplicate 아이디가 중복되었습니다. - YES Click");
                                                    } // onClick
                                                });
                                        builder.show();
                                    } else if (responseBodyStr.equals("ok")) {
                                        Log.i(TAG, "idDuplicate 아이디가 중복되지 않았을 때  statis check : " + responseBodyStr);
                                        idDuCheck.setText("✔️");

                                    } else {
                                        Log.i(TAG, "idDuplicate status가 ok도 duplicated도 아닐 때 status Check : " + responseBodyStr);
                                    } // else

                                } else if (id.getText().toString().length() == 0 || id.getText().toString().equals("")) {
                                    // 다이얼로그 값을 입력해주세요
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                    builder.setTitle("아이디의 값을 입력해 주세요.");
                                    builder.setPositiveButton("YES",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Log.i(TAG, "idDuplicate 아이디가 비어있습니다. - YES Click");
                                                } // onClick
                                            });
                                    builder.show();
                                } // else if
                            } // else

                        } catch (Exception e) {
                            Log.e(TAG, "idDuplicate run Exception : " + e);
                        } // catch
                    } // run
                }); // runOnUiThread
            } // onResponse
        }); // client.newCall
    } // idDuplicateCheck

    void setSubmit() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "sendSignUp submitCheck 현재 페이지 회원가입 내용 기입 후 제출 버튼 클릭 ");

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    if (isValidInput()) {
                        if (pw.getText().toString().equals(pwCheck.getText().toString())) {
                            sendSignUpRequest();
                        } else {
                            Log.i(TAG, "sendSignUp 비밀번호가 일치하지 않을 떄 : " + pw.getText().toString() + " / " + pwCheck.getText().toString());
                            showDialog("CHECK THE PASSWORD ✔️", "비밀번호가 일치하지 않습니다..");
                        } // else
                    } else {
                        Log.i(TAG, "sendSignUp 정보가 명확하지 않을 때");
                        showDialog("CHECK YOUR INFO ✔️", "입력하신 정보를 확인해 주세요.");
                    } // else
                } else {
                    Log.i(TAG, "sendSignUp 인터넷 연결이 필요할 때");
                    showDialog("CHECK THE INTERNET ✔️", "인터넷 연결을 확인해 주세요.");
                } // else
            } // onClick
        }); // submit.setOnClickListener
    } // setSubmit

    boolean isValidInput() {
        Log.i(TAG, "sendSignUp isValidInput");
        return id.getText().toString().trim().length() >= 4 &&
                pw.getText().toString().trim().length() == 4 &&
                nickName.getText().toString().trim().length() >= 2;
    } // isValidInput

    private void sendSignUpRequest() {
        // get 방식 파라미터 추가
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.239.85/signUp.php").newBuilder();
        urlBuilder.addQueryParameter("ver", "1.0"); // 예시
        String url = urlBuilder.build().toString();
        Log.i("[SignUp Activity]", "submitCheck String url 확인 : " + url);

        // POST 파라미터 추가
        RequestBody formBody = new FormBody.Builder()
                .add("id", id.getText().toString().trim())
                .add("pw", pw.getText().toString().trim())
                .add("nickname", nickName.getText().toString().trim())
                .build();
        // TODO (1) 바뀌지 않을 값들은 상수 처리 (변수화 - 위의 코드에서 id, pw 키 값들 예시)

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
                Log.i(TAG, "submitCheck ERROR" + e);
            } // onFailure

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.i(TAG, "submitCheck onResponse 메서드 작동");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processResponse(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } // catch
                    } // run
                }); // runOnUiThread
            } // onResponse

        }); // client.newCall
    } // sendSignUpRequest

    private void processResponse(@NonNull Response response) throws IOException {
        if (!response.isSuccessful()) {
            Log.i("[SignUp Activity]", "submitCheck 응답 실패 : " + response);
            return;
        } // if
        // 응답 성공
        Log.i("[SignUp Activity]", "submitCheck 응답 성공 : " + response);
        final String responseData = response.body().string();
        Log.i("[SignUp Activity]", "submitCheck 응답 성공 responseData : " + responseData);

        if (responseData.equals("1")) {
            handleSuccessfulSignUp();
        } else {
            Log.i(TAG, "checkSignUp (2)");
            handleSuccessfulSignUp();
        } // else
    } // processResponse

    private void handleSuccessfulSignUp() {
        Log.i(TAG, "checkSignUp (1)");

        idShared = id.getText().toString();
        pwShared = pw.getText().toString();
        pwCheckShared = pwCheck.getText().toString();
        nickNameShared = nickName.getText().toString();

        Log.i("submitCheck Insert Shared ID Check : ", idShared);
        Log.i("submitCheck Insert Shared PW Check : ", pwShared);
        Log.i("submitCheck Insert Shared PWCHECK Check : ", pwCheckShared);
        Log.i("submitCheck Insert Shared NICKNAME Check : ", nickNameShared);

        editor.putString("nickname", nickNameShared);
        editor.apply();
        editor.commit();

        Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
        startActivityflag(MainActivity.class);
    } // handleSuccessfulSignUp


    private void showDialog(String title, String message) {
        Log.i(TAG, "sendSignUp showDialog");
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            } // onClick
        }); // builder.setPositiveButton
        AlertDialog dialog = builder.create();
        dialog.show();
    } // showDialog

    void beforeSetSubmitOnClick() {
        // NOW PAGE 제출 버튼
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "submitCheck 현재 페이지 회원가입 내용 기입 후 제출 버튼 클릭 ");

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    // EditText값 예외처리
                    if (id.getText().toString().trim().length() >= 4 &&
                            pw.getText().toString().trim().length() == 4 &&
                            nickName.getText().toString().trim().length() >= 2) {

                        if (pw.getText().toString().equals(pwCheck.getText().toString())) {

                            // get 방식 파라미터 추가
                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://13.124.239.85/signUp.php").newBuilder();
                            urlBuilder.addQueryParameter("ver", "1.0"); // 예시
                            String url = urlBuilder.build().toString();
                            Log.i("[SignUp Activity]", "submitCheck String url 확인 : " + url);

                            // POST 파라미터 추가
                            RequestBody formBody = new FormBody.Builder()
                                    .add("id", id.getText().toString().trim())
                                    .add("pw", pw.getText().toString().trim())
                                    .add("nickname", nickName.getText().toString().trim())
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
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                    Log.i(TAG, "submitCheck ERROR" + e);
                                } // onFailure

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                    // TODO copy
                                    Log.i(TAG, "submitCheck onResponse 메서드 작동");

                                    // 서브 스레드 Ui 변경 할 경우 에러
                                    // 메인스레드 Ui 설정
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // 프로그래스바 안보이게 처리
//                                            findViewById(R.id.cpb).setVisibility(View.GONE);

                                                if (!response.isSuccessful()) {
                                                    // 응답 실패
                                                    Log.i("[SignUp Activity]", "submitCheck 응답 실패 : " + response);
//                                                    Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();
//                                                    builder.setTitle("⚠️ NETWORK ERROR ⚠️");
//                                                    builder.setMessage("데이터 활성화와 와이파이를 확인해 주세요. ");
//                                                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(DialogInterface dialog, int which) {
//
//                                                        }
//                                                    });
//                                                    AlertDialog dialog = builder.create();
//                                                    dialog.show();


                                                } else {
                                                    // 응답 성공
                                                    Log.i("[SignUp Activity]", "submitCheck 응답 성공 : " + response);
                                                    final String responseData = response.body().string();
                                                    Log.i("[SignUp Activity]", "submitCheck 응답 성공 responseData : " + responseData);

                                                    if (responseData.equals("1")) {

                                                        if (pw.getText().toString().equals(pwCheck.getText().toString())) {
                                                            if (id.getText().toString().length() >= 4 &&
                                                                    pw.getText().toString().length() == 4 &&
                                                                    nickName.getText().toString().length() >= 2) {

                                                                Log.i(TAG, "checkSignUp (1)");

                                                                idShared = id.getText().toString();
                                                                pwShared = pw.getText().toString();
                                                                pwCheckShared = pwCheck.getText().toString();
                                                                nickNameShared = nickName.getText().toString();
                                                                Log.i("submitCheck Insert Shared ID Check : ", idShared);
                                                                Log.i("submitCheck Insert Shared PW Check : ", pwShared);
                                                                Log.i("submitCheck Insert Shared PWCHECK Check : ", pwCheckShared);
                                                                Log.i("submitCheck Insert Shared NICKNAME Check : ", nickNameShared);

                                                                editor.putString("nickname", nickNameShared);
                                                                editor.apply();
                                                                editor.commit();

                                                                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                                                                startActivityflag(MainActivity.class);

                                                            } else {
                                                                builder.setTitle("CHECK YOUR INFO✔️");
                                                                builder.setMessage("정보를 형식에 맞춰 입력해 주세요.");
                                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                    } // onClick END
                                                                });
                                                                AlertDialog dialog = builder.create();
                                                                dialog.show();
                                                            } // else END

                                                        } else {
                                                            Log.i("[SignUp Activity]", "submitCheck 패스워드가 다를 때 : " + responseData);


                                                            builder.setTitle("PASSWORD CHECK✔️");
                                                            builder.setMessage("패스워드가 다릅니다. ");
                                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                } // onClick
                                                            });
                                                            AlertDialog dialog = builder.create();
                                                            dialog.show();
                                                        }

//                                                    } else {
//                                                        Log.i("[SignUp Activity]", "submitCheck responseData가 1이 아닐 때 : " + responseData);
                                                        Log.i(TAG, "checkSignUp (2)");

                                                        idShared = id.getText().toString();
                                                        pwShared = pw.getText().toString();
                                                        pwCheckShared = pwCheck.getText().toString();
                                                        nickNameShared = nickName.getText().toString();
                                                        Log.i("submitCheck Insert Shared ID Check : ", idShared);
                                                        Log.i("submitCheck Insert Shared PW Check : ", pwShared);
                                                        Log.i("submitCheck Insert Shared PWCHECK Check : ", pwCheckShared);
                                                        Log.i("submitCheck Insert Shared NICKNAME Check : ", nickNameShared);

                                                        editor.putString("nickname", nickNameShared);
                                                        editor.apply();
                                                        editor.commit();

                                                        Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                                                        startActivityflag(MainActivity.class);
                                                    } // else
                                                } // 응답성공 else END

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            } // catch END
                                        } // run method END
                                    }); // runOnUiThread END
                                }
                            });

                            // 비밀번호랑 비밀번호 확인이 다를 때
                        } else {
//                            Toast.makeText(getApplicationContext(), "Check your information", Toast.LENGTH_SHORT).show();

//                            // 비밀번호가 다릅니디.
//                            pwDiff.setVisibility(View.VISIBLE);

                            builder.setTitle("CHECK THE PASSWORD ✔️");
                            builder.setMessage("비밀번호가 다릅니다.");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                } // onClick
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } // else

                    } //if 문 종료 (회원가입 전에 글자 개수 체크)
                    else {
                        builder.setTitle("CHECK YOUR INFO ✔️");
                        builder.setMessage("입력하신 정정보를 확인해 주세요.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else {
//                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                    builder.setTitle("CHECK THE INTERNET ✔️");
                    builder.setMessage("인터넷 연결을 확인해 주세요.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        } // onClick
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } //else END
                if (id.getText().toString().length() >= 4 && pwCheck.getText().toString().length() >= 4 && nickName.getText().toString().length() >= 2) {

                    Log.i(TAG, "checkSignUp (3)");

                    idShared = id.getText().toString();
                    pwShared = pw.getText().toString();
                    pwCheckShared = pwCheck.getText().toString();
                    nickNameShared = nickName.getText().toString();

                    Log.i("submitCheck Insert Shared ID Check : ", idShared);
                    Log.i("submitCheck Insert Shared PW Check : ", pwShared);
                    Log.i("submitCheck Insert Shared PWCHECK Check : ", pwCheckShared);
                    Log.i("submitCheck Insert Shared NICKNAME Check : ", nickNameShared);

                    editor.putString("nickname", nickNameShared);
                    editor.apply();
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                    startActivityflag(MainActivity.class);

                } else {
//                    Toast.makeText(getApplicationContext(), "회원 정보를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                } // else
            } // onClick
        }); // submit.setOnClickListener
    } // beforeSetSubmit

} // CLASS
