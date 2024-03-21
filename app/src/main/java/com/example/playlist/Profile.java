package com.example.playlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.sdk.user.UserApiClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Profile extends AppCompatActivity {

    private UserApi userApi;

    AlertDialog.Builder builder;

    TextView profileLogo, nickName, premiumText, premiumStatusButton;
    EditText nickNameChange;
    Button nickNameChangeBtn;
    Button profileBackBtn, logOutBtn, feedBtn;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    String fromSharedNickName;
    String id;
    String idToPost;
    String nickname;
    String fromNicknameChange;
    String nowLoginUser;

    String partner_order_id, partner_user_id, item_name, quantity, total_amount;

    UpdateNickname updateNickname;

    public final String TAG = "[Profile Activity]";
    private static final String BASE_URL = "http://15.165.205.105/";

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    static Context ctx;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);
        Log.i(TAG, "onCreate()");

        ctx = this;

        builder = new AlertDialog.Builder(this);

//        updateNickname = new UpdateNickname();

        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            Log.i(TAG, "acct != null");
        } else {
            Log.i(TAG, "acct == null");
        } // else

        shared = getSharedPreferences("nickname", MODE_PRIVATE);
        editor = shared.edit();

        fromSharedNickName = shared.getString("nickname", "LOG IN");

        profileLogo = findViewById(R.id.profileLogo);
        nickName = findViewById(R.id.nickNameTextView);
        nickName.setVisibility(View.VISIBLE);

        nickNameChange = findViewById(R.id.nickNameEditText);
        nickNameChange.setVisibility(View.GONE);
        nickNameChangeBtn = findViewById(R.id.nickNameChangeButton);
        logOutBtn = findViewById(R.id.logOutBtn);
        feedBtn = findViewById(R.id.feedBtn);

        premiumText = findViewById(R.id.premiumTextView);
        premiumStatusButton = findViewById(R.id.premiumStatusBtn);

        Intent intent = getIntent();
        nowLoginUser = intent.getStringExtra("now_login_user");

        if (!fromSharedNickName.equals("LOG IN") || fromSharedNickName != null) {
            profileLogo.setText(fromSharedNickName + "'s profile");
            Log.i(TAG, "로그인 상태");
            // DB에서도 닉네임 변경해줘야 해
            nickName.setText(fromSharedNickName);

        } else {
            nickName.setText("로그인이 필요합니다.");
            nickNameChangeBtn.setVisibility(View.GONE);
            logOutBtn.setText("LOG IN");
            Log.i(TAG, "로그아웃 상태");
        } // else

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

        // TODO - FOR Retrofit USE
//        retrofitApi();
//        getUser();


        nickNameChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "nickNameChangeBtn onClick()");
                nickname = nickNameChange.getText().toString();
//                selectUserTB();

                // TextView -> editText로 변경
                // Button = 변경 -> 완료로 변경

                if (nickNameChangeBtn.getText().toString().equals("닉네임 변경")) {
                    Log.i(TAG, "닉네임 우측 버튼이 'change'일 때");

                    nickName.setVisibility(View.GONE); // 텍스트뷰 숨기고
                    nickNameChange.setVisibility(View.VISIBLE); // 에딧텍스트 보여주기
                    nickNameChange.setHint(nickName.getText().toString() + " (현재)");
                    nickNameChange.setText("");
                    nickNameChangeBtn.setText("변경 완료");

                } else {

                    if (nickNameChange.getText().toString().equals("")) {
                        nickName.setText(nickName.getText().toString());
                        nickName.setVisibility(View.VISIBLE);
                        nickNameChange.setVisibility(View.GONE);
                        nickNameChangeBtn.setText("닉네임 변경");

                    } else {
                        Log.i(TAG, "닉네임 우측 버튼이 'change'가 아닐 때");
                        editor.putString("nickname", nickNameChange.getText().toString());
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "닉네임이 " + nickNameChange.getText().toString() + "(으)로 변경되었습니다."
                                , Toast.LENGTH_SHORT).show();

                        profileLogo.setText(nickNameChange.getText().toString() + "'s profile");

                        fromNicknameChange = nickNameChange.getText().toString();
                        // TODO update nickname to user table
//                        updateNickname(id, nickname);
//                        updateUserTB();
                        //                        updateNickname.selectUserTB(nickname);
                        //                        updateNickname.updateUserTB(id, nickname);

                        nickNameChange.setHint(nickName.getText().toString());

                        ((MainActivity) MainActivity.mainCtx).logIn.setText(nickNameChange.getText().toString());

                        selectAndUpdateNickname();
                        Log.i(TAG, "match ok 1");

                        if (nickNameChange.getText().toString().length() > 12) {
                            profileLogo.setTextSize(20);
                            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(12);
                            editor.putString("id", nickNameChange.getText().toString());
                            editor.commit();
                            fromSharedNickName = shared.getString("nickname", "LOG IN");
//                            selectAndUpdateNickname();
//                            Log.i(TAG, "match ok 2");
                        } else if (nickNameChange.getText().toString().length() > 5) {
                            profileLogo.setTextSize(25);
                            editor.putString("id", nickNameChange.getText().toString());
                            editor.commit();
                            fromSharedNickName = shared.getString("nickname", "LOG IN");
//                            selectAndUpdateNickname();
//                            Log.i(TAG, "match ok 3");
                        } else if (nickNameChange.getText().toString().length() > 20) {
                            profileLogo.setTextSize(8);
                            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(9);
                            editor.putString("id", nickNameChange.getText().toString());
                            editor.commit();
                            fromSharedNickName = shared.getString("nickname", "LOG IN");
//                            selectAndUpdateNickname();
//                            Log.i(TAG, "match ok 4");
                        } else if (nickNameChange.getText().toString().length() > 25) {
                            profileLogo.setTextSize(1);
                            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(10);
                            editor.putString("id", nickNameChange.getText().toString());
                            editor.commit();
                            fromSharedNickName = shared.getString("nickname", "LOG IN");
//                            selectAndUpdateNickname();
//                            Log.i(TAG, "match ok 5");

                        } else if (fromSharedNickName.length() < 5) {
                            profileLogo.setTextSize(30);
                            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(15);
                            editor.putString("id", nickNameChange.getText().toString());
                            editor.commit();
//                            selectAndUpdateNickname();
//                            Log.i(TAG, "match ok 6");
                        } // else if

                        fromSharedNickName = shared.getString("nickname", "LOG IN");
                        selectAndUpdateNickname();
                        Log.i(TAG, "match ok 7");

                        nickName.setText(nickNameChange.getText().toString());
                        nickName.setVisibility(View.VISIBLE);
                        nickNameChange.setVisibility(View.GONE);
                        nickNameChangeBtn.setText("닉네임 변경");
                    } // else
                }
            }
        }); // nickNameChangeBtn.setOnClickListener

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "logOutBtn onClick()");

                if (logOutBtn.getText().toString().equals("LOG IN")) {
                    Intent intent = new Intent(Profile.this, LogIn.class);
                    startActivity(intent);

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                    builder.setTitle("로그아웃 하시겠습니까?");
                    // app.dialog 여서 버튼이 앞에부터 순서대로 쌓이는데, 아무래도 커스텀 해야 할 것 같다.
                    builder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "로그아웃 하시겠습니까? 네");

                                    // 로그아웃 버튼 클릭 시
                                    signOut();
                                    kakaoLogout();
                                    // TODO (1) MainActivity에서 실행 중인 MediaPlayer를 꺼줘야 함
                                    Intent intent = new Intent("LOGOUT_ACTION");
                                    sendBroadcast(intent);

                                    ((MainActivity) MainActivity.mainCtx).logIn.setText("LOG IN");
                                    editor.clear();
                                    editor.commit();

                                    Intent logOutIntent = new Intent(Profile.this, MainActivity.class);
                                    startActivity(logOutIntent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                                } // onClick
                            }); // setPositiveButton

                    builder.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "로그아웃하시겠습니까? 아니오");
                                } // onClick
                            }); // setNegativeButton
                    builder.show();
                } // else
            } // onClick
        }); // logOutBtn.setOnClickListener

        profileBackBtn = findViewById(R.id.profileBackButton);
        profileBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }); // profileBackBtn.setOnClickListener

        feedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "feedBtn onClick");
                Intent intent = new Intent(Profile.this, Feed.class);
                String user = nickName.getText().toString();
                Log.i(TAG, "onClick nickName check: " + nickName);

                if (nickName.equals("")) {
                    Log.i(TAG, "nickName이 빈 값일 때");
                } // if
                intent.putExtra("user", user);
                intent.putExtra("now_login_user", nowLoginUser);
                startActivity(intent);
//                Toast.makeText(Profile.this, "개발 중입니다.", Toast.LENGTH_SHORT).show();
            } // onClick
        }); // feedBtn

        setPremiumStatusButton();
    } // onCreate

    void updatePremiumStatus() {

    } // updatePremiumStatus

    void setPremiumStatusButton() {
        premiumStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPremiumStatus();
            } // onClick
        }); // setOnClickListener
    } // setPremiumStatusButton

    void setPremiumStatus() {
        Log.i(TAG, "membershipService setPremiumStatus");
        if (premiumStatusButton.getText().toString().equals("가입")) {
            Log.i(TAG, "membershipService setPremiumStatus *가입");
            // 프리미엄 혜택 가입 전일 때 == 무료 회원
            // 클릭 시 <무료 회원 -> 프리미엄 회원 혜택 안내서>
//            Toast.makeText(ctx, "프리미엄 회원을 위한 결제가 진행됩니다. [안내서 차후 추가 예정]", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("프리미엄 회원 가입");

            final RadioButton[] rb = new RadioButton[2];
            RadioGroup rg = new RadioGroup(this);
            rb[0] = new RadioButton(this);
            rb[0].setText("사용자: 1개월 무료 체험 • 이후 3,900원/월");
            rg.addView(rb[0]);

            rb[1] = new RadioButton(this);
            rb[1].setText("아티스트: 1개월 무료 체험 • 이후 5,900원/월");
            rg.addView(rb[1]);

            builder.setView(rg);

            builder.setPositiveButton("결제하기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "membershipService dialog positiveButton onClick");
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    Log.i(TAG, "membershipService retrofit.create");
                    ServerApi paymentService = retrofit.create(ServerApi.class);
                    Call<PaymentResponse> call = null;

                    // 라디오 버튼 선택에 따른 결제 로직 구현
                    if (rb[0].isChecked()) {
                        Log.i(TAG, "membershipService premium user check : " + rb[0].isChecked());
                        // 사용자 결제 로직
                        partner_order_id = "indie_music_player_" + nowLoginUser;
                        partner_user_id = nowLoginUser;
                        item_name = "프리미엄 회원";
                        quantity = "1";
                        total_amount = "3900";
                        Log.i(TAG, "membershipService premiumUser partner_order_id (before) : " + partner_order_id);
                        if (partner_order_id == null) {
                            Log.e(TAG, "membershipService partner_order_id is null!!"); // 추가된 로그
                        } // if
                        call = paymentService.requestPayment(partner_order_id, partner_user_id
                                , item_name, quantity, total_amount);
                        Log.i(TAG, "membershipService premiumUser partner_order_id (after) : " + partner_order_id);

                    } else if (rb[1].isChecked()) {
                        Log.i(TAG, "membershipService premium user check : " + rb[1].isChecked());
                        // 아티스트 결제 로직
                        partner_order_id = "indie_music_player_" + nowLoginUser;
                        partner_user_id = nowLoginUser;
                        item_name = "프리미엄 아티스트";
                        quantity = "1";
                        total_amount = "5900";
                        Log.i(TAG, "membershipService premiumArtist partner_order_id (before) : " + partner_order_id);
                        if (partner_order_id == null) {
                            Log.e(TAG, "membershipService premiumArtist partner_order_id is null!!"); // 추가된 로그
                        } // if
                        call = paymentService.requestPayment(partner_order_id, partner_user_id
                                , item_name, quantity, total_amount);
                        Log.i(TAG, "membershipService partner_order_id (after) : " + partner_order_id);
                        Log.i(TAG, "membershipService artist payment logic : " + call);
                    } // else if

                    // 카카오페이 결제 페이지로 이동하는 코드가 들어가야 함
                    call.enqueue(new Callback<PaymentResponse>() {
                        @Override
                        public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                            Log.i(TAG, "membershipService onResponse");

                            if (response.isSuccessful()) {
                                Log.i(TAG, "membershipService onResponse response check : " + response.isSuccessful());
                                // 성공적인 응답 처리
                                try {
                                    PaymentResponse paymentResponse = response.body();
                                    Log.i(TAG, "membershipService response.body() : " + paymentResponse);

//                                     null이 아님을 확인하는 코드 -> null일 경우 AssertionError를 발생시키고 실행을 멈춤
//                                    assert paymentResponse != null;
                                    String redirectUrl = paymentResponse.getNext_redirect_pc_url();
                                    Log.i(TAG, "membershipService response.isSuccessful : " + redirectUrl);

                                    if (redirectUrl != null) {
                                        // 웹뷰를 사용하여 카카오페이 결제 페이지를 열기
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl));
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    } else {
                                        Log.e(TAG, "membershipService redirect URL is null");
                                    } // else

                                } catch (Exception e) {
                                    Log.e(TAG, "membershipService onResponse Exception Error : " + e.getMessage());
                                } // catch

                            } else {
                                try {
                                    Log.e(TAG, "membershipService Response error body : " + response.errorBody().string());
                                    Log.e(TAG, "membershipService Response HTTP status : " + response.code());
                                } catch (IOException e) {
                                    Log.e(TAG, "membershipService onResponse IOException Error : " + e);
                                } // catch
                            } // else
                        } // onResponse

                        @Override
                        public void onFailure(Call<PaymentResponse> call, Throwable t) {
                            Log.e(TAG, "membershipService onFailure : " + t.getMessage());
                        } // onFailure
                    }); // call.enqueue
                } // onClick
            }).setNegativeButton("취소", null);
            builder.create().show();

        } else {
            // 프리미엄 혜택 이용 중일 때 == 유료 회원
            // 클릭 시 [구독 취소 안내서] <프리미엄 회원 혜택 안내서 -> 무료 회원>

        } // else
    } // setPremiumStatusButton

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.i(TAG, "membershipService onNewIntent");
        // UI 변경 코드
        TextView premiumStatusButton = findViewById(R.id.premiumStatusBtn);

        // 결제 완료/실패/취소 조건식으로 구분해야 함

        // 결제 완료
        Toast.makeText(Profile.this, "결제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        premiumStatusButton.setText("이용중");
        premiumStatusButton.setTextColor(Color.parseColor("#7878E1"));
        premiumStatusButton.setBackgroundResource(R.drawable.follower_delete_btn);

        // 결제 실패
//        Toast.makeText(this, "결제를 실패하였습니다.", Toast.LENGTH_SHORT).show();
//        premiumStatusButton.setText("가입");

        // 결제 취소
//        Toast.makeText(Profile.this, "결제가 취소되었습니다.", Toast.LENGTH_SHORT).show();
//        premiumStatusButton.setText("가입");
    } // onNewIntent


    // 구글 로그아웃
    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                // 괜찮을지 모르겠네
                startActivity(new Intent(Profile.this, MainActivity.class));
            } // onComplete
        }); // addOnCompleteListener
    } // signOut

    void kakaoLogout() {
        UserApiClient.getInstance().logout(error -> {
            if (error != null) {
                Log.e("[MAIN KAKAO LOGOUT]", "로그아웃 실패, SDK에서 토큰 삭제됨", error);
            } else {
                Log.e("[MAIN KAKAO LOGOUT]", "로그아웃 성공, SDK에서 토큰 삭제됨");
            } // else
            return null;
        }); // getInstance
    } // kakaoLogout

    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    } // onStart

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    } // onResume

    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    } // onPause

    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    } // onStop

    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    } // onDestroy

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        } // if
        return true;
    } // onTouchEvent

    public void retrofitApi() {
        Log.i(TAG, "retrofitApi Method Start");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.165.205.105/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

//        retrofit = retrofit.newBuilder().client(client).build();

        userApi = retrofit.create(UserApi.class);
    } // retrofitAPI END

    public void getUser() {

        Log.i(TAG, "getUser Method Start");
        Call<User> call = userApi.getUserByNickname("닉네임");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    Log.i(TAG, "onResponse API 호출 실패");
                    return;
                }
                User user = response.body();
                Log.i(TAG, "response body Check : " + response.body().toString());
                Log.i(TAG, "id : " + user.getId() + ", nickname : " + user.getNickname());
                id = user.getId();
                nickname = user.getNickname();
            } // onResponse

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i(TAG, "onFailureAPI 호출 실패 : " + t.getMessage());
            } // onFailure
        });
    } // getUser END

    public void updateNickname(String id, String nickname) {
        Log.i(TAG, "updateNickname Method Start");
        Call<String> call = userApi.updateUserNickname(id, nickname);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG, "updateNickname onResponse Method Start");

                if (response.isSuccessful()) {
                    Log.i(TAG, "updateNickname response.isSuccessful");
                    String result = response.body();
                    if (result.equals("failed")) {
                        Log.i(TAG, "onResponse failed result Check : " + result);
                        // SUCCESS
                    } else {
                        Log.i(TAG, "onResponse success result Check : " + result);
                        // FAILED
                    } // else
                } else {
                    Log.i(TAG, "updateNickname response failed");
                    // RESPONSE FAILED
                } // else
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), "데이터 활성화와 와이파이를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "데이터 활성화와 와이파이를 확인해 주세요");
            }
        });
    }

    public void selectUserTB() {

        Log.i("updateNickname Class", "selectUserTB Method");

        HttpUrl.Builder builder = HttpUrl.parse("http://15.165.205.105/user_info.php").newBuilder();
        builder.addQueryParameter("ver", "1.0");
        String url = builder.build().toString();
        Log.i("updateNickname", "String url check : " + url);

        nickname = nickNameChange.getText().toString();

        RequestBody formBody = new FormBody.Builder()
                .add("nickname", nickname)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.i("Update", "응답 실패 : " + response);

                } else {
                    Log.i("Update", "응답 성공 : " + response);
                    final String responseData = response.body().string();

                    if (responseData.equals("1")) {
                        Log.i("Update", "User Table match == 0");

                    } else {
                        Log.i("Update", "User Table match ok : " + responseData);
                        idToPost = responseData;
                    } // else
                } // else
            } // onResponse
        });
    }

    public void updateUserTB() {

        Log.i("updateNickname Class", "updateUserTB Method");

        HttpUrl.Builder builder = HttpUrl.parse("http://15.165.205.105/update_user.php").newBuilder();
        builder.addQueryParameter("ver", "1.0");
        String url = builder.build().toString();
        Log.i("updateNickname", "String url check : " + url);

        RequestBody formBody = new FormBody.Builder()
                .add("nickname", nickname)
                .add("id", idToPost)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.i("Update", "응답 실패 : " + response);
                } else {
                    Log.i("Update", "응답 성공 : " + response);
                    final String responseData = response.body().string();
                    if (responseData.equals("1")) {
                        Log.i("Update", "User Table match == 0");
                    } else {
                        Log.i("Update", "User Table match ok : " + responseData);
                    }

                }
            }
        });
    }

    public void selectAndUpdateNickname() {

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder builder = HttpUrl.parse("http://15.165.205.105/user_info.php").newBuilder();
        builder.addQueryParameter("ver", "1.0");
        String url = builder.build().toString();
        Log.i("updateNickname", "String url check : " + url);

        RequestBody requestBody = new FormBody.Builder()
                .add("before", fromSharedNickName.trim())
                .add("after", fromNicknameChange.trim())
                .build();

        Request request = new Request.Builder()
                .addHeader("content-type", "charset=utf-8")
                .url(url)
                .post(requestBody)
                .build();

//        Call call = (Call) client.newCall(request);

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            } // onFailure

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.i("Update", "응답 실패 : " + response);
                } else {
                    Log.i("Update", "응답 성공 : " + response);
                    final String responseData = response.body().string();

                    if (responseData.equals("false")) {
                        Log.i("Update", "User Table match == 0");
                    } else {
                        Log.i("Update", "User Table match ok : " + responseData);
                    } // else
                } // else
            } // onResponse
        }); //  client.newCall
    }
} // CLASS