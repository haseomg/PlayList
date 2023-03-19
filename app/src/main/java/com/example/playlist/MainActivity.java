package com.example.playlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
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

    int rPlay;
    Random random;
    Button leftPlayBtn;
    Button rightPlayBtn;

    Button logIn;
    Button select;
    Button comment;

    TextView songTime;
    TextView mainLogo;
    TextView playingTime;
    TextView toPlayTime;

    Toast toast;
    View toastView;

    private Button play;
    private MediaPlayer mediaPlayer;
    private int playPosition = -1;
    private boolean isDragging = false;
    String castNum;

    // 프로그레스바 진행률을 위해 생성해준 변수들
    private Runnable runnable;
    private Handler handler = new Handler();

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    String streamingRecordList;
    String[] streamingArray;
    String name;
    static final String mediaPlayerKey = "media_player";

    String personName;
    String personEmail;
    int firstplayNum;
    int playlistNum;

    boolean playCheck = false;
    boolean nowPlaying = false;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    SharedPreferences randomShared;
    SharedPreferences.Editor randomEditor;

    String fromSignUpNickName;
    String fromSharedNickName;

    ProgressDialog mProgressDialog;
    SeekBar mainSeekBar;

    public final String TAG = "[Main Activity]";

    public static Context mainCtx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "LifeCycle onCreate()");

        // final static Context
        mainCtx = this;

        toast = Toast.makeText(this, "\uD835\uDC29\uD835\uDC25\uD835\uDC1A\uD835\uDC32\uD835\uDC22\uD835\uDC27\uD835\uDC20 ♪", Toast.LENGTH_SHORT);
        toastView = toast.getView();
        toastView.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
        TextView toastMessage = toastView.findViewById(android.R.id.message);
        toastMessage.setTextColor(Color.WHITE);
        toastMessage.setTextSize(16);

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        // 받는 인텐트
        Intent intent = getIntent();
        fromSignUpNickName = intent.getStringExtra("nickName");

        // 쉐어드로부터 가져온 닉네임
        fromSharedNickName = shared.getString("id", "LOG IN");

        // TODO 예제 참고
        mediaPlayer = new MediaPlayer();
        //

        // LOGIN 버튼
        logIn = findViewById(R.id.logInButton);


        if (!fromSharedNickName.equals("LOG IN")) {
            logIn.setText(fromSharedNickName);
            Log.i("logIn.setText Check1 : ", fromSharedNickName);
        }


        // 쉐어드로부터 가져온 닉네임 비교해서 logIn 버튼 이름 설정
        if (fromSharedNickName.equals("LOG IN")) {
            logIn.setText(intent.getStringExtra("nickname"));
            Log.i(TAG, "fromSharedNickName String 값이 default값일 때");
        } else {
            logIn.setText(fromSharedNickName);
            Log.i("logIn.setText Check3 : ", fromSharedNickName);
            Log.i(TAG, "fromSharedNickName String 값을 쉐어드에서 가져왔을 때 : " + fromSharedNickName);
        }
        Log.i("[Main]", "login.getText.toString() : " + logIn.getText().toString());
        if (logIn.getText().toString().equals("null") || logIn.getText().toString().equals("")) {
            logIn.setText("LOG IN");
            Log.i("logIn.setText Check4 : ", logIn.getText().toString());

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


            if (fromSharedNickName.equals("LOG IN")) {
                editor.putString("id", personName);
                logIn.setText(personName);
                Log.i("logIn.setText Check5 : ", personName);
            } else {
                logIn.setText(fromSharedNickName);
                Log.i("logIn.setText Check6 : ", fromSharedNickName);
            }
            editor.commit();


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
        if (mediaPlayer.isPlaying()) {
            mainSeekBar.setVisibility(View.VISIBLE);
            playingTime.setVisibility(View.VISIBLE);
            toPlayTime.setVisibility(View.VISIBLE);
            comment.setVisibility(View.VISIBLE);
        }
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

                    Log.i(TAG, "버튼 이름이 [LOG IN]이 아닐 때 || 사용자 이름일 때");

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

        mainSeekBar = findViewById(R.id.mainSeekBar);
        playingTime = findViewById(R.id.mainPlayingTime);
        toPlayTime = findViewById(R.id.mainToPlayTime);
        if (!mediaPlayer.isPlaying()) {
            Log.i(TAG, "mainSeekBar.getVisibility() : " + mainSeekBar.getVisibility());
            Log.i(TAG, "playingTime.getVisibility() : " + playingTime.getVisibility());
            Log.i(TAG, "toPlayTime.getVisibility() : " + toPlayTime.getVisibility());
            Log.i(TAG, "comment.getVisibility() : " + comment.getVisibility());

//            mainSeekBar.setVisibility(View.INVISIBLE);
//            playingTime.setVisibility(View.INVISIBLE);
//            toPlayTime.setVisibility(View.INVISIBLE);
//            comment.setVisibility(View.INVISIBLE);
        }

//        mainSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                //TODO 시크바를 조작하고 있는 중
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                //TODO 시크바를 처음 터치했을 때
//                Log.i(TAG, "시크바를 터치했을 때 선택된 값 : " + seekBar.getProgress());
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                //TODO 시크바 터치가 끝났을 때
//            }
//        });

        // TODO 랜덤 넘버 추출
        randomNumber();

        // 메인 재생 버튼
        play = findViewById(R.id.mainPlayButton);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("메인 플레이 버튼 클릭", "");


//                if (mediaPlayer.isPlaying()) {
                mainSeekBar.setVisibility(View.VISIBLE);
                playingTime.setVisibility(View.VISIBLE);
                toPlayTime.setVisibility(View.VISIBLE);
                comment.setVisibility(View.VISIBLE);
                Log.i(TAG, "mainSeekBar.getVisibility() 2 : " + mainSeekBar.getVisibility());
                Log.i(TAG, "playingTime.getVisibility() 2 : " + playingTime.getVisibility());
                Log.i(TAG, "toPlayTime.getVisibility() 2 : " + toPlayTime.getVisibility());
                Log.i(TAG, "comment.getVisibility() 2 : " + comment.getVisibility());
//                }

                String playState = play.getText().toString();

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {


                    // 첫 재생시 재생목록 3개만 생성해보자 (현재 곡 개수 8개)
                    // 재생목록 어떻게 보여줄까?

                    if (!playCheck) {


                        Log.i("메인 플레이 버튼 클릭", "첫 재생");
                        Log.i(TAG, "playCheck : " + playCheck);


                        if (!playState.equals("❚❚")) {
                            Log.i("메인 플레이 버튼 클릭", "일시정지가 아닐 때");
                            play.setText("❚❚");
                            play.setTextSize(53);


                            // 직접 통신인데..
                            // num 보내고
                            Uri.Builder builder = new Uri.Builder()
                                    .appendQueryParameter("num", castNum);
                            String postParams = builder.build().getEncodedQuery();
                            new getJSONData().execute("http://54.180.155.66/" + "/file_sampling.php", postParams);


//                             get 방식 파라미터 추가
                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/file_sampling.php").newBuilder();
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
                                                        String[] songCut = songInfo.split("@@@");

                                                        String path = songCut[0];
                                                        Log.i(TAG, "String songInfo path 확인 : " + path);

                                                        String time = songCut[1];
                                                        Log.i(TAG, "String songInfo 확인 : " + time);

                                                        String[] songName = path.split("/");
                                                        name = songName[4];


                                                        Log.i(TAG, "songInfo name 확인 : " + name);
                                                        Log.i(TAG, "songInfo path 확인 : " + path);
                                                        Log.i(TAG, "songInfo time 확인 : " + time);

//                                                          경로 가져와서 음악 재생 시켜준 뒤
//                                                          초수 세팅

                                                        // TODO 4.close Player 조건 잘 세워야 함
//                                                        closePlayer();
//                                                        mediaPlayer = new MediaPlayer();
                                                        mediaPlayer.setLooping(true);
                                                        Log.i(TAG, "MediaPlayer 생성");

                                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                        Log.i(TAG, "mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)");


                                                        // 수정한 부분
                                                        String uri = "http://54.180.155.66/" + name;
                                                        Log.i(TAG, "file name from music table : " + uri);
                                                        //
                                                        play.setText("❚❚");

                                                        mediaPlayer.setDataSource(uri);
                                                        Log.i(TAG, "mediaPlayer.setDataSource(path)");


                                                        mediaPlayer.prepareAsync();
                                                        Log.i(TAG, "mediaPlayer.prepareAsync()");

                                                        // TODO
                                                        mainSeekBar.setMax(mediaPlayer.getDuration());
                                                        //

                                                        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                                                        Log.i(TAG, "mediaPlayer.setWakeMode");


//                                                        // TODO ADD for SeekBar Moving
                                                        if (mediaPlayer.isPlaying()) {
                                                            mediaPlayer.stop();
                                                            try {
                                                                mediaPlayer.prepare();
                                                            } catch (IllegalStateException e) {
                                                                e.printStackTrace();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            mediaPlayer.seekTo(0);

                                                            mainSeekBar.setProgress(0);
                                                        } else {
                                                            mediaPlayer.start();

                                                            Thread();
                                                        }
//                                                        if (mediaPlayer != null) {
//                                                            if (mediaPlayer.isPlaying()) {
//                                                                mainSeekBar.setMax(mediaPlayer.getDuration());
//                                                                mainSeekBar.setProgress(mediaPlayer.getCurrentPosition());
//                                                                play.setText("❚❚");
//                                                            }
//                                                        } else if (!mediaPlayer.isPlaying()) {
//                                                            mainSeekBar.setMax(mediaPlayer.getDuration());
//                                                            mainSeekBar.setProgress(mediaPlayer.getCurrentPosition());
//                                                            play.setText("▶");
//                                                        }
////

                                                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                            @Override
                                                            public void onPrepared(MediaPlayer mp) {
                                                                Log.i(TAG, "mediaPlayer.setOnPreparedListener");
                                                                mainSeekBar.setMax(mediaPlayer.getDuration());
                                                                mediaPlayer.start();
                                                                updateSeekBar();
//                                                                changeSeekbar();
                                                                Log.i(TAG, "mediaPlayer.start()");
                                                            }
                                                        });

//                                                        // TODO When SeekBar click, move to time from mp3 file
                                                        mainSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                            @Override
                                                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                                Log.i(TAG, "SeekBar onProgressChanged");
                                                                if (fromUser) {
                                                                    mediaPlayer.seekTo(progress);
                                                                }
                                                            }

                                                            //
                                                            @Override
                                                            public void onStartTrackingTouch(SeekBar seekBar) {
                                                                Log.i(TAG, "SeekBar onStartTrackingTouch");
                                                                isDragging = true;
                                                            }

                                                            @Override
                                                            public void onStopTrackingTouch(SeekBar seekBar) {
                                                                Log.i(TAG, "SeekBar onStopTrackingTouch");
                                                                isDragging = false;
                                                            }
                                                        });
//
//                                                        // TODO. END for SeekBar

                                                        // TODO TOAST
//                                                        Toast.makeText(getApplicationContext(), "♫", Toast.LENGTH_SHORT).show();
                                                        toast.show();

                                                        updateSeekBar();

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


                            if (mediaPlayer == null) {
                                Log.i(TAG, "> btn on Click (mp == null)");
//                    mediaPlayer = MediaPlayer.create(getApplicationContext()
//                            , R.raw.friendlikeme);
                                // TODO 원래 new 연산자 안 썼는데 null 떠서 우선 생성해줌
//                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.start();
                            } else if (!mediaPlayer.isPlaying()) {
                                mediaPlayer.seekTo(playPosition);
                                Log.i(TAG, "playPosition Check : " + playPosition);
                                mediaPlayer.start();
                            }

//                    resumeAudio() 일단 안 씀
//                    resumeAudio();

                        }
                        if (playState.equals("❚❚")) {
                            Log.i("메인 플레이 버튼 클릭", "일시정지 상태일 때");
                            Log.i(TAG, "playCheck : " + playCheck);

                            play.setText("▶");
                            play.setTextSize(53);

                            nowPlaying = false;

//                            seekBarMoving();
//                            updateSeekBar();

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
                    Log.i(TAG, "playCheck : " + playCheck);
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();

                }

                Log.i(TAG, "playCheck : " + playCheck);
            } // OnClick 메서드 닫아주는 중괄호
        });


        // 메인 오른쪽 화살표 버튼 (랜덤 플레이 버튼)
        rightPlayBtn = findViewById(R.id.rightPlayButton);
        rightPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                play.setText("▶");

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        play.setText("❚❚");
                        playCheck = true;
                    }
                });

                Log.i(TAG, "RightPlay 버튼 클릭");
                Log.i(TAG, "RightPlay-------------------------------------------");


                if (mediaPlayer.isPlaying() || !playCheck) {
                    Log.i(TAG, "mediaPlayer.isPlaying() || playCheck == false");
                    play.setText("▶"); //TODO GOOD!
                    if (play.getText().toString().equals("▶")) {
                        Log.i(TAG, "[RightPlay] 플레이 모양이 재생일 때");

                        onStopButtonClick();
                    }
                } else {
                    Log.i(TAG, "!mediaPlayer.isPlaying() || playCheck == true");
                }
            }
        });
        Log.i("[Random] mediaPlayer Check : ", String.valueOf(mediaPlayer));
        Log.i("[Random] playCheck : ", String.valueOf(playCheck));
    }

    public void Thread() {
        Log.i(TAG, "Thread Method Start");
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer.isPlaying()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mainSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }


    protected void onStart() {
        super.onStart();
        Log.i(TAG, "LifeCycle onStart()");


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
//            logIn.setText(personName);
            Log.i("logIn.setText Check7 : ", personName);
            logIn.setTextSize(13);
        } else {
            Log.i(TAG, "acct == null");
        }

    }


    protected void onResume() {
        super.onResume();
        Log.i(TAG, "LifeCycle onResume()");
        updateSeekBar();
    }

    protected void onPause() {
        super.onPause();
        Log.i(TAG, "LifeCycle onPause()");
        handler.removeCallbacksAndMessages(null);
    }

    protected void onStop() {
        super.onStop();
        Log.i(TAG, "LifeCycle onStop()");
        Log.i(TAG, "LifeCycle onStop() 로그인 액티비티에서 로그아웃 하고 돌아왔을 때");

//        if (savedInstanveStae != null) {
//
//        }
    }

    protected void onDestroy() {
        super.onDestroy();
//        stopMediaPlayer();
//        Log.i(TAG, "LifeCycle onDestroy()");
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            handler.removeCallbacksAndMessages(null);
//            mediaPlayer = null;
//        } else {
        // TODO Original
        mediaPlayer.release();
        mediaPlayer = null;
        handler.removeCallbacksAndMessages(null);
        //
//        }
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
        // TODO TOAST
//        Toast.makeText(this, "♫", Toast.LENGTH_SHORT).show();
        toast.show();
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

                // TODO. KAKAO id mysql -> 회원번호를 id 개념으로 *중복 제거 필 ㅎ
                String userNum = String.valueOf(user.getId());
                Log.i("[KAKAO user.getID *userNum]", "" + userNum);
                // TODO. KAKAO nickname -> id를 닉네임 개념으로 mysql에 추가
                String id = userEmailCut[0];
                Log.i("[KAKAO user.getEmail]", "" + user1.getEmail());
                Log.i("[KAKAO userID]", "" + id);

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {


                    // GET 방식 파라미터
                    HttpUrl.Builder builder = HttpUrl.parse("http://54.180.155.66/login_kakao.php").newBuilder();
                    builder.addQueryParameter("ver", "1.0");
                    String url = builder.build().toString();
                    Log.i(TAG, "String url Check : " + url);

                    // POST 방식 파라미터
                    RequestBody body = new FormBody.Builder()
                            .add("id", userNum.trim())
                            .add("nickname", id.trim())
                            .build();

                    // 요청 만들기
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    // 응답 CALL BACK
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            e.printStackTrace();
                            Log.i(TAG, "CALLBACK ERROR CHECK : ", e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            Log.i(TAG, "CALLBACK onResponse METHOD Start");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        if (!response.isSuccessful()) {
                                            Log.i(TAG, "응답 실패 : " + response);
                                            Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.i(TAG, "응답 성공 : " + response);
                                            final String responseData = response.body().string();
                                            Log.i(TAG, "응답 성공 responseData Check : " + responseData);

                                            if (responseData.equals("1")) {
                                                Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
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


                if (!id.equals(fromSharedNickName) && !fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(fromSharedNickName);
                    Log.i("logIn.setText Check8 : ", fromSharedNickName);
                    editor.putString("id", fromSharedNickName);
                    editor.commit();
                } else if (fromSharedNickName.equals(null) || fromSharedNickName.equals("LOG IN")) {
                    editor.putString("id", id);
                    editor.commit();
                } else if (!id.equals(fromSharedNickName) && fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(id);
                    Log.i("logIn.setText Check9 : ", id);
                    editor.putString("id", fromSharedNickName);
                    editor.commit();
                }


                if (!fromSharedNickName.equals(null) || !fromSharedNickName.equals("LOG IN")) {
                    editor.putString("id", fromSharedNickName);
                    editor.commit();
                }
                if (!id.equals(fromSharedNickName) && fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(id);
                    Log.i("logIn.setText Check10 : ", id);
                    editor.putString("id", id);
                    editor.commit();
                }


            }
            return null;

        });
    }

    void googleInsertTable() {
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            // get 방식 파라미터 추가
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/googleLogin.php").newBuilder();
            urlBuilder.addQueryParameter("ver", "1.0"); // 예시
            String url = urlBuilder.build().toString();
            Log.i("[Google]", "String url 확인 : " + url);

            // POST 파라미터 추가
            RequestBody formBody = new FormBody.Builder()
                    .add("id", personEmail)
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

//            mProgressDialog = new ProgressDialog(MainActivity.this);
//            mProgressDialog.setTitle("Music Streaming");
//            mProgressDialog.setMessage("Loading...");
//            mProgressDialog.setIndeterminate(false);
//            mProgressDialog.show();
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
//            mProgressDialog.dismiss();
        }
    }

//    private Runnable updateSeekBarRunnable = new Runnable() {
//        @Override
//        public void run() {
//            updateSeekBar();
//        }
//    };

    private void updateSeekBar() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentPosition = mediaPlayer.getCurrentPosition();
                mainSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                String progressText = String.format("%02d:%02d", currentPosition / 1000 / 60, currentPosition / 1000 % 60);
                playingTime.setText(progressText);
                updateSeekBar();
            }
        }, 1000);
    }

    public void onStopButtonClick() {
        Log.i(TAG, "[RightPlay] onStopButtonClick 메서드 작동");
        if (mediaPlayer.isPlaying() || playCheck == true) {
            Log.i(TAG, "[RightPlay] mediaPlayer.isPlaying");
            mediaPlayer.stop();
//            mediaPlayer.start();
//            play.setText("❚❚");
            playCheck = false;
//            playCheck = true;
            // changeStreaming이 있어야지만 노래를 바꿀 수 있어
            // 그런데 지금 과정이 뭔가 엉켜있어
            changeStreaming();

            mediaPlayer.start();
            play.setText("❚❚");
            playCheck = true;
            Log.i(TAG, "mediaPlayer.start() &&  playCheck : " + playCheck);

        } else {
            mediaPlayer.start();
            play.setText("▶");
            playCheck = true;
            Log.i(TAG, "[RightPlay] !mediaPlayer.isPlaying");
        }
    }


    public void changeStreaming() {


        Log.i(TAG, "[RightPlay] playcheck Check : " + playCheck);
        randomNumber();
        Log.i(TAG, "[RightPlay] -----------------------------------------------");


        Log.i(TAG, "[RightPlay] changeStreaming Method");
        Log.i(TAG, "[RightPlay] -----------------------------------------------");

        String playState = play.getText().toString();

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {


            if (playCheck == false) {


                Log.i("[RightPlay] 버튼 클릭", "재생");
                Log.i(TAG, "[RightPlay] playCheck : " + playCheck);
                Log.i(TAG, "[RightPlay] -----------------------------------------------");


                if (!playState.equals("❚❚")) {
                    Log.i("[RightPlay] 버튼 클릭", "일시정지가 아닐 때");
                    Log.i(TAG, "[RightPlay] -----------------------------------------------");
                    play.setText("❚❚");
                    play.setTextSize(53);


                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("num", castNum);
                    String postParams = builder.build().getEncodedQuery();
                    new getJSONData().execute("http://54.180.155.66/" + "/file_sampling.php", postParams);


//                             get 방식 파라미터 추가
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/file_sampling.php").newBuilder();
                    urlBuilder.addQueryParameter("ver", "1.0");
                    String url = urlBuilder.build().toString();
                    Log.i(TAG, "[RightPlay] String url 확인 : " + url);
                    Log.i(TAG, "[RightPlay] -----------------------------------------------");

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
                            Log.e(TAG, "[RightPlay] play callback onFailure : " + e);
                            Log.i(TAG, "[RightPlay] -----------------------------------------------");
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            Log.i(TAG, "[RightPlay] play callback onResponse");
                            Log.i(TAG, "[RightPlay] -----------------------------------------------");

                            // 서브 스레드 UI 변경할 경우 에러
                            // 메인 스레드 UI 설정
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        if (!response.isSuccessful()) {
                                            // 응답 실패
                                            Log.e("tag", "[RightPlay] 응답 실패 : " + response);
                                            Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();
                                            Log.i(TAG, "[RightPlay] -----------------------------------------------");
                                        } else {
                                            // 응답 성공
                                            Log.i("tag", "[RightPlay] 응답 성공");
                                            final String responseData = response.body().string().trim();
                                            Log.i("tag", "[RightPlay] responseData Check : " + responseData);
                                            Log.i(TAG, "[RightPlay] -----------------------------------------------");
                                            if (responseData.equals("1")) {
                                                Log.i("[Main]", "[RightPlay] responseData 가 1일 때 : " + responseData);
                                                Log.i(TAG, "[RightPlay] -----------------------------------------------");
                                                Toast.makeText(getApplicationContext(), "아이디 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.i("[Main]", "[RightPlay] responseData 가 1이 아닐 때 : " + responseData);
                                                Log.i(TAG, "[RightPlay] -----------------------------------------------");
//                                                        startActivityString(MainActivity.class, "nickname", responseData);

                                                String songInfo = responseData;
                                                Log.i(TAG, "[RightPlay] String songInfo 확인 : " + songInfo);
                                                Log.i(TAG, "[RightPlay] -----------------------------------------------");
                                                String[] songCut = songInfo.split("@@@");

                                                String path = songCut[0];
                                                String time = songCut[1];

                                                String[] songName = path.split("/");
                                                String name = songName[4];


                                                Log.i(TAG, "[RightPlay] song name 확인 : " + name);
                                                Log.i(TAG, "[RightPlay] song path 확인 : " + path);
                                                Log.i(TAG, "[RightPlay] song time 확인 : " + time);
                                                Log.i(TAG, "[RightPlay] -----------------------------------------------");

//                                                          경로 가져와서 음악 재생 시켜준 뒤
//                                                          초수 세팅

                                                // TODO 4.close Player 조건 잘 세워야 함
//                                                        closePlayer();
//                                                        mediaPlayer = new MediaPlayer();
                                                mediaPlayer.setLooping(true);
                                                Log.i(TAG, "[RightPlay] MediaPlayer 생성");

                                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                Log.i(TAG, "[RightPlay] mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)");


                                                // 수정한 부분
                                                String uri = "http://54.180.155.66/" + name;
                                                Log.i(TAG, "[RightPlay] file name from music table : " + uri);
                                                //

                                                mediaPlayer.setDataSource(uri);
//                                                play.setText("❚❚");
                                                Log.i(TAG, "[RightPlay] mediaPlayer.setDataSource(path)");


                                                mediaPlayer.prepareAsync();
                                                Log.i(TAG, "[RightPlay] mediaPlayer.prepareAsync()");

                                                // TODO
                                                mainSeekBar.setMax(mediaPlayer.getDuration());
                                                //

                                                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                                                Log.i(TAG, "[RightPlay] mediaPlayer.setWakeMode");
                                                Log.i(TAG, "[RightPlay] -----------------------------------------------");


//                                                        // TODO ADD for SeekBar Moving
                                                if (mediaPlayer.isPlaying()) {
                                                    mediaPlayer.stop();
                                                    try {
                                                        mediaPlayer.prepare();
                                                    } catch (IllegalStateException e) {
                                                        e.printStackTrace();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    mediaPlayer.seekTo(0);

                                                    mainSeekBar.setProgress(0);
                                                } else {
                                                    mediaPlayer.start();

                                                    Thread();
                                                }

                                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                    @Override
                                                    public void onCompletion(MediaPlayer mp) {
                                                        mainSeekBar.setProgress(0);
                                                    }
                                                });

                                                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                    @Override
                                                    public void onPrepared(MediaPlayer mp) {
                                                        Log.i(TAG, "[RightPlay] mediaPlayer.setOnPreparedListener");
                                                        mainSeekBar.setMax(mediaPlayer.getDuration());
                                                        mediaPlayer.start();
                                                        updateSeekBar();
//                                                                changeSeekbar();
                                                        Log.i(TAG, "[RightPlay] mediaPlayer.start()");
                                                        Log.i(TAG, "[RightPlay] -----------------------------------------------");
                                                    }
                                                });

//                                                        // TODO When SeekBar click, move to time from mp3 file
                                                mainSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                    @Override
                                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                        Log.i(TAG, "[RightPlay] SeekBar onProgressChanged");
                                                        Log.i(TAG, "[RightPlay] -----------------------------------------------");
                                                        if (fromUser) {
                                                            mediaPlayer.seekTo(progress);
                                                        }
                                                    }

                                                    //
                                                    @Override
                                                    public void onStartTrackingTouch(SeekBar seekBar) {
                                                        Log.i(TAG, "[RightPlay] SeekBar onStartTrackingTouch");
                                                        Log.i(TAG, "[RightPlay] -----------------------------------------------");
                                                        isDragging = true;
                                                    }

                                                    @Override
                                                    public void onStopTrackingTouch(SeekBar seekBar) {
                                                        Log.i(TAG, "[RightPlay] SeekBar onStopTrackingTouch");
                                                        Log.i(TAG, "[RightPlay] -----------------------------------------------");
                                                        isDragging = false;
                                                    }
                                                });
//
//                                                        // TODO. END for SeekBar


                                                // TODO TOAST
//                                                Toast.makeText(getApplicationContext(), "♫", Toast.LENGTH_SHORT).show();
                                                toast.show();

                                                updateSeekBar();

                                                songTime = findViewById(R.id.mainToPlayTime);
                                                songTime.setText(time);

                                                String[] exceptMp3 = name.split(".mp3");
                                                String justName = exceptMp3[0];
                                                // _ <- 이거를 공백으로 대체할 수 있을까?

                                                Log.i(TAG, "[RightPlay] song just name 확인 : " + justName);
                                                Log.i(TAG, "[RightPlay] -----------------------------------------------");


                                                mainLogo = findViewById(R.id.mainLogo);
                                                mainLogo.setText(justName);

                                                if (!responseData.equals(0)) {
//                                                            responserData " + " 기준으로 잘라줘야 해
                                                    Log.i("[RightPlay]", "responseData 가 0이 아닐 때 : " + responseData);
                                                    Log.i(TAG, "[RightPlay] -----------------------------------------------");


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

//                        첫 재생 playAudio
//                        playAudio();


                    // 재생 됐는지 체크
//                    playCheck = true;


                }


                // 아래 if (playCHeck == false 닫아주는 중괄호
            } else { // <-> if (playCheck == true

                if (!playState.equals("❚❚")) {
//                    Log.i("[RightPlay] 메인 플레이 버튼 클릭", "재시작");
//                    Log.i(TAG, "[RightPlay] playCheck : " + playCheck);
//                    play.setText("❚❚");


                    if (mediaPlayer == null) {
//                        Log.i(TAG, "> btn on Click (mp == null)");
////                    mediaPlayer = MediaPlayer.create(getApplicationContext()
////                            , R.raw.friendlikeme);
//                        // TODO 원래 new 연산자 안 썼는데 null 떠서 우선 생성해줌
////                                mediaPlayer = new MediaPlayer();
//                        mediaPlayer.start();
                    } else if (!mediaPlayer.isPlaying()) {
//                        mediaPlayer.seekTo(playPosition);
//                        Log.i(TAG, "playPosition Check : " + playPosition);
//                        mediaPlayer.start();
                    }

//                    resumeAudio() 일단 안 씀
//                    resumeAudio();

                } else if (playState.equals("❚❚")) {
                    Log.i("메인 플레이 버튼 클릭", "일시정지 상태일 때");
//                    Log.i(TAG, "playCheck : " + playCheck);
//
//                    play.setText("▶");
//                    play.setTextSize(60);
//
//
////                    pauseAudio() 일단 안 씀
////                    pauseAudio();
//
//
//                    if (mediaPlayer != null) {
//                        mediaPlayer.pause();
//                        playPosition = mediaPlayer.getCurrentPosition();
//                        Log.d("[PAUSE CHECK]", "" + playPosition);
//                        Log.i(TAG, "playCheck : " + playCheck);
//
//                    }
//
//                } // 재생 버튼이 일시정지 모양일 때
//                // + 예외 처리
//                else {
//                    Log.i(TAG, "재생 버튼 모양이 재생도 일시정지도 아님");
                }

            } // if (playCheck == true 닫아주는 중괄호


        } else {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();

        }

    }

    public void randomNumber() {

        Log.i(TAG, "randomNumber 메서드 실행");

        random = new Random();
        int rPlayList[] = new int[8];

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
        for (int c = 0; c < 8; c++) {
            Log.i(TAG, "Random Number For Playlist (2) : " + rPlayList[c]);

//                                playlistNum = playlistNum + rPlayList[c];
            numAdd = numAdd + rPlayList[c] + "-";
        }


        // TODO Shared에 넣어보자
//        if (bringInNumbers.equals("") || bringInNumbers == null) {
        randomShared = getSharedPreferences("randomNumber", MODE_PRIVATE);
        randomEditor = randomShared.edit();
        randomEditor.putString("randomNumbers", numAdd);
        Log.i(TAG, "numAdd Check : " + numAdd);
        randomEditor.apply();
        randomEditor.commit();

        String bringInNumbers = randomShared.getString("randomNumbers", "");
        Log.i(TAG, "randomNumbers bring in shared : " + bringInNumbers);
//        } else {
//            Log.i(TAG, "randomNumber -- after shared : " + bringInNumbers);
//        }
        // TODO 여기까지

        Log.i(TAG, "[Random] numAdd check : " + numAdd);
        Log.i(TAG, "[Random] -------------------------------");

        // 랜덤 숫자 1개 생성
        Log.i(TAG, "랜덤 추출 숫자 : " + firstplayNum);
        // 랜덤 숫자 4개 생성
        Log.i(TAG, "랜덤 추출 숫자들 : " + numAdd);

        castNum = Integer.toString(firstplayNum);
        // 랜덤 숫자 1개
        Log.i(TAG, "String castNum 확인 : " + castNum);
    }

//    private void stopMediaPlayer() {
//        mediaPlayer.stop();
//        mediaPlayer.release();
//        handler.removeCallbacks(updateSeekBarRunnable);
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (mediaPlayer != null) {
//            outState.putParcelable(mediaPlayerKey, (Parcelable) mediaPlayer);
//            outState.putString("media_title", name);
//            outState.putInt("media_progress", mediaPlayer.getCurrentPosition());
//        }
//    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        if (mediaPlayer != null) {
//            setTitle(name);
//            mediaPlayer.seekTo(savedInstanceState.getInt("media_progress"));
//            mediaPlayer.start();
//        }
//    }


    void seekBarMoving() {
        // TODO When SeekBar click, move to time from mp3 file
        if (!nowPlaying) {

//            mainSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                    Log.i(TAG, "[RightPlay] SeekBar onProgressChanged");
//                    Log.i(TAG, "[RightPlay] -----------------------------------------------");
//                    if (fromUser) {
//                        mediaPlayer.seekTo(progress);
//                    }
//                }
//
//                //
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                    Log.i(TAG, "[RightPlay] SeekBar onStartTrackingTouch");
//                    Log.i(TAG, "[RightPlay] -----------------------------------------------");
//                    isDragging = true;
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    Log.i(TAG, "[RightPlay] SeekBar onStopTrackingTouch");
//                    Log.i(TAG, "[RightPlay] -----------------------------------------------");
//                    isDragging = false;
//                }
//            });
////
////                                                        // TODO. END for SeekBar

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (mediaPlayer != null) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!isDragging) {
                            mainSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                        }
                    }
                }
            }).start();
        }
    }

}