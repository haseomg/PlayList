package com.example.playlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    Button upload;
    Button songList;

    TextView songTime;
    TextView mainLogo;
    TextView playingTime;
    TextView toPlayTime;

    Toast toast;
    View toastView;

    private Button play;
    private MediaPlayer mediaPlayer;
    private MediaPlayer reMediaPlayer;
    private int playPosition = -1;
    private boolean isDragging = false;
    private boolean isPlaying = false;
    String castNum;
    String artist;
    String name;
    String time;

    // 프로그레스바 진행률을 위해 생성해준 변수들
    private Runnable runnable;
    private Handler handler = new Handler();
    int currentPosition;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    String streamingRecordList;
    String[] streamingArray;
    static final String mediaPlayerKey = "media_player";

    String personName;
    String personEmail;
    int firstplayNum;
    int playlistNum;
    int num;
    int forRandomNumberCount = 0;
    String randomNumCheck;
    String firstRanNum = "";
    int firstRanNumToInt;
    int ranRanToInt;
    String nextRanNum;
    String leftPlay;

    Gif_Play gif;

    boolean playCheck = false;
    boolean nowPlaying = false;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    SharedPreferences randomShared;
    SharedPreferences.Editor randomEditor;

    SharedPreferences pastShared;
    SharedPreferences.Editor pastEditor;

    String fromSignUpNickName;
    String fromSharedNickName;
    String pastNumBox = "";
    String ranRan;

    ProgressDialog mProgressDialog;
    SeekBar mainSeekBar;

    LocationManager locationManager;
    String provider;
    static double lat, lng;
    int myPermission = 0;
    private static final String API_KEY = "5a620a6e8b9bbe71c32d3b1cf6f28455";
    private static final String WEATHER_URL =
            "https://api.openweathermap.org/data/2.5/weather?q={CITY_NAME}&appid={API_KEY}";


    public final String TAG = "[Main Activity]";

    public static Context mainCtx;
    androidx.constraintlayout.widget.ConstraintLayout mainLayout;
    androidx.constraintlayout.widget.ConstraintLayout mainPlayLayout;
    ImageView mainFull;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "LifeCycle onCreate()");

        // final static Context
        mainCtx = this;
        mainLayout = findViewById(R.id.mainLayout);
        mainPlayLayout = findViewById(R.id.mainPlayLayout);
        mainFull = findViewById(R.id.mainFull_ImageView);
        gif = new Gif_Play();

        toast = Toast.makeText(this, "\uD835\uDC29\uD835\uDC25\uD835\uDC1A\uD835\uDC32\uD835\uDC22\uD835\uDC27\uD835\uDC20 ♪", Toast.LENGTH_LONG);
        toastView = toast.getView();
        toastView.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
        TextView toastMessage = toastView.findViewById(android.R.id.message);
        toastMessage.setTextColor(Color.WHITE);
        toastMessage.setTextSize(16);

        shared = getSharedPreferences("nickname", MODE_PRIVATE);
        editor = shared.edit();

        randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
        randomEditor = randomShared.edit();
        randomNumCheck = randomShared.getString("randomNumbers", "");

        // TODO 랜덤 넘버 서버로부터 받아온당
        responseRandomNumbers();
        if (forRandomNumberCount == 0) {
            firstRanNum = randomNumCheck;
            Log.i(TAG, "forRandomNumberCount check : " + forRandomNumberCount);
            forRandomNumberCount++;
        }
        if (randomNumCheck.equals("") || randomNumCheck.length() == 0) {
            responseRandomNumbers();
        }

        // 받는 인텐트
        Intent intent = getIntent();
        fromSignUpNickName = intent.getStringExtra("nickName");

        // 쉐어드로부터 가져온 닉네임
        fromSharedNickName = shared.getString("nickname", "LOG IN");

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
                editor.putString("nickname", personName);
                logIn.setText(personName);
                Log.i("logIn.setText Check5 : ", personName);
            } else {
                logIn.setText(fromSharedNickName);
                Log.i("logIn.setText Check6 : ", fromSharedNickName);
            }
            editor.commit();


            // personEmail.setText
        }

        // 클릭 이벤트 시 재생목록 생성
        songList = findViewById(R.id.menuButton);
        songList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "재생목록 버튼 클릭");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("SongList See U Soon !");
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "songList Button onClick()");
                            }
                        });
                builder.show();
            }
        });


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

        mainLogo = findViewById(R.id.mainLogo);
//        weather();


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

        upload = findViewById(R.id.uploadButton);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "uploadButton onCLick()");
                Intent intent = new Intent(mainCtx, Upload.class);
                startActivity(intent);
            }
        });

        if (randomNumCheck.equals("") || randomNumCheck.length() == 0) {
            responseRandomNumbers();
        }

        // TODO 랜덤 넘버 추출
        Log.i(TAG, "ran shared check : " + randomNumCheck);
        if (randomNumCheck.length() > 0) {
            char first = randomNumCheck.charAt(0);
            firstRanNum = String.valueOf(first);
        }
//        firstRanNumToInt = Integer.parseInt(firstRanNum);
        Log.i(TAG, "ran first check : " + firstRanNum);
        ranRan = randomNumCheck;
        if (ranRan.length() > 0) {
            ranRan = ranRan.substring(1);
            Log.i(TAG, "ran substring check : " + ranRan);
//            ranRanToInt = Integer.parseInt(ranRan);
            randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
            randomEditor = randomShared.edit();
            randomEditor.putString("randomNumbers", ranRan);
            randomEditor.apply();
            randomEditor.commit();
            pastNumBox = pastNumBox + firstRanNum;
            Log.i(TAG, "Number : " + pastNumBox);
        }


        // 메인 재생 버튼
        play = findViewById(R.id.mainPlayButton);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("메인 플레이 버튼 클릭", "");

                if (logIn.getText().toString().equals("LOG IN")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Please Check the Log In");
                    builder.setMessage("로그인이 필요합니다.");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                } else {


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

                                // TODO 랜덤 넘버 추출
                                Log.i(TAG, "ran shared check : " + randomNumCheck);
                                if (randomNumCheck.length() > 0) {
                                    char first = randomNumCheck.charAt(0);
                                    firstRanNum = String.valueOf(first);
                                }
//        firstRanNumToInt = Integer.parseInt(firstRanNum);
                                Log.i(TAG, "ran first check : " + firstRanNum);
                                ranRan = randomNumCheck;
                                if (ranRan.length() > 0) {
                                    ranRan = ranRan.substring(1);
                                    Log.i(TAG, "ran substring check : " + ranRan);
//            ranRanToInt = Integer.parseInt(ranRan);
                                    randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
                                    randomEditor = randomShared.edit();
                                    randomEditor.putString("randomNumbers", ranRan);
                                    randomEditor.apply();
                                    randomEditor.commit();
                                    pastNumBox = pastNumBox + firstRanNum;
                                    Log.i(TAG, "Number : " + pastNumBox);
                                }


                                // 직접 통신인데..
                                // num 보내고
                                // TODO 랜덤
                                Uri.Builder builder = new Uri.Builder()
                                        .appendQueryParameter("num", "1" + firstRanNum);
                                String postParams = builder.build().getEncodedQuery();
                                new getJSONData().execute("http://54.180.155.66/" + "/file_sampling.php", postParams);


//                             get 방식 파라미터 추가
                                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/file_sampling.php").newBuilder();
                                urlBuilder.addQueryParameter("ver", "1.0");
                                String url = urlBuilder.build().toString();
                                Log.i(TAG, "String url 확인 : " + url);

                                // post 파라미터 추가
                                // TODO 랜덤
                                RequestBody formBody = new FormBody.Builder()
                                        .add("num", "1" + firstRanNum.trim())
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


                                                            // responseData를 받아서 num이랑 artist 잘라내고 songInfo 값 안에 넣어주자


                                                            String songInfo = responseData;
                                                            Log.i(TAG, "songInfo Check : " + songInfo);

                                                            String[] numCut = songInfo.split("___");
                                                            String num = numCut[0];
                                                            Log.i(TAG, "songInfo num Check : " + num);

                                                            String deleteNum = numCut[1];
                                                            String[] artistCut = deleteNum.split("###");
                                                            artist = artistCut[0];
                                                            Log.i(TAG, "songInfo artist Check : " + artist);

                                                            String deleteArtist = artistCut[1];
                                                            String[] pathCut = deleteArtist.split("@@@");
                                                            String path = pathCut[0];
                                                            Log.i(TAG, "songInfo path Check : " + path);

                                                            time = pathCut[1];
                                                            Log.i(TAG, "songInfo time Check : " + time);

                                                            String[] nameCut = path.split("/");
                                                            name = nameCut[4];
                                                            Log.i(TAG, "songInfo name Check : " + name);

                                                            insertIntoPastMusicTable();
//                                                            deleteFromMusicTable();

                                                            // TODO 4.close Player 조건 잘 세워야 함
//                                                        closePlayer();
//                                                        mediaPlayer = new MediaPlayer();
                                                            // TODO 연속 재생 시켜야 해서 한 곡 반복 해제
                                                            mediaPlayer.setLooping(false);
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
                                                            isPlaying = true;


                                                            mediaPlayer.prepareAsync();
                                                            Log.i(TAG, "mediaPlayer.prepareAsync()");

                                                            // TODO
                                                            mainSeekBar.setMax(mediaPlayer.getDuration());
                                                            //

                                                            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                                                            Log.i(TAG, "mediaPlayer.setWakeMode");


//                                                            gif.playing();
                                                            Glide.with(mainCtx)
                                                                    .asGif()
                                                                    .load(R.drawable.sea_gif)
                                                                    .centerCrop()
                                                                    .listener(new RequestListener<GifDrawable>() {
                                                                        @Override
                                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                                                            // GIF 파일 로드에 실패한 경우의 처리
                                                                            return false;
                                                                        }

                                                                        @Override
                                                                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                            // GIF 파일 로드에 성공한 경우의 처리
                                                                            resource.setLoopCount(GifDrawable.LOOP_FOREVER); // 반복 재생 설정
                                                                            resource.start(); // GIF 파일 재생 시작
                                                                            return false;
                                                                        }
                                                                    }).into(mainFull);
                                                            playingTime.setTextColor(Color.WHITE);
                                                            toPlayTime.setTextColor(Color.WHITE);
                                                            // TODO background
                                                            mainPlayLayout.setBackgroundColor(Color.parseColor("#00ff0000"));
                                                            play.setTextColor(Color.WHITE);
                                                            play.setAlpha(0.7f);
                                                            leftPlayBtn.setAlpha(0.7f);
                                                            rightPlayBtn.setAlpha(0.7f);
                                                            playingTime.setAlpha(0.7f);
                                                            toPlayTime.setAlpha(0.7f);

                                                            mainLogo.setAlpha(0.8f);
                                                            upload.setAlpha(0.8f);


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

                                                            // TODO 다시 활성화
                                                            songTime.setText(time);

                                                            String[] exceptMp3 = name.split(".mp3");
                                                            String justName = exceptMp3[0];
                                                            String reReName = justName.replace("_", " ");
                                                            // _ <- 이거를 공백으로 대체할 수 있을까?

                                                            Log.i(TAG, "song just name 확인 : " + reReName);

                                                            mainLogo.setText(reReName);

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
                            responseRandomNumbers();


                            // 아래 if (playCHeck == false 닫아주는 중괄호
                        } else { // <-> if (playCheck == true

                            if (!playState.equals("❚❚")) {
                                Log.i("메인 플레이 버튼 클릭", "재시작");
                                Log.i(TAG, "playCheck : " + playCheck);
                                mediaPlayer.start();
                                updateSeekBar();
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

                            } else {
                                // 재생 버튼이 일시정지 모양일 때
                                // + 예외 처리
                                Log.i(TAG, "재생 버튼 모양이 재생도 일시정지도 아님");
                            }

                        } // if (playCheck == true 닫아주는 중괄호


                    } else {
                        Log.i(TAG, "playCheck : " + playCheck);
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();

                    }
                } // First biggest else end


                Log.i(TAG, "playCheck : " + playCheck);
            } // OnClick 메서드 닫아주는 중괄호
        });


        // 메인 오른쪽 화살표 버튼 (랜덤 플레이 버튼)
        rightPlayBtn = findViewById(R.id.rightPlayButton);
        rightPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (logIn.getText().toString().equals("LOG IN")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("〈 Please Check the Log In 〉");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                } else {

                    if (play.getText().toString().equals("")) {

                        Log.i(TAG, "play의 모양이 아무것도 없을 때");

                    } else {

                        Log.i(TAG, "play의 모양이 아무것도 없지않을 때");

                        play.setText("▶");

                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                isPlaying = false;
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

                            Log.i(TAG, "mediaPlayer.isPlaying() || playCheck == false");
                            play.setText("▶"); //TODO GOOD!
                            if (play.getText().toString().equals("▶")) {
                                Log.i(TAG, "[RightPlay] 플레이 모양이 재생일 때");

                                onStopButtonClick();
                            }
                        }
                    }
                } // Big else End

            } // onClick End

        }); // setOnClickListener End

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

        // 메인 왼쪽 화살표 버튼 (직전 음악 재생)
        leftPlayBtn = findViewById(R.id.leftPlayButton);
        leftPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "leftPlay 버튼 클릭");

//                pastStreaming();

            }
        });

    }


    protected void onResume() {
        super.onResume();
        Log.i(TAG, "LifeCycle onResume()");
//        if (!isPlaying) {
//            Log.i(TAG, "change song check : " + mainSeekBar.getProgress());
//            reMediaPlayer = new MediaPlayer();
//            reMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mp) {
//                    Log.i(TAG, "change song onCompletion() ");
//                    // 다음곡을 재생하는 코드를 여기에 적어
//                    reMediaPlayer.reset();
//                    changeStreaming();
//                    reMediaPlayer.start();
//                    mainSeekBar.setProgress(0);
//                    updateSeekBar();
//
//                }
//            });

//        }
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

        if (logIn.getText().toString().equals("LOG IN")) {
            stopAudio();
        }

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
                    editor.putString("nickname", fromSharedNickName);
                    editor.commit();
                } else if (fromSharedNickName.equals(null) || fromSharedNickName.equals("LOG IN")) {
                    editor.putString("nickname", id);
                    editor.commit();
                } else if (!id.equals(fromSharedNickName) && fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(id);
                    Log.i("logIn.setText Check9 : ", id);
                    editor.putString("nickname", fromSharedNickName);
                    editor.commit();
                }


                if (!fromSharedNickName.equals(null) || !fromSharedNickName.equals("LOG IN")) {
                    editor.putString("nickname", fromSharedNickName);
                    editor.commit();
                }
                if (!id.equals(fromSharedNickName) && fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(id);
                    Log.i("logIn.setText Check10 : ", id);
                    editor.putString("nickname", id);
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
                currentPosition = mediaPlayer.getCurrentPosition();
                mainSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                String progressText = String.format("%02d:%02d", currentPosition / 1000 / 60, currentPosition / 1000 % 60);
                playingTime.setText(progressText);
                updateSeekBar();
            }
        }, 1000);
        if (mainSeekBar.getProgress() == 0) {
            // 다음 음악 재생
            Log.i(TAG, "mainSeekBar.getProgress Check : " + mainSeekBar.getProgress());
        }
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
//TODO randomNumber
        //        randomNumber();
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

                    if (randomNumCheck.equals("") || randomNumCheck.length() == 0) {
                        responseRandomNumbers();
                    }


                    String ranRanRan = randomShared.getString("randomNumbers", "");
                    if (ranRanRan.length() != 0) {
                        if (randomNumCheck.length() > 0) {
                            char next = ranRanRan.charAt(0);
                            nextRanNum = String.valueOf(next);
                        }
//                    firstRanNumToInt = Integer.parseInt(firstRanNum);
                        Log.i(TAG, "ran now check : " + nextRanNum);
//                    ranRanRan = randomNumCheck;
                        if (ranRanRan.length() > 0) {
                            ranRanRan = ranRanRan.substring(1);
                            Log.i(TAG, "ran nums substring(1) check : " + ranRanRan);
//                        ranRanToInt = Integer.parseInt(ranRan);
                            randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
                            randomEditor = randomShared.edit();
                            randomEditor.putString("randomNumbers", ranRanRan);
                            randomEditor.apply();
                            randomEditor.commit();
                            pastNumBox = nextRanNum + pastNumBox;
                            Log.i(TAG, "ran past nums : " + pastNumBox);
                        }
                    }

                    if (ranRanRan.length() == 0 || ranRanRan.equals("")) {
                        responseRandomNumbers();
//                        nextRanNum = "1";
//                        String reRan = randomShared.getString("randomNumbers", "");
                        String reRan = randomNumCheck;
                        Log.i(TAG, "ran reRan check : " + reRan);
                        char reNext = reRan.charAt(0);
                        nextRanNum = String.valueOf(reNext);
                        Log.i(TAG, "ran now check : " + nextRanNum);
                        reRan = reRan.substring(1);
                        Log.i(TAG, "ran nums substring(1) check : " + reRan);
//                        ranRanToInt = Integer.parseInt(ranRan);
                        randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
                        randomEditor = randomShared.edit();
                        randomEditor.putString("randomNumbers", reRan);
                        randomEditor.apply();
                        randomEditor.commit();
                        pastNumBox = reNext + pastNumBox;
                        Log.i(TAG, "ran past  nums : " + pastNumBox);
                        if (reRan.length() > 0) {
                            String reRanRan = randomShared.getString("randomNumbers", "");
                            Log.i(TAG, "ran reRanRan check : " + reRanRan);
                            char next = reRanRan.charAt(0);
                            nextRanNum = String.valueOf(next);
                            Log.i(TAG, "ran now check : " + nextRanNum);
                            reRanRan = reRanRan.substring(1);
                            Log.i(TAG, "ran nums substring(1) check : " + reRanRan);
//                        ranRanToInt = Integer.parseInt(ranRan);
                            randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
                            randomEditor = randomShared.edit();
                            randomEditor.putString("randomNumbers", reRanRan);
                            randomEditor.apply();
                            randomEditor.commit();
                            pastNumBox = next + pastNumBox;
                            Log.i(TAG, "ran past  nums : " + pastNumBox);
                        }
                    }
                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("num", "1" + nextRanNum);
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
                            .add("num", "1" + nextRanNum.trim())
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
                                                Log.i(TAG, "[RightPlay] songInfo Check : " + songInfo);

                                                String[] numCut = songInfo.split("___");
                                                String num = numCut[0];
                                                Log.i(TAG, "[RightPlay]songInfo num Check : " + num);

                                                String deleteNum = numCut[1];
                                                String[] artistCut = deleteNum.split("###");
                                                artist = artistCut[0];
                                                Log.i(TAG, "[RightPlay]songInfo artist Check : " + artist);

                                                String deleteArtist = artistCut[1];
                                                String[] pathCut = deleteArtist.split("@@@");
                                                String path = pathCut[0];
                                                Log.i(TAG, "[RightPlay]songInfo path Check : " + path);

                                                time = pathCut[1];
                                                Log.i(TAG, "[RightPlay]songInfo time Check : " + time);

                                                String[] nameCut = path.split("/");
                                                name = nameCut[4];
                                                String reName = name.replace("_", " ");
                                                Log.i(TAG, "[RightPlay]songInfo name Check : " + name);

                                                Log.i(TAG, "[RightPlay] -----------------------------------------------");

//                                                          경로 가져와서 음악 재생 시켜준 뒤
//                                                          초수 세팅

                                                // TODO 4.close Player 조건 잘 세워야 함
//                                                        closePlayer();
//                                                        mediaPlayer = new MediaPlayer();
                                                mediaPlayer.setLooping(false);
                                                Log.i(TAG, "[RightPlay] MediaPlayer 생성");

                                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                Log.i(TAG, "[RightPlay] mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)");


                                                // 수정한 부분
                                                String uri = "http://54.180.155.66/" + name;
                                                Log.i(TAG, "[RightPlay] file name from music table : " + uri);
                                                //

                                                mediaPlayer.setDataSource(uri);

                                                isPlaying = true;
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

//                                                gif.playing();
                                                Glide.with(mainCtx)
                                                        .asGif()
                                                        .load(R.drawable.sea_gif)
                                                        .centerCrop()
                                                        .listener(new RequestListener<GifDrawable>() {
                                                            @Override
                                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                                                // GIF 파일 로드에 실패한 경우의 처리
                                                                return false;
                                                            }

                                                            @Override
                                                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                // GIF 파일 로드에 성공한 경우의 처리
                                                                resource.setLoopCount(GifDrawable.LOOP_FOREVER); // 반복 재생 설정
                                                                resource.start(); // GIF 파일 재생 시작
                                                                return false;
                                                            }
                                                        }).into(mainFull);
                                                playingTime.setTextColor(Color.WHITE);
                                                toPlayTime.setTextColor(Color.WHITE);
                                                // TODO background
                                                mainPlayLayout.setBackgroundColor(Color.parseColor("#00ff0000"));
                                                play.setTextColor(Color.WHITE);
                                                play.setAlpha(0.7f);
                                                leftPlayBtn.setAlpha(0.7f);
                                                rightPlayBtn.setAlpha(0.7f);
                                                playingTime.setAlpha(0.7f);
                                                toPlayTime.setAlpha(0.7f);

                                                mainLogo.setAlpha(0.8f);
                                                upload.setAlpha(0.8f);

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
                                                        // 다음곡을 재생하는 코드를 여기에 적어
                                                        isPlaying = false;
//                                                        rightPlayBtn.performClick();
//                                                        mediaPlayer.reset();
//                                                        changeStreaming();
//                                                        mediaPlayer.start();
//                                                        mainSeekBar.setProgress(0);
//                                                        updateSeekBar();

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
                                                String reReName = justName.replace("_", " ");
                                                // _ <- 이거를 공백으로 대체할 수 있을까?

                                                Log.i(TAG, "[RightPlay] song just name 확인 : " + reReName);
                                                Log.i(TAG, "[RightPlay] -----------------------------------------------");


                                                mainLogo = findViewById(R.id.mainLogo);
                                                mainLogo.setText(reReName);

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

    // TODO randomNumber Method
    public void randomNumber() {

        randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
        randomEditor = randomShared.edit();

        String bringInNumbers = randomShared.getString("randomNumbers", "");
        Log.i(TAG, "randomNumbers bring in shared : " + bringInNumbers);

//        if (bringInNumbers != "" || bringInNumbers != null) {
//
//        } else {
        Log.i(TAG, "randomNumber 메서드 실행");

        random = new Random();
        int rPlayList[] = new int[8];

        for (int i = 0; i < 8; i++) {
            rPlay = random.nextInt(8) + 1;
            Log.i(TAG, "Random Number For Music Play : " + rPlay);


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
//            }


            // TODO Random Numbers Shared에 넣어보자

            randomEditor.putString("randomNumbers", numAdd);
            Log.i(TAG, "numAdd Check : " + numAdd);
            randomEditor.apply();
            randomEditor.commit();

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
    }


    private void insertIntoPastMusicTable() {
        // num은 nono
        // name, artist, time만 pastMusicTable에 넣어줄 것이다.
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

            HttpUrl.Builder builder = HttpUrl.parse("http://54.180.155.66/insert_pastMusic.php").newBuilder();
            builder.addQueryParameter("ver", "1.0");
            String url = builder.build().toString();
            Log.i(TAG, "insertPastMusicTable String url Check : " + url);

            // POST Parameter Add
            RequestBody body = new FormBody.Builder()
                    .add("name", name)
                    .add("artist", artist)
                    .add("time", time)
                    .build();

            // Request Add
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // CallBack
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("insertIntoMusicTable", "" + e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.i("insertIntoMusicTable", "onResponse Method");

                    // 서브 스레드 - UI 변경할 경우 에러
                    // 메인 스레드 - UI 설정
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!response.isSuccessful()) {
                                    Log.i(TAG, "insertIntoMusicTB 응답 실패 시 response Check : " + response);

                                } else {
                                    Log.i(TAG, "insertIntoMusicTB 응답 성공 시 response Check : " + response);
                                    final String responseData = response.body().string();
                                    Log.i(TAG, "insertIntoMusicTB 응답 성공 시 responseData : " + responseData);

                                    if (responseData.equals("1")) {
                                        Log.i(TAG, "insertIntoMusicTB responseData가 1일 때");
                                    } else {
                                        Log.i(TAG, "insertIntoMusicTB responseData가 1이 아닐 때");
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
    }

    private void deleteFromMusicTable() {
        // 내가 들은 음악 뮤직 테이블에서 삭제되게 하기
        // name을 php로 보내
        String deleteName = name;
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

            HttpUrl.Builder builder = HttpUrl.parse("http://54.180.155.66/delete_Music.php").newBuilder();
            builder.addQueryParameter("ver", "1.0");
            String url = builder.build().toString();
            Log.i(TAG, "delete_Music  String url Check : " + url);

            // POST Parameter Add
            RequestBody body = new FormBody.Builder()
                    .add("name", name)
                    .build();

            // Request Add
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            // CallBack
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.i("d", "" + e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.i("delete_Music", "onResponse Method");

                    // 서브 스레드 - UI 변경할 경우 에러
                    // 메인 스레드 - UI 설정
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!response.isSuccessful()) {
                                    Log.i(TAG, "delete_Music 응답 실패 시 response Check : " + response);

                                } else {
                                    Log.i(TAG, "delete_Music 응답 성공 시 response Check : " + response);
                                    final String responseData = response.body().string();
                                    Log.i(TAG, "delete_Music 응답 성공 시 responseData : " + responseData);

                                    if (responseData.equals("1")) {
                                        Log.i(TAG, "delete_Music responseData가 1일 때");
                                    } else {
                                        // 계속 responseData == 0
                                        // php file하고 같이 확인 필요함
                                        Log.i(TAG, "delete_Music responseData가 1이 아닐 때");
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

    // TODO weather
    public void weather() {
        OkHttpClient client = new OkHttpClient();

        String city = "Suwon";
        String url = WEATHER_URL.replace("{CITY_NAME}", city).replace("{API_KEY}", API_KEY);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("response Check : " + response);
                }
                String responseData = response.body().string();
                Log.i(TAG, "responseData Check : " + responseData);
                // TODO WEATHER
                mainLogo.setText("날씨 : " + responseData);
            }
        });
    }


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

    public void responseRandomNumbers() {
        Log.i(TAG, "bringGetSongInfo Method");

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder builder = HttpUrl.parse("http://54.180.155.66/create_random_numbers.php").newBuilder();
        builder.addQueryParameter("ver", "1.0");
        String url = builder.build().toString();
        Log.i(TAG, "bringGet String url check : " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String result = response.body().string();
                    Log.i("bringGet", "response check : " + result);
//                    num = Integer.parseInt(result);
                    randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
                    randomEditor = randomShared.edit();
                    String bringRanNum = randomShared.getString("randomNumbers", "");
                    Log.i(TAG, "ran bring check : " + bringRanNum);
                    if (bringRanNum.equals("") || bringRanNum.equals(null)) {
                        randomEditor.putString("randomNumbers", result);
                        Log.i(TAG, "numAdd Check : " + result);
                        randomEditor.apply();
                        randomEditor.commit();
                        randomNumCheck = bringRanNum;
                        Log.i("bringGet", "num check : " + num);
                    } else {
                        Log.i(TAG, "[else] bringGetShared RanNum Check : " + bringRanNum);
                    }
                    randomNumCheck = bringRanNum;
                    Log.i(TAG, "ran bring check : " + randomNumCheck);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // nothing
                        }
                    });
                }
            }
        });

        class GetNumber extends AsyncTask<String, Void, Integer> {

            @Override
            protected Integer doInBackground(String... urls) {
                Integer number = null;
                try {
                    URL url = new URL(urls[0]);
                    HttpURLConnection urlConnection
                            = (HttpURLConnection) url.openConnection();
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        number = Integer.parseInt(reader.readLine());
                    } finally {
                        urlConnection.disconnect();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return number;
            }

            protected void onPostExecute(Integer result) {
                int number = result;
                Log.i(TAG, "bringGet number check : " + number);
            }


        }
    }

    public void pastStreaming() {

        String playState = play.getText().toString();

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {


            if (!playCheck) {


                Log.i("[LeftPlay] 버튼 클릭", "재생");
                Log.i(TAG, "[LeftPlay] playCheck : " + playCheck);
                Log.i(TAG, "[LeftPlay] -----------------------------------------------");


                Log.i("[LeftPlay] 버튼 클릭", "일시정지가 아닐 때");
                Log.i(TAG, "[LeftPlay] -----------------------------------------------");
                play.setText("❚❚");
                play.setTextSize(53);

                // pastNumBox의 앞숫자부터 가져와서 틀어줄 건데
                // 틀때마다 앞숫자 잘라줘야함
                if (pastNumBox.length() != 0) {
                    char cutFirst = pastNumBox.charAt(0);
                    // pastNumPlay로 재생
                    String pastNumPlay = String.valueOf(cutFirst);
                    // 듣고 나서 잘라 준 것
                    leftPlay = pastNumPlay.substring(0);
                    pastShared = getSharedPreferences("pastNumbers", MODE_PRIVATE);
                    pastEditor = pastShared.edit();
                    pastEditor.putString("pastNumbers", pastNumBox);
                    pastEditor.putString("pastNumber", leftPlay);
                    pastEditor.apply();
                    pastEditor.commit();
                    // left play로 재생
                } else {

                }
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("num", leftPlay);
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
                        .add("num", leftPlay.trim())
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
                        Log.e(TAG, "[LeftPlay] play callback onFailure : " + e);
                        Log.i(TAG, "[LeftPlay] -----------------------------------------------");
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.i(TAG, "[LeftPlay] play callback onResponse");
                        Log.i(TAG, "[LeftPlay] -----------------------------------------------");

                        // 서브 스레드 UI 변경할 경우 에러
                        // 메인 스레드 UI 설정
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {

                                    if (!response.isSuccessful()) {
                                        // 응답 실패
                                        Log.e("tag", "[LeftPlay] 응답 실패 : " + response);
                                        Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();
                                        Log.i(TAG, "[LeftPlay] -----------------------------------------------");
                                    } else {
                                        // 응답 성공
                                        Log.i("tag", "[LeftPlay] 응답 성공");
                                        final String responseData = response.body().string().trim();
                                        Log.i("tag", "[LeftPlay] responseData Check : " + responseData);
                                        Log.i(TAG, "[LeftPlay] -----------------------------------------------");
                                        if (responseData.equals("1")) {
                                            Log.i("[Main]", "[LeftPlay] responseData 가 1일 때 : " + responseData);
                                            Log.i(TAG, "[LeftPlay] -----------------------------------------------");
                                            Toast.makeText(getApplicationContext(), "아이디 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Log.i("[Main]", "[LeftPlay] responseData 가 1이 아닐 때 : " + responseData);
                                            Log.i(TAG, "[LeftPlay] -----------------------------------------------");
//                                                        startActivityString(MainActivity.class, "nickname", responseData);

                                            String songInfo = responseData;
                                            Log.i(TAG, "[LeftPlay] songInfo Check : " + songInfo);

                                            String[] numCut = songInfo.split("___");
                                            String num = numCut[0];
                                            Log.i(TAG, "[LeftPlay]songInfo num Check : " + num);

                                            String deleteNum = numCut[1];
                                            String[] artistCut = deleteNum.split("###");
                                            artist = artistCut[0];
                                            Log.i(TAG, "[LeftPlay]songInfo artist Check : " + artist);

                                            String deleteArtist = artistCut[1];
                                            String[] pathCut = deleteArtist.split("@@@");
                                            String path = pathCut[0];
                                            Log.i(TAG, "[LeftPlay]songInfo path Check : " + path);

                                            time = pathCut[1];
                                            Log.i(TAG, "[LeftPlay]songInfo time Check : " + time);

                                            String[] nameCut = path.split("/");
                                            name = nameCut[4];
                                            Log.i(TAG, "[LeftPlay]songInfo name Check : " + name);

                                            Log.i(TAG, "[LeftPlay] -----------------------------------------------");

                                            mediaPlayer.setLooping(false);
                                            Log.i(TAG, "[LeftPlay] MediaPlayer 생성");

                                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                            Log.i(TAG, "[LeftPlay] mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)");


                                            // 수정한 부분
                                            String uri = "http://54.180.155.66/" + name;
                                            Log.i(TAG, "[LeftPlay] file name from music table : " + uri);

                                            mediaPlayer.setDataSource(uri);
                                            isPlaying = true;
                                            Log.i(TAG, "[LeftPlay] mediaPlayer.setDataSource(path)");


                                            mediaPlayer.prepareAsync();
                                            Log.i(TAG, "[LeftPlay] mediaPlayer.prepareAsync()");

                                            // TODO
                                            mainSeekBar.setMax(mediaPlayer.getDuration());
                                            //

                                            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                                            Log.i(TAG, "[LeftPlay] mediaPlayer.setWakeMode");
                                            Log.i(TAG, "[LeftPlay] -----------------------------------------------");

//                                            gif.playing();
                                            Glide.with(mainCtx)
                                                    .asGif()
                                                    .load(R.drawable.sea_gif)
                                                    .centerCrop()
                                                    .listener(new RequestListener<GifDrawable>() {
                                                        @Override
                                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                                            // GIF 파일 로드에 실패한 경우의 처리
                                                            return false;
                                                        }

                                                        @Override
                                                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                                            // GIF 파일 로드에 성공한 경우의 처리
                                                            resource.setLoopCount(GifDrawable.LOOP_FOREVER); // 반복 재생 설정
                                                            resource.start(); // GIF 파일 재생 시작
                                                            return false;
                                                        }
                                                    }).into(mainFull);
                                            playingTime.setTextColor(Color.WHITE);
                                            toPlayTime.setTextColor(Color.WHITE);
                                            // TODO background
                                            mainPlayLayout.setBackgroundColor(Color.parseColor("#00ff0000"));
                                            play.setTextColor(Color.WHITE);
                                            play.setAlpha(0.7f);
                                            leftPlayBtn.setAlpha(0.7f);
                                            rightPlayBtn.setAlpha(0.7f);
                                            playingTime.setAlpha(0.7f);
                                            toPlayTime.setAlpha(0.7f);

                                            mainLogo.setAlpha(0.8f);
                                            upload.setAlpha(0.8f);

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
                                                    // 다음곡을 재생하는 코드를 여기에 적어
                                                    isPlaying = false;
                                                    mainSeekBar.setProgress(0);
//                                                        rightPlayBtn.performClick();
                                                }
                                            });

                                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                                @Override
                                                public void onPrepared(MediaPlayer mp) {
                                                    Log.i(TAG, "[LeftPlay] mediaPlayer.setOnPreparedListener");
                                                    mainSeekBar.setMax(mediaPlayer.getDuration());
                                                    mediaPlayer.start();
                                                    updateSeekBar();
//                                                                changeSeekbar();
                                                    Log.i(TAG, "[LeftPlay] mediaPlayer.start()");
                                                    Log.i(TAG, "[LeftPlay] -----------------------------------------------");
                                                }
                                            });


//                                                        // TODO When SeekBar click, move to time from mp3 file
                                            mainSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                @Override
                                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                    Log.i(TAG, "[LeftPlay] SeekBar onProgressChanged");
                                                    Log.i(TAG, "[LeftPlay] -----------------------------------------------");
                                                    if (fromUser) {
                                                        mediaPlayer.seekTo(progress);
                                                    }
                                                }

                                                //
                                                @Override
                                                public void onStartTrackingTouch(SeekBar seekBar) {
                                                    Log.i(TAG, "[LeftPlay] SeekBar onStartTrackingTouch");
                                                    Log.i(TAG, "[LeftPlay] -----------------------------------------------");
                                                    isDragging = true;
                                                }

                                                @Override
                                                public void onStopTrackingTouch(SeekBar seekBar) {
                                                    Log.i(TAG, "[LeftPlay] SeekBar onStopTrackingTouch");
                                                    Log.i(TAG, "[LeftPlay] -----------------------------------------------");
                                                    isDragging = false;
                                                }
                                            });
                                            toast.show();


                                            updateSeekBar();

                                            songTime = findViewById(R.id.mainToPlayTime);
                                            songTime.setText(time);

                                            String[] exceptMp3 = name.split(".mp3");
                                            String justName = exceptMp3[0];
                                            String reReName = justName.replace("_", " ");
                                            // _ <- 이거를 공백으로 대체할 수 있을까?

                                            Log.i(TAG, "[LeftPlay] song just name 확인 : " + reReName);
                                            Log.i(TAG, "[LeftPlay] -----------------------------------------------");


                                            mainLogo = findViewById(R.id.mainLogo);
                                            mainLogo.setText(reReName);

                                            if (!responseData.equals(0)) {
//                                                            responserData " + " 기준으로 잘라줘야 해
                                                Log.i("[LeftPlay]", "responseData 가 0이 아닐 때 : " + responseData);
                                                Log.i(TAG, "[LeftPlay] -----------------------------------------------");


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


                // 아래 if (playCHeck == false 닫아주는 중괄호
            } else { // <-> if (playCheck == true

//                if (!playState.equals("❚❚")) {튼
//
//                } else if (playState.equals("❚❚")) {
//                    Log.i("LeftPlay 버튼 클릭", "일시정지 상태일 때");
//                }

            } // if (playCheck == true 닫아주는 중괄호


        } else {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();

        }
    }

}