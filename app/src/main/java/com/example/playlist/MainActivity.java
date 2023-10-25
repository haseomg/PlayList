package com.example.playlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    int rPlay;
    Random random;
    Button leftPlayBtn;
    Button rightPlayBtn;

    Button logIn;
    Button select;
    Button upload;
    Button comment;
    ImageView songList;
    ImageView chatList;
    ImageView heart, likedUser;
    boolean isHeartFilled = false;
    boolean isNowPlaying;

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
    long startTime;
    long totalPlayTime = 0;

    String castNum;
    String artist;
    String name;
    String pastSongName;
    String time;
    String now_song;
    String played = "";
    String selected_song;
    String nowPlayingStatus;

    // 프로그레스바 진행률을 위해 생성해준 변수들
    private Runnable runnable;
    private Handler handler = new Handler();
    int currentPosition;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    String personName, personEmail;

    ArrayList<CommentModel> commentList = new ArrayList<>();
    CommentAdapter commentAdapter;
    RecyclerView commentView;
    LinearLayoutManager commentLayoutManager;
    String getUserName, getSongName, getSelectedTime, getMsg;

    ArrayList<PlayedModel> playedList = new ArrayList<>();
    ServerApi serverApi;
    AllSongsModel allSongsModel;

    int firstplayNum;
    int playlistNum;
    int num;
    int forRandomNumberCount = 0;
    String randomNumCheck;
    String firstRanNum = "";
    String nextRanNum;
    String leftPlay;
    String reName;

    Gif_Play gif;

    boolean playCheck = false;
    boolean nowPlaying = false;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    SharedPreferences randomShared;
    SharedPreferences.Editor randomEditor;

    SharedPreferences pastShared;
    SharedPreferences.Editor pastEditor;

    SharedPreferences playedListShared;
    SharedPreferences.Editor playedListEditor;

    SharedPreferences pastSongisPlayingCheckShared;
    SharedPreferences.Editor pastSongisPlayingCheckEditor;

    SharedPreferences forBeforeAndNextShared;
    SharedPreferences.Editor forBeforeAndNextEditor;

    String fromSignUpNickName;
    String fromSharedNickName;
    String pastNumBox = "";
    String ranRan;

    PreferenceManager nowPlayingPreference;
    String nowPlayingStr;

    SeekBar mainSeekBar;
    String gifName;
    int gifCount = 0;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 적절한 쿼리를 실행하여 데이터베이스를 버전 2로 업데이트합니다.
        }
    };

    LocationManager locationManager;
    String provider;
    static double lat, lng;
    int myPermission = 0;
    private static final String API_KEY = "5a620a6e8b9bbe71c32d3b1cf6f28455";
    private static final String WEATHER_URL =
            "https://api.openweathermap.org/data/2.5/weather?q={CITY_NAME}&appid={API_KEY}";

    public static Context mainCtx;
    androidx.constraintlayout.widget.ConstraintLayout mainLayout;
    androidx.constraintlayout.widget.ConstraintLayout mainPlayLayout;
    ImageView mainFull;

    static final String BASE_URL = "http://54.180.155.66/";
    int syncInterval = 3000; // 코멘트 동기화 간격 3초
    static final int TIME_INTERVAL = 1;
    // 3초 간격
    static final int TIME_RANGE = 3;
    Handler syncHandler;
    Runnable syncRunnable;
    long currentTimeForViewsIncrement;

    public final String TAG = "[Main Activity]";
    private int totalPlayTimeSecondsForViewsCount;
    ArrayList<UpdateLikedModel> selectLikedList = new ArrayList<>();
    private String rePastSongName;
    String reSongName;

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
        serverApi = ApiClient.getApiClient().create(ServerApi.class);

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

        playedListShared = getSharedPreferences("played_list", MODE_PRIVATE);
        playedListEditor = playedListShared.edit();

        pastSongisPlayingCheckShared = getSharedPreferences("past_song", MODE_PRIVATE);
        pastSongisPlayingCheckEditor = pastSongisPlayingCheckShared.edit();

        forBeforeAndNextShared = getSharedPreferences("checking_timing", MODE_PRIVATE);
        forBeforeAndNextEditor = forBeforeAndNextShared.edit();

        // TODO 랜덤 넘버 서버로부터 받아온당
        responseRandomNumbers();
        if (forRandomNumberCount == 0) {
            firstRanNum = randomNumCheck;
            Log.i(TAG, "forRandomNumberCount check : " + forRandomNumberCount);
            forRandomNumberCount++;
        } // for

        if (randomNumCheck.equals("") || randomNumCheck.length() == 0) {
            responseRandomNumbers();
        } // if
        // 받는 인텐트
        Intent intent = getIntent();
        if (intent != null) {
            String notificationData = intent.getStringExtra("test");
            if (notificationData != null)
                Log.d("FCM_TEST", notificationData);
        } // if

//        String token = FirebaseMessaging.getInstance().getToken().getResult();
//        Log.e(TAG, "fcm token : " + token);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        System.out.println("token : " + token);
//                        Log.e(TAG, "token : " + token);
//                        Toast.makeText(MainActivity.this, "Your device registration token is" + token
//                                , Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                Log.i(TAG, "fcm onSuccess : " + token);
            }
        });

        fromSignUpNickName = intent.getStringExtra("nickName");

        // 쉐어드로부터 가져온 닉네임
        fromSharedNickName = shared.getString("nickname", "LOG IN");

        // TODO 예제 참고
        mediaPlayer = new MediaPlayer();
        // LOGIN 버튼
        logIn = findViewById(R.id.logInButton);

        if (!fromSharedNickName.equals("LOG IN")) {
            logIn.setText(fromSharedNickName);
            Log.i("logIn.setText Check1 : ", fromSharedNickName);
            getUUIDFromTable(fromSharedNickName);
            Log.i(TAG, "uuid - getuuidTable 1 : " + fromSharedNickName);
        }

        // 쉐어드로부터 가져온 닉네임 비교해서 logIn 버튼 이름 설정
        if (fromSharedNickName.equals("LOG IN")) {
            logIn.setText(intent.getStringExtra("nickname"));
            Log.i(TAG, "fromSharedNickName String 값이 default값일 때");

        } else {
            logIn.setText(fromSharedNickName);
            Log.i("logIn.setText Check3 : ", fromSharedNickName);
            Log.i(TAG, "fromSharedNickName String 값을 쉐어드에서 가져왔을 때 : " + fromSharedNickName);
//            getUUIDFromTable(fromSharedNickName);
            Log.i(TAG, "uuid - getuuidTable 2 : " + fromSharedNickName);
        }
        Log.i("[Main]", "login.getText.toString() : " + logIn.getText().toString());
        if (logIn.getText().toString().equals("null") || logIn.getText().toString().equals("")) {
            logIn.setText("LOG IN");
            Log.i("logIn.setText Check4 : ", logIn.getText().toString());
        } //if

        // TODO - User, Liked Match
        selectLikes();

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
        } // if END

        likedUser = findViewById(R.id.likedUserList);
        likedUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "likedUser onClick");
                Intent likedIntent = new Intent(MainActivity.this, LikedList.class);
                Log.i(TAG, "now_song check: " + now_song);
                likedIntent.putExtra("selected_song", now_song);
                likedIntent.putExtra("now_login_user", logIn.getText().toString());
                Log.i(TAG, "likedUser onClick : " + logIn.getText().toString());
                startActivity(likedIntent);
            } // onClick
        }); // setOnCllickListener

        // TODO songList clickEvent
        songList = findViewById(R.id.menuButton);
        songList.setVisibility(View.GONE);
        songList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i(TAG, "재생목록 버튼 클릭");

                if (logIn.getText().toString().equals("LOG IN")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("LOG IN PLEASE");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.i(TAG, "songList Button onClick()");
                                } // onClick
                            }); // OnClickListener
                    builder.show();

                } else {
                    Intent songListIntent = new Intent(MainActivity.this, Played.class);
                    songListIntent.putExtra("userName", logIn.getText().toString());
                    String nowPlayingSong = mainLogo.getText().toString();
                    String[] cutName = nowPlayingSong.split(" • ");
                    String realName = cutName[0];
                    songListIntent.putExtra("playingSongName", realName);
                    startActivity(songListIntent);
                } // else
            } // Btn onClick
        }); // setOnClickListener

        setCommentView();

        // PICK 액티비티로 가는 버튼
        select = findViewById(R.id.selectableButton);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "PICK 버튼 클릭");
                Intent intent = new Intent(MainActivity.this, Selectable.class);
                String mainLogoText = mainLogo.getText().toString();
                String[] cutMainLogo = mainLogoText.split(" • ");
                String songName = cutMainLogo[0];
                intent.putExtra("user_name", logIn.getText().toString());
                intent.putExtra("song_name", songName);
                startActivity(intent);
            }
        });

        // comment 액티비티
        comment = findViewById(R.id.commentButton);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!logIn.getText().toString().equals("LOG IN")) {
                    Intent intent = new Intent(MainActivity.this, Comment.class);
                    // user_name, song_name, selected_time, msg
                    intent.putExtra("user_name", logIn.getText().toString());
                    Log.i(TAG, "comment user_name check : " + logIn.getText().toString());
                    intent.putExtra("song_name", mainLogo.getText().toString());
                    Log.i(TAG, "comment song_name check : " + mainLogo.getText().toString());
                    intent.putExtra("selected_time", playingTime.getText().toString());
                    Log.i(TAG, "comment selected_time check : " + playingTime.getText().toString());
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "로그인 후 음악을 재생해 주세요.", Toast.LENGTH_SHORT).show();
                } // else END
            } // onClick END
        }); // setOnClickListener END

        heart = findViewById(R.id.heartImageView);
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHeartClicked(v);

                if (logIn.getText().toString().equals("LOG IN")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Please Check the Log In");
                    builder.setMessage("로그인이 필요합니다.");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(TAG, "heart Button - 로그인이 필요합니다. OK 버튼 클릭");
                            Intent logInIntent = new Intent(mainCtx, LogIn.class);
                            startActivity(logInIntent);
                        }
                    });
                    builder.show();

                } else {
                    Log.i(TAG, "heart 버튼 클릭");
                } // bigger else END
            } // onClick END
        });

        if (mediaPlayer.isPlaying()) {
            mainSeekBar.setVisibility(View.VISIBLE);
            playingTime.setVisibility(View.VISIBLE);
            toPlayTime.setVisibility(View.VISIBLE);
            heart.setVisibility(View.VISIBLE);
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
                    profileIntent.putExtra("now_login_user", logIn.getText().toString());
                    startActivity(profileIntent);
                }
            }
        });

        mainLogo = findViewById(R.id.mainLogo);

        setNowPlayingShared("default");
//        weather();

        mainSeekBar = findViewById(R.id.mainSeekBar);

        playingTime = findViewById(R.id.mainPlayingTime);
        toPlayTime = findViewById(R.id.mainToPlayTime);
        if (!mediaPlayer.isPlaying()) {
            Log.i(TAG, "mainSeekBar.getVisibility() : " + mainSeekBar.getVisibility());
            Log.i(TAG, "playingTime.getVisibility() : " + playingTime.getVisibility());
            Log.i(TAG, "toPlayTime.getVisibility() : " + toPlayTime.getVisibility());
            Log.i(TAG, "heart.getVisibility() : " + heart.getVisibility());

//            mainSeekBar.setVisibility(View.INVISIBLE);
//            playingTime.setVisibility(View.INVISIBLE);
//            toPlayTime.setVisibility(View.INVISIBLE);
//            heart.setVisibility(View.INVISIBLE);
        } // if

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
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                Log.i("메인 플레이 버튼 클릭", "");

                forBeforeAndNextEditor.putString("checking_timing", "now_song");
                forBeforeAndNextEditor.commit();
                currentTimeForViewsIncrement = System.currentTimeMillis();

                if (logIn.getText().toString().equals("LOG IN")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Please Check the Log In");
                    builder.setMessage("로그인이 필요합니다.");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(TAG, "로그인이 필요합니다. OK 버튼 클릭");
                            Intent loginIntent = new Intent(mainCtx, LogIn.class);
                            startActivity(loginIntent);
                        }
                    });
                    builder.show();
                } else {

                    setMediaPlayer();
//                if (mediaPlayer.isPlaying()) {
                    if (logIn.getText().toString().equals("LOG IN")) {
                        mainSeekBar.setVisibility(View.GONE);
                        heart.setVisibility(View.GONE);
                        playingTime.setVisibility(View.GONE);
                        toPlayTime.setVisibility(View.GONE);
                    } else {
                        mainSeekBar.setVisibility(View.VISIBLE);
                        heart.setVisibility(View.VISIBLE);
                        playingTime.setVisibility(View.VISIBLE);
                        toPlayTime.setVisibility(View.VISIBLE);
                    }
//                    Log.i(TAG, "mainSeekBar.getVisibility() 2 : " + mainSeekBar.getVisibility());
//                    Log.i(TAG, "playingTime.getVisibility() 2 : " + playingTime.getVisibility());
//                    Log.i(TAG, "toPlayTime.getVisibility() 2 : " + toPlayTime.getVisibility());
//                    Log.i(TAG, "heart.getVisibility() 2 : " + heart.getVisibility());
//                }


                    int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                    if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                        // 첫 재생시 재생목록 3개만 생성해보자 (현재 곡 개수 8개)
                        // 재생목록 어떻게 보여줄까?
                        String playState = play.getText().toString();

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
                                        .appendQueryParameter("num", 1 + firstRanNum);
                                String postParams = builder.build().getEncodedQuery();
                                new getJSONData().execute("http://54.180.155.66/" + "file_sampling.php", postParams);

//                             get 방식 파라미터 추가
                                HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/file_sampling.php").newBuilder();
                                urlBuilder.addQueryParameter("ver", "1.0");
                                String url = urlBuilder.build().toString();
                                Log.i(TAG, "String url 확인 : " + url);

                                // post 파라미터 추가
                                // TODO 랜덤
                                RequestBody formBody = new FormBody.Builder()
                                        .add("num", 1 + firstRanNum.trim())
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
                                            @RequiresApi(api = Build.VERSION_CODES.Q)
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
//                                                            Toast.makeText(getApplicationContext(), "아이디 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
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
                                                            Log.i(TAG, "songInfo name Check (1) : " + name);

//                                                            insertIntoPastMusicTable();
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
                                                            // TODO gif 직접 추가
                                                            Glide.with(mainCtx)
                                                                    .asGif()
                                                                    .load(R.drawable.gradation)
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

                                                            // TODO setSongListButton
                                                            songList.setVisibility(View.VISIBLE);
                                                            likedUser.setVisibility(View.VISIBLE);

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
                                                            } // else


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
                                                            // TODO TOAST
                                                            Toast.makeText(getApplicationContext(), "♫", Toast.LENGTH_SHORT).show();
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

                                                            if (artist.contains("_")) {
                                                                String artistName = artist.replace("_", " ");
                                                                mainLogo.setText(reReName + " • " + artistName);
                                                                Log.i(TAG, "artist check (1) " + artistName);
                                                            } else {
                                                                mainLogo.setText(reReName + " • " + artist);
                                                                Log.i(TAG, "artist check (2) " + artist);
                                                            }
                                                            now_song = reReName;
                                                            Log.i(TAG, "now_song now 1 (first play) : " + now_song);
                                                            updateHeart();

                                                            // TODO now_song이 null이여서 빈 값이 들어가
                                                            try {
                                                                Log.i(TAG, "now_song now 5 (before insert) : " + now_song);
                                                                // TODO setPlayedInsert (1) in play.setOnClickListener
                                                                if (now_song.length() > 2 || now_song != null) {
                                                                    setPlayedInsertToTable(logIn.getText().toString(), now_song);
                                                                    Log.i(TAG, "played - now_song now 3 (when insert) : " + now_song);
                                                                } // if

                                                            } catch (NullPointerException e) {
                                                                Log.e(TAG, "now_song now NULL : " + e);
                                                            } // catch
//                } // if
                                                            if (!responseData.equals(0)) {
//                                                            responserData " + " 기준으로 잘라줘야 해
                                                                Log.i("[Main]", "responseData 가 0이 아닐 때 : " + responseData);
                                                            } // if
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                } // catch
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

                                startTime = System.currentTimeMillis();
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
//                                commentAdapter.clearItems();
//                                commentAdapter.notifyDataSetChanged();

//                            seekBarMoving();
//                            updateSeekBar();

//                    pauseAudio() 일단 안 씀
//                    pauseAudio();

                                if (mediaPlayer != null) {
                                    Log.i(TAG, "mediaPlayer.pause");
                                    mediaPlayer.pause();

                                } else {
                                    Log.i(TAG, "mediaPlayer == null");
                                }
//
                                playPosition = mediaPlayer.getCurrentPosition();
                                Log.d("[PAUSE CHECK]", "" + playPosition);
                                Log.i(TAG, "playCheck : " + playCheck);
//                                }
                            } else {
                                // 재생 버튼이 일시정지 모양일 때
                                // + 예외 처리
                                Log.i(TAG, "재생 버튼 모양이 재생도 일시정지도 아님");
                            }
                        } // if (playCheck == true 닫아주는 중괄호

                    } else {
                        Log.i(TAG, "playCheck : " + playCheck);
                        Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();

                    } // else
                } // First biggest else end

//               Log.i(TAG, "playCheck : " + playCheck);
//                  TODO setPlayedInsert (2) in play.setOnClickListener
//                // TODO now_song이 null이여서 빈 값이 들어가
////                if (now_song != null || !now_song.equals("")) {
////                Log.i(TAG, "now_song now 3 (insert) : " + now_song);
////                setPlayedInsertToTable(logIn.getText().toString(), now_song);
//
////                } // if
            } // OnClick 메서드 닫아주는 중괄호
        });

        // 메인 오른쪽 화살표 버튼 (랜덤 플레이 버튼)
        rightPlayBtn = findViewById(R.id.rightPlayButton);
        rightPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pastSongisPlayingCheckEditor.putString("now", "next");
                pastSongisPlayingCheckEditor.commit();
                gifCount++;
                changeSong();

            } // onClick End

        }); // setOnClickListener End

//        Log.i("[Random] mediaPlayer Check : ", String.valueOf(mediaPlayer));
//        Log.i("[Random] playCheck : ", String.valueOf(playCheck));
//
//        try {
//            Intent bringSongItem = getIntent();
//            String song_name = bringSongItem.getStringExtra("song_name");
//            Log.i(TAG, "bringSongItem : " + song_name);
//            if (!song_name.equals("") || song_name != null) {
//                setItemClickStreaming(song_name);
//            } else {
//
//            } // else

//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        } // catch
    } // onCreate END

    void incrementSongCount() {
        // 곡 조회수 누적 - 곡 이름, 곡 조회수
        MusicClickListener musicClickListener = new MusicClickListener();
        String songName = mainLogo.getText().toString().replace(" ", "_");
        musicClickListener.onClick(songName + ".mp3");
        Log.i(TAG, "increment check : " + songName + ".mp3");
    } // incrementSongCount

    void setChatListButton() {
        chatList = findViewById(R.id.chatListButton);
        Log.i(TAG, "logIn Text check : " + logIn.getText().toString());
        shared = getSharedPreferences("nickname", MODE_PRIVATE);
        editor = shared.edit();

        SharedPreferences chat_shared = getSharedPreferences("USER", MODE_PRIVATE);
        SharedPreferences.Editor chat_editor = chat_shared.edit();

        if (logIn.getText().toString().equals("LOG IN")) {
            chatList.setVisibility(View.GONE);
        } else {
            chatList.setVisibility(View.VISIBLE);
            chatList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "ChatList Button onClick()");
                    Intent intent = new Intent(getApplicationContext(), ChatSelect.class);
                    String me = shared.getString("nickname", "");
                    String user_name = logIn.getText().toString();
                    intent.putExtra("username", user_name);
                    intent.putExtra("before_class", "home");
                    chat_editor.putString("name", user_name);
                    chat_editor.commit();
                    // User로
                    Log.i(TAG, "-> ChatSelect Class / Shared nickname check : " + me);
                    startActivity(intent);
                } // onClick END
            }); // setOnClickListener END
        } // else END
    } // method END

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
    } // thread


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
                pastSongisPlayingCheckEditor.putString("now", "past");
                pastSongisPlayingCheckEditor.commit();
                changeSong();
//                pastStreaming();

            } // onClick
        }); // setOnClickListener
    } // onStart


    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "LifeCycle onResume()");

        updateSeekBar();
        setChatListButton();
        try {
            if (!commentList.get(0).getMsg().equals("") || commentList.get(0).getMsg() != null) {
                commentListAddItem();
            } // if
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "comment List IndexOutOfBoundsException e : " + e);
        } // catch

        // TODO activity start
        updateHeart();

        registerReceiver(playMusicReceiver,
                new IntentFilter("com.example.playlist.PLAY_MUSIC"));
    } // onResume

    protected void onPause() {
        super.onPause();
        Log.i(TAG, "LifeCycle onPause()");
        handler.removeCallbacksAndMessages(null);
    } // onPause

    protected void onStop() {
        super.onStop();
        Log.i(TAG, "LifeCycle onStop()");
        Log.i(TAG, "LifeCycle onStop() 로그인 액티비티에서 로그아웃 하고 돌아왔을 때");

        if (logIn.getText().toString().equals("LOG IN")) {
            stopAudio();
        } // if
    } // onStop

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
        nowPlayingPreference.removeKey(getApplicationContext(), "now_playing");
        //
//        }
        unregisterReceiver(playMusicReceiver);
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
        Toast.makeText(this, "♫", Toast.LENGTH_SHORT).show();
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

                try {
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
                        } //  onFailure

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
                                            } // if
                                        } // else

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } // catch
                                } // run
                            }); // runOnUiThread
                        } // onResponse
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


                } catch (NullPointerException e) {
                    e.printStackTrace();
                } // catch
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
                                    } // else
                                } // else

                            } catch (Exception e) {
                                e.printStackTrace();
                            } // catch
                        } // run
                    }); // runOnUiThread
                } // onResponse
            }); // client.newCall
        } // if
        Log.i(TAG, "GOOGLE INSERT INTO TABLE COMPLETE? CHECKING!");
    } // method

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
//                now_song = mainLogo.getText().toString();
//                Log.i(TAG, "now_song in updateSeekBar : " + now_song);
                currentPosition = mediaPlayer.getCurrentPosition();
                mainSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                String progressText = String.format("%02d:%02d", currentPosition / 1000 / 60, currentPosition / 1000 % 60);
                playingTime.setText(progressText);
                updateSeekBar();
//                commentAdapter.clearItems();
//                commentAdapter.notifyDataSetChanged();

            }
        }, 1000);
//        if (mainSeekBar.getProgress() == 0) {
//            // 다음 음악 재생
//            Log.i(TAG, "Auto next 1 : " + mainSeekBar.getProgress());
////            changeSong();
//        }
    }

    public void onStopButtonClick() {
        Log.i(TAG, "[RightPlay] onStopButtonClick 메서드 작동");
        if (mediaPlayer.isPlaying() || playCheck == true) {
            Log.i(TAG, "[RightPlay] mediaPlayer.isPlaying");
            mediaPlayer.stop();
//            mediaPlayer.start();
//            play.setText("❚❚");
            playCheck = false;

//            forBeforeAndNextEditor.putString("checking_timing", "next_song");
//            forBeforeAndNextEditor.commit();

//            playCheck = true;
            String needSongTimingCheck = pastSongisPlayingCheckShared.getString("now", "none");
            Log.i(TAG, "divide - needSongTimingCheck : " + needSongTimingCheck);
            String pastItemCheck = playedListShared.getString(logIn.getText().toString(), "default");
//            String timingStatus = forBeforeAndNextShared.getString("checking_timing", "default");
//            Log.i(TAG, "onStopButtonClick timingStatus : " + timingStatus);


//            if (needSongTimingCheck.equals("next") || needSongTimingCheck == "next" || !timingStatus.equals("after_song") || timingStatus != "after_song") {
//                // TODO (1) timingStatus = next_song
//                changeStreaming();
//                Log.i(TAG, "onStopButtonClick (if) : " + needSongTimingCheck + " / " + timingStatus);
//
//
//            }
//            if (needSongTimingCheck.equals("past") || needSongTimingCheck == "past" || timingStatus.equals("after_song") || timingStatus == "after_song") {
//                // TODO (2) timingStatus = after_song
//                pastStreaming();
//                Log.i(TAG, "onStopButtonClick (else) : " + needSongTimingCheck + " / " + timingStatus);
//            } // else

            // 이전 곡 재생 후 다음 곡 재생하면, 곡 정보와 아티스트 이름 불일치
            // TODO (1) timingStatus = next_song
            String check_timing = forBeforeAndNextShared.getString("checking_timing", "default");

//            if (check_timing.equals("after_song") || check_timing == "after_song") {
//                Log.i(TAG, "pastStreaming - onStopButtonClick : " + needSongTimingCheck);
//                changeStreaming();
//
//            } else {
            // TODO 첫 재생, 이전 곡 재생
            if (needSongTimingCheck.equals("next") || needSongTimingCheck == "next") {
                Log.i(TAG, "nowStatus) changeStreaming - StopButtonClick (if) : " + needSongTimingCheck);
                // TODO 1. 현재는 이전 곡 재생 이후 다음곡 재생 시 changeStreaming으로 넘어가서 랜덤 넘버 기준으로 곡 정보 가져오는 중
                // TODO 2. 아니지 맞지
                changeStreaming();

                // TODO (2) timingStatus = after_song
            } else if (needSongTimingCheck.equals("pick") || needSongTimingCheck == "pick") {
                Log.i(TAG, "nowStatus) itemClickStreaming - StopButtonClick (else if) : " + needSongTimingCheck);
                setItemClickStreaming(selected_song);
                Log.i(TAG, "nowStatus) itemClickStreaming - select song check : " + needSongTimingCheck + " / " + reSongName);
            } else {
                Log.i(TAG, "nowStatus) pastStreaming - onStopButtonClick (else) : " + needSongTimingCheck);
                pastStreaming();
            } // else

//            else if (needSongTimingCheck.equals("past") || needSongTimingCheck == "past"){
//                Log.i(TAG, "checking pastStreaming - onStopButtonClick (else) : " + needSongTimingCheck);
//                pastStreaming();
//            } else {
//                Log.i(TAG, "checking setItemClickStreaming : " + reSongName);
//                setItemClickStreaming(reSongName);
//            } // else

//            } // else

//            } else if (needSongTimingCheck.equals("past")) {
//                Log.i(TAG, "onStopButtonClick (past)");
//                pastStreaming();
//
//            } else if (pastItemCheck.equals("") || pastItemCheck.equals("default")) {
//                Log.i(TAG, "onStopButtonClick (empty)");
//                changeStreaming();
//            }

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

        Log.i(TAG, "onClick Check : " + playCheck);
        //        randomNumber();
        Log.i(TAG, "[changeStreaming] -----------------------------------------------");

        forBeforeAndNextEditor.putString("checking_timing", "next_song");
        forBeforeAndNextEditor.commit();

        Log.i(TAG, "[changeStreaming] changeStreaming Method");
        Log.i(TAG, "[changeStreaming] -----------------------------------------------");

        String playState = play.getText().toString();

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

            if (playCheck == false) {

                Log.i("[changeStreaming] 버튼 클릭", "재생");
                Log.i(TAG, "[changeStreaming] playCheck : " + playCheck);
                Log.i(TAG, "[changeStreaming] -----------------------------------------------");

                if (!playState.equals("❚❚") || playState.equals("❚❚")) {
                    Log.i("[changeStreaming] 버튼 클릭", "일시정지가 아닐 때");
                    Log.i(TAG, "[changeStreaming] -----------------------------------------------");
                    play.setText("❚❚");
                    play.setTextSize(53);

                    if (randomNumCheck.equals("") || randomNumCheck.length() == 0) {
                        responseRandomNumbers();
                    } // if

                    String ranRanRan = randomShared.getString("randomNumbers", "");
                    if (ranRanRan.length() != 0) {

                        if (randomNumCheck.length() > 0) {
                            char next = ranRanRan.charAt(0);
                            nextRanNum = String.valueOf(next);
                        } // if
//                    firstRanNumToInt = Integer.parseInt(firstRanNum);
                        Log.i(TAG, "[changeStreaming] ran now check : " + nextRanNum);
//                    ranRanRan = randomNumCheck;
                        if (ranRanRan.length() > 0) {
                            ranRanRan = ranRanRan.substring(1);
                            Log.i(TAG, "[changeStreaming] ran nums substring(1) check : " + ranRanRan);
//                        ranRanToInt = Integer.parseInt(ranRan);
                            randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
                            randomEditor = randomShared.edit();
                            randomEditor.putString("randomNumbers", ranRanRan);
                            randomEditor.apply();
                            randomEditor.commit();
                            pastNumBox = nextRanNum + pastNumBox;
                            Log.i(TAG, "[changeStreaming]ran past nums : " + pastNumBox);
                        } // if
                    }

                    if (ranRanRan.length() == 0 || ranRanRan.equals("")) {
                        responseRandomNumbers();
//                        nextRanNum = "1";
//                        String reRan = randomShared.getString("randomNumbers", "");
                        String reRan = randomNumCheck;
                        Log.i(TAG, "[changeStreaming] ran reRan check : " + reRan);
                        char reNext = reRan.charAt(0);
                        nextRanNum = String.valueOf(reNext);
                        Log.i(TAG, "[changeStreaming] ran now check : " + nextRanNum);
                        reRan = reRan.substring(1);
                        Log.i(TAG, "[changeStreaming] ran nums substring(1) check : " + reRan);
//                        ranRanToInt = Integer.parseInt(ranRan);
                        randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
                        randomEditor = randomShared.edit();
                        randomEditor.putString("randomNumbers", reRan);
                        randomEditor.apply();
                        randomEditor.commit();
                        pastNumBox = reNext + pastNumBox;
                        Log.i(TAG, "[changeStreaming] ran past  nums : " + pastNumBox);
                        if (reRan.length() > 0) {
                            String reRanRan = randomShared.getString("randomNumbers", "");
                            Log.i(TAG, "[changeStreaming] ran reRanRan check : " + reRanRan);
                            char next = reRanRan.charAt(0);
                            nextRanNum = String.valueOf(next);
                            Log.i(TAG, "[changeStreaming] ran now check : " + nextRanNum);
                            reRanRan = reRanRan.substring(1);
                            Log.i(TAG, "[changeStreaming] ran nums substring(1) check : " + reRanRan);
//                        ranRanToInt = Integer.parseInt(ranRan);
                            randomShared = getSharedPreferences("randomNumbers", MODE_PRIVATE);
                            randomEditor = randomShared.edit();
                            randomEditor.putString("randomNumbers", reRanRan);
                            randomEditor.apply();
                            randomEditor.commit();
                            pastNumBox = next + pastNumBox;
                            Log.i(TAG, "[changeStreaming] ran past  nums : " + pastNumBox);
                        } // if
                    } // if
                    // TODO name or num?
                    String needSongTimingCheck = pastSongisPlayingCheckShared.getString("now", "none");
                    Log.i(TAG, "divide - needSongTimingCheck : " + needSongTimingCheck);
                    Log.i(TAG, "divide - pastSongName Check : " + pastSongName);

//                    if (needSongTimingCheck == "past" || needSongTimingCheck.equals("past")) {
//                        Log.i(TAG, "divide - songTiming (if past) : " + needSongTimingCheck);
//
//                        if (!pastSongName.equals("") || pastSongName != "") {
//
//                            if (pastSongName.contains(" ")) {
//                                String rePastSongName = pastSongName.replace(" ", "_");
//                                Log.i(TAG, "divide rePastSongName Check : " + rePastSongName);
//                                Uri.Builder builder = new Uri.Builder()
//                                        .appendQueryParameter("past_song", pastSongName);
//                                String postParams = builder.build().getEncodedQuery();
//                                new getJSONData().execute("http://54.180.155.66/" + "/file_sampling.php", postParams);
//                            } // if
//                        } // if
//
//                    } else {
                    try {
                        if (nextRanNum.equals("0") || nextRanNum == "0") {
                            nextRanNum = "10";
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "divide - songTiming (else) : " + needSongTimingCheck);
                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("num", 1 + nextRanNum);
                    String postParams = builder.build().getEncodedQuery();
                    new getJSONData().execute("http://54.180.155.66/" + "/file_sampling.php", postParams);

//                    } // else

//                             get 방식 파라미터 추가
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/file_sampling.php").newBuilder();
                    urlBuilder.addQueryParameter("ver", "1.0");
                    String url = urlBuilder.build().toString();
                    Log.i(TAG, "[changeStreaming] String url 확인 : " + url);
                    Log.i(TAG, "[changeStreaming] -----------------------------------------------");

                    // post 파라미터 추가
                    try {
                        if (nextRanNum == null) {
                            nextRanNum = "1";
                        } // if

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        nextRanNum = "1";
                    } // catch

                    RequestBody formBody = new FormBody.Builder()
                            .add("num", 1 + nextRanNum.trim())
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
                            Log.e(TAG, "[changeStreaming] play callback onFailure : " + e);
                            Log.i(TAG, "[changeStreaming]  -----------------------------------------------");
                        } // onFailure

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            Log.i(TAG, "[changeStreaming] play callback onResponse");
                            Log.i(TAG, "[changeStreaming] -----------------------------------------------");

                            // 서브 스레드 UI 변경할 경우 에러
                            // 메인 스레드 UI 설정
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.Q)
                                @Override
                                public void run() {

                                    try {

                                        if (!response.isSuccessful()) {
                                            // 응답 실패
                                            Log.e(TAG, "[changeStreaming] 응답 실패 : " + response);
                                            Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();
                                            Log.i(TAG, "[changeStreaming] -----------------------------------------------");

                                        } else {
                                            // 응답 성공
                                            Log.i(TAG, "[changeStreaming] 응답 성공");
                                            final String responseData = response.body().string().trim();
                                            Log.i(TAG, "[changeStreaming] responseData Check : " + responseData);
                                            Log.i(TAG, "[changeStreaming] -----------------------------------------------");

                                            if (responseData.equals("1")) {
                                                Log.i(TAG, "[changeStreaming] responseData 가 1일 때 : " + responseData);
                                                Log.i(TAG, "[changeStreaming] -----------------------------------------------");
//                                                Toast.makeText(getApplicationContext(), "아이디 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();

                                            } else {
                                                Log.i(TAG, "[changeStreaming] responseData 가 1이 아닐 때 : " + responseData);
                                                Log.i(TAG, "[changeStreaming] -----------------------------------------------");
//                                                        startActivityString(MainActivity.class, "nickname", responseData);

                                                String songInfo = responseData;
                                                Log.i(TAG, "[changeStreaming] songInfo Check : " + songInfo);

                                                String[] numCut = songInfo.split("___");
                                                String num = numCut[0];
                                                Log.i(TAG, "[changeStreaming] ongInfo num Check : " + num);

                                                String deleteNum = numCut[1];
                                                String[] artistCut = deleteNum.split("###");
                                                artist = artistCut[0];
                                                Log.i(TAG, "[changeStreaming] songInfo artist Check : " + artist);

                                                String deleteArtist = artistCut[1];
                                                String[] pathCut = deleteArtist.split("@@@");
                                                String path = pathCut[0];
                                                Log.i(TAG, "[changeStreaming] songInfo path Check : " + path);

                                                time = pathCut[1];
                                                Log.i(TAG, "[changeStreaming] songInfo time Check : " + time);

                                                String[] nameCut = path.split("/");

                                                String timing = pastSongisPlayingCheckShared.getString("now", "default");

                                                if (pastSongName == "" || pastSongName == null || pastSongName.equals("")) {
                                                    name = nameCut[4];
                                                    Log.i(TAG, "songInfo name Check (2) : " + name);
                                                    String reName = name.replace("_", " ");
                                                    Log.i(TAG, "changeStreaming - songInfo name Check *if : " + name);

                                                    Log.i(TAG, "[changeStreaming]  -----------------------------------------------");
                                                } else {
                                                    // changeStreaming 상황에서 조건식 설정 잘못했어서 곡을 스트리밍할 때 필요한 곡 이름이 세팅 안되어있었고,
                                                    // 아래 코드 추가 후 해결
                                                    name = nameCut[4];
                                                    Log.i(TAG, "songInfo name Check (6) : " + name);
                                                }

//                                                if (timing.equals("next") || timing == "next") {
//                                                    Log.i(TAG, "changeStreaming - songInfo name Check *if next : " + name);
//                                                } else {
//                                                    // TODO check (1) < 버튼 클릭 시 name에 값 넣어주고 여기서 확인
//                                                    // TODO check (2) > 버튼 클릭 시 name에 값을 넣어주고
//                                                    name = pastSongName;
//                                                    Log.i(TAG, "changeStreaming - songInfo name Check *else : " + name);
//                                                } // else

                                                mediaPlayer.setLooping(false);
                                                Log.i(TAG, "[changeStreaming]  MediaPlayer 생성");

                                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                Log.i(TAG, "[changeStreaming] mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)");

                                                Log.i(TAG, "[changeStreaming] song name before streaming check : " + name);
                                                ;
                                                String uri = "http://54.180.155.66/" + name;
                                                Log.i(TAG, "[changeStreaming] file name from music table : " + uri);
                                                // 경로
                                                mediaPlayer.setDataSource(uri);

                                                isPlaying = true;
//                                                play.setText("❚❚");
                                                Log.i(TAG, "[changeStreaming] mediaPlayer.setDataSource(path)");

                                                mediaPlayer.prepareAsync();
                                                Log.i(TAG, "[changeStreaming] mediaPlayer.prepareAsync()");

                                                // TODO
                                                mainSeekBar.setMax(mediaPlayer.getDuration());

                                                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                                                Log.i(TAG, "[changeStreaming] mediaPlayer.setWakeMode");
                                                Log.i(TAG, "[changeStreaming]  -----------------------------------------------");

//                                                gif.playing();

                                                // TODO gif 직접추가
                                                Glide.with(mainCtx)
                                                        .asGif()
                                                        .load(R.drawable.gradation)
                                                        .centerCrop()
                                                        .listener(new RequestListener<GifDrawable>() {
                                                            @Override
                                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                                                // GIF 파일 로드에 실패한 경우의 처리
                                                                return false;
                                                            } // onLoadFailed

                                                            @Override
                                                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                // GIF 파일 로드에 성공한 경우의 처리
                                                                resource.setLoopCount(GifDrawable.LOOP_FOREVER); // 반복 재생 설정
                                                                resource.start(); // GIF 파일 재생 시작
                                                                return false;
                                                            } // onResourceReady
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

                                                // TODO ADD for SeekBar Moving
                                                if (mediaPlayer.isPlaying()) {
                                                    mediaPlayer.stop();

                                                    try {
                                                        mediaPlayer.prepare();

                                                    } catch (IllegalStateException e) {
                                                        e.printStackTrace();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    } // catch
                                                    mediaPlayer.seekTo(0);
                                                    mainSeekBar.setProgress(0);

                                                } else {
                                                    mediaPlayer.start();
                                                    Thread();
                                                } // else

                                                // TODO TOAST
                                                Toast.makeText(getApplicationContext(), "♫", Toast.LENGTH_SHORT).show();
                                                toast.show();

                                                updateSeekBar();

                                                songTime = findViewById(R.id.mainToPlayTime);
                                                songTime.setText(time);

                                                String[] exceptMp3 = name.split(".mp3");
                                                String justName = exceptMp3[0];
                                                String reReName = justName.replace("_", " ");

                                                Log.i(TAG, "[RightPlay] song just name 확인 : " + reReName);
                                                Log.i(TAG, "[RightPlay] -----------------------------------------------");

                                                mainLogo = findViewById(R.id.mainLogo);
                                                if (artist.contains("_")) {
                                                    String artistName = artist.replace("_", " ");
                                                    mainLogo.setText(reReName + " • " + artistName);
                                                    Log.i(TAG, "artist check (3) " + artistName);

                                                } else {
                                                    mainLogo.setText(reReName + " • " + artist);
                                                    Log.i(TAG, "artist check (4) " + artist);
                                                }
                                                // ㅇ기여기
                                                now_song = reReName;
                                                Log.i(TAG, "now_song now 4 (change streaming) : " + now_song);

                                                // TODO setPlayedInsert (3) in changeStreaming
                                                String playTimingCheck = pastSongisPlayingCheckShared.getString("now", "none");
                                                if (playTimingCheck.equals("next")) {
                                                    setPlayedInsertToTable(logIn.getText().toString(), now_song);
                                                    Log.i(TAG, "played - Insert check (setPlayedInsertToTable) : " + logIn.getText().toString() + " / " + now_song);
                                                    Log.i(TAG, "played - playTimingCheck (next) : " + playTimingCheck);
                                                    Log.i(TAG, "played - now_song now 4 *if (change Streaming) : " + now_song);

                                                } else {
                                                    Log.i(TAG, "played - playTimingCheck (past) : " + playTimingCheck);
                                                    Log.i(TAG, "played - now_song now 4 *else (change Streaming) : " + now_song);
                                                } // else

                                                // TODO selectLikes
                                                updateHeart();

                                                if (!responseData.equals(0)) {
//                                                            responserData " + " 기준으로 잘라줘야 해
                                                    Log.i("[RightPlay]", "responseData 가 0이 아닐 때 : " + responseData);
                                                    Log.i(TAG, "[RightPlay] -----------------------------------------------");
                                                } // if
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } // catch
                                }
                            });
                        }
                    });
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
////                    pauseAudio() 일단 안 씀
////                    pauseAudio();
//
//                    if (mediaPlayer != null) {
//                        mediaPlayer.pause();
//                        playPosition = mediaPlayer.getCurrentPosition();
//                        Log.d("[PAUSE CHECK]", "" + playPosition);
//                        Log.i(TAG, "playCheck : " + playCheck);
//
//                    }
                }
            } // if (playCheck == true 닫아주는 중괄호

        } else {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
        } // else

        // TODO user Listened music Info add 2
        // Arraylist로 변경
//                                                played = played + now_song + "//";
//                                                Log.i(TAG, "User Played Check : " + played);
        // TODO user Listened music Info add (first streaming)
//                                                 playedList = song name, artist name, time add
        played = played + now_song + "//";
//                                                 TODO 다음 곡 들을 때만
        String playTimingCheck = pastSongisPlayingCheckShared.getString("now", "none");

        // TODO setPlayedInsert (3) in changeStreaming
//        if (playTimingCheck.equals("next")) {
//            setPlayedInsertToTable(logIn.getText().toString(), now_song);
//            Log.i(TAG, "played - Insert check (setPlayedInsertToTable) : " + logIn.getText().toString() + " / " + now_song);
//            Log.i(TAG, "playTimingCheck (next) : " + playTimingCheck);
//
//        } else {
//            Log.i(TAG, "playTimingCheck (past) : " + playTimingCheck);
//        } // else
    } // changeStreaming

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
                                    } // else
                                } // else
                            } catch (Exception e) {
                                e.printStackTrace();
                            } // catch
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
        cutLastPlayedSong();

        Log.i(TAG, "pastStreaming - onClick Check : " + playCheck);
        Log.i(TAG, "[pastStreaming] -----------------------------------------------");

        Log.i(TAG, "[pastStreaming] Method");
        Log.i(TAG, "[pastStreaming] -----------------------------------------------");

        String playState = play.getText().toString();
        forBeforeAndNextEditor.putString("checking_timing", "after_song");
        forBeforeAndNextEditor.commit();

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

            if (playCheck == false) {

                Log.i("[pastStreaming] 버튼 클릭", "재생");
                Log.i(TAG, "[pastStreaming] playCheck : " + playCheck);
                Log.i(TAG, "[pastStreaming] -----------------------------------------------");

                if (!playState.equals("❚❚") || playState.equals("❚❚")) {
                    Log.i("[pastStreaming] 버튼 클릭", "일시정지가 아닐 때");
                    Log.i(TAG, "[pastStreaming] -----------------------------------------------");
                    play.setText("❚❚");
                    play.setTextSize(53);

                    // TODO name or num?
                    String needSongTimingCheck = pastSongisPlayingCheckShared.getString("now", "none");
                    Log.i(TAG, "pastStreaming divide - needSongTimingCheck : " + needSongTimingCheck);
                    Log.i(TAG, "pastStreaming divide - pastSongName Check : " + pastSongName);

//                    if (needSongTimingCheck == "past" || needSongTimingCheck.equals("past")) {
//                        Log.i(TAG, "divide - songTiming (if past) : " + needSongTimingCheck);
//
//                        if (!pastSongName.equals("") || pastSongName != "") {
//
                    Log.i(TAG, "checking pastStreaming : " + pastSongName);
                    try {
                        if (pastSongName.contains(" ")) {
                            rePastSongName = pastSongName.replace(" ", "_");
                            Log.i(TAG, "pastStreaming divide rePastSongName Check : " + rePastSongName);
                            Uri.Builder builder = new Uri.Builder()
                                    .appendQueryParameter("past_song", rePastSongName);
                            String postParams = builder.build().getEncodedQuery();
                            new getJSONData().execute("http://54.180.155.66/" + "/past_file_sampling.php", postParams);
                        } else {
                            rePastSongName = pastSongName;
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
//                             get 방식 파라미터 추가
                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/past_file_sampling.php").newBuilder();
                    urlBuilder.addQueryParameter("ver", "1.0");
                    String url = urlBuilder.build().toString();
                    Log.i(TAG, "[pastStreaming] String url 확인 : " + url);
                    Log.i(TAG, "[pastStreaming] -----------------------------------------------");

                    RequestBody formBody = new FormBody.Builder()
                            .add("past_song", rePastSongName.trim())
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
                            Log.e(TAG, "[pastStreaming] play callback onFailure : " + e);
                            Log.i(TAG, "[pastStreaming]  -----------------------------------------------");
                        } // onFailure

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            Log.i(TAG, "[pastStreaming] play callback onResponse");
                            Log.i(TAG, "[pastStreaming] -----------------------------------------------");

                            // 서브 스레드 UI 변경할 경우 에러
                            // 메인 스레드 UI 설정
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.Q)
                                @Override
                                public void run() {

                                    try {

                                        if (!response.isSuccessful()) {
                                            // 응답 실패
                                            Log.e(TAG, "[pastStreaming] 응답 실패 : " + response);
                                            Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();
                                            Log.i(TAG, "[pastStreaming] -----------------------------------------------");

                                        } else {
                                            // 응답 성공
                                            Log.i(TAG, "[pastStreaming] 응답 성공");
                                            final String responseData = response.body().string().trim();
                                            Log.i(TAG, "[pastStreaming] responseData Check : " + responseData);
                                            Log.i(TAG, "[pastStreaming] -----------------------------------------------");

                                            if (responseData.equals("1")) {
                                                Log.i(TAG, "[pastStreaming] responseData 가 1일 때 : " + responseData);
                                                Log.i(TAG, "[pastStreaming] -----------------------------------------------");
//                                                Toast.makeText(getApplicationContext(), "아이디 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();

                                            } else {
                                                Log.i(TAG, "[pastStreaming] responseData 가 1이 아닐 때 : " + responseData);
                                                Log.i(TAG, "[pastStreaming] -----------------------------------------------");
//                                                        startActivityString(MainActivity.class, "nickname", responseData);

                                                String songInfo = responseData;
                                                Log.i(TAG, "checking [pastStreaming] songInfo Check : " + songInfo);

                                                String[] numCut = songInfo.split("___");
                                                String num = numCut[0];
                                                Log.i(TAG, "checking [pastStreaming] songInfo num Check : " + num);

                                                String deleteNum = numCut[1];
                                                String[] artistCut = deleteNum.split("###");
                                                artist = artistCut[0];
                                                Log.i(TAG, "checking [pastStreaming] songInfo artist Check : " + artist);

                                                String deleteArtist = artistCut[1];
                                                String[] pathCut = deleteArtist.split("@@@");
                                                String path = pathCut[0];
                                                Log.i(TAG, "checking [pastStreaming] songInfo path Check : " + path);

                                                time = pathCut[1];
                                                Log.i(TAG, "checking [pastStreaming] songInfo time Check : " + time);

                                                String[] nameCut = path.split("/");

                                                if (pastSongName == "" || pastSongName == null || pastSongName.equals("")) {
                                                    name = nameCut[4];
                                                    Log.i(TAG, "songInfo name Check (3) : " + name);
                                                    String reName = name.replace("_", " ");
                                                    Log.i(TAG, "pastStreaming - songInfo name Check *if : " + name);

                                                    Log.i(TAG, "[pastStreaming]  -----------------------------------------------");

                                                } else {
                                                    // TODO check (1) < 버튼 클릭 시 name에 값 넣어주고 여기서 확인
                                                    // TODO check (2) > 버튼 클릭 시 name에 값을 넣어주고
                                                    Log.i(TAG, "songInfo name Check (4) before : " + name);
                                                    name = rePastSongName;
                                                    Log.i(TAG, "songInfo name Check (4) after : " + name);
                                                    Log.i(TAG, "pastStreaming - songInfo name Check *else : " + name);
                                                } // else

                                                mediaPlayer.setLooping(false);
                                                Log.i(TAG, "[pastStreaming]  MediaPlayer 생성");

                                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                Log.i(TAG, "[pastStreaming] mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)");

                                                Log.i(TAG, "[pastStreaming] song name before streaming check : " + name);
                                                ;
                                                String uri = "http://54.180.155.66/" + name;
                                                Log.i(TAG, "[pastStreaming] file name from music table : " + uri);
                                                // 경로
                                                mediaPlayer.setDataSource(uri);

                                                isPlaying = true;
//                                                play.setText("❚❚");
                                                Log.i(TAG, "[pastStreaming] mediaPlayer.setDataSource(path)");

                                                mediaPlayer.prepareAsync();
                                                Log.i(TAG, "[pastStreaming] mediaPlayer.prepareAsync()");

                                                // TODO
                                                mainSeekBar.setMax(mediaPlayer.getDuration());

                                                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                                                Log.i(TAG, "[pastStreaming] mediaPlayer.setWakeMode");
                                                Log.i(TAG, "[pastStreaming]  -----------------------------------------------");

//                                                gif.playing();

                                                // TODO gif 직접추가
                                                Glide.with(mainCtx)
                                                        .asGif()
                                                        .load(R.drawable.gradation)
                                                        .centerCrop()
                                                        .listener(new RequestListener<GifDrawable>() {
                                                            @Override
                                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                                                // GIF 파일 로드에 실패한 경우의 처리
                                                                return false;
                                                            } // onLoadFailed

                                                            @Override
                                                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                // GIF 파일 로드에 성공한 경우의 처리
                                                                resource.setLoopCount(GifDrawable.LOOP_FOREVER); // 반복 재생 설정
                                                                resource.start(); // GIF 파일 재생 시작
                                                                return false;
                                                            } // onResourceReady
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

                                                // TODO ADD for SeekBar Moving
                                                if (mediaPlayer.isPlaying()) {
                                                    mediaPlayer.stop();

                                                    try {
                                                        mediaPlayer.prepare();

                                                    } catch (IllegalStateException e) {
                                                        e.printStackTrace();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    } // catch
                                                    mediaPlayer.seekTo(0);
                                                    mainSeekBar.setProgress(0);

                                                } else {
                                                    mediaPlayer.start();
                                                    Thread();
                                                } // else

                                                // TODO TOAST
                                                Toast.makeText(getApplicationContext(), "♫", Toast.LENGTH_SHORT).show();
                                                toast.show();

                                                updateSeekBar();

                                                songTime = findViewById(R.id.mainToPlayTime);
                                                songTime.setText(time);

                                                String[] exceptMp3 = name.split(".mp3");
                                                String justName = exceptMp3[0];
                                                String reReName = justName.replace("_", " ");

                                                Log.i(TAG, "[pastStreaming] song just name 확인 : " + reReName);
                                                Log.i(TAG, "[pastStreaming] -----------------------------------------------");

                                                mainLogo = findViewById(R.id.mainLogo);
                                                if (artist.contains("_")) {
                                                    String artistName = artist.replace("_", " ");
                                                    mainLogo.setText(reReName + " • " + artistName);
                                                    Log.i(TAG, "pastStreaming - artist check (3) " + artistName);

                                                } else {
                                                    mainLogo.setText(reReName + " • " + artist);
                                                    Log.i(TAG, "pastStreaming - artist check (4) " + artist);
                                                }
                                                now_song = reReName;
                                                Log.i(TAG, "pastStreaming - now_song now 4 (change streaming) : " + now_song);

                                                // TODO setPlayedInsert (3) in changeStreaming
                                                String playTimingCheck = pastSongisPlayingCheckShared.getString("now", "none");
                                                if (playTimingCheck.equals("next")) {
                                                    setPlayedInsertToTable(logIn.getText().toString(), now_song);
                                                    Log.i(TAG, "pastStreaming - Insert check (setPlayedInsertToTable) : " + logIn.getText().toString() + " / " + now_song);
                                                    Log.i(TAG, "pastStreaming - playTimingCheck (next) : " + playTimingCheck);
                                                    Log.i(TAG, "pastStreaming - now_song now 4 *if (change Streaming) : " + now_song);

                                                } else {
                                                    Log.i(TAG, "pastStreaming - playTimingCheck (past) : " + playTimingCheck);
                                                    Log.i(TAG, "pastStreaming - now_song now 4 *else (change Streaming) : " + now_song);
                                                } // else

                                                // TODO selectLikes
                                                updateHeart();

                                                if (!responseData.equals(0)) {
//                                                            responserData " + " 기준으로 잘라줘야 해
                                                    Log.i("[pastStreaming]", "responseData 가 0이 아닐 때 : " + responseData);
                                                    Log.i(TAG, "[pastStreaming] -----------------------------------------------");
                                                } // if
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } // catch
                                }
                            });
                        }
                    });
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
                    Log.i("pastStreaming 버튼 클릭", "일시정지 상태일 때");
//                    Log.i(TAG, "playCheck : " + playCheck);
//
//                    play.setText("▶");
//                    play.setTextSize(60);
//
////                    pauseAudio() 일단 안 씀
////                    pauseAudio();
//
//                    if (mediaPlayer != null) {
//                        mediaPlayer.pause();
//                        playPosition = mediaPlayer.getCurrentPosition();
//                        Log.d("[PAUSE CHECK]", "" + playPosition);
//                        Log.i(TAG, "playCheck : " + playCheck);
//
//                    }
                }
            } // if (playCheck == true 닫아주는 중괄호

        } else {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
        } // else

        // TODO user Listened music Info add 2
        // Arraylist로 변경
//                                                played = played + now_song + "//";
//                                                Log.i(TAG, "User Played Check : " + played);
        // TODO user Listened music Info add (first streaming)
//                                                 playedList = song name, artist name, time add
        played = played + now_song + "//";
//                                                 TODO 다음 곡 들을 때만
        String playTimingCheck = pastSongisPlayingCheckShared.getString("now", "none");

        // TODO setPlayedInsert (3) in changeStreaming
//        if (playTimingCheck.equals("next")) {
//            setPlayedInsertToTable(logIn.getText().toString(), now_song);
//            Log.i(TAG, "played - Insert check (setPlayedInsertToTable) : " + logIn.getText().toString() + " / " + now_song);
//            Log.i(TAG, "playTimingCheck (next) : " + playTimingCheck);
//
//        } else {
//            Log.i(TAG, "playTimingCheck (past) : " + playTimingCheck);
//        } // else
    }

    // TODO. UUID 가져오는 방식 변경 (레트로핏으로 서버에서 가져오고, SQlite에 insert)
    private void getUUIDFromTable(String me) {
        Log.i(TAG, "getUUIDFRomToTable Method");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.180.155.66/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);
        retrofit2.Call<ResponseModel> call = serverApi.getUUID(me);
        call.enqueue(new retrofit2.Callback<ResponseModel>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseModel> call, retrofit2.Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    // 성공적인 응답 처리
//                    Toast.makeText(MainActivity.this, "Data selected successfully", Toast.LENGTH_SHORT).show();
                    ResponseModel responseModel = response.body();

                    if (responseModel != null) {
                        List<String> uuidsFromResponse = responseModel.getUUIDs();
                        Log.d(TAG, "UUID - Received UUIDs: " + Arrays.toString(uuidsFromResponse.toArray()));
                        String receivedUuids = TextUtils.join(", ", uuidsFromResponse);
                        String[] uuidArray = receivedUuids.split(", ");
                        List<Uuid> uuidEntities = new ArrayList<>();

                        for (String uuidStr : uuidArray) {
                            Uuid newUuidEntity = new Uuid(uuidStr);
                            uuidEntities.add(newUuidEntity);
                        } // for

                        if (uuidsFromResponse != null) {
                            // 밑에 코드에서 uuidsFromResponse를 사용하여 처리 수행
                            // 예 : 저장, 출력, 변수 저장 등
                            UUIDDatabase db = Room.databaseBuilder(getApplicationContext(), UUIDDatabase.class, "uuid")
                                    .allowMainThreadQueries()
                                    .addMigrations(MIGRATION_1_2)
                                    .build();
                            UuidDao uuidDao = db.uuidDao();

                            new Thread(() -> {
                                // 데이터베이스의 기존 UUID를 삭제합니다.
                                uuidDao.deleteAll();

                                // 새로 받은 UUID를 데이터베이스에 삽입합���다.
                                for (Uuid uuidEntity : uuidEntities) {
                                    uuidDao.insert(uuidEntity);
                                } // for

                                // 저장이 완료된 후 데이터베이스에서 모든 UUID를 가져와 출력합니다.
                                runOnUiThread(() -> {
                                    db.uuidDao().getAll().observe(MainActivity.this, uuids -> {
                                        if (uuids.isEmpty()) {
                                            Toast.makeText(MainActivity.this, "데이터베이스가 비어 있습니다.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            StringBuilder sb = new StringBuilder();
                                            for (Uuid uuid : uuids) {
                                                sb.append("UUID : ").append(uuid.uuid).append("\n");
                                            } // for
                                            Log.i(TAG, "UUID: " + sb.toString());
                                        }
                                    });
                                });
                            }).start();
                        } else {
                            Log.d(TAG, "uuids : " + "응답 데이터가 null 입니다.");
                            editor.putString("UUID", "none_uuid");
                            editor.commit();
                        } //else

                    } else {
                        Log.d(TAG, "uuid : " + "응답 데이터가 null 입니다.");
                    } // else

                } else {
                    // 실패한 응답 처리
                    Toast.makeText(MainActivity.this, "Failed to select data", Toast.LENGTH_SHORT).show();
                } // else
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseModel> call, Throwable t) {
                // 에러 처리
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void setCommentView() {
        commentView = findViewById(R.id.commentView);
        commentLayoutManager = new LinearLayoutManager(this);
        commentView.setLayoutManager(commentLayoutManager);
        commentView.setHasFixedSize(true);
        commentAdapter = new CommentAdapter(this, commentList);
        commentView.setAdapter(commentAdapter);
    }

    void commentListAddItem() {
//        CommentModel commentModel = new CommentModel(user_name, song_name, selected_time, msg);
        getUserName = getIntent().getStringExtra("user_name");
        getSongName = getIntent().getStringExtra("song_name");
        getSelectedTime = getIntent().getStringExtra("selected_time");
        getMsg = getIntent().getStringExtra("msg");
        Log.i(TAG, "commentList - getIntent getUserName : " + getUserName);
        Log.i(TAG, "commentList - getIntent getSongName : " + getSongName);
        Log.i(TAG, "commentList - getIntent getSelectedTime : " + getSelectedTime);
        Log.i(TAG, "commentList - writeComment check : " + getMsg);

        CommentModel item = new CommentModel(getMsg);
        commentList.add(item);
        commentAdapter.addItem(item);
        commentAdapter.notifyDataSetChanged();
    } // commentListAddItem

    private void startSyncComments() {
        Log.i(TAG, "startSyncComments");
        syncHandler = new Handler();

        syncRunnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void run() {
                Log.i(TAG, "startSyncComments");

                try {
                    int currentPositionInMilliseconds = mediaPlayer.getCurrentPosition();
                    int currentPositionSeconds = currentPositionInMilliseconds / 1000;
                    int minutes = currentPositionSeconds / 60;
                    int seconds = currentPositionSeconds % 60;
                    Log.i(TAG, "startSyncComments currentPositionSeconds : " + currentPositionInMilliseconds);
                    String timeFormatted = String.format("%02d:%02d", minutes, seconds);
                    Log.i(TAG, "startSyncComments positionSecondsStr : " + timeFormatted);

                    incrementSongView();
                    fetchAndDisplayComments(timeFormatted);
                    syncHandler.postDelayed(syncRunnable, syncInterval);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } // catch
            } // run
        }; // syncRunnable
        syncHandler.postDelayed(syncRunnable, syncInterval);
    } // startSyncComments

    private void fetchAndDisplayComments(String currentTime) {
        Log.i(TAG, "fetchAndDisplayComments method");

        String songName = mainLogo.getText().toString();
        Log.i(TAG, "now playing song name : " + songName);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi commentService = retrofit.create(ServerApi.class);

        // TODO. 곡 이름 기준으로 모든 행 정보옴 가져옴
        retrofit2.Call<List<CommentModel>> call = commentService.getComments(songName);
        call.enqueue(new retrofit2.Callback<List<CommentModel>>() {
            @Override
            public void onResponse(retrofit2.Call<List<CommentModel>> call, retrofit2.Response<List<CommentModel>> response) {

                if (response.isSuccessful()) {
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "fetchAndDisplayComments onResponse : " + response.code());
                    Log.i(TAG, "fetchAndDisplayComments Server ResponseBody: " + responseBody);
                    Log.i(TAG, "fetchAndDisplayComments Server Response: " + response);
                    Log.i(TAG, "fetchAndDisplayComments Server Response.message : " + response.message());
                    Log.i(TAG, "fetchAndDisplayComments response.isSuccessful");
                    List<CommentModel> comments = response.body();

//                    if (comments != null) {
                    Log.i(TAG, "fetchAndDisplayComments comment != null");
                    commentAdapter.clearItems();
                    Log.i(TAG, "fetchAndDisplayComments currentTime : " + currentTime);
                    int currentTimeSeconds = timeStringToSeconds(currentTime);
                    Log.i(TAG, "fetchAndDisplayComments currentTimeSeconds : " + currentTimeSeconds);
                    int lowerBound = currentTimeSeconds - TIME_RANGE / 2;
                    Log.i(TAG, "fetchAndDisplayComments lowerBound : " + lowerBound);
                    int upperBound = currentTimeSeconds + TIME_RANGE / 2;
                    Log.i(TAG, "fetchAndDisplayComments upperBound : " + upperBound);

                    // TODO - for문 진입을 안 한 듯이 보임
                    for (CommentModel comment : comments) {
                        Log.i(TAG, "onResponse get Comment");
                        if (!comments.contains(null)) {
                            Log.i(TAG, "fetchAndDisplayComments contains !null : " + comments);
//                        try {
//                        if (getUserName != null || !getUserName.equals("")) {
                            String user = comment.getUser_name();
                            Log.i(TAG, "fetchAndDisplayComments user_name : " + user);
                            String song = comment.getSong_name();
                            Log.i(TAG, "fetchAndDisplayComments song_name : " + song);
                            String selected_time = comment.getSelected_time();
                            Log.i(TAG, "fetchAndDisplayComments selected_time : " + selected_time);
                            int selectedTimeSeconds = timeStringToSeconds(selected_time);
                            Log.i(TAG, "fetchAndDisplayComments selectedTimeSeconds : " + selectedTimeSeconds);
                            // TODO selected_time int로 변환
                            String msg = comment.getMsg();
                            Log.i(TAG, "fetchAndDisplayComments message : " + msg);

                            // TODO 현재는 곡 기준으로 다
                            if (selectedTimeSeconds >= lowerBound && selectedTimeSeconds <= upperBound) {
                                // 범위 내에 있을 경우, 리사이클러뷰에 추가
                                Log.i(TAG, "fetchAndDisplayComments recyclerview add");
                                commentList.add(comment);

                            } else {
                                Log.i(TAG, "fetchAndDisplayComments 범위에 해당되지 않습니다");
                            } // else
                        } else {
                            Log.i(TAG, "fetchAndDisplayComments contains null : " + comments);
                        }
//                        } // if
//                        } catch (NullPointerException e) {
//                            Toast.makeText(MainActivity.this, "ERROR : " + e, Toast.LENGTH_SHORT).show();
//                        } // catch
                    } // for END
                    commentAdapter.notifyDataSetChanged();
//                    } // if (comments != null)
                } else {
                    Toast.makeText(MainActivity.this, "check the comment response", Toast.LENGTH_SHORT).show();
                } // else
            } // onResponse

            @Override
            public void onFailure(retrofit2.Call<List<CommentModel>> call, Throwable t) {
                Log.i(TAG, "fetchAndDisplayComments onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueque
    } // method END

    private int timeStringToSeconds(String timeString) {
        String[] timeParts = timeString.split(":");
        int minutes = Integer.parseInt(timeParts[0]);
        int seconds = Integer.parseInt(timeParts[1]);

        return minutes * 60 + seconds;
    } // timeStringToSeconds

    void incrementSongView() {
        // TODO -
        currentTimeForViewsIncrement = System.currentTimeMillis();
        totalPlayTime += currentTimeForViewsIncrement - startTime;
        totalPlayTimeSecondsForViewsCount = (int) (totalPlayTime / 1000);
        Log.i(TAG, "totalPlayTimeSeconds Check : " + totalPlayTimeSecondsForViewsCount);

        nowPlayingStr = nowPlayingPreference.getString(getApplicationContext(), "now_playing");
        String nowState = mainLogo.getText().toString();
        selectLikes();

        // TODO total views
        if (totalPlayTimeSecondsForViewsCount >= 100 && !nowState.equals(nowPlayingStr)) {
            //  TODO - 해당 곡의 총 길이수에 % 기준으로 잘라와 *초수 기준말고
            Log.i(TAG, "increment song - if) totalPlayTimeSeconds >= 5 : " + totalPlayTimeSecondsForViewsCount + " / " + nowState + " / " + nowPlayingStr);
            // TODO 곡 조회수 누적
            incrementSongCount();
            setNowPlayingShared(nowState);

        } else if (nowState.equals(nowPlayingStr)) {
            Log.i(TAG, "increment song - else if) now playing == now playing shared : " + totalPlayTimeSecondsForViewsCount + " / " + nowState + " / " + nowPlayingStr);
        } // else if
//        else {
//            Log.i(TAG, "totalPlayTimeSeconds <= 30 : " + totalPlayTimeSeconds);
//        } // else END

        playPosition = mediaPlayer.getCurrentPosition();
        Log.d("[PAUSE CHECK]", "" + playPosition);
        Log.i(TAG, "playCheck : " + playCheck);
    } // incrementSongView END

    void setNowPlayingShared(String now) {
        nowPlayingPreference = new PreferenceManager();
        nowPlayingPreference.settingShared(getApplicationContext(), "now_playing", now);
    } // setNowPlayingShared

    void onHeartClicked(View view) {

        if (mainLogo.getText().equals("날씨 : 선선함")) {
            Log.i(TAG, "updateLikes onHeartClicked if)");

        } else {
            Log.i(TAG, "updateLikes onHeartClicked else)");
            String user_id = logIn.getText().toString();
            Log.i(TAG, "updateLikes user : " + user_id);
            String song_name = mainLogo.getText().toString();
            Log.i(TAG, "updateLikes song : " + song_name);

            if (isHeartFilled) {
                // TODO - delete liked
                updateLikes(user_id, song_name);
                Log.i(TAG, "updateLikes onHeartClicked if) isHeartFilled : " + isHeartFilled);
                heart.setImageResource(R.drawable.purple_empty_heart);

            } else {
                Log.i(TAG, "updateLikes onHeartClicked else) !isHeartFilled : " + isHeartFilled);

//                if (mainLogo.getText().equals(getSongName)) {
                // TODO - add liked
                updateLikes(user_id, song_name);
                heart.setImageResource(R.drawable.purple_full_heart);

//                } else {
//                    Log.i(TAG, "updateLikes onHexartClicked else) getSongName : " + getSongName);
//
//                } // else
            } // else

            isHeartFilled = !isHeartFilled;
            Log.i(TAG, "onHeartClicked isHeartFilled check : " + isHeartFilled);
        }
    } // onHeartClicked

    void updateLikes(String user_id, String song_name) {
        Log.i(TAG, "updateLikes");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi updateLikesApi = retrofit.create(ServerApi.class);
        retrofit2.Call<List<UpdateLikedModel>> call = updateLikesApi.updateLikes(user_id, song_name);

        call.enqueue(new retrofit2.Callback<List<UpdateLikedModel>>() {
            @Override
            public void onResponse(retrofit2.Call<List<UpdateLikedModel>> call, retrofit2.Response<List<UpdateLikedModel>> response) {
                Log.i(TAG, "updateLikes onResponse");

                if (response.isSuccessful()) {
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "updateLikes response success : " + responseBody);
                    List<UpdateLikedModel> updateLikedModels = response.body();
                    Log.i(TAG, "updateLikes response.body : " + updateLikedModels);

                } else {
                    Log.i(TAG, "updateLikes response failed : " + response.body());

                }
            } // onResponse

            @Override
            public void onFailure(retrofit2.Call<List<UpdateLikedModel>> call, Throwable t) {
                Log.i(TAG, "updateLikes onFailure : " + t.getMessage());
            } // onFailure
        });
    } // updateLikes

    void selectLikes() {
        Log.i(TAG, "selectLikes : " + logIn.getText().toString());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        String user_id = logIn.getText().toString();
        Log.i(TAG, "selectLikes user : " + user_id);

        ServerApi selectLikesApi = retrofit.create(ServerApi.class);
        retrofit2.Call<List<UpdateLikedModel>> call = selectLikesApi.selectLikes(user_id);

        call.enqueue(new retrofit2.Callback<List<UpdateLikedModel>>() {
            @Override
            public void onResponse(retrofit2.Call<List<UpdateLikedModel>> call, retrofit2.Response<List<UpdateLikedModel>> response) {
                Log.i(TAG, "selectLikes onResponse");

                if (response.isSuccessful()) {
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "selectLikes response success : " + responseBody);
                    List<UpdateLikedModel> selectModels = response.body();
                    Log.i(TAG, "selectLikes response.body : " + selectModels);

                    for (UpdateLikedModel selectModel : selectModels) {
                        String user = selectModel.getUser_id();
                        Log.i(TAG, "selectLikes user : " + user);
                        String song = selectModel.getSong_name();
                        Log.i(TAG, "selectLikes song : " + song);

                        selectLikedList.add(selectModel);
                    }
                } else {
                    Log.i(TAG, "selectLikes response failed : " + response.body());

                }
            } // onResponse

            @Override
            public void onFailure(retrofit2.Call<List<UpdateLikedModel>> call, Throwable t) {
                Log.i(TAG, "selectLikes onFailure : " + t.getMessage());
            } // onFailure
        });
    } // selectLikes

    void updateHeart() {
        selectLikes();
        String likedSongsCheck = "";

        for (int i = 0; i < selectLikedList.size(); i++) {
            likedSongsCheck = likedSongsCheck + selectLikedList.get(i).getSong_name() + ",  ";
            Log.i(TAG, "isHeartFilled for check : " + likedSongsCheck + " / " + selectLikedList.get(i).getSong_name());

            Log.i(TAG, "isHeartFilled now : " + now_song);
            Log.i(TAG, "now_song now 2 (updateHeart) : " + now_song);

            try {

                if (likedSongsCheck.contains(now_song)) {
                    Log.i(TAG, "isHeartFilled check : " + now_song + " / " + likedSongsCheck + " / " + selectLikedList.get(i).getSong_name());
                    isHeartFilled = true;
                    heart.setImageResource(R.drawable.purple_full_heart);

                } else {
                    isHeartFilled = false;
                    heart.setImageResource(R.drawable.purple_empty_heart);
                } // else
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (likedSongsCheck.length() > 100) {
                selectLikedList.clear();
            } // if
        }
    } // updateHeart

    void setMediaPlayer() {
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "mediaPlayer.setOnPreparedListener");
                mainSeekBar.setProgress(0);
                mainSeekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                isNowPlaying = true;
                startTime = System.currentTimeMillis();
                updateSeekBar();
                if (mediaPlayer.isPlaying()) {
                    startSyncComments();
                } else {
                    Log.i(TAG, "노래 일시정지");
                } // else
                Log.i(TAG, "mediaPlayer.start()");
            } // onPrepared
        }); // setOnPreparedListener

        // TODO When SeekBar click, move to time from mp3 file
        mainSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "SeekBar onProgressChanged");
                if (logIn.getText().toString().equals("LOG IN")) {

                } else {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                        Log.i(TAG, "fromUser");
                    } // if

                } // else END

                // TODO progress bar 기준으로
                if (progress == seekBar.getMax() && seekBar.getMax() > 0) {
                    changeSong();
                } // if
            } // onProgressChanged

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

        // TODO - next song streaming (Now Song)
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onCompletion(MediaPlayer mp) {
                long endTime = System.currentTimeMillis();
                long playbackTime = endTime - startTime;
                totalPlayTime += playbackTime;
                int totalPlayTimeSeconds = (int) (totalPlayTime / 1000);
                Log.i(TAG, "totalPlayTime Check now : " + totalPlayTimeSeconds);

                isPlaying = false;
                play.setText("❚❚");
                playCheck = true;

//                mediaPlayer.start();
//                changeStreaming();
//                Log.i(TAG, "Auto changeStreaming : " + mainSeekBar.getProgress() + " / " + mainSeekBar.getMax());
//                if (mainSeekBar.getProgress() == mainSeekBar.getMax()) {
//                    mainSeekBar.setProgress(0);
//                    changeSong();
//                }
                commentAdapter.clearItems();
                commentAdapter.notifyDataSetChanged();
            }
        });
    } // setMediaPlayer END

    void changeSong() {

        Glide.with(mainCtx)
                .asGif()
                .load(R.drawable.gradation)
                .centerCrop()
                .listener(new RequestListener<GifDrawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                        // GIF 파일 로드에 실패한 경우의 처리
                        return false;
                    } // onLoadFailed

                    @Override
                    public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                        // GIF 파일 로드에 성공한 경우의 처리
                        resource.setLoopCount(GifDrawable.LOOP_FOREVER); // 반복 재생 설정
                        resource.start(); // GIF 파일 재생 시작
                        return false;
                    } // onResourceReady
                }).into(mainFull);

        Log.i(TAG, "Auto changeSong");
        // TODO total views (exception
        currentTimeForViewsIncrement = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        totalPlayTime = 0;
        totalPlayTimeSecondsForViewsCount = 0;

        if (logIn.getText().toString().equals("LOG IN")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("〈 Please Check the Log In 〉");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                } // dialog setPositiveButton onClick
            }); // builder.setPositiveButton
            builder.show();

        } else {

            if (play.getText().toString().equals("")) {
                Log.i(TAG, "play의 모양이 아무것도 없을 때");

            } else {
                Log.i(TAG, "play의 모양이 아무것도 없지않을 때");

                play.setText("▶");

                Log.i(TAG, "RightPlay 버튼 클릭-------------------------------------------");

                if (mediaPlayer.isPlaying() || !playCheck) {
                    Log.i(TAG, "mediaPlayer.isPlaying() || playCheck == false");
                    play.setText("▶"); //TODO GOOD!

                    if (play.getText().toString().equals("▶")) {
                        Log.i(TAG, "[RightPlay] 플레이 모양이 재생일 때");

                        onStopButtonClick();
                    } // if

                } else {
                    Log.i(TAG, "!mediaPlayer.isPlaying() || playCheck == true");
                    play.setText("▶"); //TODO GOOD!
                    if (play.getText().toString().equals("▶")) {
                        Log.i(TAG, "[RightPlay] 플레이 모양이 재생일 때");

                        onStopButtonClick();
                    } // if
                } // else
            } // else
        } // Big else End
    } // changeSong

    private void setPlayedInsertToTable(String user, String playedSongs) {
        // TODO 곡 들을 때마다 서버로 곡 기록 보내주기
        Log.i(TAG, "played - setPlayedInsertToTable");

        retrofit2.Call<Void> call = serverApi.loadPlayedRecord(user, playedSongs);
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                Log.i(TAG, "played - onResponse");

                if (response.isSuccessful()) {
                    Log.i(TAG, "played - setPlayedInsertToTable onResponse() isSuccessful");
                    Log.i(TAG, "played - Insert (response success)  and response.body check : " + response.body());

                } else {
                    Log.i(TAG, "played - setPlayedInsertToTable onResponse() !!!!!isSuccessful");
                    Log.i(TAG, "played - Insert (response not successful)  and response.body check : " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Log.i(TAG, "played - onFailure : " + t.getMessage());
            } // onFailure
        }); // call
    } // setPlayedInsertToTable

    void setPlayedDeleteToTable(String song, String user) {
        // TODO 곡 들을 때마다 서버로 곡 기록 보내주기
        Log.i(TAG, "setDelete - setPlayedDeleteToTable : " + song + " / " + user);


        retrofit2.Call<Void> call = serverApi.deletePlayedRecord(song, user);
        call.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "setDelete - setPlayedDeleteToTable onResponse() isSuccessful");
                    Log.i(TAG, "setDelete - Insert (response success)  and response.body check : " + response.body());

                } else {
                    Log.i(TAG, "setDelete - setPlayedDeleteToTable onResponse() !!!!!isSuccessful");
                    Log.i(TAG, "setDelete - Insert (response not successful)  and response.body check : " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                Log.i(TAG, "setDelete - onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueue
    } // setPlayedDeleteToTable

    public void cutLastPlayedSong() {
        // TODO < 버튼 클릭 시 playedList 마지막 아이템의 해당 곡 제목 기준으로 서버로 스트리밍 요청
        try {
            String pastSongs = playedListShared.getString(logIn.getText().toString(), "");
            Log.i(TAG, "cutLastPlayedSong pastSongs check : " + pastSongs);

            String[] cutPastSongs = pastSongs.split("//");
            String beforeSong = cutPastSongs[cutPastSongs.length - 1];
            Log.i(TAG, "cutLastPlayedSong beforeSong : " + beforeSong);

            // TODO 테이블 지우는 내용은 여기만 설정
            setPlayedDeleteToTable(beforeSong, logIn.getText().toString());
            Log.i(TAG, "setDelete : " + beforeSong + " / " + logIn.getText().toString());

            // TODO 아래에서 name에 값을 넣어주고, 곡 제목과 아티스트가 다르게 나오게 됐을 확률 높음
            if (!pastSongs.equals("") || pastSongs != null) {
                pastSongName = beforeSong + ".mp3";
                Log.i(TAG, "cutLastPlayedSong song name check : " + pastSongName);

            }

            // TODO 끝부분 잘라서 다시 쉐어드에 넣기
            String[] cutNowPlaySongName = pastSongs.split(beforeSong + "//");
            String rePutStringInShared = cutNowPlaySongName[0];
            Log.i(TAG, "cutLastPlayedSong rePutStringInShared check : " + rePutStringInShared);
            ;
            playedListEditor.putString(logIn.getText().toString(), rePutStringInShared);
            playedListEditor.commit();

            // TODO 서버랑 통신해서 이전 재생 곡 지워주기
//            setPlayedDeleteToTable(logIn.getText().toString(), beforeSong);

        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "cutLastPlayedSong ArrayIndexOutOfBoundsException : " + e);
            pastSongisPlayingCheckEditor.putString("now", "next");
            pastSongisPlayingCheckEditor.commit();
        } // catch
    } // cutLastPlayedSong

    // TODO played에서 아이템 클릭 시 finish 되면서 intent로 보내주기
    public void setItemClickStreaming(String song_name) {
        this.selected_song = song_name;
        Log.i(TAG, "checking song_name : " + song_name);
        String playState = play.getText().toString();
        Log.i(TAG, "checking playState : " + playState);

        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

            if (playCheck == false) {

                Log.i("[checking] 버튼 클릭", "재생");
                Log.i(TAG, "[checking] playCheck : " + playCheck);

                if (!playState.equals("❚❚") || playState.equals("❚❚")) {
                    Log.i("[checking] 버튼 클릭", "일시정지가 아닐 때");
                    Log.i(TAG, "[checking] -----------------------------------------------");
                    play.setText("❚❚");
                    play.setTextSize(53);

                    try {
                        if (song_name.contains(" ")) {
                            reSongName = song_name.replace(" ", "_");
                            Log.i(TAG, "checking reSongName Check : " + reSongName);
                            Uri.Builder builder = new Uri.Builder()
                                    .appendQueryParameter("selected_song", reSongName + ".mp3");
                            String postParams = builder.build().getEncodedQuery();
                            new getJSONData().execute("http://54.180.155.66/" + "/select_file_sampling.php", postParams);
                        } // if
                    } catch (NullPointerException e) { //
                        Log.e(TAG, "checking setItemClickStreaming ERROR : " + e);
                    } // catch

                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/select_file_sampling.php").newBuilder();
                    urlBuilder.addQueryParameter("ver", "1.0");
                    String url = urlBuilder.build().toString();
                    Log.i(TAG, "checking String url 확인 : " + url);

                    reSongName = song_name.replace(" ", "_");
                    Log.i(TAG, "checking !!!!!!! setItemClickStreaming reSongName : " + reSongName);
                    RequestBody formBody = new FormBody.Builder()
                            .add("selected_song", reSongName + ".mp3".trim())
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
                            Log.e(TAG, "checking - selectStreaming play callback onFailure : " + e);
                            Log.i(TAG, "checking - selectStreaming -----------------------------------------------");
                        } // onFailure

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            Log.i(TAG, "checking - selectStreaming play callback onResponse");
                            Log.i(TAG, "checking - selectStreaming -----------------------------------------------");

                            // 서브 스레드 UI 변경할 경우 에러
                            // 메인 스레드 UI 설정
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.Q)
                                @Override
                                public void run() {

                                    try {
                                        if (!response.isSuccessful()) {
                                            // 응답 실패
                                            Log.e(TAG, "checking - selectStreaming 응답 실패 : " + response);
                                            Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();
                                            Log.i(TAG, "checking - selectStreaming  -----------------------------------------------");

                                        } else {
                                            // 응답 성공
                                            Log.i(TAG, "checking - selectStreaming  응답 성공");
                                            final String responseData = response.body().string().trim();
                                            Log.i(TAG, "checking - selectStreaming  responseData Check : " + responseData);
                                            Log.i(TAG, "checking - selectStreaming  -----------------------------------------------");

                                            if (responseData.equals("1")) {
                                                Log.i(TAG, "checking - selectStreaming responseData 가 1일 때 : " + responseData);
                                                Log.i(TAG, "checking - selectStreaming -----------------------------------------------");
//                                                Toast.makeText(getApplicationContext(), "아이디 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();

                                            } else {
                                                Log.i(TAG, "checking - selectStreaming responseData 가 1이 아닐 때 : " + responseData);
                                                Log.i(TAG, "checking - selectStreaming  -----------------------------------------------");
//                                                        startActivityString(MainActivity.class, "nickname", responseData);

                                                String songInfo = responseData;
                                                Log.i(TAG, "checking - selectStreaming songInfo Check : " + songInfo);

                                                String[] numCut = songInfo.split("___");
                                                String num = numCut[0];
                                                Log.i(TAG, "checking - selectStreaming songInfo num Check : " + num);

                                                String deleteNum = numCut[1];
                                                String[] artistCut = deleteNum.split("###");
                                                artist = artistCut[0];
                                                Log.i(TAG, "checking - selectStreaming  songInfo artist Check : " + artist);

                                                String deleteArtist = artistCut[1];
                                                String[] pathCut = deleteArtist.split("@@@");
                                                String path = pathCut[0];
                                                Log.i(TAG, "checking - selectStreaming songInfo path Check : " + path);

                                                time = pathCut[1];
                                                Log.i(TAG, "checking - selectStreaming songInfo time Check : " + time);

                                                String[] nameCut = path.split("/");

                                                if (pastSongName == "" || pastSongName == null || pastSongName.equals("")) {
                                                    name = nameCut[4];
                                                    Log.i(TAG, "checking - selectStreaming songInfo name Check (5) : " + name);
                                                    String reName = name.replace("_", " ");
                                                    Log.i(TAG, "checking - selectStreaming songInfo name Check *if : " + name);

                                                    Log.i(TAG, "checking - selectStreaming   -----------------------------------------------");

                                                } else {
                                                    // TODO check (1) < 버튼 클릭 시 name에 값 넣어주고 여기서 확인
                                                    // TODO check (2) > 버튼 클릭 시 name에 값을 넣어주고
                                                    Log.i(TAG, "checking - selectStreaming  - songInfo name Check *else before : " + reSongName);
                                                    name = reSongName + ".mp3";
                                                    Log.i(TAG, "checking - selectStreaming  - songInfo name Check *else after : " + reSongName);
                                                } // else

                                                mediaPlayer.setLooping(false);
                                                Log.i(TAG, "checking - selectStreaming  MediaPlayer 생성");

                                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                Log.i(TAG, "checking - selectStreaming mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)");

                                                Log.i(TAG, "checking - selectStreaming song name before streaming check : " + name);

                                                String uri = "http://54.180.155.66/" + name;
                                                Log.i(TAG, "checking - selectStreaming  file name from music table : " + uri);
                                                // 경로
                                                mediaPlayer.setDataSource(uri);

                                                isPlaying = true;
//                                                play.setText("❚❚");
                                                Log.i(TAG, "checking - selectStreaming mediaPlayer.setDataSource(path)");

                                                mediaPlayer.prepareAsync();
                                                Log.i(TAG, "checking - selectStreaming mediaPlayer.prepareAsync()");

                                                // TODO
                                                mainSeekBar.setMax(mediaPlayer.getDuration());

                                                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                                                Log.i(TAG, "checking - selectStreaming mediaPlayer.setWakeMode");
                                                Log.i(TAG, "checking - selectStreaming  -----------------------------------------------");

//                                                gif.playing();

                                                // TODO gif 직접추가
                                                Glide.with(mainCtx)
                                                        .asGif()
                                                        .load(R.drawable.gradation)
                                                        .centerCrop()
                                                        .listener(new RequestListener<GifDrawable>() {
                                                            @Override
                                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                                                // GIF 파일 로드에 실패한 경우의 처리
                                                                return false;
                                                            } // onLoadFailed

                                                            @Override
                                                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                // GIF 파일 로드에 성공한 경우의 처리
                                                                resource.setLoopCount(GifDrawable.LOOP_FOREVER); // 반복 재생 설정
                                                                resource.start(); // GIF 파일 재생 시작
                                                                return false;
                                                            } // onResourceReady
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

                                                // TODO ADD for SeekBar Moving
                                                if (mediaPlayer.isPlaying()) {
                                                    mediaPlayer.stop();

                                                    try {
                                                        mediaPlayer.prepare();

                                                    } catch (IllegalStateException e) {
                                                        e.printStackTrace();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    } // catch
                                                    mediaPlayer.seekTo(0);
                                                    mainSeekBar.setProgress(0);

                                                } else {
                                                    mediaPlayer.start();
                                                    Thread();
                                                } // else

                                                // TODO TOAST
                                                Toast.makeText(getApplicationContext(), "♫", Toast.LENGTH_SHORT).show();
                                                toast.show();

                                                updateSeekBar();

                                                songTime = findViewById(R.id.mainToPlayTime);
                                                songTime.setText(time);

                                                String[] exceptMp3 = name.split(".mp3");
                                                String justName = exceptMp3[0];
                                                String reReName = justName.replace("_", " ");

                                                Log.i(TAG, "checking - selectStreaming song just name in mainLogo : " + reReName);
                                                Log.i(TAG, "checking - selectStreaming -----------------------------------------------");

                                                mainLogo = findViewById(R.id.mainLogo);
                                                if (artist.contains("_")) {
                                                    String artistName = artist.replace("_", " ");
                                                    mainLogo.setText(reReName + " • " + artistName);
                                                    Log.i(TAG, "checking - artist check (3) " + artistName);

                                                } else {
                                                    mainLogo.setText(reReName + " • " + artist);
                                                    Log.i(TAG, "checking - artist check (4) " + artist);
                                                }
                                                now_song = reReName;
                                                Log.i(TAG, "checking - now_song now 4 (change streaming) : " + now_song);

                                                // TODO setPlayedInsert (3) in changeStreaming
                                                String playTimingCheck = pastSongisPlayingCheckShared.getString("now", "none");
//                                                if (playTimingCheck.equals("next")) {
                                                setPlayedInsertToTable(logIn.getText().toString(), now_song);
                                                Log.i(TAG, "checking - Insert check (setPlayedInsertToTable) : " + logIn.getText().toString() + " / " + now_song);
                                                Log.i(TAG, "checking - playTimingCheck (next) : " + playTimingCheck);
                                                Log.i(TAG, "checking - now_song now 4 *if (change Streaming) : " + now_song);
                                                Log.i(TAG, "checking - playTimingCheck (past) : " + playTimingCheck);
                                                Log.i(TAG, "checking - now_song now 4 *else (change Streaming) : " + now_song);

                                                // TODO selectLikes
                                                updateHeart();

                                                if (!responseData.equals(0)) {
//                                                            responserData " + " 기준으로 잘라줘야 해
                                                    Log.i("[checking]", "responseData 가 0이 아닐 때 : " + responseData);
                                                    Log.i(TAG, "[checking] -----------------------------------------------");
                                                } // if
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } // catch
                                }
                            });
                        }
                    });
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
                    } // else if

                } else if (playState.equals("❚❚")) {
                    Log.i("checking 버튼 클릭", "일시정지 상태일 때");
                } // else if
            } // if (playCheck == true 닫아주는 중괄호

        } else {
            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
        } // else

        played = played + now_song + "//";
        String playTimingCheck = pastSongisPlayingCheckShared.getString("now", "none");
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private final BroadcastReceiver playMusicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String songName = intent.getStringExtra("selected_song");
            Log.i(TAG, "likedSongCheck now_song (before) : " + name + " / " + selected_song);
            // TODO setting issue for select song streaming (now - last song item streaming)
            selected_song = songName;
            Log.i(TAG, "likedSongCheck now_song (after) : " + name + " / " + selected_song);
            String timing = pastSongisPlayingCheckShared.getString("now", "default");
            songList.setVisibility(View.VISIBLE);

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {  // 현재 재생 중인 경우
                Log.i(TAG, "likedSongCheck timing (before) 1 : " + timing);
                pastSongisPlayingCheckEditor.putString("now", "pick");
                pastSongisPlayingCheckEditor.commit();
                Log.i(TAG, "likedSongCheck timing (after) 2 : " + timing);
                changeSong();

            } else {  // 현재 재생 중이 아닐 경우
                Log.i(TAG, "likedSongCheck timing (before) 3 : " + timing);
                pastSongisPlayingCheckEditor.putString("now", "pick");
                pastSongisPlayingCheckEditor.commit();
                // TODO (첫 재생할 때 로직 추가 *하지만 곡 이름 기준 스트리밍)

                setFirstStreaming(selected_song);
//                changeSong();
            } // else
        } // onReceive
    }; // BroadcastReceiver

    void setFirstStreaming(String selected_song) {

        currentTimeForViewsIncrement = System.currentTimeMillis();

        if (logIn.getText().toString().equals("LOG IN")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Please Check the Log In");
            builder.setMessage("로그인이 필요합니다.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.i(TAG, "setFirstStreaming 로그인이 필요합니다. OK 버튼 클릭");
                    Intent loginIntent = new Intent(mainCtx, LogIn.class);
                    startActivity(loginIntent);
                }
            });
            builder.show();

        } else {
            setMediaPlayer();
            if (logIn.getText().toString().equals("LOG IN")) {
                mainSeekBar.setVisibility(View.GONE);
                heart.setVisibility(View.GONE);
                playingTime.setVisibility(View.GONE);
                toPlayTime.setVisibility(View.GONE);

            } else {
                mainSeekBar.setVisibility(View.VISIBLE);
                heart.setVisibility(View.VISIBLE);
                playingTime.setVisibility(View.VISIBLE);
                toPlayTime.setVisibility(View.VISIBLE);
            } // else

            int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
            if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                String playState = play.getText().toString();

                if (!playCheck) {
                    Log.i("setFirstStreaming 메인 플레이 버튼 클릭", "첫 재생");
                    Log.i(TAG, "setFirstStreaming playCheck : " + playCheck);

                    if (!playState.equals("❚❚")) {
                        Log.i("setFirstStreaming 메인 플레이 버튼 클릭", "일시정지가 아닐 때");
                        play.setText("❚❚");
                        play.setTextSize(53);

                    } // if !playCheck

                    if (selected_song.contains(" ")) {
                        reName = selected_song.replace(" ", "_") + ".mp3";
                        Log.i(TAG, "setFirstStreaming reName check : " + reName);
                    } else {
                        reName = selected_song + ".mp3";
                    } // else

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("selected_song", reName);
                    String postParams = builder.build().getEncodedQuery();
                    new getJSONData().execute("http://54.180.155.66/" + "select_file_sampling.php", postParams);

                    HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.155.66/select_file_sampling.php").newBuilder();
                    urlBuilder.addQueryParameter("ver", "1.0");
                    String url = urlBuilder.build().toString();
                    Log.i(TAG, "setFirstStreaming String url 확인 : " + url);

                    RequestBody formBody = new FormBody.Builder()
                            .add("selected_song", reName.trim())
                            .build();

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .post(formBody)
                            .build();

                    // 응답 콜백
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            Log.e(TAG, "setFirstStreaming play callback onFailure : " + e);
                        } // onFailure

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            Log.i(TAG, "setFirstStreaming play callback onResponse");

                            // 서브 스레드 UI 변경할 경우 에러
                            // 메인 스레드 UI 설정
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.Q)
                                @Override
                                public void run() {

                                    try {
                                        if (!response.isSuccessful()) {
                                            // 응답 실패
                                            Log.e("setFirstStreaming", "응답 실패 : " + response);
                                            Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Log.i("setFirstStreaming", "응답 성공");
                                            final String responseData = response.body().string().trim();
                                            Log.i("setFirstStreaming", responseData);
                                            if (responseData.equals("1")) {
                                                Log.i("[setFirstStreaming]", "responseData 가 1일 때 : " + responseData);
//                                                            Toast.makeText(getApplicationContext(), "아이디 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.i("[setFirstStreaming]", "responseData 가 1이 아닐 때 : " + responseData);

                                                String songInfo = responseData;
                                                Log.i(TAG, "setFirstStreaming songInfo Check : " + songInfo);

                                                String[] numCut = songInfo.split("___");
                                                String num = numCut[0];
                                                Log.i(TAG, "setFirstStreaming songInfo num Check : " + num);

                                                String deleteNum = numCut[1];
                                                String[] artistCut = deleteNum.split("###");
                                                artist = artistCut[0];
                                                Log.i(TAG, "setFirstStreaming songInfo artist Check : " + artist);

                                                String deleteArtist = artistCut[1];
                                                String[] pathCut = deleteArtist.split("@@@");
                                                String path = pathCut[0];
                                                Log.i(TAG, "setFirstStreaming songInfo path Check : " + path);

                                                time = pathCut[1];
                                                Log.i(TAG, "setFirstStreaming songInfo time Check : " + time);

                                                String[] nameCut = path.split("/");
                                                name = nameCut[4];
                                                Log.i(TAG, "setFirstStreaming songInfo name Check (1) : " + name);

                                                mediaPlayer.setLooping(false);

                                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                Log.i(TAG, "setFirstStreaming mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)");

                                                String uri = "http://54.180.155.66/" + name;
                                                Log.i(TAG, "setFirstStreaming file name from music table : " + uri);

                                                play.setText("❚❚");

                                                mediaPlayer.setDataSource(uri);
                                                Log.i(TAG, "setFirstStreaming mediaPlayer.setDataSource(path)");
                                                isPlaying = true;


                                                mediaPlayer.prepareAsync();
                                                Log.i(TAG, "setFirstStreaming mediaPlayer.prepareAsync()");

                                                // TODO
                                                mainSeekBar.setMax(mediaPlayer.getDuration());

                                                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                                                Log.i(TAG, "setFirstStreaming mediaPlayer.setWakeMode");

                                                Glide.with(mainCtx)
                                                        .asGif()
                                                        .load(R.drawable.gradation)
                                                        .centerCrop()
                                                        .listener(new RequestListener<GifDrawable>() {
                                                            @Override
                                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                                                // GIF 파일 로드에 실패한 경우의 처리
                                                                return false;
                                                            } // onLoadFailed

                                                            @Override
                                                            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                                                // GIF 파일 로드에 성공한 경우의 처리
                                                                resource.setLoopCount(GifDrawable.LOOP_FOREVER); // 반복 재생 설정
                                                                resource.start(); // GIF 파일 재생 시작
                                                                return false;
                                                            } // onResourceReady
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

                                                // TODO setSongListButton
                                                songList.setVisibility(View.VISIBLE);
                                                likedUser.setVisibility(View.VISIBLE);

//                                              // TODO ADD for SeekBar Moving
                                                if (mediaPlayer.isPlaying()) {
                                                    mediaPlayer.stop();

                                                    try {
                                                        mediaPlayer.prepare();
                                                    } catch (IllegalStateException e) {
                                                        e.printStackTrace();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    } // catch
                                                    mediaPlayer.seekTo(0);
                                                    mainSeekBar.setProgress(0);

                                                } else {
                                                    mediaPlayer.start();

                                                    Thread();
                                                } // else
                                                Toast.makeText(getApplicationContext(), "♫", Toast.LENGTH_SHORT).show();
                                                toast.show();

                                                updateSeekBar();

                                                songTime = findViewById(R.id.mainToPlayTime);
                                                songTime.setText(time);

                                                String[] exceptMp3 = name.split(".mp3");
                                                String justName = exceptMp3[0];
                                                String reReName = justName.replace("_", " ");
                                                Log.i(TAG, "setFirstStreaming song just name 확인 : " + reReName);

                                                if (artist.contains("_")) {
                                                    String artistName = artist.replace("_", " ");
                                                    mainLogo.setText(reReName + " • " + artistName);
                                                    Log.i(TAG, "setFirstStreaming artist check (1) " + artistName);
                                                } else {
                                                    mainLogo.setText(reReName + " • " + artist);
                                                    Log.i(TAG, "setFirstStreaming artist check (2) " + artist);
                                                } // else
                                                now_song = reReName;
                                                Log.i(TAG, "setFirstStreamingnow_song now 1 (first play) : " + now_song);
                                                updateHeart();

                                                // TODO now_song이 null이여서 빈 값이 들어가
                                                try {
                                                    Log.i(TAG, "setFirstStreaming now_song now 5 (before insert) : " + now_song);
                                                    // TODO setPlayedInsert (1) in play.setOnClickListener
                                                    if (now_song.length() > 2 || now_song != null) {
                                                        setPlayedInsertToTable(logIn.getText().toString(), now_song);
                                                        Log.i(TAG, "setFirstStreaming - now_song now 3 (when insert) : " + now_song);
                                                    } // if

                                                } catch (NullPointerException e) {
                                                    Log.e(TAG, "setFirstStreaming now_song now NULL : " + e);
                                                } // catch
//                } // if
                                                if (!responseData.equals(0)) {
                                                    Log.i("[Main]", "setFirstStreaming responseData 가 0이 아닐 때 : " + responseData);
                                                } // if
                                            } // else
                                        } // else
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } // catch
                                } // run
                            }); // runOnUi
                        } // onResponse
                    }); // if !playCheck
                    playCheck = true;
                } // if
            } else {
                String playState = play.getText().toString();

                if (!playState.equals("❚❚")) {
                    Log.i("setFirstStreaming 메인 플레이 버튼 클릭", "재시작");
                    Log.i(TAG, "setFirstStreaming playCheck : " + playCheck);
                    mediaPlayer.start();
                    updateSeekBar();
                    play.setText("❚❚");

                    startTime = System.currentTimeMillis();
                    if (mediaPlayer == null) {
                        Log.i(TAG, "setFirstStreaming > btn on Click (mp == null)");
                        mediaPlayer.start();

                    } else if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.seekTo(playPosition);
                        Log.i(TAG, "setFirstStreaming playPosition Check : " + playPosition);
                        mediaPlayer.start();
                    } // else if
                }
                if (playState.equals("❚❚")) {
                    Log.i("setFirstStreaming 메인 플레이 버튼 클릭", "일시정지 상태일 때");
                    Log.i(TAG, "setFirstStreaming playCheck : " + playCheck);

                    play.setText("▶");
                    play.setTextSize(53);

                    nowPlaying = false;

                    if (mediaPlayer != null) {
                        Log.i(TAG, "setFirstStreaming mediaPlayer.pause");
                        mediaPlayer.pause();

                    } else {
                        Log.i(TAG, "setFirstStreaming mediaPlayer == null");
                    } // else

                    playPosition = mediaPlayer.getCurrentPosition();
                    Log.d("setFirstStreaming [PAUSE CHECK]", "" + playPosition);
                    Log.i(TAG, "setFirstStreaming playCheck : " + playCheck);
                } else {
                    Log.i(TAG, "setFirstStreaming 재생 버튼 모양이 재생도 일시정지도 아님");
                } // else
            } // if (playCheck == true)
        }
    } // setFirstStreaming

} // MainActivity CLASS END