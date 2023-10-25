package com.example.playlist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Feed extends AppCompatActivity {

    static Context feedCtx;
    private static final String TAG = "FEED";

    ImageView genreFirst, genreSecond, genreThird, profile;
    TextView feedLogo, profileMusic, nameFloatingButton, msgBtn, followCheck, userName;
    Button close;

    int genre_pick, profile_edit, profileDefaultCheck, genreDefaultCheck;
    int followNum, followingNum = 0;
    int genreFirstImageId, genreSecondImageId, genreThirdImageId;
    public String song_name, user, forNoneEditModeCheck, nowLoginUser, feedUser;

    ArrayList<FeedCommentModel> feedCommentList = new ArrayList<>();
    androidx.recyclerview.widget.RecyclerView feedCommentRecyclerVIew;
    FeedCommentModel feedCommentModel;
    FeedCommentAdapter feedCommentAdapter;
    LinearLayoutManager feedCommentLayoutManager;

    private ServerApi serverApi;
    private static final String BASE_URL = "http://54.180.155.66/";
    private final int GET_GALLERY_IMAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feed);

        initial();
    } // onCreate

    private void initial() {
        feedCtx = Feed.this;

        serverApi = ApiClient.getApiClient().create(ServerApi.class);

        Intent intent = getIntent();
        nowLoginUser = intent.getStringExtra("now_login_user");
        Log.i(TAG, "likedUser getIntent : " + nowLoginUser);
        user = intent.getStringExtra("user");

        feedLogo = findViewById(R.id.feedLogo);
        close = findViewById(R.id.feedCloseButton);
        profile = findViewById(R.id.feedProfileImage);
        profileMusic = findViewById(R.id.feedProfileMusic);
        userName = findViewById(R.id.feedUserName);
        followCheck = findViewById(R.id.feedFollowFollowingText);
        nameFloatingButton = findViewById(R.id.feedNameFloatingButton);
        msgBtn = findViewById(R.id.feedMsgBtn);

        genre_pick = R.drawable.genre_pick; // 장르 수정 모드 이미지
        genreDefaultCheck = R.drawable.genre_default; // 장르 기본 이미지
        genreFirstImageId = R.drawable.genre_default; // 첫번째 장르가 기본이미지 인지
        genreSecondImageId = R.drawable.genre_default; // 두번째 장르가 기본이미지 인지
        genreThirdImageId = R.drawable.genre_default; // 세번째 장르가 기본이미지 인지

        // TODO 만약 장르 이미지에 세팅된 이미지가 없을 경우 (첫 접속, 미설정 상태)
        genreFirst = findViewById(R.id.genre_first); // 첫번째 장르 이미지뷰 참조
        genreFirst.setImageResource(genreDefaultCheck); // 첫번째 장르 이미지뷰에 장르 기본 이미지 세팅
        genreSecond = findViewById(R.id.genre_second); // 두번째 장르 이미지뷰 참조
        genreSecond.setImageResource(genreDefaultCheck); // 두번째 장르 이미지뷰에 장르 기본 이미지 세팅
        genreThird = findViewById(R.id.genre_third); // 세번째 장르 이미지뷰 참조
        genreThird.setImageResource(genreDefaultCheck); // 세번째 장르 이미지뷰에 장르 기본 이미지 세팅

        // TODO 만약 프로필 이미지에 세팅된 이미지가 없을 경우
        profile_edit = R.drawable.gray_profile_edit; // 프로필 수정 모드 이미지
        profileDefaultCheck = R.drawable.gray_profile; // 프로필 기본 이미지
        profile.setImageResource(profileDefaultCheck); // 프로필 이미지뷰에 프로필 기본 이미지 세팅

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

        selectFollow(nowLoginUser, feedUser); // 내가 상대를 팔로우했을 때 버튼 이름, 백그라운드, 색상 세팅
        setClose(); // 닫기 버튼 클릭 이벤트
        setUIForMe(); // 피드 호스트 구별 (나/상대) 후 버튼에 피드 편집/채팅 목록 or 팔로우/메시지로 세팅
        setProfileImage(); // 프로필 이미지 (수정 모드에서만 클릭 이벤트)
        setGenreOnClick(); // 선호 장르 세가지 (수정 모드에서만 클릭 이벤트)
        setProfileMusic(); // 프로필 뮤직 (수정 모드에서만 클릭 이벤트)
        setNameFloatingButton(); // 팔로우/팔로잉 로직 or 피드 편집/피드 저장
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

            } // onClick
        }); // genreFirst.setOnClickListener

        genreSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGenreSelect();
            } // onClick
        }); //genreSecond.setOnClickListener

        genreThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGenreSelect();
            } // onClick
        }); //genreSecond.setOnClickListener
    } // setGenre

    void setProfileImage() {
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - Null Exception
                try {
                    if (!nowLoginUser.equals(feedUser)) {
                        // 현재 로그인한 유저가 피드의 주인이 아닐 때
                        Log.i(TAG, "profileImage onClick (if) : " + nowLoginUser + " / " + feedUser);

                    } else if (nameFloatingButton.getText().toString().equals("피드 편집")) {
                        // 현재 피드 편집 모드가 아닐 때
                        Log.i(TAG, "profileImage onClick (else if) : " + nowLoginUser + " / " + feedUser);

                    } else {
                        Log.i(TAG, "profileImage onClick (else) : " + nowLoginUser + " / " + feedUser);
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
                } // if != null
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
        profileMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "profileMusic onClick");

                try {
                    if (!nowLoginUser.equals(feedUser)) {
                        // 현재 로그인한 유저가 피드의 주인이 아닐 때
                        Log.i(TAG, "profileMusic onClick (if) : " + nowLoginUser + " / " + feedUser);

                    } else if (nameFloatingButton.getText().toString().equals("피드 편집")) {
                        // 현재 피드 편집 모드가 아닐 때
                        Log.i(TAG, "profileMusic onClick (else if) : " + nowLoginUser + " / " + feedUser);

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

    void setFollow() {
        // TODO (1) 다이얼로그 생성
        if (nameFloatingButton.getText().toString().equals("팔로우")) {
            Log.i(TAG, "setFollow (if) follow : " + nameFloatingButton.getText().toString());
            new AlertDialog.Builder(Feed.this, R.style.AlertDialogCustom)
                    .setMessage(feedUser + "님을 팔로우하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 팔로잉으로 텍스트 바꾸고 버튼 배경색과 텍스트 글씨 변집우
                            nameFloatingButton.setText("팔로잉");
                            nameFloatingButton.setBackgroundResource(R.drawable.feed_button);
                            nameFloatingButton.setTextColor(Color.parseColor("#CD6CAC6C"));
                            // TODO 팔로우 데이터 insert
                            insertFollow(nowLoginUser, feedUser);
                        } // onClick
                    }) // setPositiveButton
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        } // onClick
                    }) // setNegativeButton
                    .show();

        } else if (nameFloatingButton.getText().toString().equals("팔로잉")) {
            Log.i(TAG, "setFollow (else if) following : " + nameFloatingButton.getText().toString());
            new AlertDialog.Builder(Feed.this, R.style.AlertDialogCustom)
                    .setMessage("팔로우를 취소하시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 팔로우로 텍스트 바꾸고 버튼 배경색과 텍스트 글씨 변집우
                            nameFloatingButton.setText("팔로우");
                            nameFloatingButton.setBackgroundResource(R.drawable.follow_button);
                            nameFloatingButton.setTextColor(Color.WHITE);

                            // TODO 팔로우 데이터 delete
                            deleteFollow(nowLoginUser, feedUser);

                        } // onClick
                    }) // setPositiveButton
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        } // onClick
                    }) // setNegativeButton
                    .show();
        }
    } // setFollowBtn

    void setUIForMe() {
        // TODO 나와 상대 피드 구별

//        String nowLoginUser = ((MainActivity) MainActivity.mainCtx).logIn.getText().toString();
//        Log.i(TAG, "setUIForMe now login user check : " + nowLoginUser + " / " + user);
        if (feedUser.equals(nowLoginUser)) {
            nameFloatingButton.setText("피드 편집");
            nameFloatingButton.setTextSize(13.5F);
            nameFloatingButton.setBackgroundResource(R.drawable.feed_button);
            nameFloatingButton.setTextColor(Color.parseColor("#CD666C66"));

            msgBtn.setText("채팅 목록");
            msgBtn.setTextSize(13.5F);
            setMsgOrChatRoomBtn(); // 채팅 목록 버튼 클릭 시 채팅 목록 액티비티로 입장

        } else {
            // TODO 내가 상대를 팔로우 중일 때 (테이블에 데이터가 존재하면) - 팔로잉으로 setting
            // TODO 내가 상대를 팔로우 중이지 않을 때 (테이블에 데이터가 존재하지 않으면) - 팔로우로 setting
            nameFloatingButton.setText("팔로우");
            nameFloatingButton.setBackgroundResource(R.drawable.follow_button);
            nameFloatingButton.setTextColor(Color.WHITE);
            msgBtn.setText("메시지");
        } // else
    } // setUIForMe

    void setNameFloatingButton() {
        nameFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFollow();
                setFeedEditMode();

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
        nameFloatingButton.setText("피드 편집");

        // TODO 이 로직은 프로필과 장르가 원래 기본이미지였을 떄
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
            if (!nowLoginUser.equals(feedUser)) {
                // 현재 로그인한 유저가 피드의 주인이 아닐 때
                Log.i(TAG, "genreSelect onClick (if) : " + nowLoginUser + " / " + feedUser);

            } else if (nameFloatingButton.getText().toString().equals("피드 편집")) {
                // 현재 피드 편집 모드가 아닐 때
                Log.i(TAG, "genreSelect onClick (else if) : " + nowLoginUser + " / " + feedUser);

            } else {
                Log.i(TAG, "genreSelect onClick (else) : " + nowLoginUser + " / " + feedUser);
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

    void setFeedEditMode() {
        if (nameFloatingButton.getText().toString().equals("피드 편집")) {
            nameFloatingButton.setText("피드 저장");
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

        } else if (nameFloatingButton.getText().toString().equals("피드 저장")) {
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
    } // setFeedEditMode

    void setClose() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } // onClick
        }); // close
    } // setClose

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void insertFollow(String me, String you) {
        Log.i(TAG, "insertFollow : " + me + " / " + you);
        Call<Void> call = serverApi.insertFollow(me, you);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "insertFollow onResponse");
                if (response.isSuccessful()) {
                    Log.i(TAG, "insertFollow Method onResponse() isSuccessful");
                    Log.i(TAG, "insertFollow (response success)  and response.body check : " + response.body());

                } else {
                    Log.i(TAG, "insertFollow Method onResponse() !isSuccessful");
                    Log.i(TAG, "insertFollow (response not successful)  and response.body check : " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "insertFollow onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueue
    } // insertFollow END

    private void deleteFollow(String me, String you) {
        Log.i(TAG, "deleteFollow : " + me + " / " + you);
        Call<Void> call = serverApi.deleteFollow(me, you);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "deleteFollow onResponse");
                if (response.isSuccessful()) {
                    Log.i(TAG, "deleteFollow Method onResponse() isSuccessful");
                    Log.i(TAG, "deleteFollow (response success)  and response.body check : " + response.body());

                } else {
                    Log.i(TAG, "deleteFollow Method onResponse() !isSuccessful");
                    Log.i(TAG, "deleteFollow (response not successful)  and response.body check : " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "deleteFollow onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueue
    } // deleteFollow END

    private void selectFollow(String me, String you) {
        // TODO - 내가 상대를 팔로우했을 때 버튼 이름, 백그라운드, 색상 세팅
        Log.i(TAG, "selectFollow : " + me + " / " + you);
        Call<ResponseBody> call = serverApi.selectFollow(me, you);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "selectFollow onResponse");
                if (response.isSuccessful()) {
                    Log.i(TAG, "selectFollow Method onResponse() isSuccessful");
                    Log.i(TAG, "selectFollow (response success)  and response.body check : " + response.body());

                    try {
                        String result = response.body() != null ? response.body().string() : "";
                        if ("1".equals(result)) {
                            // 팔로우 정보가 이미 존재함
                            Log.i(TAG, "selectFollow response.body if (1) check : " + result);
                            nameFloatingButton.setText("팔로잉");
                            nameFloatingButton.setBackgroundResource(R.drawable.feed_button);
                            nameFloatingButton.setTextColor(Color.parseColor("#CD6CAC6C"));

                        } else if ("0".equals(result)) {
                            // 팔로우 정보가 없음
                            Log.i(TAG, "selectFollow response.body else check : " + result);
                        } // else if
                    } catch (NullPointerException | IOException e) {
                        e.printStackTrace();
                    } // catch

//                    if (response.body().equals("1") || response.body().equals(1)) {
//                        Log.i(TAG, "selectFollow response.body if (1) check : " + response.body());
//                        nameFloatingButton.setText("팔로잉");
//                        nameFloatingButton.setBackgroundResource(R.drawable.feed_button);
//                        nameFloatingButton.setTextColor(Color.parseColor("#CD66666C"));
//
//                    } else {
//                        Log.i(TAG, "selectFollow response.body else check : " + response.body());
//                    } // else

                } else {
                    Log.i(TAG, "selectFollow Method onResponse() !isSuccessful");
                    Log.i(TAG, "selectFollow (response not successful)  and response.body check : " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "selectFollow onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueue
    } // selectFollow END

} // CLASS END