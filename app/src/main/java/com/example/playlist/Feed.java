package com.example.playlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

public class Feed extends AppCompatActivity {

    private static final String TAG = "FEED";
    ImageView genreFirst, genreSecond, genreThird, profile;
    int genre_pick, genre_profile_edit, profile_edit, profileDefaultCheck, genreDefaultCheck;
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
    int genreFirstImageId, genreSecondImageId, genreThirdImageId;
    String forNoneEditModeCheck, nowLoginUser, feedUser;

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
        Intent intent = getIntent();
        nowLoginUser = intent.getStringExtra("now_login_user");
        user = intent.getStringExtra("user");

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
        genreFirstImageId = R.drawable.genre_default;
        genreSecondImageId = R.drawable.genre_default;
        genreThirdImageId = R.drawable.genre_default;

        // TODO 만약 장르에 세팅된 이미지가 없을 경우
        genreFirst = findViewById(R.id.genre_first);
        genreFirst.setImageResource(genreDefaultCheck);
        genreSecond = findViewById(R.id.genre_second);
        genreSecond.setImageResource(genreDefaultCheck);
        genreThird = findViewById(R.id.genre_third);
        genreThird.setImageResource(genreDefaultCheck);

        // TODO 만약 프로필 이미지에 세팅된 이미지가 없을 경우
        profile_edit = R.drawable.gray_profile_edit;
        profileDefaultCheck = R.drawable.gray_profile;
        profile.setImageResource(profileDefaultCheck);

        // TODO 디비에 데이터를 넣고, 수정하고, 가져올 때 기준이 될 유저 이름 (user_name)
        userName.setText(user);
        feedUser = userName.getText().toString();
        // TODO 저장 버튼 클릭 시 (feed_table)에 데이터(profile_music) 추가
        profileMusic.setText("프로필 뮤직을 선택해 주세요.");
        // TODO 나의 피드일 경우 팔로잉 버튼은 [프로필 편집] / 메시지는 [채팅 목록]
        // TODO 팔로잉 버튼 클릭 시
        // TODO (1) 상대가 나를 팔로우할 시, 디비(follow_table)에 데이터 추가, 그리고 액티비티 생성 시
        // TODO (1-1) 디비의 데이터가 몇개인지 (나를 팔로우한 사람을 세어서) 조회해서 숫자를 받아서 세팅
        // TODO (2) 내가 상대를 팔로우할 시 디비(follow_table)에 데이터 추가, 그리고 액티비티 생성 시
        // TODO (2-1) 디비의 데이터가 몇개인지 (내가 팔로우한 사람을 세어서) 조회해서 숫자를 받아서 세팅
        followCheck.setText("팔로워 " + followNum + "명 • 팔로잉 " + followingNum + "명");

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
        setDivideUser();
        setMsgOrChatRoomBtn();
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
                setGenreSelect();

//                if (!user.equals(userName.getText().toString())) {
//                    // 현재 로그인한 유저가 피드의 주인이 아닐 때
//
//                } else if (followBtn.getText().toString().equals("피드 저장")) {
//                    // 현재 피드 편집 모드가 아닐 때
//
//                } else {
//                    GenreSelectDialog dialog = new GenreSelectDialog();
//                    dialog.setListener(new GenreSelectDialog.OnGenreSelectedListener() {
//                        @Override
//                        public void onGenresSelected(ArrayList<String> selectedGenres) {
//
//                        } // onGenresSelected
//                    }); // setListener
//                    dialog.show(getSupportFragmentManager(), "GenreSelect");
//                } // else

            } // onClick
        }); // genreFirst.setOnClickListener

        genreSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO (1) dialog? activity? select
                // TODO (2) genre type arrangement
                // TODO (3) genre pick -> setGenre, genre pick check

                setGenreSelect();

//                GenreSelectDialog dialog = new GenreSelectDialog();
//                dialog.setListener(new GenreSelectDialog.OnGenreSelectedListener() {
//                    @Override
//                    public void onGenresSelected(ArrayList<String> selectedGenres) {
//
//                    } // onGenresSelected
//                }); // setListener
//                dialog.show(getSupportFragmentManager(), "GenreSelect");
            } // onClick
        }); //genreSecond.setOnClickListener

        genreThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO (1) dialog? activity? select
                // TODO (2) genre type arrangement
                // TODO (3) genre pick -> setGenre, genre pick check
                setGenreSelect();

//                GenreSelectDialog dialog = new GenreSelectDialog();
//                dialog.setListener(new GenreSelectDialog.OnGenreSelectedListener() {
//                    @Override
//                    public void onGenresSelected(ArrayList<String> selectedGenres) {
//
//                    } // onGenresSelected
//                }); // setListener
//                dialog.show(getSupportFragmentManager(), "GenreSelect");
            } // onClick
        }); //genreSecond.setOnClickListener
    } // setGenre

    void setProfile() {
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - Null Exception
                try {
                    if (!nowLoginUser.equals(userName.getText().toString())) {
                        // 현재 로그인한 유저가 피드의 주인이 아닐 때
                        Log.i(TAG, "profileImage onClick (if) : " + nowLoginUser + " / "  + userName.getText().toString());

                    } else if (followBtn.getText().toString().equals("피드 편집")) {
                        // 현재 피드 편집 모드가 아닐 때
                        Log.i(TAG, "profileImage onClick (else if) : " + nowLoginUser + " / "  + userName.getText().toString());

                    } else {
                        Log.i(TAG, "profileImage onClick (else) : " + nowLoginUser + " / "  + userName.getText().toString());
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, GET_GALLERY_IMAGE);
                    } // else

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } // catch

            } // onClick
        }); // profile.setOnClickListener
    } // setProfile

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult : " + resultCode);

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // TODO - DB에 넣어줄 이미지의 정보 (경로, 파일 형식)
            Uri selectedImageUri = data.getData();
            profile.setImageURI(selectedImageUri);
            // 이미지는 잘 가져오는데 프로필 뮤직이 해제됨.
        } // if

        if (resultCode == RESULT_OK) {
            try {
                ArrayList<String> selected_genres = data.getStringArrayListExtra("selected_genres");
                if (selected_genres != null) {
                    for (String genre : selected_genres) {
                        Log.i(TAG, "Selected genre: " + genre);
                    } // for
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } // catch
        } // if

        if (resultCode == RESULT_OK) {
            String selected_profile_music = data.getStringExtra("selected_profile_music");
            Log.i(TAG, "profileMusic Selected: " + selected_profile_music);
            profileMusic.setText(selected_profile_music);  // 화면에 표시되는 프로필 음악 업데이트.
        } // if (resultCode == RESULT_OK)
    } // onActivityResult

    void setProfileMusic() {
        feedProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "profileMusic onClick");

                try {
                    if (!nowLoginUser.equals(userName.getText().toString())) {
                        // 현재 로그인한 유저가 피드의 주인이 아닐 때
                        Log.i(TAG, "profileMusic onClick (if) : " + nowLoginUser + " / "  + userName.getText().toString());

                    } else if (followBtn.getText().toString().equals("피드 편집")) {
                        // 현재 피드 편집 모드가 아닐 때
                        Log.i(TAG, "profileMusic onClick (else if) : " + nowLoginUser + " / "  + userName.getText().toString());

                    } else {
                        ProfileMusicSelectDialog dialog = new ProfileMusicSelectDialog();
                        dialog.setListener(new ProfileMusicSelectDialog.OnProfileSelectedListener() {
                            @Override
                            public void onProfileSelected(ArrayList<String> selectedProfileMusic) {
                                Log.i(TAG, "profileMusic onProfileSelected (else) : " + selectedProfileMusic);
                            } // onProfileSelected
                        }); // setListener
                        dialog.show(getSupportFragmentManager(), "ProfileMusicSelect");

                    } // else

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } // catch
            } // onClick
        }); // setOnClickListener
    } // setProfileMusic

    void setFeedEdit() {

    } // setFeedEditBtn

    void setFollow() {

    } // setFollowBtn

    void setUIForMe() {
        // TODO 나와 상대 피드 구별

//        String nowLoginUser = ((MainActivity) MainActivity.mainCtx).logIn.getText().toString();
//        Log.i(TAG, "setUIForMe now login user check : " + nowLoginUser + " / " + user);
        if (feedUser.equals(nowLoginUser)) {
            followBtn.setText("피드 편집");
            followBtn.setTextSize(13.5F);
            msgBtn.setText("채팅 목록");
            msgBtn.setTextSize(13.5F);

        } else {
            followBtn.setText("팔로잉");
            msgBtn.setText("메시지");
        } // else
    } // setUIForMe

    void setDivideUser() {
        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followBtn.getText().toString().equals("피드 편집")) {
                    followBtn.setText("피드 저장");
                    // TODO 프로필 이미지, 장르 세개 이미지 세팅 변화
                    // TODO 피드 수정 상태 !

                    if (genreFirstImageId == R.drawable.genre_default) {
                        genreFirst.setImageResource(R.drawable.genre_pick);
                        genreFirstImageId = R.drawable.genre_pick;  // update the image ID
                    }

                    if (genreSecondImageId == R.drawable.genre_default) {
                        genreSecond.setImageResource(R.drawable.genre_pick);
                        genreSecondImageId = R.drawable.genre_pick;
                    }

                    if (genreThirdImageId == R.drawable.genre_default) {
                        genreThird.setImageResource(R.drawable.genre_pick);
                        genreThirdImageId = R.drawable.genre_pick;
                    }

                    // 만약 세팅된 장르가 없을 경우, 만약 세팅된 프로필 이미지가 없을 경우
                    if (profileDefaultCheck == R.drawable.gray_profile) {
                        profile.setImageResource(R.drawable.gray_profile_edit);
                        profileDefaultCheck = R.drawable.gray_profile_edit;
                    }
//                    if (genreDefaultCheck == R.id.genre_first) {
//                        genreFirst.setImageResource(R.drawable.genre_pick);
//                    }
//                    if (genreDefaultCheck == R.id.genre_second) {
//                        genreSecond.setImageResource(R.drawable.genre_pick);
//                    }
//                    if (genreDefaultCheck == R.id.genre_third) {
//                        genreThird.setImageResource(R.drawable.genre_pick);
//                    } // if

                } else if (followBtn.getText().toString().equals("피드 저장")) {
                    // TODO 디비에 해당 부분 저장

                    new AlertDialog.Builder(Feed.this, R.style.AlertDialogCustom)
                            .setMessage("피드를 저장하시겠습니까?")
                            .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // "네" 버튼 클릭 시 수행할 작업을 여기에 작성하세요.
                                    saveFeed();
                                } // onClick
                            }) // setPositiveButton
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    saveFeed();
                                } // onClick
                            }) // setNegativeButton
                            .show();

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

    void saveFeed() {
        // TODO 이 로직은 원래 기본이미지였을 떄
        followBtn.setText("피드 편집");

        if (genreFirstImageId == R.drawable.genre_pick && genreSecondImageId == R.drawable.genre_pick
                && genreThirdImageId == R.drawable.genre_pick && profileDefaultCheck == R.drawable.gray_profile_edit) {
            genreFirst.setImageResource(R.drawable.genre_default);
            genreFirstImageId = R.drawable.genre_default;  // update the image ID

            genreSecond.setImageResource(R.drawable.genre_default);
            genreSecondImageId = R.drawable.genre_default;

            genreThird.setImageResource(R.drawable.genre_default);
            genreThirdImageId = R.drawable.genre_default;

            profile.setImageResource(R.drawable.gray_profile);
            profileDefaultCheck = R.drawable.gray_profile;
        } // if
//        if (genreFirstImageId == R.drawable.genre_pick) {
//            genreFirst.setImageResource(R.drawable.genre_default);
//            genreFirstImageId = R.drawable.genre_default;
//        }
//
//        if (genreSecondImageId == R.drawable.genre_pick) {
//            genreSecond.setImageResource(R.drawable.genre_default);
//            genreSecondImageId = R.drawable.genre_default;
//        }
//
//        if (genreThirdImageId == R.drawable.genre_pick) {
//            genreThird.setImageResource(R.drawable.genre_default);
//            genreThirdImageId = R.drawable.genre_default;
//        }
//
//        // 만약 세팅된 장르가 없을 경우, 만약 세팅된 프로필 이미지가 없을 경우
//        if (profileDefaultCheck == R.drawable.gray_profile_edit) {
//            profile.setImageResource(R.drawable.gray_profile);
//            profileDefaultCheck = R.drawable.gray_profile;
//        } // if

        // TODO 원래 기본이미지가 아니였을 때 - 원래 값 세팅 = DB not insert
        // TODO 수정했을 때 - 달라진 값 세팅 = DB insert 또는 update
    } // seveFeed END

    void setMsgOrChatRoomBtn() {
        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgBtn.getText().toString().equals("채팅 목록")) {
                    Intent chatRoomIntent = new Intent(Feed.this, ChatSelect.class);
                    chatRoomIntent.putExtra("before_class", "feed");
                    chatRoomIntent.putExtra("username", user);
                    startActivity(chatRoomIntent);
                } else {

                } // else
            } // onClick
        }); // setOnClickListener
    } // setMsgOrChatRoomBtn

    void setGenreSelect() {
        try {
            if (!nowLoginUser.equals(userName.getText().toString())) {
                // 현재 로그인한 유저가 피드의 주인이 아닐 때
                Log.i(TAG, "genreSelect onClick (if) : " + nowLoginUser + " / "  + userName.getText().toString());

            } else if (followBtn.getText().toString().equals("피드 편집")) {
                // 현재 피드 편집 모드가 아닐 때
                Log.i(TAG, "genreSelect onClick (else if) : " + nowLoginUser + " / "  + userName.getText().toString());

            } else {
                Log.i(TAG, "genreSelect onClick (else) : " + nowLoginUser + " / "  + userName.getText().toString());
                GenreSelectDialog dialog = new GenreSelectDialog();
                dialog.setListener(new GenreSelectDialog.OnGenreSelectedListener() {
                    @Override
                    public void onGenresSelected(ArrayList<String> selectedGenres) {

                    } // onGenresSelected
                }); // setListener
                dialog.show(getSupportFragmentManager(), "GenreSelect");
            } // else

        } catch (NullPointerException e) {
            e.printStackTrace();
        } // catch
    } // setGenreSelect

} // CLASS END