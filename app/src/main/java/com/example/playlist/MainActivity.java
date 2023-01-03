package com.example.playlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    Random random;
    int rPlay;
    Button leftPlayBtn;
    Button rightPlayBtn;

    Button logIn;
    Button select;
    Button comment;

    TextView songTime;
    TextView mainLogo;

    private Button play;
    private MediaPlayer mediaPlayer;
    private int playPosition = 0;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    String streamingRecordList;
    String[] streamingArray;

    String personName;
    String personEmail;
    int firstplayNum;
    int playlistNum;


    boolean playCheck = false;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    String fromSignUpNickName;
    String fromSharedNickName;

    private ProgressDialog progressDialog;
    ProgressDialog mProgressDialog;

    public final String TAG = "[Main Activity]";

    public static Context mainCtx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate()");

        // final static Context
        mainCtx = this;

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        // 받는 인텐트
        Intent intent = getIntent();
        fromSignUpNickName = intent.getStringExtra("nickname");

        // 쉐어드로부터 가져온 닉네임
        fromSharedNickName = shared.getString("id", "LOG IN");

        // LOGIN 버튼
        logIn = findViewById(R.id.logInButton);


        if (!fromSharedNickName.equals("LOG IN")) {
            logIn.setText(fromSharedNickName);
        }


        // 쉐어드로부터 가져온 닉네임 비교해서 logIn 버튼 이름 설정
        if (fromSharedNickName.equals("LOG IN")) {
            logIn.setText(intent.getStringExtra("nickname") + "'S");
            Log.i(TAG, "fromSharedNickName String 값이 default값일 때");
        } else {
            logIn.setText(fromSharedNickName + "'S");
            Log.i(TAG, "fromSharedNickName String 값을 쉐어드에서 가져왔을 때 : " + fromSharedNickName);
        }
        Log.i("[Main]", "login.getText.toString() : " + logIn.getText().toString());
        if (logIn.getText().toString().equals("null'S")) {
            logIn.setText("LOG IN");
        }

        // 쉐어드로부터 가져온 닉네임 길이에 따라서 글자 사이즈 설정
        if (fromSharedNickName.length() > 20) {
            logIn.setTextSize(10);
        } else if (fromSharedNickName.length() > 12) {
            logIn.setTextSize(12);
        } else if (fromSharedNickName.length() < 5) {
            logIn.setTextSize(15);
        }


        // 카카오 유저 정보 가져오기
        getUserInfo();


        // 구글 로그인
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        Log.i(TAG, "acct : " + acct);
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            Log.i(TAG, "personName : " + personName);
            Log.i(TAG, "personEmail : " + personEmail);

            googleInsertTable();

            editor.putString("id", personName);
            editor.commit();

            logIn.setText(personName + "'S");

            // personEmail.setText
        }


        // PICK 액티비티로 가는 버튼
        select = findViewById(R.id.selectableButton);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "PICK 버튼 클릭");
                Intent intent = new Intent(MainActivity.this, Selectable.class);

                startActivity(intent);
            }
        });

        // COMMENT 액티비티로 가는 버튼
        comment = findViewById(R.id.commentButton);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Comment 버튼 클릭");
                Intent intent = new Intent(MainActivity.this, Comment.class);

                startActivity(intent);
            }
        });

        // LOG IN || User 버튼
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "LOG IN || User 클릭");

                if (logIn.getText().toString().equals("LOG IN")) {

                    Log.i(TAG, "버튼 이름이 [LOG IN]일 때");

                    Intent intent = new Intent(MainActivity.this, LogIn.class);

                    editor.remove("nickName");
                    editor.commit();

                    startActivity(intent);

                } else {

                    Log.i(TAG, "버튼 이름이 [LOG IN]이 아닐 때 || 사용자 이름'S일 때");

                    Intent profileIntent = new Intent(MainActivity.this, Profile.class);
                    startActivity(profileIntent);

                }
            }
        });

        // 메인 왼쪽 화살표 버튼 (직전 음악 재생)
        leftPlayBtn = findViewById(R.id.leftPlayButton);
        leftPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "leftPlay 버튼 클릭");

//                if (song)

            }
        });


        // 메인 재생 버튼
        play = findViewById(R.id.mainPlayButton);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("메인 플레이 버튼 클릭", "");


                String playState = play.getText().toString();

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {


                    // 첫 재생시 재생목록 3개만 생성해보자 (현재 곡 개수 8개)
                    // 재생목록 어떻게 보여줄까?

                    if (playCheck == false) {


                        Log.i("메인 플레이 버튼 클릭", "첫 재생");
                        Log.i(TAG, "playCheck : " + playCheck);


                        if (!playState.equals("❚❚")) {
                            Log.i("메인 플레이 버튼 클릭", "일시정지가 아닐 때");
                            play.setText("❚❚");
                            play.setTextSize(45);


                            int rPlayList[] = new int[8];
                            random = new Random();


                            for (int i = 0; i < 8; i++) {
//                    rPlay = random.nextInt(8) + 1;
//                    Log.i(TAG, "Random Number For Music Play : " + rPlay);

                                rPlayList[i] = random.nextInt(8) + 1;
                                for (int j = 0; j < i; j++) {
                                    if (rPlayList[i] == rPlayList[j]) {
                                        i--;
                                    }
                                }
                            }
                            firstplayNum = 0;
                            // b < ? = ? 부분은 총 뽑아낼 랜덤 숫자 조건
                            for (int b = 0; b < 1; b++) {
                                Log.i(TAG, "Random Number For Music Play (1) : " + rPlayList[b]);

                                firstplayNum = firstplayNum + rPlayList[b];
                            }

                            playlistNum = 0;
                            String numAdd = "-";
                            for (int c = 0; c < 4; c++) {
                                Log.i(TAG, "Random Number For Playlist (2) : " + rPlayList[c]);

//                                playlistNum = playlistNum + rPlayList[c];
                                numAdd = numAdd + rPlayList[c] + "-";
                            }

                            Log.i(TAG, "랜덤 추출 숫자 : " + firstplayNum);
                            Log.i(TAG, "랜덤 추출 숫자들 : " + numAdd);

                            String castNum = Integer.toString(firstplayNum);
                            Log.i(TAG, "String castNum 확인 : " + castNum);


                            // 직접 통신인데..
                            // num 보내고
                            Uri.Builder builder = new Uri.Builder()
                                    .appendQueryParameter("num", castNum);
                            String postParams = builder.build().getEncodedQuery();
                            new getJSONData().execute("http://54.180.123.224/" + "/file_sampling.php", postParams);


//                             get 방식 파라미터 추가
                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.123.224/file_sampling.php").newBuilder();
                            urlBuilder.addQueryParameter("ver", "1.0");
                            String url = urlBuilder.build().toString();
                            Log.i(TAG, "String url 확인 : " + url);


                            // post 파라미터 추가
                            RequestBody formBody = new FormBody.Builder()
                                    .add("num", castNum.trim())
                                    .build();
                            // num을 보내고 -> 테이블의 num을 기준으로 path, name 가져올 거야

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
                                    Log.e(TAG, "play callback onFailure : " + e);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    Log.i(TAG, "play callback onResponse");

                                    // 서브 스레드 UI 변경할 경우 에러
                                    // 메인 스레드 UI 설정
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {

                                                if (!response.isSuccessful()) {
                                                    // 응답 실패
                                                    Log.e("tag", "응답 실패 : " + response);
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
//                                                        startActivityString(MainActivity.class, "nickname", responseData);

                                                        String songInfo = responseData;
                                                        Log.i(TAG, "String songInfo 확인 : " + songInfo);
                                                        String[] songCut = songInfo.split("@@@0");

                                                        String path = songCut[0];
                                                        String time = songCut[1];

                                                        String[] songName = path.split("/");
                                                        String name = songName[4];


                                                        Log.i(TAG, "song name 확인 : " + name);
                                                        Log.i(TAG, "song path 확인 : " + path);
                                                        Log.i(TAG, "song time 확인 : " + time);

//                                                          경로 가져와서 음악 재생 시켜준 뒤
//                                                          초수 세팅

                                                        closePlayer();
                                                        mediaPlayer = new MediaPlayer();
                                                        Log.i(TAG, "MediaPlayer 생성");

                                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                        Log.i(TAG, "mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)");


                                                        String uri = "http://43.201.69.32" + path;
                                                        mediaPlayer.setDataSource(uri);
                                                        Log.i(TAG, "mediaPlayer.setDataSource(path)");


                                                        mediaPlayer.prepareAsync();
                                                        Log.i(TAG, "mediaPlayer.prepareAsync()");

                                                        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                                                        Log.i(TAG, "mediaPlayer.setWakeMode");

                                                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                            @Override
                                                            public void onPrepared(MediaPlayer mp) {
                                                                Log.i(TAG, "mediaPlayer.setOnPreparedListener");
                                                                mediaPlayer.start();
                                                                Log.i(TAG, "mediaPlayer.start()");
                                                            }
                                                        });


                                                        Log.i(TAG, "mediaPlayer.start");

                                                        Toast.makeText(getApplicationContext(), "재생 중", Toast.LENGTH_SHORT).show();


                                                        songTime = findViewById(R.id.mainToPlayTime);
                                                        songTime.setText(time);

                                                        String[] exceptMp3 = name.split(".mp3");
                                                        String justName = exceptMp3[0];
                                                        // _ <- 이거를 공백으로 대체할 수 있을까?

                                                        Log.i(TAG, "song just name 확인 : " + justName);


                                                        mainLogo = findViewById(R.id.mainLogo);
                                                        mainLogo.setText(justName);

                                                        if (!responseData.equals(0)) {
//                                                            responserData " + " 기준으로 잘라줘야 해
                                                            Log.i("[Main]", "responseData 가 0이 아닐 때 : " + responseData);


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


//                    if (firstplayNum.equals("1")) {
//                        Log.i(TAG, "랜덤 추출 숫자가 1일 때");
//                        // 랜덤 추출 숫자 => 서버로 보내고
//                        // music 테이블의 랜덤 추출 숫자에 맞는 num의 path 가져오기


//                    } else if (firstplayNum.equals("2")) {
//                        Log.i(TAG, "랜덤 추출 숫자가 2일 때");
//
//                    } else if (firstplayNum.equals("3")) {
//                        Log.i(TAG, "랜덤 추출 숫자가 3일 때");
//
//                    } else if (firstplayNum.equals("4")) {
//                        Log.i(TAG, "랜덤 추출 숫자가 4일 때");
//
//                    } else if (firstplayNum.equals("5")) {
//                        Log.i(TAG, "랜덤 추출 숫자가 5일 때");
//
//                    } else if (firstplayNum.equals("6")) {
//                        Log.i(TAG, "랜덤 추출 숫자가 6일 때");
//
//                    } else if (firstplayNum.equals("7")) {
//                        Log.i(TAG, "랜덤 추출 숫자가 7일 때");
//
//                    } else if (firstplayNum.equals("8")) {
//                        Log.i(TAG, "랜덤 추출 숫자가 8일 때");
//                    }
//                    Log.i(TAG, "String firstplayNum : " + firstplayNum);
//                            firstplayNum = 0;


//                        첫 재생 playAudio
//                        playAudio();


                            // 재생 됐는지 체크
                            playCheck = true;

                        }


                        // 아래 if (playCHeck == false 닫아주는 중괄호
                    } else { // <-> if (playCheck == true

                        if (!playState.equals("❚❚")) {
                            Log.i("메인 플레이 버튼 클릭", "재시작");
                            Log.i(TAG, "playCheck : " + playCheck);
                            play.setText("❚❚");


//                        if (mediaPlayer == null) {
//                    mediaPlayer = MediaPlayer.create(getApplicationContext()
//                            , R.raw.friendlikeme);
//                    mediaPlayer.start();
//                } else if (!mediaPlayer.isPlaying()) {
//                    mediaPlayer.seekTo(playPosition);
//                    mediaPlayer.start();
//                }

//                    resumeAudio() 일단 안 씀
//                    resumeAudio();

                        } else if (playState.equals("❚❚")) {
                            Log.i("메인 플레이 버튼 클릭", "일시정지 상태일 때");
                            Log.i(TAG, "playCheck : " + playCheck);

                            play.setText("▶");
                            play.setTextSize(60);


//                    pauseAudio() 일단 안 씀
//                    pauseAudio();


                            if (mediaPlayer != null) {
                                mediaPlayer.pause();
                                playPosition = mediaPlayer.getCurrentPosition();
                                Log.d("[PAUSE CHECK]", "" + playPosition);
                                Log.i(TAG, "playCheck : " + playCheck);

                            }

                        } // 재생 버튼이 일시정지 모양일 때
                        // + 예외 처리
                        else {
                            Log.i(TAG, "재생 버튼 모양이 재생도 일시정지도 아님");
                        }

                    } // if (playCheck == true 닫아주는 중괄호


                } else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();

                }

            } // OnClick 메서드 닫아주는 중괄호
        });


        // 메인 오른쪽 화살표 버튼 (랜덤 플레이 버튼)
        rightPlayBtn =

                findViewById(R.id.rightPlayButton);
        rightPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "RightPlay 버튼 클릭");

                stopAudio();
                closePlayer();


                random = new Random();
                rPlay = random.nextInt(4) + 1;
                play.setText("▶");

                Log.i("[MAIN]", "int rPlay Check : " + rPlay);

//                if (rPlay == 1) {
//                    Log.i("[MAIN]", "friendLikeMe 재생");
//                    play.setText("❚❚");
//
//                    if (mediaPlayer == null) {
////                        mediaPlayer = MediaPlayer.create(getApplicationContext()
////                                , R.raw.friendlikeme);
//
//                        String song = "friendlikeme";
//
//
////                        Log.i("[Streaming Record End Cut]","" + StringUtils)
//
//                        mediaPlayer.start();
//                    } else if (!mediaPlayer.isPlaying()) {
//                        mediaPlayer.seekTo(playPosition);
//                        mediaPlayer.start();
//                    }
//
//                } else if (rPlay == 2) {
//                    Log.i("[MAIN]", "waves 재생");
//                    play.setText("❚❚");
//
//                    if (mediaPlayer == null) {
////                        mediaPlayer = MediaPlayer.create(getApplicationContext()
////                                , R.raw.waves);
//
//                        String song = "waves";
//
//                        mediaPlayer.start();
//                    } else if (!mediaPlayer.isPlaying()) {
//                        mediaPlayer.seekTo(playPosition);
//                        mediaPlayer.start();
//                    }
//
//                } else if (rPlay == 3) {
//                    Log.i("[MAIN]", "bonfire 재생");
//                    play.setText("❚❚");
//
//                    if (mediaPlayer == null) {
////                        mediaPlayer = MediaPlayer.create(getApplicationContext()
////                                , R.raw.bonfire);
//
//                        String song = "bonfire";
//
//                        mediaPlayer.start();
//                    } else if (!mediaPlayer.isPlaying()) {
//                        mediaPlayer.seekTo(playPosition);
//                        mediaPlayer.start();
//                    }
//
//                } else if (rPlay == 4) {
//                    Log.i("[MAIN]", "rain 재생");
//                    play.setText("❚❚");
//
//                    if (mediaPlayer == null) {
////                        mediaPlayer = MediaPlayer.create(getApplicationContext()
////                                , R.raw.rain);
//
//                        String song = "rain";
//
//                        mediaPlayer.start();
//                    } else if (!mediaPlayer.isPlaying()) {
//                        mediaPlayer.seekTo(playPosition);
//                        mediaPlayer.start();
//                    }
//                }
//
            }
        });


    }


    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");


        logIn = findViewById(R.id.logInButton);
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            Log.i(TAG, "personName : " + personName);
            Log.i(TAG, "personEmail : " + personEmail);
            logIn.setText(personName);
            logIn.setTextSize(13);
        } else {
            Log.i(TAG, "acct == null");
        }

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


    public void closePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playAudio() {
        closePlayer();
//        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.waves);
        mediaPlayer.start();
        Toast.makeText(this, "재생 중", Toast.LENGTH_SHORT).show();
    }

    private void resumeAudio() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(playPosition);
            mediaPlayer.start();

            Toast.makeText(this, "재시작", Toast.LENGTH_SHORT).show();
        }
    }

    // Media Player 관련 메서드들
    private void pauseAudio() {
        if (mediaPlayer != null) {
            playPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();

            Log.i("[MAIN]", "stopAudio");

            Toast.makeText(this, "일시정지", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();

            Log.i("[MAIN]", "stopAudio");


//            Toast.makeText(this, "중지됨.", Toast.LENGTH_SHORT).show();
        }
    }


    // 구글 로그아웃
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


    // 카카오 유저 정보 가져오는 메서드
    public void getUserInfo() {
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
                Log.i("[KAKAO userEmail]", "" + user1.getEmail());

                String userID = user1.getEmail();
                String[] userEmailCut = userID.split("@");
                String id = userEmailCut[0];
                Log.i("[KAKAO userID]", "" + user1.getEmail());
                Log.i("[KAKAO userID]", "" + id);

                if (!id.equals(fromSharedNickName) && !fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(fromSharedNickName + "'S");
                    editor.putString("id", fromSharedNickName);
                    editor.commit();
                    Log.i("여긴가?", "");
                } else if (fromSharedNickName.equals(null) || fromSharedNickName.equals("LOG IN")) {
                    editor.putString("id", id);
                    editor.commit();
                    Log.i("여긴가? 22", "");
                } else if (!id.equals(fromSharedNickName) && fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(id + "'S");
                    editor.putString("id", fromSharedNickName);
                    editor.commit();
                    Log.i("여긴가? 33", "");
                }


                if (!fromSharedNickName.equals(null) || !fromSharedNickName.equals("LOG IN")) {
                    editor.putString("id", fromSharedNickName);
                    editor.commit();
                    Log.i("여긴가? 44", "");
                }
                if (!id.equals(fromSharedNickName) && fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(id + "'S");
                    editor.putString("id", id);
                    editor.commit();
                    Log.i("여긴가? 55", "");
                }


            }
            return null;

        });
    }

    void googleInsertTable() {
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            // get 방식 파라미터 추가
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.123.224/googleLogin.php").newBuilder();
            urlBuilder.addQueryParameter("ver", "1.0"); // 예시
            String url = urlBuilder.build().toString();
            Log.i("[Google]", "String url 확인 : " + url);

            // POST 파라미터 추가
            RequestBody formBody = new FormBody.Builder()
                    .add("id", personName)
                    .add("nickname", personName)
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
                    Log.i("[Google]", "" + e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.i("[Google]", "onResponse 메서드 작동");

                    // 서브 스레드 Ui 변경 할 경우 에러
                    // 메인스레드 Ui 설정
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {


                                if (!response.isSuccessful()) {
                                    // 응답 실패
                                    Log.i("[Google]", "응답 실패 : " + response);
                                    Toast.makeText(getApplicationContext(), "네트워크 문제 발생"
                                            , Toast.LENGTH_SHORT).show();
                                } else {
                                    // 응답 성공
                                    final String responseData = response.body().string();
                                    Log.i("[Google]", "응답 성공 (responseData) : " + responseData);

                                    if (responseData.equals("1")) {
                                        Log.i("[Google]", "responseData.equals(\"1\") else : " + responseData);
//                                                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
//                                                startActivityflag(MainActivity.class);
                                    } else {
                                        Log.i("[Google]", "responseData.equals(\"0\") else : " + responseData);
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

        Log.i(TAG, "GOOGLE INSERT INTO TABLE COMPLETE? CHECKING!");

    }

    class getJSONData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Music Streaming");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.i(TAG, "PHPComm.getJson(params[0], params[1]) : " + PHPComm.getJson(params[0], params[1]));
                return PHPComm.getJson(params[0], params[1]);
            } catch (Exception e) {
                return new String("Exception : " + e.getMessage());
            }
        }

        protected void onPostExecute(String result) {
//            showList(result);
            mProgressDialog.dismiss();
        }
    }
}