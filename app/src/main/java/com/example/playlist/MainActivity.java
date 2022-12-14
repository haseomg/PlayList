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

        // ?????? ?????????
        Intent intent = getIntent();
        fromSignUpNickName = intent.getStringExtra("nickname");

        // ?????????????????? ????????? ?????????
        fromSharedNickName = shared.getString("id", "LOG IN");

        // LOGIN ??????
        logIn = findViewById(R.id.logInButton);


        if (!fromSharedNickName.equals("LOG IN")) {
            logIn.setText(fromSharedNickName);
        }


        // ?????????????????? ????????? ????????? ???????????? logIn ?????? ?????? ??????
        if (fromSharedNickName.equals("LOG IN")) {
            logIn.setText(intent.getStringExtra("nickname") + "'S");
            Log.i(TAG, "fromSharedNickName String ?????? default?????? ???");
        } else {
            logIn.setText(fromSharedNickName + "'S");
            Log.i(TAG, "fromSharedNickName String ?????? ??????????????? ???????????? ??? : " + fromSharedNickName);
        }
        Log.i("[Main]", "login.getText.toString() : " + logIn.getText().toString());
        if (logIn.getText().toString().equals("null'S")) {
            logIn.setText("LOG IN");
        }

        // ?????????????????? ????????? ????????? ????????? ????????? ?????? ????????? ??????
        if (fromSharedNickName.length() > 20) {
            logIn.setTextSize(10);
        } else if (fromSharedNickName.length() > 12) {
            logIn.setTextSize(12);
        } else if (fromSharedNickName.length() < 5) {
            logIn.setTextSize(15);
        }


        // ????????? ?????? ?????? ????????????
        getUserInfo();


        // ?????? ?????????
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


        // PICK ??????????????? ?????? ??????
        select = findViewById(R.id.selectableButton);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "PICK ?????? ??????");
                Intent intent = new Intent(MainActivity.this, Selectable.class);

                startActivity(intent);
            }
        });

        // COMMENT ??????????????? ?????? ??????
        comment = findViewById(R.id.commentButton);
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Comment ?????? ??????");
                Intent intent = new Intent(MainActivity.this, Comment.class);

                startActivity(intent);
            }
        });

        // LOG IN || User ??????
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "LOG IN || User ??????");

                if (logIn.getText().toString().equals("LOG IN")) {

                    Log.i(TAG, "?????? ????????? [LOG IN]??? ???");

                    Intent intent = new Intent(MainActivity.this, LogIn.class);

                    editor.remove("nickName");
                    editor.commit();

                    startActivity(intent);

                } else {

                    Log.i(TAG, "?????? ????????? [LOG IN]??? ?????? ??? || ????????? ??????'S??? ???");

                    Intent profileIntent = new Intent(MainActivity.this, Profile.class);
                    startActivity(profileIntent);

                }
            }
        });

        // ?????? ?????? ????????? ?????? (?????? ?????? ??????)
        leftPlayBtn = findViewById(R.id.leftPlayButton);
        leftPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "leftPlay ?????? ??????");

//                if (song)

            }
        });


        // ?????? ?????? ??????
        play = findViewById(R.id.mainPlayButton);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("?????? ????????? ?????? ??????", "");


                String playState = play.getText().toString();

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {


                    // ??? ????????? ???????????? 3?????? ??????????????? (?????? ??? ?????? 8???)
                    // ???????????? ????????? ?????????????

                    if (playCheck == false) {


                        Log.i("?????? ????????? ?????? ??????", "??? ??????");
                        Log.i(TAG, "playCheck : " + playCheck);


                        if (!playState.equals("??????")) {
                            Log.i("?????? ????????? ?????? ??????", "??????????????? ?????? ???");
                            play.setText("??????");
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
                            // b < ? = ? ????????? ??? ????????? ?????? ?????? ??????
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

                            Log.i(TAG, "?????? ?????? ?????? : " + firstplayNum);
                            Log.i(TAG, "?????? ?????? ????????? : " + numAdd);

                            String castNum = Integer.toString(firstplayNum);
                            Log.i(TAG, "String castNum ?????? : " + castNum);


                            // ?????? ????????????..
                            // num ?????????
                            Uri.Builder builder = new Uri.Builder()
                                    .appendQueryParameter("num", castNum);
                            String postParams = builder.build().getEncodedQuery();
                            new getJSONData().execute("http://54.180.123.224/" + "/file_sampling.php", postParams);


//                             get ?????? ???????????? ??????
                            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.123.224/file_sampling.php").newBuilder();
                            urlBuilder.addQueryParameter("ver", "1.0");
                            String url = urlBuilder.build().toString();
                            Log.i(TAG, "String url ?????? : " + url);

                            // post ???????????? ??????
                            RequestBody formBody = new FormBody.Builder()
                                    .add("num", castNum.trim())
                                    .build();
                            // num??? ????????? -> ???????????? num??? ???????????? path, name ????????? ??????

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
                                    Log.e(TAG, "play callback onFailure : " + e);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    Log.i(TAG, "play callback onResponse");

                                    // ?????? ????????? UI ????????? ?????? ??????
                                    // ?????? ????????? UI ??????
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            try {

                                                if (!response.isSuccessful()) {
                                                    // ?????? ??????
                                                    Log.e("tag", "?????? ?????? : " + response);
                                                    Toast.makeText(getApplicationContext(), "???????????? ?????? ??????", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    // ?????? ??????
                                                    Log.i("tag", "?????? ??????");
                                                    final String responseData = response.body().string().trim();
                                                    Log.i("tag", responseData);
                                                    if (responseData.equals("1")) {
                                                        Log.i("[Main]", "responseData ??? 1??? ??? : " + responseData);
                                                        Toast.makeText(getApplicationContext(), "????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.i("[Main]", "responseData ??? 1??? ?????? ??? : " + responseData);
//                                                        startActivityString(MainActivity.class, "nickname", responseData);

                                                        String songInfo = responseData;
                                                        Log.i(TAG, "String songInfo ?????? : " + songInfo);
                                                        String[] songCut = songInfo.split("@@@0");

                                                        String path = songCut[0];
                                                        String time = songCut[1];

                                                        String[] songName = path.split("/");
                                                        String name = songName[4];


                                                        Log.i(TAG, "song name ?????? : " + name);
                                                        Log.i(TAG, "song path ?????? : " + path);
                                                        Log.i(TAG, "song time ?????? : " + time);

//                                                          ?????? ???????????? ?????? ?????? ????????? ???
//                                                          ?????? ??????

                                                        closePlayer();
                                                        mediaPlayer = new MediaPlayer();
                                                        Log.i(TAG, "MediaPlayer ??????");

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

                                                        Toast.makeText(getApplicationContext(), "?????? ???", Toast.LENGTH_SHORT).show();


                                                        songTime = findViewById(R.id.mainToPlayTime);
                                                        songTime.setText(time);

                                                        String[] exceptMp3 = name.split(".mp3");
                                                        String justName = exceptMp3[0];
                                                        // _ <- ????????? ???????????? ????????? ??? ??????????

                                                        Log.i(TAG, "song just name ?????? : " + justName);


                                                        mainLogo = findViewById(R.id.mainLogo);
                                                        mainLogo.setText(justName);

                                                        if (!responseData.equals(0)) {
//                                                            responserData " + " ???????????? ???????????? ???
                                                            Log.i("[Main]", "responseData ??? 0??? ?????? ??? : " + responseData);


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
//                        Log.i(TAG, "?????? ?????? ????????? 1??? ???");
//                        // ?????? ?????? ?????? => ????????? ?????????
//                        // music ???????????? ?????? ?????? ????????? ?????? num??? path ????????????


//                    } else if (firstplayNum.equals("2")) {
//                        Log.i(TAG, "?????? ?????? ????????? 2??? ???");
//
//                    } else if (firstplayNum.equals("3")) {
//                        Log.i(TAG, "?????? ?????? ????????? 3??? ???");
//
//                    } else if (firstplayNum.equals("4")) {
//                        Log.i(TAG, "?????? ?????? ????????? 4??? ???");
//
//                    } else if (firstplayNum.equals("5")) {
//                        Log.i(TAG, "?????? ?????? ????????? 5??? ???");
//
//                    } else if (firstplayNum.equals("6")) {
//                        Log.i(TAG, "?????? ?????? ????????? 6??? ???");
//
//                    } else if (firstplayNum.equals("7")) {
//                        Log.i(TAG, "?????? ?????? ????????? 7??? ???");
//
//                    } else if (firstplayNum.equals("8")) {
//                        Log.i(TAG, "?????? ?????? ????????? 8??? ???");
//                    }
//                    Log.i(TAG, "String firstplayNum : " + firstplayNum);
//                            firstplayNum = 0;


//                        ??? ?????? playAudio
//                        playAudio();


                            // ?????? ????????? ??????
                            playCheck = true;

                        }


                        // ?????? if (playCHeck == false ???????????? ?????????
                    } else { // <-> if (playCheck == true

                        if (!playState.equals("??????")) {
                            Log.i("?????? ????????? ?????? ??????", "?????????");
                            Log.i(TAG, "playCheck : " + playCheck);
                            play.setText("??????");


//                        if (mediaPlayer == null) {
//                    mediaPlayer = MediaPlayer.create(getApplicationContext()
//                            , R.raw.friendlikeme);
//                    mediaPlayer.start();
//                } else if (!mediaPlayer.isPlaying()) {
//                    mediaPlayer.seekTo(playPosition);
//                    mediaPlayer.start();
//                }

//                    resumeAudio() ?????? ??? ???
//                    resumeAudio();

                        } else if (playState.equals("??????")) {
                            Log.i("?????? ????????? ?????? ??????", "???????????? ????????? ???");
                            Log.i(TAG, "playCheck : " + playCheck);

                            play.setText("???");
                            play.setTextSize(60);


//                    pauseAudio() ?????? ??? ???
//                    pauseAudio();


                            if (mediaPlayer != null) {
                                mediaPlayer.pause();
                                playPosition = mediaPlayer.getCurrentPosition();
                                Log.d("[PAUSE CHECK]", "" + playPosition);
                                Log.i(TAG, "playCheck : " + playCheck);

                            }

                        } // ?????? ????????? ???????????? ????????? ???
                        // + ?????? ??????
                        else {
                            Log.i(TAG, "?????? ?????? ????????? ????????? ??????????????? ??????");
                        }

                    } // if (playCheck == true ???????????? ?????????


                } else {
                    Toast.makeText(getApplicationContext(), "????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                }

            } // OnClick ????????? ???????????? ?????????
        });


        // ?????? ????????? ????????? ?????? (?????? ????????? ??????)
        rightPlayBtn =

                findViewById(R.id.rightPlayButton);
        rightPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "RightPlay ?????? ??????");

                stopAudio();
                closePlayer();


                random = new Random();
                rPlay = random.nextInt(4) + 1;
                play.setText("???");

                Log.i("[MAIN]", "int rPlay Check : " + rPlay);

//                if (rPlay == 1) {
//                    Log.i("[MAIN]", "friendLikeMe ??????");
//                    play.setText("??????");
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
//                    Log.i("[MAIN]", "waves ??????");
//                    play.setText("??????");
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
//                    Log.i("[MAIN]", "bonfire ??????");
//                    play.setText("??????");
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
//                    Log.i("[MAIN]", "rain ??????");
//                    play.setText("??????");
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

    // ????????? ????????????
    void kakaoLogout() {
        UserApiClient.getInstance().logout(error -> {
            if (error != null) {
                Log.e("[MAIN KAKAO LOGOUT]", "???????????? ??????, SDK?????? ?????? ?????????", error);
            } else {
                Log.e("[MAIN KAKAO LOGOUT]", "???????????? ??????, SDK?????? ?????? ?????????");
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
        Toast.makeText(this, "?????? ???", Toast.LENGTH_SHORT).show();
    }

    private void resumeAudio() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(playPosition);
            mediaPlayer.start();

            Toast.makeText(this, "?????????", Toast.LENGTH_SHORT).show();
        }
    }

    // Media Player ?????? ????????????
    private void pauseAudio() {
        if (mediaPlayer != null) {
            playPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();

            Log.i("[MAIN]", "stopAudio");

            Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();

            Log.i("[MAIN]", "stopAudio");


//            Toast.makeText(this, "?????????.", Toast.LENGTH_SHORT).show();
        }
    }


    // ?????? ????????????
    void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                // ???????????? ????????????
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
    }


    // ????????? ?????? ?????? ???????????? ?????????
    public void getUserInfo() {
        String TAG = "getUserInfo()";
        UserApiClient.getInstance().me((user, meError) -> {

            if (meError != null) {
                Log.e(TAG, "????????? ?????? ?????? ??????", meError);
//                logIn.setText("LOG IN");
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
                    Log.i("??????????", "");
                } else if (fromSharedNickName.equals(null) || fromSharedNickName.equals("LOG IN")) {
                    editor.putString("id", id);
                    editor.commit();
                    Log.i("?????????? 22", "");
                } else if (!id.equals(fromSharedNickName) && fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(id + "'S");
                    editor.putString("id", fromSharedNickName);
                    editor.commit();
                    Log.i("?????????? 33", "");
                }


                if (!fromSharedNickName.equals(null) || !fromSharedNickName.equals("LOG IN")) {
                    editor.putString("id", fromSharedNickName);
                    editor.commit();
                    Log.i("?????????? 44", "");
                }
                if (!id.equals(fromSharedNickName) && fromSharedNickName.equals("LOG IN")) {
                    logIn.setText(id + "'S");
                    editor.putString("id", id);
                    editor.commit();
                    Log.i("?????????? 55", "");
                }


            }
            return null;

        });
    }

    void googleInsertTable() {
        int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
        if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            // get ?????? ???????????? ??????
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://54.180.123.224/googleLogin.php").newBuilder();
            urlBuilder.addQueryParameter("ver", "1.0"); // ??????
            String url = urlBuilder.build().toString();
            Log.i("[Google]", "String url ?????? : " + url);

            // POST ???????????? ??????
            RequestBody formBody = new FormBody.Builder()
                    .add("id", personName)
                    .add("nickname", personName)
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
                    Log.i("[Google]", "" + e);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    Log.i("[Google]", "onResponse ????????? ??????");

                    // ?????? ????????? Ui ?????? ??? ?????? ??????
                    // ??????????????? Ui ??????
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {


                                if (!response.isSuccessful()) {
                                    // ?????? ??????
                                    Log.i("[Google]", "?????? ?????? : " + response);
                                    Toast.makeText(getApplicationContext(), "???????????? ?????? ??????"
                                            , Toast.LENGTH_SHORT).show();
                                } else {
                                    // ?????? ??????
                                    final String responseData = response.body().string();
                                    Log.i("[Google]", "?????? ?????? (responseData) : " + responseData);

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