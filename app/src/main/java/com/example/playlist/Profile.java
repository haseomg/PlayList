package com.example.playlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    TextView profileLogo;
    TextView nickName;
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

    UpdateNickname updateNickname;

    public final String TAG = "[Profile Activity]";

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
                        }

                        fromSharedNickName = shared.getString("nickname", "LOG IN");
                        selectAndUpdateNickname();
                        Log.i(TAG, "match ok 7");

                        nickName.setText(nickNameChange.getText().toString());
                        nickName.setVisibility(View.VISIBLE);
                        nickNameChange.setVisibility(View.GONE);
                        nickNameChangeBtn.setText("닉네임 변경");


                    }
                }
            }
        });

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

                                    ((MainActivity) MainActivity.mainCtx).logIn.setText("LOG IN");
                                    editor.clear();
                                    editor.commit();

                                    Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });

                    builder.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "로그아웃하시겠습니까? 아니오");
                                } // onClick
                            });
                    builder.show();
                }
            }
        });


        profileBackBtn = findViewById(R.id.profileBackButton);
        profileBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        feedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "feedBtn onClick");
                Intent intent = new Intent(Profile.this, Feed.class);
                String user = nickName.getText().toString();
                Log.i(TAG, "onClick nickName check: " + nickName);

                if (nickName.equals("")) {
                    Log.i(TAG, "nickName이 빈 값일 때");
                }
                intent.putExtra("user", user);
                intent.putExtra("now_login_user", nowLoginUser);
                startActivity(intent);
//                Toast.makeText(Profile.this, "개발 중입니다.", Toast.LENGTH_SHORT).show();
            } // onClick
        }); // feedBtn

    }

    // 구글 로그아웃
    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                // 괜찮을지 모르겠네
                startActivity(new Intent(Profile.this, MainActivity.class));
            }
        });
    }

    void kakaoLogout() {
        UserApiClient.getInstance().logout(error -> {
            if (error != null) {
                Log.e("[MAIN KAKAO LOGOUT]", "로그아웃 실패, SDK에서 토큰 삭제됨", error);
            } else {
                Log.e("[MAIN KAKAO LOGOUT]", "로그아웃 성공, SDK에서 토큰 삭제됨");
            }
            return null;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    public void retrofitApi() {
        Log.i(TAG, "retrofitApi Method Start");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.180.155.66/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

//        retrofit = retrofit.newBuilder().client(client).build();

        userApi = retrofit.create(UserApi.class);
    }

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
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i(TAG, "onFailureAPI 호출 실패 : " + t.getMessage());
            }
        });
    }

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
                    }
                } else {
                    Log.i(TAG, "updateNickname response failed");
                    // RESPONSE FAILED
                }
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

        HttpUrl.Builder builder = HttpUrl.parse("http://54.180.155.66/user_info.php").newBuilder();
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
                    }

                }
            }
        });

    }

    public void updateUserTB() {

        Log.i("updateNickname Class", "updateUserTB Method");

        HttpUrl.Builder builder = HttpUrl.parse("http://54.180.155.66/update_user.php").newBuilder();
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

        HttpUrl.Builder builder = HttpUrl.parse("http://54.180.155.66/user_info.php").newBuilder();
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