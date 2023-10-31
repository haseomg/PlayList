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

public class FollowingFragmentActivity extends Fragment {

    String TAG = "FollowingFragmentActivity";

    public RecyclerView followingRecyclerView;
    public RecyclerView.Adapter followingAdapter;
    public ArrayList<FollowingModel> followingList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follow_fragment, container, false);
        followingRecyclerView = (RecyclerView) view.findViewById(R.id.followRecyclerView);
        followingRecyclerView.setHasFixedSize(true);

        return view;
    } // onCreateView

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

        // TODO (1) 서버로부터 통신 받아서 'followerLiist'에 데이터들 추가

        // TODO (2) 리사이클러뷰에 데이터 전송
        followingAdapter = new FollowingAdapter(followingList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        followingRecyclerView.setLayoutManager(layoutManager);
        followingRecyclerView.setAdapter(followingAdapter);
    } // onStart

} // fragment