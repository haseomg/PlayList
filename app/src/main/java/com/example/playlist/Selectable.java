package com.example.playlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class Selectable extends AppCompatActivity {

    Button back;
    SeekBar allSeekBar;
    TextView top1, top2, top3;
    TextView top1Text, top2Text, top3Text;
    ImageView home;
    LinearLayoutManager allLayoutManager;

    ArrayList<AllSongsModel> allSongsList = new ArrayList<>();
    ArrayList<TopViewsModel> topSongsList = new ArrayList<>();
    ArrayList<UpdateLikedModel> likedList = new ArrayList<>();
    AllSongListAdapter allSongsListAdapter;
    HorizontalAdapter horizontalAdapter;
    RecyclerView all_songs_recyclerView;
    RecyclerView horizontalRecyclerView;

    String getUserName, countToStr, getSongName;
    public int playing_position = -1;

    private static final String BASE_URL = "http://54.180.155.66/";

    String TAG = "[Selectable CLASS]";

    static Context selectableCtx;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectable);

        initial();
        setLikeSongs();
        setTopSongs();
        setAllSongs();
    } // onCreate END

    private void initial() {

        selectableCtx = Selectable.this;

        // TODO - TOP 3
        top1 = findViewById(R.id.top1);
        top2 = findViewById(R.id.top2);
        top3 = findViewById(R.id.top3);
        top1Text = findViewById(R.id.top1Text);
        top2Text = findViewById(R.id.top2Text);
        top3Text = findViewById(R.id.top3Text);

        Intent intent = getIntent();
        getUserName = intent.getStringExtra("user_name");
        Log.i(TAG, "getIntent) getUserName : " + getUserName);
        getSongName = intent.getStringExtra("song_name");
        Log.i(TAG, "getIntent) getSongName : " + getSongName);

        // TODO - Liked
        horizontalRecyclerView = findViewById(R.id.likedRecyclerView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
        horizontalAdapter = new HorizontalAdapter(this, likedList);
        horizontalRecyclerView.setAdapter(horizontalAdapter);

        // TODO - ALL SONGS
        all_songs_recyclerView = findViewById(R.id.allRecyclerView);
        allLayoutManager = new LinearLayoutManager(this);
        all_songs_recyclerView.setLayoutManager(allLayoutManager);
        all_songs_recyclerView.setHasFixedSize(true);
        allSongsListAdapter = new AllSongListAdapter(this, allSongsList);
        all_songs_recyclerView.setAdapter(allSongsListAdapter);
//        allSeekBar = findViewById(R.id.allSeekBar);
//        allSeekBar.setVisibility(View.GONE);

        Log.i(TAG, "allSongCheck songName : " + getSongName);

        for (int i = 0; i < allSongsList.size(); i++) {
            if (allSongsList.get(i).getName().equals(getSongName)) {
                playing_position = i;
                Log.i(TAG, "allSongCheck) Played playing_position: " + playing_position);
                break;
            } // if
        } // for

        allSongsListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.i(TAG, "Recyclerview itemclick position check : " + position);
                // 아이템 클릭 시 해당 노래 스트리밍
                AllSongsModel clickedItem = allSongsListAdapter.getAllSongsModel(position);
                String song_name = allSongsList.get(position).getName();
                Log.i(TAG, "song_name check : " + song_name);
                String artist_name = allSongsList.get(position).getArtist();
                Log.i(TAG, "artist_name check : " + artist_name);
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                Log.i(TAG, "allsongs item click song_name check : " + song_name );
//                finish();

            } // onItemClick END
        }); // setOnItemClickListener END

        home = findViewById(R.id.homeImage);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } // onClick
        }); // setOnClickListener

        back = findViewById(R.id.selectableBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } // onClick END
        }); // setOnClickListener END
    } // initial method END

    void setTopSongs() {
        Log.i(TAG, "setTopSongs");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi topSongFetchApi = retrofit.create(ServerApi.class);

        Call<List<TopViewsModel>> call = topSongFetchApi.getTopSongs();
        call.enqueue(new Callback<List<TopViewsModel>>() {
            @Override
            public void onResponse(Call<List<TopViewsModel>> call, Response<List<TopViewsModel>> response) {
                Log.i(TAG, "setTopSongs onResponse : " + response);
                if (response.isSuccessful()) {
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "setTopSongs responseBody : " + responseBody);
                    List<TopViewsModel> topSongsModels = response.body();
                    Log.i(TAG, "setTopSongs response.body : " + topSongsModels);

                    int count = 0;
                    for (TopViewsModel topViews : topSongsModels) {
                        count++;

                        String name = topViews.getName();
                        String[] fileTypeCut = name.split(".mp3");
                        String realName = fileTypeCut[0];
                        String realNameReplace = realName.replace("_", " ");
                        Log.i(TAG, "setTopSongs topViews name : " + name + " / " + realNameReplace + " / " + count);
                        String artist = topViews.getArtist();
                        Log.i(TAG, "setTopSongs topViews artist : " + artist);
                        String views = topViews.getViews();
                        Log.i(TAG, "setTopSongs topViews views : " + views);

                        // TODO  String views To Long - For converToNumber
                        long viewsCount = Long.parseLong(views);
                        Log.i(TAG, "setTopSongs topViews viewsCount : " + viewsCount);
                        convertViews(viewsCount);
                        Log.i(TAG, "convertToViews) setTopSongs topViews : " + countToStr);

                        if (count == 1) {
                            top1.setText(realNameReplace);
                            top1Text.setText("▶ " + countToStr);
                        } else if (count == 2) {
                            top2.setText(realNameReplace);
                            top2Text.setText("▶ " + countToStr);
                        } else if (count == 3) {
                            top3.setText(realNameReplace);
                            top3Text.setText("▶ " + countToStr);
                        } // else if

                        topSongsList.add(topViews);
                    } // for
                } else {

                } // else
            } // onResponse

            @Override
            public void onFailure(Call<List<TopViewsModel>> call, Throwable t) {
                Log.i(TAG, "setTopSongs onFailure : " + t);
            } // onFailure
        }); // call.enqueque
    } // setTopSongs

    void setAllSongs() {
        Log.i(TAG, "setAllSongs method");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi songFetchApi = retrofit.create(ServerApi.class);

        Call<List<AllSongsModel>> call = songFetchApi.getAllSongs();
        call.enqueue(new Callback<List<AllSongsModel>>() {
            @Override
            public void onResponse(Call<List<AllSongsModel>> call, Response<List<AllSongsModel>> response) {
                Log.i(TAG, "setAllSongs onResponse");

                if (response.isSuccessful()) {
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "setAllSongs responseBody : " + responseBody);
                    List<AllSongsModel> allSongsModels = response.body();
                    Log.i(TAG, "setAllSongs response.body : " + allSongsModels);

//                    if (allSongsModels != null) {
                    allSongsListAdapter.clearItems();

                    for (AllSongsModel allSongs : allSongsModels) {
                        String name = allSongs.getName();
                        Log.i(TAG, "setAllSongs song : " + name);
                        String artist = allSongs.getArtist();
                        Log.i(TAG, "setAllSongs artist : " + artist);
                        String time = allSongs.getTime();
                        Log.i(TAG, "setAllSongs time : " + time);
                        String views = allSongs.getViews();
                        Log.i(TAG, "setAllSongs views : " + views);

                        Log.i(TAG, "setAllSongs recyclerview add");
                        allSongsList.add(allSongs);
                    } // for END


                    allSongsListAdapter.notifyDataSetChanged();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setGradientAllSongsRecyclerView();
                        } // run
                    }, 100); // 100ms 딜레이 후 그라데이션 효과 적용
//                    } // != null
                } // response Success
            } //onResponse

            @Override
            public void onFailure(Call<List<AllSongsModel>> call, Throwable t) {
                Log.i(TAG, "setAllSongs onFailure");

            } // onFailure
        }); // call.enqueue

        allSongsScrolled();
        setAllSeekBar();
    } // setAllSong Method END

    void setLikeSongs() {
        Log.i(TAG, "setLikeSongs");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi likedSongFetchApi = retrofit.create(ServerApi.class);

        Call<List<UpdateLikedModel>> call = likedSongFetchApi.selectLikes(getUserName);
        call.enqueue(new Callback<List<UpdateLikedModel>>() {
            @Override
            public void onResponse(Call<List<UpdateLikedModel>> call, Response<List<UpdateLikedModel>> response) {
                Log.i(TAG, "setLikeSongs onResponse : " + response);

                if (response.isSuccessful()) {
                    Log.i(TAG, "setLikeSongs response success : " + response);
                    String responseBody = new Gson().toJson(response.body());
                    Log.i(TAG, "setLikeSongs responseBody : " + responseBody);
                    List<UpdateLikedModel> updateLikedModels = response.body();
                    Log.i(TAG, "setLikeSongs response.body : " + updateLikedModels);

                    for (UpdateLikedModel likedModel : updateLikedModels) {
                        String name = likedModel.getUser_id();
                        Log.i(TAG, "setLikeSongs name : " + name);
                        String song = likedModel.getSong_name();
                        Log.i(TAG, "setLikeSongs song : " + song);

                        likedList.add(likedModel);
                        horizontalAdapter.notifyDataSetChanged();
                    } // for
                } else {
                    Log.i(TAG, "setLikeSongs response failed : " + response);
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<List<UpdateLikedModel>> call, Throwable t) {
                Log.i(TAG, "setLikeSongs onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueue
    } // setLikeSongs

    public String convertViews(long count) {
        if (count >= 1000 && count < 10000) { // 1K
            countToStr = (count / 1000) + "." + Math.round(count % 1000 / 100) + "K";
        } else if (count >= 10000 && count < 100000) { // 10K
            countToStr = (count / 1000) + "." + Math.round(count % 1000 / 100) + "K";
        } else if (count >= 100000 && count < 1000000) { // 100K
            countToStr = (count / 1000) + "." + Math.round(count % 1000 / 100) + "K";
        } else if (count >= 1000000 && count < 10000000) { // 1M
            countToStr = (count / 1000000) + "." + Math.round(count % 1000000 / 100000) + "M";
        } else if (count >= 10000000 && count < 100000000) { // 10M
            countToStr = (count / 1000000) + "." + Math.round(count % 1000000 / 100000) + "M";
        } else if (count >= 100000000 && count < 1000000000) { // 100M
            countToStr = (count / 1000000) + "." + Math.round(count % 1000000 / 100000) + "M";
        } else if (count >= 1000000000) { // 1B
            countToStr = (count / 1000000000) + "B";
        }
        Log.i(TAG, "convertToViews) countToStr : " + countToStr);
        return countToStr;
    } // convertViews

    public void allSongsScrolled() {
        all_songs_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i(TAG, "All Songs onScrolled ");

                int itemCount = all_songs_recyclerView.getAdapter().getItemCount();
//                allSeekBar.setMax(allSongsListAdapter.getItemCount() - 5);

                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                Log.i(TAG, "onScrolled firstVisibleItemPosition: " + firstVisibleItemPosition);
//                allSeekBar.setProgress(firstVisibleItemPosition);
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                Log.i(TAG, "onScrolled lastVisibleItemPosition: " + lastVisibleItemPosition);
                int totalItemCount = recyclerView.getAdapter().getItemCount() - 1;
                Log.i(TAG, "onScrolled totalItemCount: " + totalItemCount);
                Log.i(TAG, "onScrolled itemCount: " + itemCount);


                if (lastVisibleItemPosition >= totalItemCount) {
                    allSongsListAdapter.clearGradientEffect();
                } else {
                    allSongsListAdapter.setLastVisibleItemPosition(lastVisibleItemPosition);
                    if(!allSongsListAdapter.isApplyGradient()) {
                        allSongsListAdapter.applyGradientEffect();
                    } // if
                } // else
            } // onScrolled

//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                Log.i(TAG, "onScrollStateChanged");
//
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {  // 스크롤이 멈췄을 때
//                    Log.i(TAG, "onScrollStateChanged: " + newState);
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                            int lastVisibleItemPos = layoutManager.findLastVisibleItemPosition();
//
//                            if (lastVisibleItemPos < layoutManager.getItemCount() - 1) {
//                                // 화면에 보이는 아이템의 다음 아이템이 존재하면 그라데이션 표시
//                                allSongsListAdapter.applyGradientEffect();
//
//                            } else {
//                                // 화면에 보이는 아이템의 다음 아이템이 존재하지 않으면 그라데이션 효과 없음
//                                allSongsListAdapter.clearGradientEffect();
//                            } // else
//                        } // run
//                    }, 100);
//                } // if
//            } // onScrollStateChanged
        }); // addOnScrollListener
    } // allSongsScrolled

    public void setLikedScrolled() {

        horizontalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.i(TAG, "Liked onScrolled");

                int itemCount = horizontalRecyclerView.getAdapter().getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                Log.i(TAG, "onScrolled firstVisibleItemPosition: " + firstVisibleItemPosition);
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                Log.i(TAG, "onScrolled lastVisibleItemPosition: " + lastVisibleItemPosition);
                int totalItemCount = recyclerView.getAdapter().getItemCount() - 1;
                Log.i(TAG, "onScrolled totalItemCount: " + totalItemCount);
                Log.i(TAG, "onScrolled itemCount: " + itemCount);

//                if (lastVisibleItemPosition >= totalItemCount) {
//                    horizontalAdapter.clearGradientEffect();
//                } else {
//                    horizontalAdapter.setLastVisibleItemPosition(lastVisibleItemPosition);
//                    if(!horizontalAdapter.isApplyGradient()) {
//                        horizontalAdapter.applyGradientEffect();
//                    } // if
//                } // else

            } // onScrolled
        }); // addOnScrollListener
    } // setLikedScrolled

    public void setGradientAllSongsRecyclerView() {
        LinearLayoutManager layoutManager = (LinearLayoutManager)all_songs_recyclerView.getLayoutManager();
        int lastCompletelyVisibleItemPos = layoutManager.findLastCompletelyVisibleItemPosition();

        if(lastCompletelyVisibleItemPos < layoutManager.getItemCount()-1){
            allSongsListAdapter.applyGradientEffect();
        } else {
            allSongsListAdapter.clearGradientEffect();
        } // else
    } // setGradientAllSongsRecyclerView

    public void setAllSeekBar() {

//        allSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) { // 사용자가 직접 드래그한 경우에만 실행합니다.
//                    ((LinearLayoutManager) all_songs_recyclerView.getLayoutManager()).scrollToPositionWithOffset(progress, 0);
//                    // progress 값에 해당하는 위치로 RecyclerView 스크롤
//                }
//            } / onProgressChanged

//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                allSongsListAdapter.disableGradientEffect();
//            } // onStartTrackingTouch
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                allSongsListAdapter.enableGradientEffect();
//            } // onStopTrackingTouch
//        }); // setOnSeekBarChangeListener
    } // setAllSeekBar

} // Selectable CLASS END