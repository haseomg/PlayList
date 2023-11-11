package com.example.playlist;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Feed extends AppCompatActivity {

    static Context feedCtx;
    private static final String TAG = "FeedActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;

    ImageView genreFirst, genreSecond, genreThird, profile;
    TextView feedLogo, profileMusic, nameFloatingButton, msgBtn, followText, followingText, userName;
    Button close;

    Uri selectedProfileImageUri;
    int genre_pick, profile_edit, profileDefaultCheck, genreDefaultCheck;
    int genreFirstImageId, genreSecondImageId, genreThirdImageId;
    int followNum, followingNum = 0;
    public String uuidForChat, song_name, user, forNoneEditModeCheck, nowLoginUser, feedUser;
    private ArrayList<String> uuidValues;

    ArrayList<FeedCommentModel> feedCommentList = new ArrayList<>();
    androidx.recyclerview.widget.RecyclerView feedCommentRecyclerVIew;
    FeedCommentModel feedCommentModel;
    FeedCommentAdapter feedCommentAdapter;
    LinearLayoutManager feedCommentLayoutManager;

    private ServerApi serverApi;
    private static final String BASE_URL = "http://13.124.239.85/";
    private final int GET_GALLERY_IMAGE = 200;

    String selected_profileMusic, fileName, getLikeGenreFirst, getLikeGenreSecond, getLikeGenreThird;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SharedPreferences clickedGenreShared;
    SharedPreferences.Editor clickedGenreEditor;
    String getSharedProfileMusic, getSharedGenreFirst, getSharedGenreSecond, getSharedGenreThird, getSelectedProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "feedLifecycle onCreate");
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feed);

        initial();
    } // onCreate

    private void initial() {
        feedCtx = Feed.this;

        sharedPreferences = getSharedPreferences("selected_profile_music", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        clickedGenreShared = getSharedPreferences("selected_genre_position", MODE_PRIVATE);
        clickedGenreEditor = clickedGenreShared.edit();

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
        followText = findViewById(R.id.feedFollowText);
        followingText = findViewById(R.id.feedFollowingText);
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
        fetchAndDisplayFeedComments(feedUser);

        // TODO 저장 버튼 클릭 시 (feed_table)에 데이터(profile_music) 추가
        profileMusic.setText("프로필 뮤직을 선택해 주세요.");

        // TODO 나의 피드일 경우 팔로잉 버튼은 [프로필 편집] / 메시지는 [채팅 목록]
        // TODO 팔로잉 버튼 클릭 시
        // TODO (1) 상대가 나를 팔로우할 시, 디비(follow_table)에 데이터 추가, 그리고 액티비티 생성 시
        // TODO (1-1) 디비의 데이터가 몇개인지 (나를 팔로우한 사람을 세어서) 조회해서 숫자를 받아서 세팅
        // TODO (2) 내가 상대를 팔로우할 시 디비(follow_table)에 데이터 추가, 그리고 액티비티 생성 시
        // TODO (2-1) 디비의 데이터가 몇개인지 (내가 팔로우한 사람을 세어서) 조회해서 숫자를 받아서 세팅
        selectFollow(nowLoginUser, feedUser); // 내가 상대를 팔로우했을 때 버튼 이름, 백그라운드, 색상 세팅
        setFeedUserFollow(feedUser); // 팔로우•팔로잉 세팅

        feedCommentRecyclerVIew = findViewById(R.id.feedCommentRecyclerView);
        feedCommentLayoutManager = new LinearLayoutManager(this);
        feedCommentRecyclerVIew.setLayoutManager(feedCommentLayoutManager);
        feedCommentRecyclerVIew.setHasFixedSize(true);
        feedCommentAdapter = new FeedCommentAdapter(this, feedCommentList);
        feedCommentRecyclerVIew.setAdapter(feedCommentAdapter);

        setClose(); // 닫기 버튼 클릭 이벤트
        setUIForMe(); // 피드 호스트 구별 (나/상대) 후 버튼에 피드 편집/채팅 목록 or 팔로우/메시지로 세팅
        setProfileImage(); // 프로필 이미지 (수정 모드에서만 클릭 이벤트)
        setGenreOnClick(); // 선호 장르 세가지 (수정 모드에서만 클릭 이벤트)
        setProfileMusic(); // 프로필 뮤직 (수정 모드에서만 클릭 이벤트)
        setNameFloatingButton(); // 팔로우/팔로잉 로직 or 피드 편집/피드 저장
        feedFollowFollowingClickEvent(); // 팔로우/팔로잉 텍스트 클릭 시 프래그먼트
//        setMatchGenreImage(); // 피드 액티비티 접근 시 장르 이미지들 세팅
    } // initial END

    void setMatchGenreImage() {
        // TODO - 첫번째 장르 이미지 세팅 이슈 해결 필요
        for (int i = 0; i < 3; i ++) {
            if (i == 0) {
                matchGenreImage("1", genreFirst);
            }
            if (i == 1) {
                matchGenreImage("2", genreSecond);
            }
            if (i == 2) {
                matchGenreImage("3", genreThird);

            } else {

            } // else
        } // for
    } // setMatchGenreImage

    void setSelectUserFeedData() {
        // TODO - 유저 이름 기준으로 '프로필 음악', '프로필 이미지 경로', '선호 장르 3가지'
        //  프로필 음악 값이 없을 경우 '프로필 뮤직을 선택해 주세요.'
        //  프로필 이미지 경로 값이 없을 경우 기본 이미지 세팅
        //  선호 장르 값이 없을 경우 기본 이미지 세팅
        Log.i(TAG, "setSelectUserFeedData");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi feedDataFetchApi = retrofit.create(ServerApi.class);

        // TODO - 피드 주인 기준 데이터 뿌려줘야 해
        Call<List<FeedUserDataModel>> call = feedDataFetchApi.getFeedUserData(feedUser);
        call.enqueue(new Callback<List<FeedUserDataModel>>() {
            @Override
            public void onResponse(Call<List<FeedUserDataModel>> call, Response<List<FeedUserDataModel>> response) {
                Log.i(TAG, "setSelectUserFeedData onResponse");

                if (response.isSuccessful()) {
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "setSelectUserFeedData onResponse responseBody : " + responseBody);
                    List<FeedUserDataModel> feedUserDataModels = response.body();
                    Log.i(TAG, "setSelectUserFeedData onResponse response.body : " + feedUserDataModels);

                    for (FeedUserDataModel feedUserData : feedUserDataModels) {
                        String user_name = feedUserData.getUser_name();
                        Log.i(TAG, "setSelectUserFeedData user_name : " + user_name);
                        String profile_music = feedUserData.getProfile_music();
                        Log.i(TAG, "setSelectUserFeedData profile_music : " + getPro);
                        String profile_image = feedUserData.getProfile_image();
                        Log.i(TAG, "setSelectUserFeedData profile_image : ");
                        String like_genre_first = feedUserData.getLike_genre_first();
                        Log.i(TAG, "setSelectUserFeedData like_genre_first : ");
                        String like_genre_second = feedUserData.getLike_genre_second();
                        Log.i(TAG, "setSelectUserFeedData like_genre_second : ");
                        String like_genre_third = feedUserData.getLike_genre_third();
                        Log.i(TAG, "setSelectUserFeedData like_genre_third : ");
                    }
                }
            } // onResponse

            @Override
            public void onFailure(Call<List<FeedUserDataModel>> call, Throwable t) {

            } // onFailure
        }); // call.enqueue
    } // setSelectUserFeedData

    void setSelectProfileImageFromServer() {
        // TODO - 서버에서 이미지 다운로드하고 값이 있을 경우 세팅/없을 경우 기본 이미지

    } // setSelectProfileImageFromServer
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        } // if
        return true;
    } // onTouchEvent

    void matchGenreImage(String number, ImageView imageView) {
        String clickedItem = clickedGenreShared.getString(number, "0");
        Log.i(TAG, "clickedItem : " + clickedItem);
        String[] cutColor = clickedItem.split(" \\(");
        // 소문자로 변환
        String genreName = cutColor[0].toLowerCase();
        Log.i(TAG, "clickedItem *genreName 1 : " + genreName);
        // '-' 를 '_' 로 재배치
        if (genreName.contains("-")) {
            genreName.replace("-", "_");
            Log.i(TAG, "clickedItem *genreName 2 : " + genreName);
        } // if
        if (genreName.equals("r&b")) {
            genreName = "rhythmnblues";
            Log.i(TAG, "clickedItem *genreName 3 : " + genreName);
        } // if

        Log.i(TAG, "clickedItem *genreName 4 : " + genreName);
        String settingName = "R.drawable." + genreName;
        Log.i(TAG, "clickedItem *settingName (string) : " + settingName);
        getImageId(feedCtx, settingName);
        int id = feedCtx.getResources().getIdentifier("drawable/" + genreName, null, feedCtx.getPackageName());
        Log.i(TAG, "clickedItem image id check : " + id);
        imageView.setImageResource(id);
//        imageView.setImageResource(R.drawable.jazz);
    } // matchGenreImage

    public static int getImageId(Context context, String imageName) {
        return context.getResources().getIdentifier("drawable/" + imageName, null, context.getPackageName());
    }

    void setGenreOnClick() {
        // TODO - 몇번째 사진 클릭했는지 체킹 필요
        genreFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedGenreEditor.putString("selected_genre_position", "1");
                clickedGenreEditor.commit();
                setGenreSelect();
                // 다이얼로그 확인 버튼 누르면
//                matchGenreImage("1", genreFirst);
            } // onClick
        }); // genreFirst.setOnClickListener

        genreSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedGenreEditor.putString("selected_genre_position", "2");
                clickedGenreEditor.commit();
                setGenreSelect();
//                matchGenreImage("2", genreSecond);
            } // onClick
        }); //genreSecond.setOnClickListener

        genreThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedGenreEditor.putString("selected_genre_position", "3");
                clickedGenreEditor.commit();
                setGenreSelect();
//                matchGenreImage("3", genreThird);
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
                        // 외부 저장소 권한 요청
                        requestStoragePermission();

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
            selectedProfileImageUri = selectedImageUri;
            getSelectedProfileImage = selectedImageUri.toString();
            Log.i(TAG, "selectedImageUri (selectedImageUri check) : " + selectedImageUri);
            Log.i(TAG, "selectedImageUri (getSelectedProfileImage) : " + getSelectedProfileImage);

            Glide.with(Feed.this)
                    .load(selectedImageUri)
                    .apply(new RequestOptions()
                            .circleCrop()).into(profile);
            getSharedProfileMusic = sharedPreferences.getString(feedUser, "프로필 뮤직을 선택해 주세요.");
            Log.i(TAG, "getSharedProfileMusic 1 : " + getSharedProfileMusic);
            if (profileMusic.getText().toString().equals("프로필 뮤직을 선택해 주세요.")) {
                editor.putString(feedUser, "프로필 뮤직을 선택해 주세요.");
                editor.commit();

                Log.i(TAG, "saveUserProfileImageInFeed onActivityResult *else : " + getSharedProfileMusic);
                profileMusic.setText("프로필 뮤직을 선택해 주세요.");

            } else {
                Log.i(TAG, "saveUserProfileImageInFeed onActivityResult *if : " + getSharedProfileMusic);
                profileMusic.setText(getSharedProfileMusic + " ▶");

            } // else
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
            getSharedProfileMusic = sharedPreferences.getString(feedUser, "프로필 뮤직을 선택해 주세요.");
//            String selected_profile_music = data.getStringExtra("selected_profile_music");
            if (getSharedProfileMusic.equals("프로필 뮤직을 선택해 주세요.")) {
                profileMusic.setText(getSharedProfileMusic);
            } else {
                profileMusic.setText(getSharedProfileMusic + " ▶");  // 화면에 표시되는 프로필 음악 업데이트.
                Log.i(TAG, "getSharedProfileMusic 2 : " + getSharedProfileMusic);

            } // else
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
                    .setCancelable(false)
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 팔로잉으로 텍스트 바꾸고 버튼 배경색과 텍스트 글씨 변집우
                            nameFloatingButton.setText("팔로잉");
                            nameFloatingButton.setBackgroundResource(R.drawable.feed_button);
                            nameFloatingButton.setTextColor(Color.parseColor("#CD6CAC6C"));
                            // TODO 팔로우 데이터 insert
                            insertFollow(nowLoginUser, feedUser);
                            setFeedUserFollow(feedUser);
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
                    .setCancelable(false)
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 팔로우로 텍스트 바꾸고 버튼 배경색과 텍스트 글씨 변집우
                            nameFloatingButton.setText("팔로우");
                            nameFloatingButton.setBackgroundResource(R.drawable.follow_button);
                            nameFloatingButton.setTextColor(Color.WHITE);

                            // TODO 팔로우 데이터 delete
                            deleteFollow(nowLoginUser, feedUser);
                            setFeedUserFollow(feedUser);
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

            setMsgOrChatRoomBtn(); // 메시지 버튼 클릭 시
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
        Log.i(TAG, "feedLifecycle onStart()");
        setFeedUserFollow(feedUser);
    }

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "feedLifecycle onResume()");
    }

    protected void onPause() {
        super.onPause();
        Log.i(TAG, "feedLifecycle onPause()");
    }

    protected void onStop() {
        super.onStop();
        Log.i(TAG, "feedLifecycle onStop()");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "feedLifecycle onDestroy()");
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

            if (profile.getDrawable() == null || profileDefaultCheck == R.drawable.gray_profile || profileDefaultCheck == R.drawable.gray_profile_edit) {
                // 기본 이미지가 세팅되어 있을 경우에 대한 처리
                profile.setImageResource(R.drawable.gray_profile);
                profileDefaultCheck = R.drawable.gray_profile;

            } else {
                // 기본 이미지가 세팅되어 있지 않을 경우에 대한 처리
                profile.setImageResource(Integer.parseInt(String.valueOf(selectedProfileImageUri)));
                profileDefaultCheck = R.drawable.gray_profile;
            } // else
        } // if

        // TODO 원래 기본이미지가 아니였을 때 - 원래 값 세팅 = DB not insert
        // TODO - check (1) 프로필 이미지 (2) 프로필 뮤직 (3) 선호 장르 세가지 = null 허용
        //  DB에서 값 가져올 때 null 이면 기본 이미지 세팅
        //  not null이면 해당 위치에 세팅


        // TODO 수정했을 때 - 달라진 값 세팅 = DB insert 또는 update
    } // seveFeed END

    void setMsgOrChatRoomBtn() {
        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "can chat check msgBtn onClick()");
                if (msgBtn.getText().toString().equals("채팅 목록")) {
                    Intent chatRoomIntent = new Intent(Feed.this, ChatSelect.class);
                    chatRoomIntent.putExtra("before_class", "feed");
                    chatRoomIntent.putExtra("username", user);
                    startActivity(chatRoomIntent);

                } else {
                    // TODO - 메시지 버튼의 이름이 채팅 목록이 아닐 때
                    Log.i(TAG, "msgBtn check : " + msgBtn.getText().toString());

                    if (nameFloatingButton.getText().toString().equals("팔로우")) {
                        Log.i(TAG, "msgBtn chat check (if) : " + nameFloatingButton.getText().toString());
                        ;
                        new AlertDialog.Builder(Feed.this, R.style.AlertDialogCustom)
                                .setMessage(feedUser + "님을 팔로우 시 채팅이 가능합니다.")
                                .setCancelable(false)
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // "네" 버튼 클릭 시 수행할 작업을 여기에 작성하세요.

                                    } // onClick
                                }) // setPositiveButton
                                .show();

                    } else {
                        Log.i(TAG, "msgBtn chat check (else) : " + nameFloatingButton.getText().toString());
                        ;
                        // TODO - Following Status

                        // 소켓 서버 열어서 채팅 가능하게
                    } // else
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
                        for (int i = 0; i < selectedGenres.size(); i++) {
                            Log.i(TAG, "onGenresSelected: " + i + " / " + selectedGenres.get(i));
                        } // for END
                    } // onGenresSelected
                }); // setListener
                dialog.show(getSupportFragmentManager(), "GenreSelect");
            } // else

        } catch (NullPointerException e) {
            e.printStackTrace();
        } // catch
    } // setGenreSelect

    void setFeedEditMode() {
        String infoStatus = "default";

        if (nameFloatingButton.getText().toString().equals("피드 편집")) {
            nameFloatingButton.setText("피드 저장");
            if (profileMusic.getText().toString().equals("프로필 뮤직을 선택해 주세요.")) {
                Log.i(TAG, "saveUserProfileImageInFeed setFeedEditMode *if: " + profileMusic.getText().toString());
                editor.putString(feedUser, "프로필 뮤직을 선택해 주세요.");
                editor.commit();
            } else {
                Log.i(TAG, "saveUserProfileImageInFeed setFeedEditMode *else : " + profileMusic.getText().toString());

            } // else
            // TODO 프로필 이미지, 장르 세개 이미지 세팅 변화
            // TODO 피드 수정 상태 !

            if (infoStatus.equals("default")) {

            }

            if (genreFirstImageId == R.drawable.genre_default) {
                Log.i(TAG, "setFeedEditMode 1번째 genre default image");
                genreFirst.setImageResource(R.drawable.genre_pick);
                genreFirstImageId = R.drawable.genre_pick;  // update the image ID
            }

            if (genreSecondImageId == R.drawable.genre_default) {
                Log.i(TAG, "setFeedEditMode 2번째 genre default image");
                genreSecond.setImageResource(R.drawable.genre_pick);
                genreSecondImageId = R.drawable.genre_pick;
            }

            if (genreThirdImageId == R.drawable.genre_default) {
                Log.i(TAG, "setFeedEditMode 3번째 genre default image");
                genreThird.setImageResource(R.drawable.genre_pick);
                genreThirdImageId = R.drawable.genre_pick;
            }

            // 만약 세팅된 장르가 없을 경우, 만약 세팅된 프로필 이미지가 없을 경우
            if (profileDefaultCheck == R.drawable.gray_profile) {
                Log.i(TAG, "setFeedEditMode 4번째 profile default image");
                profile.setImageResource(R.drawable.gray_profile_edit);
                profileDefaultCheck = R.drawable.gray_profile_edit;
            } // if

        } else if (nameFloatingButton.getText().toString().equals("피드 저장")) {

            new AlertDialog.Builder(Feed.this, R.style.AlertDialogCustom)
                    .setMessage("피드를 저장하시겠습니까?")
                    .setCancelable(false)
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveFeed();
                            getLikeGenreFirst = clickedGenreShared.getString("1", "0");
                            getLikeGenreSecond = clickedGenreShared.getString("2", "0");
                            getLikeGenreThird = clickedGenreShared.getString("3", "0");
                            getSharedProfileMusic = sharedPreferences.getString(feedUser, "프로필 뮤직을 선택해 주세요.");

                            // TODO 디비에 이미지 저장
                            try {
                                Log.i(TAG, "saveUserProfileImageInFeed execute (try)");
                                if (selectedProfileImageUri != null) {
                                    saveUserProfileImageInFeed();
                                    Log.i(TAG, "saveUserProfileImageInFeed uri != null");

                                    insertFeedUserData(nowLoginUser, getSharedProfileMusic,
                                            "profile_image/" + fileName, getLikeGenreFirst,
                                            getLikeGenreSecond, getLikeGenreThird);
                                } else {
                                    Log.i(TAG, "saveUserProfileImageInFeed uri == null");
                                } // else

                            } catch (NullPointerException e) {
                                Log.e(TAG, "saveUserProfileImageInFeed execute (catch Null) : " + e);
                            } // catch
                            // TODO 디비에 유저의 피드 정보들 저장
                        } // onClick
                    }) // setPositiveButton
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveFeed();
                            dialog.dismiss();
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
//        if (me != you || !me.equals(you)) {

        Log.i(TAG, "selectFollow : " + me + " / " + you);
        Call<ResponseBody> call = serverApi.selectFollow(me, you);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "selectFollow onResponse");
                if (response.isSuccessful()) {
                    Log.i(TAG, "selectFollow Method onResponse() isSuccessful");
//                            // TODO [Issue] Server Down T T
//                            // TODO Check 1 - following button set issue

                    try {
                        String result = response.body() != null ? response.body().string() : "";
                        if ("0".equals(result)) {
                            Log.i(TAG, "selectFollow response.body if == 0) check : " + result);

                        } else {
                            // 팔로우 정보가 이미 존재함
                            Log.i(TAG, "selectFollow response.body else) check : " + result);
                            nameFloatingButton.setText("팔로잉");
                            nameFloatingButton.setBackgroundResource(R.drawable.feed_button);
                            nameFloatingButton.setTextColor(Color.parseColor("#CD6CAC6C"));

//                            setFeedUserFollow(feedUser);
                        } // else
                    } catch (NullPointerException | IOException e) {
                        e.printStackTrace();
                    } // catch
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

    void setFeedUserFollow(String me) {
        Call<ResponseBody> call = serverApi.setFeedUserFollow(me);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "setFeedUserFollow Method onResponse() isSuccessful");
                    Log.i(TAG, "setFeedUserFollow (response success)  and response.body check : " + response.body());

                    try {
                        String result = response.body() != null ? response.body().string() : "";
                        if ("0".equals(result)) {
                            Log.i(TAG, "setFeedUserFollow response.body if == 0) check : " + result);

                        } else {
                            // 팔로우 정보가 이미 존재함
                            Log.i(TAG, "setFeedUserFollow response.body else) check : " + result);
                            String[] cutForFollow = result.split(" / ");
                            String follow = cutForFollow[0]; // 나를 팔로우
                            followNum = Integer.parseInt(follow);
                            String follower = cutForFollow[1]; // 내가 팔로우
                            followingNum = Integer.parseInt(follower);
                            followText.setText("팔로워 " + follower + "명 ");
                            followingText.setText("• 팔로잉 " + follow + "명");
                            // TODO

                            Log.i(TAG, "followCheck onResponse follow : " + follow);
                            Log.i(TAG, "followCheck onResponse follower : " + follower);

                        } // else
                    } catch (NullPointerException | IOException e) {
                        e.printStackTrace();
                    } // catch

                } else {
                    Log.i(TAG, "setFeedUserFollow Method onResponse() !isSuccessful");
                    Log.i(TAG, "setFeedUserFollow (response not successful)  and response.body check : " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "setFeedUserFollow onFailure : " + t.getMessage());
            } // onFailure
        }); // call.
    } // setFeedUserFollow

    private void fetchAndDisplayFeedComments(String user) {
        Log.i(TAG, "fetchAndDisplayFeedComments method");

        Log.i(TAG, "fetchAndDisplayFeedComments now feed user name : " + feedUser);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi feedCommentService = retrofit.create(ServerApi.class);

        // TODO. 곡 이름 기준으로 모든 행 정보옴 가져옴
        retrofit2.Call<List<FeedCommentModel>> call = feedCommentService.getFeedComments(feedUser);
        call.enqueue(new retrofit2.Callback<List<FeedCommentModel>>() {
            @Override
            public void onResponse(retrofit2.Call<List<FeedCommentModel>> call, retrofit2.Response<List<FeedCommentModel>> response) {

                if (response.isSuccessful()) {
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "fetchAndDisplayFeedComments onResponse : " + response.code());
                    Log.i(TAG, "fetchAndDisplayFeedComments Server ResponseBody: " + responseBody);
                    Log.i(TAG, "fetchAndDisplayFeedComments Server Response: " + response);
                    Log.i(TAG, "fetchAndDisplayFeedComments Server Response.message : " + response.message());
                    Log.i(TAG, "fetchAndDisplayFeedComments response.isSuccessful");
                    List<FeedCommentModel> feedComments = response.body();

                    for (FeedCommentModel feedComment : feedComments) {
                        Log.i(TAG, "fetchAndDisplayFeedComments onResponse get Comment");
                        if (!feedComments.contains(null)) {
                            Log.i(TAG, "fetchAndDisplayFeedComments contains !null : " + feedComments);
//                        try {
//                        if (getUserName != null || !getUserName.equals("")) {
                            String song = feedComment.getSong();
                            Log.i(TAG, "fetchAndDisplayFeedComments song_name : " + song);
//                            String user = feedComment.getUser();
//                            Log.i(TAG, "fetchAndDisplayComments user_name : " + user);
                            String selected_time = feedComment.getSelected_time();
                            Log.i(TAG, "fetchAndDisplayFeedComments selected_time : " + selected_time);
                            // TODO selected_time int로 변환
                            String msg = feedComment.getMsg();
                            Log.i(TAG, "fetchAndDisplayFeedComments message : " + msg);

                            feedCommentList.add(feedComment);

                        } else {
                            Log.i(TAG, "fetchAndDisplayFeedComments contains null : " + feedComments);
                        } // else
                    } // for END
                    feedCommentAdapter.notifyDataSetChanged();
//                    } // if (comments != null)
                } else {
//                    Toast.makeText(Feed.this, "check the comment response", Toast.LENGTH_SHORT).show();
                } // else
            } // onResponse

            @Override
            public void onFailure(retrofit2.Call<List<FeedCommentModel>> call, Throwable t) {
                Log.i(TAG, "fetchAndDisplayFeedComments onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueque
    } // fetchAndDisplayFeedComments

    void feedFollowFollowingClickEvent() {
        followText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO (1) 팔로워 클릭 시 데이터 전달
                Intent intent = new Intent(Feed.this, FollowFollowing.class);
                intent.putExtra("user", feedUser);
                intent.putExtra("now_login_user", nowLoginUser);
                intent.putExtra("status", "follower");
                startActivity(intent);
            } // onClick
        }); // setOnClickListener

        followingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO (2) 팔로잉 클릭 시 데이터 전달
                Intent intent = new Intent(Feed.this, FollowFollowing.class);
                intent.putExtra("user", feedUser);
                intent.putExtra("status", "following");
                startActivity(intent);
            } // onClick
        }); // setOnClickListener
    } // feedFollowClickEvent

    // content:// 형식의 URI를 실제 파일 경로로 변환하는 메소드
    private String getRealPathFromUri(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            } // if
            cursor.close();
        } // if
        return filePath;
    } // getRealPathFromUri


    void saveUserProfileImageInFeed() {

        Log.i(TAG, "saveUserProfileImageInFeed method");

        // TODO (3) - 여기에다 비즈니스 로직 추가
        // TODO (4) - php 파일 생성
        // TODO (5) - 사진 업로드 확인

        String filePath = getRealPathFromUri(selectedProfileImageUri);
        Log.i(TAG, "saveUserProfileImageInFeed filePath check : " + filePath);
        String[] addFileType = filePath.split("\\.");
        String fileType = addFileType[1];
        Log.i(TAG, "saveUserProfileImageInFeed fileType check : " + fileType);
        String newFileName = nowLoginUser + "_profile_image." + fileType;
        fileName = newFileName;
        Log.i(TAG, "insertFeedUserData saveUserProfileImageInFeed newFileName : " + newFileName);
        Log.i(TAG, "insertFeedUserData saveUserProfileImageInFeed fileName : " + fileName);
        Log.i(TAG, "saveUserProfileImageInFeed newFileName : " + newFileName);
        File file = new File(String.valueOf(filePath));
        Log.i(TAG, "saveUserProfileImageInFeed f(ile exists check) : " + file.exists());
        Log.i(TAG, "saveUserProfileImageInFeed (file can read check) : " + file.canRead());
        Log.i(TAG, "saveUserProfileImageInFeed (file can write check) : " + file.canWrite());
        Log.i(TAG, "saveUserProfileImageInFeed selectedProfileImageUri : " + filePath);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", newFileName, requestFile);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi uploadProfileImageService = retrofit.create(ServerApi.class);

        Call<ResponseBody> call = uploadProfileImageService.uploadProfileImage
                (body, RequestBody.create(MediaType.parse("text/plain"), feedUser + "_profile_image"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.i(TAG, "saveUserProfileImageInFeed onResponse *파일 업로드 성공");

                    try {
                        String responseBody = response.body().string();
                        Log.i(TAG, "saveUserProfileImageInFeed *서버 응답: " + responseBody);
                    } catch (IOException e) {
                        Log.e(TAG, "saveUserProfileImageInFeed onResponse *ERROR: " + e);
                    } // catch

                } else {
                    Log.i(TAG, "saveUserProfileImageInFeed onResponse *파일 업로드 실패");
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "saveUserProfileImageInFeed onFailure : " + t.getMessage());
            } // onFailure
        }); // enqueue

    } // saveUserProfileImageInFeed

    void setFeedUserData() {
        // TODO Check (1) DB Insert Data - nowLoginUser (== me), getSelectedProfileImage, getSharedProfileMusic
        //  getSharedGenreFirst, getSharedGenreSecond, getSharedGenreThird

        Log.i(TAG, "setFeedUserData");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);

        // TODO Check (2) 'feed_user_data' 테이블에 유저의 피드 데이터 저장
        //  저장할 정보들 - image, user, profile_music, genre_first, second, third

//        Call<Void> call = serverApi.insertFeedData()
    } // setFeedUserData END

    // 권한을 요청하는 메소드
    private void requestStoragePermission() {
        Log.i(TAG, "saveUserProfileImageInFeed requestStoragePermission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "saveUserProfileImageInFeed requestStoragePermission *if (not yet)");
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    PERMISSION_REQUEST_CODE);
        } else {
            Log.i(TAG, "saveUserProfileImageInFeed requestStoragePermission *else (already)");
            // 이미 권한이 허용된 경우에 대한 처리 수행
        } // else
    } // void END

    // 권한 요청 결과 처리를 위한 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i(TAG, "saveUserProfileImageInFeed onRequestPermissionsResult : " + requestCode + " / " + permissions);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            Log.i(TAG, "saveUserProfileImageInFeed onRequestPermissionResult *if : " + requestCode);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "saveUserProfileImageInFeed onRequestPermissionResult *if permission granted: " + requestCode);
                // 권한이 허용된 경우에 대한 처리를 수행할 수 있습니다.
            } else {
                Log.i(TAG, "saveUserProfileImageInFeed onRequestPermissionResult *if permission denied: " + requestCode);
                // 권한이 거부된 경우에 대한 처리를 수행할 수 있습니다.
            } // else
        } // if
    } // method END

    // TODO - '피드 저장' 버튼 클릭 후 '네' 클릭 시 유저의 피드 데이터 저장
    void insertFeedUserData(String userName, String profileMusic, String profileImage, String likeGenreFirst, String likeGenreSecond, String likeGenreThird) {
        Log.i(TAG, "insertFeedUserData userName : " + userName);
        Log.i(TAG, "insertFeedUserData profileMusic : " + profileMusic);
        Log.i(TAG, "insertFeedUserData profileImage : " + profileImage);
        Log.i(TAG, "insertFeedUserData likeGenreFirst : " + likeGenreFirst);
        Log.i(TAG, "insertFeedUserData likeGenreSecond : " + likeGenreSecond);
        Log.i(TAG, "insertFeedUserData likeGenreThird : " + likeGenreThird);

        Call<Void> call = serverApi.insertFeedData(userName, profileMusic, profileImage, likeGenreFirst, likeGenreSecond, likeGenreThird);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "insertFeedUserData onResponse");

                if (response.isSuccessful()) {
                    Log.i(TAG, "insertFeedUserData onResponse() *SUCCESS : " + response.body());

                } else {
                    Log.i(TAG, "insertFeedUserData onResponse() *FAILURE : " + response.body());

                } // else
            } // onResponse

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "insertFeedUserData onFailure : " + t.getMessage());
            } // onFailure
        }); // enqueue
    } // insertFeedUserData

} // CLASS END