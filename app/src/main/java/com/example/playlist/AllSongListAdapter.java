package com.example.playlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AllSongListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String countToStr;
    private ArrayList<AllSongsModel> allSongsList;
    private SharedPreferences preferences;
    private boolean applyGradient = true;
    private boolean isGradientEnabled = true;
    private int oldPosition;
    private int playing_position = 0;
    private int lastVisibleItemPosition;
    private int selected_position = -1;
    String songName;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    } // setOnItemClickListener END

    String TAG = "[AllSongsListAdapter]";

    AllSongListAdapter(Context context, ArrayList<AllSongsModel> allSongsList) {
        Log.i(TAG, "AllSongListAdapter constructor (context, arraylist)");
        this.context = context;
        this.allSongsList = allSongsList;
    } // constructor END

    public void addItem(AllSongsModel item) {
        Log.i(TAG, "addItem Method");
        if (allSongsList != null) {
            Log.i(TAG, "chatList != null : " + allSongsList);
            allSongsList.add(item);
        } // if END
        else {
            Log.i(TAG, "chatList == null : " + allSongsList);
        } // else END
    } /// addItem Method END

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        Log.i(TAG, "view check : " + view);
        return new AllSongsHolder(view);
    } // onCreateViewHolder END

    public void setLastVisibleItemPosition(int position) {
        this.lastVisibleItemPosition = position;
        notifyDataSetChanged();
    } // setLastVisibleItemPosition

    public void clearGradientEffect() {
        this.applyGradient = false;
        notifyDataSetChanged();
    } // clearGradientEffect

    public void applyGradientEffect() {
        this.applyGradient = true;
        notifyDataSetChanged();
    } // applyGradientEffect

    public void disableGradientEffect() {
        isGradientEnabled = false;
        notifyDataSetChanged();
    } // disableGradientEffect

    public void enableGradientEffect() {
        // 그라데이션 효과 활성화
        isGradientEnabled = true;
        notifyDataSetChanged();  // 모든 아이템 뷰 갱신
    } // enableGradientEffect

    public void removeGradientEffect() {
        lastVisibleItemPosition = -1;
        notifyDataSetChanged();
    } // removeGradientEffect


    public boolean isApplyGradient() {
        return applyGradient;
    } // isApplyGradient

    public void setLastVisibleGradient(@NonNull RecyclerView.ViewHolder holder, int firstPosition, int lastVisiblePosition) {
        // view에 그라데이션 세팅할 것인데 여기서 세팅 내용 추가하고, 클래스에서 해당 세팅 내용 가져와서 포지션 값 넣어줘야 해 (스크롤 이벤트 내)
        // holder null 의심
        try {
            Log.i(TAG, "setLastVisibleGradient holder: " + holder);

        } catch (NullPointerException e) {
            Log.e(TAG, "setLastVisibleGradient: " + e);
        } // catch

        for (int i = firstPosition; i < lastVisiblePosition; i++) {
            Log.i(TAG, "setLastVisibleGradient i check : " + i);

            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{Color.TRANSPARENT, Color.BLACK});

            if (i == lastVisiblePosition) {
                Log.i(TAG, "setLastVisibleGradient i check : " + i);
                holder.itemView.setBackground(gradientDrawable);

            } else if (i == getItemCount()) {
                Log.i(TAG, "setLastVisibleGradient i check : " + i);
                holder.itemView.setBackground(null);

            } else {
                Log.i(TAG, "setLastVisibleGradient i check : " + i);
                holder.itemView.setBackground(null);

            } // else
        } // for
    } // setLastVisibleGradient

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Log.i(TAG, "onBindViewHolder Method");
        AllSongsModel allSongsModel = allSongsList.get(position);

        playing_position = ((Selectable) Selectable.selectableCtx).playing_position;
//            ((AllSongsHolder) holder).music_image.setI(allSongsList.get(position).getTime());
        String songName = allSongsList.get(position).getName();
        String[] cutFileFormat = songName.split("\\.mp3");
        String reName = cutFileFormat[0];
        String realName = reName.replace("_", " ");

        String artistName = allSongsList.get(position).getArtist();
        String artistRealName = artistName.replace("_", " ");

        ((AllSongsHolder) holder).song_name.setText(realName);
        ((AllSongsHolder) holder).artistAndTime.setText(artistRealName + " • " + allSongsList.get(position).getTime());
//        int countToInt = Integer.parseInt(allSongsModel.getViews());

//        if (countToInt < 1000) {
//            ((AllSongsHolder) holder).music_time.setText(" • " + allSongsList.get(position).getTime());
//
//        } else {
        long viewsCount = Long.parseLong(allSongsModel.getViews());
        Log.i(TAG, "setAllSongs viewsCount : " + viewsCount);
        convertViews(viewsCount);
        Log.i(TAG, "setAllSongs) views : " + countToStr);

//        if (applyGradient && position == lastVisibleItemPosition && lastVisibleItemPosition != -1) {
//            applyGradient(holder.itemView);
//            ((AllSongsHolder) holder).artistAndTime.setTextColor(Color.parseColor("#7C40407F"));
//
//        } else {
//            clearGradient(holder.itemView);
//            ((AllSongsHolder) holder).artistAndTime.setTextColor(Color.parseColor("#E440407F"));
//        } // else

//        GradientDrawable drawable = (GradientDrawable) holder.itemView.getBackground();

        try {
            Log.i(TAG, "onBindViewHolder Gradient able : " + isGradientEnabled);
            Log.i(TAG, "onBindViewHolder Gradient position : " + position);
            Log.i(TAG, "onBindViewHolder Gradient lastVisibleItemPosition : " + lastVisibleItemPosition);
//            if (isGradientEnabled) {
//                //  && position <= lastVisibleItemPosition
//                drawable.setAlpha(255);
//            } else {
//                // else if (position > lastVisibleItemPosition) {
//                drawable.setAlpha(0);
//            } // else
        } catch (NullPointerException e) {
            e.printStackTrace();
        } // catch
        // TODO (1)  to did - check "Selectable" 액티비티 시작 시 현재 메인에서 재생 중인 음악 아이템 체킹 (색상 변경) 되어있음
        // TODO (2) issue - 1번의 상태에서 다른 아이템 클릭 시 추가적으로 체킹 (색상 변경) 되는 상황
        // TODO (3) want - 체킹 (색상 변경) 아이템은 한 개만 되어 있어야 함
        // 현재 체킹되어있는 아이템의 포지션 값을 찾아야 해
        String playingSong = ((MainActivity) MainActivity.mainCtx).mainLogo.getText().toString();
        String[] cutPlayingSong = playingSong.split(" • ");
        String reRealName = cutPlayingSong[0];
        String changeName = reRealName.replace(" ", "_");
        Log.i(TAG, "playingSong) onBindViewHolder check : " + reRealName);
        String originalName = allSongsList.get(position).getName();
        Log.i(TAG, "allSongCheck) song name : " + originalName);
        if (position == playing_position) {
            Log.i(TAG, "allSongCheck onBindViewHolder *if : " + playing_position + " / " + originalName + " / " + changeName + ".mp3");
            holder.itemView.setBackgroundColor(Color.parseColor("#B57878E1"));
            ((AllSongsHolder) holder).song_name.setTextColor(Color.parseColor("#BDC7F6"));

        } else if (selected_position == position || allSongsList.get(position).getName().equals(changeName + ".mp3")) {
            Log.i(TAG, "allSongCheck) onBindViewHolder *else if : " + selected_position + " / " + originalName + " / " + changeName + ".mp3");
            holder.itemView.setBackgroundColor(Color.parseColor("#FFE2E7FB"));
            ((AllSongsHolder) holder).song_name.setTextColor(Color.parseColor("#E440407F"));
        } else {
            Log.i(TAG, "allSongCheck) onBindViewHolder *else : " + selected_position + " / " + originalName + " / " + changeName + ".mp3");
            holder.itemView.setBackgroundColor(Color.parseColor("#B57878E1"));  // 원래 색상으로 설정
            ((AllSongsHolder) holder).song_name.setTextColor(Color.parseColor("#BDC7F6"));
        } // else

    } // onBindViewHolder END

    private void clearGradient(View itemView) {
        itemView.setBackground(null);
    } // clearGradient

    private void applyGradient(View itemView) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.TRANSPARENT, Color.parseColor("#AAB9FF")});

//        itemView.setBackground(gradientDrawable);
    } // clearGradient

    private void applyToBottomGradient(View itemView) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{Color.TRANSPARENT, Color.parseColor("#AAB9FF")});

        itemView.setBackground(gradientDrawable);
    } //applyToBottomGradient

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount Method");
        Log.i(TAG, "AllSongsList.size check : " + allSongsList.size());
        return allSongsList.size();
    } // getItemCount END

    public void clearItems() {
        Log.i(TAG, "clearItems");
        allSongsList.clear();
    } // clearItems END

    private class AllSongsHolder extends RecyclerView.ViewHolder {

        public final TextView song_name;
        public final TextView artistAndTime;
        public final ImageView music_image;
        public final ImageView allSonsAlbum;
        public final ImageView feed;
        public final androidx.constraintlayout.widget.ConstraintLayout allSongsView;

        public AllSongsHolder(View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.song_name);
            song_name.setSelected(true);
            artistAndTime = itemView.findViewById(R.id.artistAndTime);
            artistAndTime.setSelected(true);
            music_image = itemView.findViewById(R.id.music_image);
            allSonsAlbum = itemView.findViewById(R.id.allSonsAlbum);
            feed = itemView.findViewById(R.id.feedIcon);
            allSongsView = itemView.findViewById(R.id.allSongsView);

            // TODO itemView onClick()
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "itemView) allSongsAdapter allSongsView click");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (selected_position != position) {
                            oldPosition = selected_position;
                            Log.i(TAG, "changeColor position) onClick oldPosition : " + oldPosition);
                            selected_position = position;
                            Log.i(TAG, "changeColor position) onClick selected_position : " + selected_position);

                            notifyItemChanged(oldPosition);
                            notifyItemChanged(selected_position);
                        }

                        if (playing_position != RecyclerView.NO_POSITION) {
                            notifyItemChanged(playing_position);
                        } // if

                        playing_position = position;

                        AllSongsModel clickedItem = allSongsList.get(position);
                        songName = clickedItem.getName();

                        v.setBackgroundColor(Color.parseColor("#B57878E1"));
                        // 새로운 액티비티로 이동 (여기서는 MainActiviy 가정)
//                        Intent intent = new Intent(context, MainActivity.class);

                        // 인탠트에 추가 데이터 넣기 (여기서는 선택한 곡의 이름)
                        String[] fileTypeCut = songName.split(".mp3");
                        String selected_song = fileTypeCut[0];
//                        intent.putExtra("selected_song", selected_song);

                        Intent intent = new Intent("com.example.playlist.PLAY_MUSIC");
                        Log.i(TAG, "checking playedItem onClick : " + selected_song);
                        intent.putExtra("selected_song", selected_song);

                        context.sendBroadcast(intent);
                        // 액티비터 시작
//                        context.startActivity(intent);
                        notifyItemChanged(position);
                    } // if

                    //
//                        float xTranslationValue = 0f; // 슬라이드 위치 값 설정
//                        float yTranslationValue = 0f;
//                        long animationDuration = 500; // 애니메이션 지속 시간 설정
//
//                        Animation slideAnimation = new TranslateAnimation(0f, xTranslationValue, 0f, yTranslationValue);
//                        slideAnimation.setDuration(animationDuration);
//                        v.startAnimation(slideAnimation);
                    //

                    //
//                        onItemClickListener.onItemClick(position);
                    //

                } // onClick END
            }); // itemView.setOnClickListener END
        } // constructor END
    } // AllSongsHolder class END

    public AllSongListAdapter(ArrayList<AllSongsModel> allSongsList) {
        this.allSongsList = allSongsList;
    } // constructor END

    public AllSongsModel getAllSongsModel(int position) {
        return allSongsList.get(position);
    }

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


} // AllSongsAdapter CLASS END
