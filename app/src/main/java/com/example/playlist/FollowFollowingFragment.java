package com.example.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FollowFollowingFragment extends AppCompatActivity {

    String status, user;
    TextView back, userLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_following_fragment);

        fragmentInitial();
    } // onCreate

    void fragmentInitial() {

        Intent intent = getIntent();
        status = intent.getStringExtra("status");
        user = intent.getStringExtra("user");
        Log.i("TAG", "fragmentInitial status : " + status);
        Log.i("TAG", "fragmentInitial user : " + user);

        // 객체 선언
        Fragment followFragment = new FollowFragmentActivity();
        Fragment followingFragment = new FollowingFragmentActivity();

        // 제일 처음 띄워줄 뷰를 세팅, commit까지 해줘야 함
        // TODO (1) 팔로우 버튼 클릭해서 접속 시 팔로우
        // TODO (2) 팔로잉 버튼 클릭해서 접속 시 팔로잉
        if (status.equals("follower")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, followFragment).commitAllowingStateLoss();

        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, followingFragment).commitAllowingStateLoss();
        } // else

        // 바텀 네비게이션 객체 선언
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 바텀 네비게이션 클릭 리스너 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new
                                                                         BottomNavigationView.OnNavigationItemSelectedListener() {

                                                                             @Override
                                                                             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                                                                 switch (item.getItemId()) {
                                                                                     case R.id.fragment_follow:

                                                                                         // replace (프래그먼트를 띄워줄 frameLayout, 교체할 fragment 객체)
                                                                                         getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                                                                                 followFragment).commitAllowingStateLoss();
                                                                                         return true;

                                                                                     case R.id.fragment_following:
                                                                                         getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,
                                                                                                 followingFragment).commitAllowingStateLoss();
                                                                                         return true;

                                                                                     default:
                                                                                         return false;
                                                                                 } // switch

                                                                             } // onNavigationItemSelected
                                                                         }); // setOnNavigationItemSelectedListener

        userLogo = findViewById(R.id.fragmentUser);
        userLogo.setText(user);
        back = findViewById(R.id.fragmentBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } // onClick
        }); // setOnClickListener
    } // fragmentInitial
} // CLASS