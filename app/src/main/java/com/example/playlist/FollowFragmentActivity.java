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

    public RecyclerView followRecyclerView;
    public RecyclerView.Adapter followAdapter;
    public ArrayList<FollowerModel> followerList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.follow_fragment, container, false);
        followRecyclerView = (RecyclerView) view.findViewById(R.id.followRecyclerView);
        followRecyclerView.setHasFixedSize(true);

        return view;
    } // onCreateView

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

        // TODO (1) 서버로부터 통신 받아서 'followerLiist'에 데이터들 추가

        // TODO (2) 리사이클러뷰에 데이터 전송
        followAdapter = new FollowerAdapter(followerList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        followRecyclerView.setLayoutManager(layoutManager);
        followRecyclerView.setAdapter(followAdapter);
    } // onStart

} // Fragment