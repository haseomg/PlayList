package com.example.playlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.user.UserApiClient;

public class Profile extends Activity {

    TextView profileLogo;
    TextView nickName;
    EditText nickNameChange;
    Button nickNameChangeBtn;
    Button profileBackBtn;
    Button logOutBtn;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    String fromSharedNickName;

    public final String TAG = "[Profile Activity]";

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_profile);
        Log.i(TAG, "onCreate()");

        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            Log.i(TAG, "acct != null");
        } else {
            Log.i(TAG, "acct == null");
        }


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
                Log.i(TAG, "nickNameChangeBtn onClick()");

                // TextView -> editText로 변경
                // Button = 변경 -> 완료로 변경

                if (nickNameChangeBtn.getText().toString().equals("change")) {
                    Log.i(TAG, "닉네임 우측 버튼이 'change'일 때");

                    nickName.setVisibility(View.GONE); // 텍스트뷰 숨기고
                    nickNameChange.setVisibility(View.VISIBLE); // 에딧텍스트 보여주기
                    nickNameChange.setHint(nickName.getText().toString() + " (NOW)");
                    nickNameChange.setText("");
                    nickNameChangeBtn.setText("complete");

                } else {

                    if (nickNameChange.getText().toString().equals("")) {

                        nickName.setText(nickName.getText().toString());
                        nickName.setVisibility(View.VISIBLE);
                        nickNameChange.setVisibility(View.GONE);
                        nickNameChangeBtn.setText("change");
                    } else {
                        Log.i(TAG, "닉네임 우측 버튼이 'change'가 아닐 때");

                        editor.putString("id", nickNameChange.getText().toString());
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "닉네임이 " + nickNameChange.getText().toString() + "(으)로 변경되었습니다."
                                , Toast.LENGTH_SHORT).show();

                        profileLogo.setText(nickNameChange.getText().toString() + "'s profile");


                        // DB에서도 닉네임 변경해줘야 해
                        nickNameChange.setHint(nickName.getText().toString());

                        ((MainActivity) MainActivity.mainCtx).logIn.setText(nickNameChange.getText().toString());

                        if (nickNameChange.getText().toString().length() > 12) {
                            profileLogo.setTextSize(20);
                            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(12);
                            editor.putString("id", nickNameChange.getText().toString());
                            editor.commit();
                        } else if (nickNameChange.getText().toString().length() > 5) {
                            profileLogo.setTextSize(25);
                            editor.putString("id", nickNameChange.getText().toString());
                            editor.commit();
                        } else if (nickNameChange.getText().toString().length() > 20) {
                            profileLogo.setTextSize(8);
                            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(9);
                            editor.putString("id", nickNameChange.getText().toString());
                            editor.commit();
                        } else if (nickNameChange.getText().toString().length() > 25) {
                            profileLogo.setTextSize(1);
                            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(10);
                            editor.putString("id", nickNameChange.getText().toString());
                            editor.commit();
                        } else if (fromSharedNickName.length() < 5) {
                            profileLogo.setTextSize(30);
                            ((MainActivity) MainActivity.mainCtx).logIn.setTextSize(15);
                            editor.putString("id", nickNameChange.getText().toString());
                            editor.commit();
                        }

                        nickName.setText(nickNameChange.getText().toString());
                        nickName.setVisibility(View.VISIBLE);
                        nickNameChange.setVisibility(View.GONE);
                        nickNameChangeBtn.setText("change");


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

                    builder.setTitle("Are you sure want to log out?");

                    // app.dialog 여서 버튼이 앞에부터 순서대로 쌓이는데, 아무래도 커스텀 해야 할 것 같다.
                    builder.setPositiveButton("YES",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "로그아웃하시겠습니까? 네");

                                    // 로그아웃 버튼 클릭 시
                                    signOut();
                                    kakaoLogout();

                                    ((MainActivity) MainActivity.mainCtx).logIn.setText("LOG IN");
                                    editor.clear();
                                    editor.commit();

                                    Toast.makeText(getApplicationContext(),"로그아웃 되었습니다.",Toast.LENGTH_SHORT).show();
                                }
                            });

                    builder.setNegativeButton("NO",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "로그아웃하시겠습니까? 아니오");
                                }
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

}