package com.example.playlist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FollowFragmentActivity extends Fragment {

    String TAG = "FollowFragmentActivity";
    String user, status;

    public RecyclerView followRecyclerView;
    public RecyclerView.Adapter followAdapter;
    public ArrayList<FollowerModel> followerList = new ArrayList<>();
    private static final String BASE_URL = "http://13.124.239.85/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.follow_fragment, container, false);
        followRecyclerView = (RecyclerView) view.findViewById(R.id.followRecyclerView);
        followRecyclerView.setHasFixedSize(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            user = bundle.getString("user");
            Log.i(TAG, "fetchAndDisplayFollowerList onCreateView : " + user);
            status = bundle.getString("status");
            Log.i(TAG, "fetchAndDisplayFollowerList onCreateView : " + status);
        } // if
        return view;
    } // onCreateView

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

        // TODO -리사이클러뷰에 데이터 전송
        followAdapter = new FollowerAdapter(followerList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        followRecyclerView.setLayoutManager(layoutManager);
        followRecyclerView.setAdapter(followAdapter);

//        fetchAndDisplayFollowerList("haseomg");
    } // onStart

//    void fetchAndDisplayFollowerList(String user) {
//        Log.i(TAG, "fetchAndDisplayFollowerList user check : " + user);
//        String status = "follower";
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
//        Call<List<FollowerModel>> call = followerService.getFollowList(user, status);
//        call.enqueue(new Callback<List<FollowerModel>>() {
//            @Override
//            public void onResponse(Call<List<FollowerModel>> call, Response<List<FollowerModel>> response) {
//                if (response.isSuccessful()) {
//                    String responseBody = new Gson().toJson(response.body());
//                    Log.i(TAG, "fetchAndDisplayFollowerList onResponse response.code : " + response.code());
//                    Log.i(TAG, "fetchAndDisplayFollowerList onResponse responseBody : " + responseBody);
//                    List<FollowerModel> follow = response.body();
//
//                    for (FollowerModel followerModel : follow) {
//                        Log.i(TAG, "fetchAndDisplayFollowerList onResponse getFollower");
//
//                        if (!follow.contains(null)) {
//                            Log.i(TAG, "fetchAndDisplayFollowerList onResponse !null : " + followerModel);
//
//                            String user = followerModel.getUser_name();
//                            Log.i(TAG, "fetchAndDisplayFollowerList onResponse user check: " + user);
//
//                            followerList.add(followerModel);
//
//                        } else {
//                            Log.i(TAG, "fetchAndDisplayFollowerList onResponse null : " + followerModel);
//                        } // else
//                    } // for END
//                    followAdapter.notifyDataSetChanged();
//
//                } else {
//
//                } // else
//            } // onResponse
//
//            @Override
//            public void onFailure(Call<List<FollowerModel>> call, Throwable t) {
//                Log.e(TAG, "fetchAndDisplayFollowerList onFailure : " + t.getMessage());
//            } // onFailure
//        }); //enqueue
//    } // fetchAndDisplayFollowerList
} // Fragment