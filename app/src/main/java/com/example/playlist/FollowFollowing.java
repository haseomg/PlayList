package com.example.playlist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FollowFollowing extends AppCompatActivity {

    String TAG = "FollowFollowingActivity";
    String bringUserName, bringEnterClickStatus, bringNowLoginUser;
    TextView user_logo, back, follower_btn, following_btn;
    View followerView, followingView;

    private ServerApi serverApi;
    private static final String BASE_URL = "http://13.124.239.85/";
    androidx.recyclerview.widget.RecyclerView followRecyclerView;
    // TODO - Follower (팔로워 세팅)
    ArrayList<FollowerModel> followerList = new ArrayList<>();
    ArrayList<FollowingModel> followingList = new ArrayList<>();


    FollowerModel followerModel;
    FollowingModel followingModel;
    FollowerAdapter followerAdapter;
    FollowingAdapter followingAdapter;
    LinearLayoutManager followerLayoutManager;
    LinearLayoutManager followingLayoutManager;

    // TODO - Following (팔로잉 세팅)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_following);

        initial();
    } // onCreate

    public void initial() {
        Intent intent = getIntent();
        bringUserName = intent.getStringExtra("user");
        bringEnterClickStatus = intent.getStringExtra("status");
        bringNowLoginUser = intent.getStringExtra("now_login_user");
        Log.i(TAG, "bringGetFeed user check : " + bringUserName);
        Log.i(TAG, "bringGetFeed status check : " + bringEnterClickStatus);
        Log.i(TAG, "bringGetFeed now_login_user check : " + bringNowLoginUser);

        user_logo = findViewById(R.id.followUserLogo);
        user_logo.setText(bringUserName);
        back = findViewById(R.id.followBack);
        follower_btn = findViewById(R.id.follower_btn);
        following_btn = findViewById(R.id.following_btn);
        setFollowBtnUI();

        serverApi = ApiClient.getApiClient().create(ServerApi.class);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } // onClick
        }); // back.setOnClick END

//        setFollowInitial();
        setFollowerBtn();
        setFollowingBtn();

        if (bringEnterClickStatus.equals("follower")) {
            Log.i(TAG, "bringGetFeed follower");
            bringGetFollowerData(bringUserName);
        } else {
            Log.i(TAG, "bringGetFeed following");
            bringGetFollowingData(bringUserName);
        } // else
    } // initial

    void setFollowerBtn() {
        follower_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follower_btn.setTextColor(Color.WHITE);
                following_btn.setTextColor(Color.parseColor("#5D5D9F"));
                follower_btn.setTextSize(21);
                following_btn.setTextSize(18);

//                followerView.setVisibility(View.VISIBLE);
//                followingView.setVisibility(View.INVISIBLE);

                followerView.setBackgroundColor(Color.WHITE);
                followingView.setBackgroundColor(Color.parseColor("#9AA7E4"));

                bringEnterClickStatus = "follower";
                bringGetFollowerData(bringUserName);
            } // onClick
        }); // setOnClickListener END
    } // setFollowerBtn

    void setFollowingBtn() {
        following_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                following_btn.setTextColor(Color.WHITE);
                follower_btn.setTextColor(Color.parseColor("#5D5D9F"));
                following_btn.setTextSize(21);
                follower_btn.setTextSize(18);

//                followingView.setVisibility(View.VISIBLE);
//                followerView.setVisibility(View.INVISIBLE);

                followingView.setBackgroundColor(Color.WHITE);
                followerView.setBackgroundColor(Color.parseColor("#9AA7E4"));

                bringEnterClickStatus = "following";
                bringGetFollowingData(bringUserName);
            } // onClick
        }); // setOnClickListener END
    } // setFollowingBtn

    void setFollowBtnUI() {
        followerView = findViewById(R.id.follower_btn_view);
        followingView = findViewById(R.id.following_btn_view);
        if (bringEnterClickStatus.equals("follower")) {
            follower_btn.setTextColor(Color.WHITE);
            following_btn.setTextColor(Color.parseColor("#5D5D9F"));
            follower_btn.setTextSize(21);
            following_btn.setTextSize(18);
//            followerView.setVisibility(View.VISIBLE);
//            followingView.setVisibility(View.INVISIBLE);
            followerView.setBackgroundColor(Color.WHITE);
            followingView.setBackgroundColor(Color.parseColor("#9AA7E4"));
        } else {
            following_btn.setTextColor(Color.WHITE);
            follower_btn.setTextColor(Color.parseColor("#5D5D9F"));
            following_btn.setTextSize(21);
            follower_btn.setTextSize(18);
//            followerView.setVisibility(View.INVISIBLE);
//            followingView.setVisibility(View.VISIBLE);
            followerView.setBackgroundColor(Color.parseColor("#9AA7E4"));
            followingView.setBackgroundColor(Color.WHITE);
        } // else
    } // setFollowBtnUI

    void bringGetFollowerData(String user) {
        setFollowInitial();
        Log.i(TAG, "bringGetFeed Follower user check : " + user);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi followerService = retrofit.create(ServerApi.class);
        Call<List<FollowerModel>> call = followerService.getFollowerList(user);
        call.enqueue(new Callback<List<FollowerModel>>() {
            @Override
            public void onResponse(Call<List<FollowerModel>> call, Response<List<FollowerModel>> response) {
                followerAdapter = new FollowerAdapter(FollowFollowing.this, followerList);
                followRecyclerView.setAdapter(followerAdapter);

                setFollowerDeleteButton(); // 팔로워 리스트의 팔로워 아이템 중 삭제 버튼 클릭 이벤트
                setMovingFeedClickEvent(); // 피드 액티비티로 이동

                if (response.isSuccessful()) {
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "bringGetFeed Follower Data onResponse response.code : " + response.code());
                    Log.i(TAG, "bringGetFeed Follower Data onResponse responseBody : " + responseBody);
                    List<FollowerModel> follower = response.body();
                    followerList.clear();
                    followingList.clear();

                    for (FollowerModel followerModel : follower) {
                        Log.i(TAG, "bringGetFeed Follower Data onResponse getFollower");


                        if (!follower.contains(null)) {
                            Log.i(TAG, "bringGetFeed Follower Data onResponse !null : " + followerModel);

                            String me = followerModel.getUser_name();
                            Log.i(TAG, "bringGetFeed Follower Data onResponse user check: " + me);

                            followerList.add(followerModel);
                            for (int i = 0; i < followerList.size(); i++) {
                                Log.i(TAG, "bringGetFeed Follower Data onResponse : " + followerList.get(i).getUser_name());
                            } // for

                        } else {
                            Log.i(TAG, "bringGetFeed Follower Data onResponse null : " + followerModel);
                        } // else
                    } // for END
                    followerAdapter.notifyDataSetChanged();
                } else {

                } // else
            } // onResponse

            @Override
            public void onFailure(Call<List<FollowerModel>> call, Throwable t) {
                Log.e(TAG, "bringGetFeed Follower Data onFailure : " + t.getMessage());
            } // onFailure
        }); //enqueue
    } // bringGetFollowData

    void bringGetFollowingData(String user) {
        setFollowInitial();
        Log.i(TAG, "bringGetFeed Following Data user check : " + user);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi followerService = retrofit.create(ServerApi.class);
        Call<List<FollowingModel>> call = followerService.getFollowingList(user);
        call.enqueue(new Callback<List<FollowingModel>>() {
            @Override
            public void onResponse(Call<List<FollowingModel>> call, Response<List<FollowingModel>> response) {

                if (response.isSuccessful()) {
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "bringGetFeed Following onResponse response.code : " + response.code());
                    Log.i(TAG, "bringGetFeed Following onResponse responseBody : " + responseBody);
                    List<FollowingModel> following = response.body();
                    followerList.clear();
                    followingList.clear();

                    for (FollowingModel followerModel : following) {
                        Log.i(TAG, "bringGetFeed Following onResponse getFollowing");

                        if (!following.contains(null)) {
                            Log.i(TAG, "bringGetFeed Following onResponse !null : " + followerModel);

                            String you = followerModel.getUser_name();
                            Log.i(TAG, "bringGetFeed Following onResponse user check: " + you);

                            followingList.add(followerModel);
                        } else {
                            Log.i(TAG, "bringGetFeed Following onResponse null : " + followerModel);
                        } // else
                    } // for END

                    followingAdapter = new FollowingAdapter(FollowFollowing.this, followingList);
                    setFollowingDeleteButton();
                    setFollowingReAddButton();
                    setMovingFeedClickEventFromFollowingList();

                    followRecyclerView.setAdapter(followingAdapter);
                    followingAdapter.notifyDataSetChanged();
                } else {

                } // else
            } // onResponse

            @Override
            public void onFailure(Call<List<FollowingModel>> call, Throwable t) {
                Log.e(TAG, "bringGetFeed Following onFailure : " + t.getMessage());
            } // onFailure
        }); //enqueue
    } // bringGetFollowData


//    void bringGetFollowingData(String user) {
//        setFollowInitial();
//        Log.i(TAG, "bringGetFeed Following Data user check : " + user);
//        Gson gson = new GsonBuilder()
//                .setLenient()
//                .create();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//        ServerApi followerService = retrofit.create(ServerApi.class);
//        Call<List<FollowingModel>> call = followerService.getFollowingList(user);
//        call.enqueue(new Callback<List<FollowingModel>>() {
//            @Override
//            public void onResponse(Call<List<FollowingModel>> call, Response<List<FollowingModel>> response) {
//                followingAdapter = new FollowingAdapter(FollowFollowing.this, followingList);
//                followRecyclerView.setAdapter(followingAdapter);
//
//                if (response.isSuccessful()) {
//                    String responseBody = new Gson().toJson(response.body());
//                    Log.i(TAG, "bringGetFeed Following onResponse response.code : " + response.code());
//                    Log.i(TAG, "bringGetFeed Following onResponse responseBody : " + responseBody);
//                    List<FollowingModel> following = response.body();
//                    followerList.clear();
//                    followingList.clear();
//
//
//                    for (FollowingModel followerModel : following) {
//                        Log.i(TAG, "bringGetFeed Following onResponse getFollowing");
//
//                        if (!following.contains(null)) {
//                            Log.i(TAG, "bringGetFeed Following onResponse !null : " + followerModel);
//
//                            String you = followerModel.getUser_name();
//                            Log.i(TAG, "bringGetFeed Following onResponse user check: " + you);
//
//                            followingList.add(followingModel);
//
//                        } else {
//                            Log.i(TAG, "bringGetFeed Following onResponse null : " + followerModel);
//                        } // else
//                    } // for END
//                    followingAdapter.notifyDataSetChanged();
//                } else {
//
//                } // else
//            } // onResponse
//
//            @Override
//            public void onFailure(Call<List<FollowingModel>> call, Throwable t) {
//                Log.e(TAG, "bringGetFeed Following onFailure : " + t.getMessage());
//            } // onFailure
//        }); //enqueue
//    } // bringGetFollowData

//    void setFollowInitial() {
//        followRecyclerView = findViewById(R.id.follow_recyclerVIew);
//        RecyclerView.LayoutManager layoutManager;
//        RecyclerView.Adapter adapter;
//
//        if (bringEnterClickStatus.equals("follower")) {
//            layoutManager = new LinearLayoutManager(this);
//            adapter = new FollowerAdapter(this, followerList);
//        } else {
//            layoutManager = new LinearLayoutManager(this);
//            adapter = new FollowingAdapter(this, followingList);
//        } // else
//
//        followRecyclerView.setLayoutManager(layoutManager);
//        followRecyclerView.setHasFixedSize(true);
//        followRecyclerView.setAdapter(adapter);
//    } // setFollowInitial


    void setFollowInitial() {
        if (bringEnterClickStatus.equals("follower")) {
            followRecyclerView = findViewById(R.id.follow_recyclerVIew);
            followerLayoutManager = new LinearLayoutManager(this);
            followRecyclerView.setLayoutManager(followerLayoutManager);
            followRecyclerView.setHasFixedSize(true);
            followerAdapter = new FollowerAdapter(this, followerList);
            followRecyclerView.setAdapter(followerAdapter);

        } else {
            followRecyclerView = findViewById(R.id.follow_recyclerVIew);
            followingLayoutManager = new LinearLayoutManager(this);
            followRecyclerView.setLayoutManager(followingLayoutManager);
            followRecyclerView.setHasFixedSize(true);
            followingAdapter = new FollowingAdapter(this, followingList);
            followRecyclerView.setAdapter(followingAdapter);
        } // else
    } // setFollowInitial

    private void insertFollow(String me, String you) {
        Log.i(TAG, "followingSetButton insertFollow : " + me + " / " + you);
        Call<Void> call = serverApi.insertFollow(me, you);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "followingSetButton insertFollow onResponse");
                if (response.isSuccessful()) {
                    Log.i(TAG, "followingSetButton insertFollow Method onResponse() isSuccessful");
                    Log.i(TAG, "followingSetButton insertFollow (response success)  and response.body check : " + response.body());

                } else {
                    Log.i(TAG, "followingSetButton insertFollow Method onResponse() !isSuccessful");
                    Log.i(TAG, "followingSetButton insertFollow (response not successful)  and response.body check : " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "followingSetButton insertFollow onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueue
    } // insertFollow

    private void deleteFollow(String me, String you) {
        Log.i(TAG, "followingSetButton followerSetDeleteBtn : " + me + " / " + you);
        Call<Void> call = serverApi.deleteFollow(me, you);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "followingSetButton followerSetDeleteBtn onResponse");
                if (response.isSuccessful()) {
                    Log.i(TAG, "followingSetButton followerSetDeleteBtn Method onResponse() isSuccessful");
                    Log.i(TAG, "followingSetButton followerSetDeleteBtn (response success)  and response.body check : " + response.body());

                } else {
                    Log.i(TAG, "followingSetButton followerSetDeleteBtn Method onResponse() !isSuccessful");
                    Log.i(TAG, "followingSetButton followerSetDeleteBtn (response not successful)  and response.body check : " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "followingSetButton followerSetDeleteBtn onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueue
    } // deleteFollow END

    private void setFollowingDeleteButton() {
        followingAdapter.setOnItemFollowDeleteClickListener(new FollowingAdapter.OnItemFollowDeleteButtonClickListener() {
            @Override
            public void onItemFollowDeleteButtonClick(FollowingModel followingModel) {
                // TODO - 서버와의 통신
                Log.i(TAG, "followingSetButton (activity) delete");
                deleteFollow(bringUserName, followingModel.getUser_name());
            } // onItemFollowDeleteButtonOnClick
        }); // setOnItemFollowDeleteClickListener
    } // setFollowingDeleteButton

    private void setFollowingReAddButton() {
        followingAdapter.setOnItemFollowReAddClickListener(new FollowingAdapter.OnItemFollowReAddButtonClickListener() {
            @Override
            public void onItemFollowReAddButtonClick(FollowingModel followingModel) {
                // TODO - 서버와의 통신
                Log.i(TAG, "followingSetButton (activity) reAdd");
                insertFollow(bringUserName, followingModel.getUser_name());
            } // onItemFollowReAddButtonClick
        }); // setOnItemFollowReAddClickListener
    } // setFollowingReAddButton

    private void setFollowerDeleteButton() {
        followerAdapter.setOnItemClickListener(new FollowerAdapter.OnItemClickListener() {
            @Override
            public void onItemDeleteButtonClick(FollowerModel followerModel) {
                Log.i(TAG, "followerSetDeleteBtn (activity) : " + followerModel.getUser_name());
                // TODO 해당 아이템 리사이클러뷰에서 삭제
                new AlertDialog.Builder(FollowFollowing.this, R.style.AlertDialogCustom)
                        .setMessage(followerModel.getUser_name() + "님을 팔로워 리스트에서 삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // "네" 버튼 클릭 시 수행할 작업을 여기에 작성하세요.
                                followerList.remove(followerModel);  // ArrayList에서 해당 아이템 삭제
                                followerAdapter.notifyDataSetChanged();
                                deleteFollow(followerModel.getUser_name(), bringUserName);
                            } // onClick
                        }) // setPositiveButton

                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            } // setNegativeButton
                        }) // setNegativeButton
                        .show();
            } // onItemDeleteButtonClick
        }); // setOnItemClickListener
    } // setFollowerDeleteButton

    private void setMovingFeedClickEvent() {
        followerAdapter.setOnFeedMovingEventListener(new FollowerAdapter.OnFeedMovingEventListener() {
            @Override
            public void onFeedItemClick(FollowerModel followerModel) {
                Log.i(TAG, "followerClickEvent (activity) onClick : " + followerModel.getUser_name());
                Intent intent = new Intent(FollowFollowing.this, Feed.class);
                intent.putExtra("user", followerModel.getUser_name());
                intent.putExtra("now_login_user", bringNowLoginUser);
                startActivity(intent);
            } // onFeedItemClick
        }); //setOnFeedMovingEventListener
    } // setMovingFeedClickEvent

    private void setMovingFeedClickEventFromFollowingList() {
        followingAdapter.setOnItemMovingEventClickListener(new FollowingAdapter.OnItemMovingEventClickListener() {
            @Override
            public void onFollowingListItemClick(FollowingModel followingModel) {
                Log.i(TAG, "followingClickEvent (activity) onClick : " + followingModel.getUser_name());
                Intent intent = new Intent(FollowFollowing.this, Feed.class);
                intent.putExtra("user", followingModel.getUser_name());
                intent.putExtra("now_login_user", bringNowLoginUser);
                startActivity(intent);
            } // onFollowingListItemClick
        }); // setOnItemMovingEventClickListener
    } // setMovingFeedClickEventFromFollowingList

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    } // onStart END

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    } // onResume END

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    } // onPause END

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    } // onStop END

    protected void onDestroy() { // 어플리케이션 종료시 실행
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    } // onDestroy END

} // FollowFollowing