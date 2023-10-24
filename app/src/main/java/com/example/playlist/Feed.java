package com.example.playlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

public class Feed extends AppCompatActivity {

    private static final String TAG = "FEED";
    ImageView genreFirst, genreSecond, genreThird, profile;
    int genre_pick, genre_profile_edit, genre_profile_default;
    int genreDefaultCheck;
    TextView feedLogo, profileMusic, feedProfile, followBtn, msgBtn, followCheck, userName;
    Button close, selectProfileMusicPositiveBtn, selectProfileMusicNegativeBtn,
    selectGenrePositiveBtn, selectGenreNegativeBtn;
    ArrayList<FeedCommentModel> feedCommentList = new ArrayList<>();
    androidx.recyclerview.widget.RecyclerView feedCommentRecyclerVIew;
    FeedCommentModel feedCommentModel;
    FeedCommentAdapter feedCommentAdapter;
    LinearLayoutManager feedCommentLayoutManager;

    private static final String BASE_URL = "http://54.180.155.66/";
    public String song_name, user;
    private final int GET_GALLERY_IMAGE = 200;

    static Context feedCtx;

    int followNum, followingNum = 0;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feed);

        initial();
    } // onCreate

    private void initial() {
        feedCtx = Feed.this;

        feedLogo = findViewById(R.id.feedLogo);
        profileMusic = findViewById(R.id.feedProfileMusic);
        close = findViewById(R.id.feedCloseButton);
        profile = findViewById(R.id.feedProfileImage);
        feedProfile = findViewById(R.id.feedProfileMusic);
        userName = findViewById(R.id.userNameFeed);
        followCheck = findViewById(R.id.textView);
        followBtn = findViewById(R.id.followBtn);
        msgBtn = findViewById(R.id.msgBtn);

        genre_pick = R.drawable.genre_pick;
        genre_profile_edit = R.drawable.gray_profile_edit;
        genreDefaultCheck = R.drawable.genre_default;

        genreFirst = findViewById(R.id.genre_first);
        genreFirst.setImageResource(genreDefaultCheck);
        genreSecond = findViewById(R.id.genre_second);
        genreSecond.setImageResource(genreDefaultCheck);
        genreThird = findViewById(R.id.genre_third);
        genreThird.setImageResource(genreDefaultCheck);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        // TODO 디비에 데이터를 넣고, 수정하고, 가져올 때 기준이 될 유저 이름 (user_name)
        userName.setText(user);
        // TODO 저장 버튼 클릭 시 (feed_table)에 데이터(profile_music) 추가
        profileMusic.setText("프로필 뮤직을 선택해 주세요.");
        // TODO 나의 피드일 경우 팔로잉 버튼은 [프로필 편집] / 메시지는 [채팅 목록]
        // TODO 팔로잉 버튼 클릭 시
        // TODO (1) 상대가 나를 팔로우할 시, 디비(follow_table)에 데이터 추가, 그리고 액티비티 생성 시
        // TODO (1-1) 디비의 데이터가 몇개인지 (나를 팔로우한 사람을 세어서) 조회해서 숫자를 받아서 세팅
        // TODO (2) 내가 상대를 팔로우할 시 디비(follow_table)에 데이터 추가, 그리고 액티비티 생성 시
        // TODO (2-1) 디비의 데이터가 몇개인지 (내가 팔로우한 사람을 세어서) 조회해서 숫자를 받아서 세팅
        followCheck.setText("팔로워 " + followNum +"명 • 팔로잉 " + followingNum + "명");

        feedCommentRecyclerVIew = findViewById(R.id.feedCommentRecyclerView);
        feedCommentLayoutManager = new LinearLayoutManager(this);
        feedCommentRecyclerVIew.setLayoutManager(feedCommentLayoutManager);
        feedCommentRecyclerVIew.setHasFixedSize(true);
        feedCommentAdapter = new FeedCommentAdapter(this, feedCommentList);
        feedCommentRecyclerVIew.setAdapter(feedCommentAdapter);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }); // close

        setProfile();
        setGenreOnClick();
        setProfileMusic();
        setFeedEdit();
        setFollow();
        setUIForMe();
    } // initial END

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        } // if
        return true;
    } // onTouchEvent

    void setGenreOnClick() {
        genreFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO (1) dialog? activity? select
                // TODO (2) genre type arrangement
                // TODO (3) genre pick -> setGenre, genre pick check

                GenreSelectDialog dialog = new GenreSelectDialog();
                dialog.setListener(new GenreSelectDialog.OnGenreSelectedListener() {
                    @Override
                    public void onGenresSelected(ArrayList<String> selectedGenres) {

                    } // onGenresSelected
                }); // setListener
                dialog.show(getSupportFragmentManager(), "GenreSelect");
            } // onClick
        }); // genreFirst.setOnClickListener

        genreSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO (1) dialog? activity? select
                // TODO (2) genre type arrangement
                // TODO (3) genre pick -> setGenre, genre pick check

                GenreSelectDialog dialog = new GenreSelectDialog();
                dialog.setListener(new GenreSelectDialog.OnGenreSelectedListener() {
                    @Override
                    public void onGenresSelected(ArrayList<String> selectedGenres) {

                    } // onGenresSelected
                }); // setListener
                dialog.show(getSupportFragmentManager(), "GenreSelect");
            } // onClick
        }); //genreSecond.setOnClickListener

        genreThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO (1) dialog? activity? select
                // TODO (2) genre type arrangement
                // TODO (3) genre pick -> setGenre, genre pick check

                GenreSelectDialog dialog = new GenreSelectDialog();
                dialog.setListener(new GenreSelectDialog.OnGenreSelectedListener() {
                    @Override
                    public void onGenresSelected(ArrayList<String> selectedGenres) {

                    } // onGenresSelected
                }); // setListener
                dialog.show(getSupportFragmentManager(), "GenreSelect");
            } // onClick
        }); //genreSecond.setOnClickListener
    } // setGenre

    void setProfile() {
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            } // onClick
        }); // profile.setOnClickListener
    } // setProfile

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : " + resultCode);

        if (resultCode == RESULT_OK) {
            ArrayList<String> selected_genres = data.getStringArrayListExtra("selected_genres");
            for (String genre : selected_genres) {
                Log.i(TAG, "Selected genre: " + genre);
                // Here you can update your image view based on the selected genres
            } // for
        } // if

        if (resultCode == RESULT_OK) {
            String selected_profile_music = data.getStringExtra("selected_profile_music");
            Log.i(TAG,"profileMusic Selected: " + selected_profile_music);
            profileMusic.setText(selected_profile_music);  // 화면에 표시되는 프로필 음악 업데이트.
        } // if (resultCode == RESULT_OK)
    } // onActivityResult

    void setProfileMusic() {
        feedProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "profileMusic onClick");
                ProfileMusicSelectDialog dialog = new ProfileMusicSelectDialog();
                dialog.setListener(new ProfileMusicSelectDialog.OnProfileSelectedListener() {
                    @Override
                    public void onProfileSelected(ArrayList<String> selectedProfileMusic) {
                        Log.i(TAG, "profileMusic onProfileSelected : " + selectedProfileMusic);
                    } // onProfileSelected
                }); // setListener
                dialog.show(getSupportFragmentManager(), "ProfileMusicSelect");
            } // onClick
        }); // setOnClickListener
    } // setProfileMusic

    void setFeedEdit() {

    } // setFeedEditBtn

    void setFollow() {

    } // setFollowBtn

    void setUIForMe() {
        // TODO 나와 상대 피드 구별
        String nowLoginUser = ((MainActivity) MainActivity.mainCtx).logIn.getText().toString();
        Log.i(TAG, "setUIForMe now login user check : " + nowLoginUser + " / " + user);
        if (nowLoginUser.equals(user)) {
            followBtn.setText("프로필 편집");
            followBtn.setTextSize(12);
            msgBtn.setText("채팅 목록");
            msgBtn.setTextSize(12);
        } else {
            followBtn.setText("팔로잉");
            msgBtn.setText("메시지");
        } // else
    } // setUIForMe

    void setFollowBtn() {
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followBtn.getText().toString().equals("프로필 편집")) {
                    followBtn.setText("프로필 저장");
                    // TODO 프로필 이미지, 장르 세개 이미지 세팅 변
                    // 프로필 저장으로 텍스트 변경 -> 저장하시겠습니까?
                    // 확인 -> 저장
                    // 취소 -> 원래대로

                    // 만약 세팅된 장르가 없을 경우, 만약 세팅된 프로필 이미지가 없을 경우
                    if (profile == R.id.feedProfileImage) {

                    }

                } else if (followBtn.getText().toString().equals("프로필 저장")) {

                } // else
            } // onClick
        }); // setOnClickListener
    } // setProfileEditMode

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

} // CLASS END