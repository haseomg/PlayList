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
    ImageView genreFirst, genreSecond, genreThird, profile, feedEditBtn, feedEditCompleteBtn, followBtn, followerBtn;
    TextView feedLogo, profileMusic, feedProfile;
    Button close, selectProfileMusicPositiveBtn, selectProfileMusicNegativeBtn,
    selectGenrePositiveBtn, selectGenreNegativeBtn;
    ArrayList<FeedCommentModel> feedCommentList = new ArrayList<>();
    androidx.recyclerview.widget.RecyclerView feedCommentRecyclerVIew;
    FeedCommentModel feedCommentModel;
    FeedCommentAdapter feedCommentAdapter;
    LinearLayoutManager feedCommentLayoutManager;

    private static final String BASE_URL = "http://54.180.155.66/";
    private String song_name;
    private final int GET_GALLERY_IMAGE = 200;

    static Context feedCtx;

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

        feedEditBtn = findViewById(R.id.feedEditBtn);
        feedEditCompleteBtn = findViewById(R.id.feedEditOkBtn);
        followBtn = findViewById(R.id.followBtn);
        followerBtn = findViewById(R.id.followerBtn);

        genreFirst = findViewById(R.id.genre_first);
        genreSecond = findViewById(R.id.genre_second);
        genreThird = findViewById(R.id.genre_third);
        Intent intent = getIntent();
        String user = intent.getStringExtra("user");
        feedLogo.setText(user);
        profileMusic.setText(user + " - hello ☁︎");

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
        // TODO 피드 수정/완료 기능
        feedEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedEditBtn.setVisibility(View.INVISIBLE);
                feedEditCompleteBtn.setVisibility(View.VISIBLE);
            } // onClick
        }); // setOnClickListener

        feedEditCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedEditCompleteBtn.setVisibility(View.INVISIBLE);
                feedEditBtn.setVisibility(View.VISIBLE);
            } // onClick
        }); // setOnClickListener
    } // setFeedEditBtn

    void setFollow() {
        // TODO 팔로우/팔로우 취소 버튼
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followBtn.setVisibility(View.INVISIBLE);
                followerBtn.setVisibility(View.VISIBLE);
            } // onClick
        }); // setOnClickListener

        followerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - 팔로우를 취소하시겠습니까?
                followerBtn.setVisibility(View.INVISIBLE);
                followBtn.setVisibility(View.VISIBLE);
            } // onClick
        }); // setOnClickListener
    } // setFollowBtn

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