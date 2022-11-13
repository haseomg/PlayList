package com.example.playlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

public class MainActivity extends AppCompatActivity {

    Button select;
    Button comment;
    Button logIn;
    Button play;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    String personName;
    String personEmail;

    String fromSignUpNickName;
    String fromSharedNickName;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    public static Context mainCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("메인 [MainActivity]", "onCreate");

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        fromSharedNickName = shared.getString("nickName", "LOG IN");

        mainCtx = this;


        mainCtx = this;

        logIn = findViewById(R.id.logInButton);
        if (fromSharedNickName.length() > 20) {
            logIn.setTextSize(10);
        } else if (fromSharedNickName.length() > 12) {
            logIn.setTextSize(12);
        } else if (fromSharedNickName.length() < 5) {
            logIn.setTextSize(15);
        }

        getKakaoUserInfo();

        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        Log.i("[MainActivity]", "acct : " + acct);
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            Log.i("[MainActivity]", "personName : " + personName);
            Log.i("[MainActivity]", "personEmail : " + personEmail);

            editor.putString("nickName", personName);
            editor.commit();

            logIn.setText(personName + "'S");

            // personEmail.setText
        }


        Intent intent = getIntent();

        select = findViewById(R.id.selectableButton);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Selectable.class);

                startActivity(intent);
            }
        });

        comment = findViewById(R.id.commentButton);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // comment
                Intent intent = new Intent(MainActivity.this, Comment.class);

                startActivity(intent);
            }
        });

        fromSignUpNickName = intent.getStringExtra("nickname");


        if (!fromSharedNickName.equals("LOG IN")) {
            logIn.setText(fromSharedNickName);
        }

        if (fromSharedNickName.equals("LOG IN")) {
            logIn.setText(intent.getStringExtra("nickname") + "'S");
            Log.i("[MainActivity]", "fromSharedNickName String 값이 default값일 때");
        } else {
            logIn.setText(fromSharedNickName + "'S");
            Log.i("[MainActivity]", "fromSharedNickName String 값을 쉐어드에서 가져왔을 때 : " + fromSharedNickName);
        }
        Log.i("[Main]", "login.getText.toString() : " + logIn.getText().toString());
        if (logIn.getText().toString().equals("null'S")) {
            logIn.setText("LOG IN");
        }

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (logIn.getText().toString().equals("LOG IN")) {

                    Intent intent = new Intent(MainActivity.this, LogIn.class);

                    editor.remove("nickName");
                    editor.commit();

                    startActivity(intent);

                } else {
                    // Dialog "로그아웃 하시겠습니까?"
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(mainCtx);

                    builder.setTitle("선택하시겠습니까?");

                    builder.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("[MainActivity]","프로필/로그아웃 선택창 취소");
                        }
                    });

                    builder.setNeutralButton("[PROFILE]",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i("[MainActivity]", "로그아웃 취소");
                                    Intent profileIntent = new Intent(MainActivity.this, Profile.class);
                                    startActivity(profileIntent);
                                }
                            });

                    builder.setNegativeButton("[LOGOUT]",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i("[MainActivity]", "로그아웃 완료");

                                    signOut();
                                    kakaoLogout();

                                    logIn.setText("LOG IN");
                                    editor.clear();
                                    editor.commit();
                                }
                            });
                    builder.show();
                }
            }
        });


        play = findViewById(R.id.mainPlayButton);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String playState = play.getText().toString();

                if (!playState.equals("❚❚")) {
                    play.setText("❚❚");
                    play.setTextSize(45);
                    Log.i("메인 플레이 버튼 클릭", "일시정지가 아닐 때");
                } else if (playState.equals("❚❚")) {
                    play.setText("▶");
                    play.setTextSize(60);

                    Log.i("메인 플레이 버튼 클릭", "재생이 아닐 때");
                }
            }
        });


    }


    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                // 괜찮을지 모르겠네
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
    }

    protected void onStart() {
        super.onStart();
        Log.e("메인 [MainActivity]", "onStart");

        logIn = findViewById(R.id.logInButton);
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            Log.i("[MainActivity]", "personName : " + personName);
            Log.i("[MainActivity]", "personEmail : " + personEmail);
            logIn.setText(personName);
            logIn.setTextSize(13);
        }

    }

    protected void onResume() {
        super.onResume();
        Log.e("메인 [MainActivity]", "onResume");
    }

    protected void onPause() {
        super.onPause();
        Log.e("메인 [MainActivity]", "onPause");
    }

    protected void onStop() {
        super.onStop();
        Log.e("메인 [MainActivity]", "onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.e("메인 [MainActivity]", "onDestroy");
    }

    // 카카오 로그아웃
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


    public void getKakaoUserInfo() {
        String TAG = "getUserInfo()";
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.e(TAG, "사용자 정보 요청 실패", meError);
//                logIn.setText("LOG IN");
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
                System.out.println("user.getName : " + user1.getEmail());
                Log.i("[KAKAO userEmail]","" + user1.getEmail());

                String userID = user1.getEmail();
                String[] userEmailCut = userID.split("@");
                String id = userEmailCut[0];
                Log.i("[KAKAO userID]","" + user1.getEmail());
                Log.i("[KAKAO userID]","" + id);

                if (!id.equals(fromSharedNickName) && !fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(fromSharedNickName + "'S");
                    editor.putString("nickName",fromSharedNickName);
                    editor.commit();
                    Log.i("여긴가?","");
                } else if (fromSharedNickName.equals(null) || fromSharedNickName.equals("LOG IN")) {
                    editor.putString("nickName",id);
                    editor.commit();
                    Log.i("여긴가? 22","");
                } else if (!id.equals(fromSharedNickName) && fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(id + "'S");
                    editor.putString("nickName",fromSharedNickName);
                    editor.commit();
                    Log.i("여긴가? 33","");
                }


                if (!fromSharedNickName.equals(null) || !fromSharedNickName.equals("LOG IN")) {
                    editor.putString("nickName",fromSharedNickName);
                    editor.commit();
                    Log.i("여긴가? 44","");
                } if (!id.equals(fromSharedNickName) && fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(id + "'S");
                    editor.putString("nickName",id);
                    editor.commit();
                    Log.i("여긴가? 55","");
                }


            }
            return null;
        });
    }

}