package com.example.playlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LikedList extends Activity {

    String TAG = "[LikedList Activity]";
    String song_name, now_login_user;
    Button close;
    TextView title;
    LinearLayoutManager likedLayoutManager;

    ArrayList<LikedModel> likedList = new ArrayList<>();
    LikedAdapter likedAdapter;
    RecyclerView likedRecyclerView;

    private static final String BASE_URL = "http://15.165.205.105/";
    static Context likedCtx;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_liked_list);

        initial();
    } // onCreate END

    private void initial() {
        likedCtx = LikedList.this;

        close = findViewById(R.id.likedListClose);
        setClose();
        title = findViewById(R.id.likedTitle);

        Intent intent = getIntent();
        song_name = intent.getStringExtra("selected_song");
        now_login_user = intent.getStringExtra("now_login_user");
        Log.i(TAG, "likedUser getIntent : " + now_login_user);

//        if (song_name.contains("_")) {
//            String replaceSongName = song_name.replace("_", " ");
//            title.setText(replaceSongName + " liked");
//
//        } else {
        Log.i(TAG, "song name check : " + song_name);
        if (song_name.contains("_") || song_name.length() > 0) {
            song_name.replace("_", " ");

            if (song_name.length() > 15) {
                title.setTextSize(15);
                title.setText(song_name + " liked");

            } else {
                title.setTextSize(18);
                title.setText(song_name + " liked");
            } // else
        } // if
//        }

        likedRecyclerView = findViewById(R.id.likedRecyclerView);
        likedLayoutManager = new LinearLayoutManager(this);
        likedRecyclerView.setLayoutManager(likedLayoutManager);
        likedRecyclerView.setHasFixedSize(true);
        likedAdapter = new LikedAdapter(this, likedList);
        likedRecyclerView.setAdapter(likedAdapter);

        setLikedUser();
        likedAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i(TAG, "likedAdapter onItemClick : " + position);
                Intent intent = new Intent(getApplicationContext(), Feed.class);
                String user = likedList.get(position).getUser();
                Log.i(TAG, "onItemClick user : " + user);
                ;
                Log.i(TAG, "likedAdapter onItemClick user : " + user);
                intent.putExtra("user", user);
                intent.putExtra("now_login_user", now_login_user);
                Log.i(TAG, "likedUser onItemClick : " + now_login_user);
                finish();
                //                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                Log.i(TAG, "allsongs item click song_name check : " + song_name );
//                finish();
            } // onItemClick
        });
    } // initial END

    void setClose() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } // onClick END
        }); // setOnClickListener END
    } // setClose END

    void setLikedUser() {
        Log.i(TAG, "setLikedUser");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi likedUserFetchApi = retrofit.create(ServerApi.class);

        Call<List<LikedModel>> call = likedUserFetchApi.getLikedUsers(song_name);
        call.enqueue(new Callback<List<LikedModel>>() {
            @Override
            public void onResponse(Call<List<LikedModel>> call, Response<List<LikedModel>> response) {
                Log.i(TAG, "onResponse : " + response);

                if (response.isSuccessful()) {
                    List<LikedModel> likedUsers = response.body();

                    for (LikedModel likedUser : likedUsers) {
                        String user = likedUser.getUser();
                        Log.i(TAG, "selected user : " + user);

                        likedList.add(likedUser);
                        likedAdapter.notifyDataSetChanged();
                    } // for
                } else {
                    Log.i(TAG, "response failed : " + response);
                }
            } // onResponse

            @Override
            public void onFailure(Call<List<LikedModel>> call, Throwable t) {
                Log.i(TAG, "onFailure : " + t.getMessage());
            } //onFailure
        }); // call.enqueue
    } // setLikedUsers

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    } // onTouchEvent

} // CLASS END