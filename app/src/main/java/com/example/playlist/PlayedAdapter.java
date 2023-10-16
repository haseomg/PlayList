package com.example.playlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayedAdapter extends RecyclerView.Adapter<PlayedAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PlayedModel> playedList;
    private OnItemClickListener onItemClickListener;
    private int position;
    private int oldPosition;
    private int selected_position = -1;
    String TAG = "[PlayedAdapter]";

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    } // setOnItemClickListener END

    PlayedAdapter(Context context, ArrayList<PlayedModel> playedList) {
        Log.i(TAG, "LikedAdapter constructor (context, arraylist)");
        this.context = context;
        this.playedList = playedList;
    } // Constructor END

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.played_item, parent, false);
        Log.i(TAG, "view check : " + view);
        return new ViewHolder(view);
    } // onCreateViewHolder END


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");
        PlayedModel playedModel = playedList.get(position);
        Log.i(TAG, "playedModel onBindViewHolder : " + playedModel);


        // TODO. Now songName null

        try {
            String songName = playedModel.getSong_name();
            Log.i(TAG, "setPlayed onBindViewHolder: " + songName);

            if (songName.contains("_")) {
                String realName = songName.replace("_", " ");
                holder.song_name.setText(realName);

            } else {
                holder.song_name.setText(playedList.get(position).getSong_name());
            } // else

        } catch (NullPointerException e) {
            Log.e(TAG, "onBindViewHolder Null " + e);
        } // catch

        // TODO 아이템 색상 체킹
        String playingSong = ((MainActivity) MainActivity.mainCtx).mainLogo.getText().toString();
        String[] cutPlayingSong = playingSong.split(" • ");
        String realName = cutPlayingSong[0];
        Log.i(TAG, "playingSong) onBindViewHolder check : " + realName);
        // TODO (1)  to did - check "Played" 액티비티 시작 시 현재 메인에서 재생 중인 음악 아이템 체킹 (색상 변경) 되어있음
        // TODO (2) issue - 1번의 상태에서 다른 아이템 클릭 시 추가적으로 체킹 (색상 변경) 되는 상황
        // TODO (3) want - 체킹 (색상 변경) 아이템은 한 개만 되어 있어야 함
        if (selected_position == position || playedList.get(position).getSong_name().equals(realName)) {
            Log.i(TAG, "changeColor onBindViewHolder *if : " + selected_position + " / " + playedList.get(position).getSong_name());
            holder.itemView.setBackgroundColor(Color.parseColor("#7878E1"));
            holder.song_name.setTextColor(Color.parseColor("#AAB9FF"));
        } else {
            Log.i(TAG, "changeColor onBindViewHolder *else: " + selected_position + " / " + playedList.get(position).getSong_name());
            holder.itemView.setBackgroundColor(Color.parseColor("#AAB9FF"));
            holder.song_name.setTextColor(Color.parseColor("#7878E1"));
        } // else

//        if (playedList.get(position).getSong_name().equals(realName)) {
//            holder.itemView.setBackgroundColor(Color.parseColor("#7878E1"));
//            holder.song_name.setTextColor(Color.parseColor("#AAB9FF"));
//        } else {
//            holder.itemView.setBackgroundColor(Color.parseColor("#AAB9FF"));
//            holder.song_name.setTextColor(Color.parseColor("#7878E1"));
//        } // else
    } // onBindViewHolder

    public void addItem(PlayedModel playedModel) {
        playedList.add(playedModel);
    } // addItem

    public int getPosition() {
        return position;
    } // getPosition

    public void setPosition(int position) {
        this.position = position;
    } // setPosition

    public void removeItem(int position) {
        playedList.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    } // removeItem

    public PlayedAdapter(ArrayList<PlayedModel> playedList) {
        this.playedList = playedList;
    } // Constructor

    @Override
    public int getItemCount() {
        return playedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView song_name;
        TextView artistAndTime;
        ImageView music_image;
        ImageView played_list;

        public ViewHolder(View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.played_song_name);
            song_name.setSelected(true);
            artistAndTime = itemView.findViewById(R.id.played_artist_time);
            artistAndTime.setSelected(true);
            music_image = itemView.findViewById(R.id.played_album);
            played_list = itemView.findViewById(R.id.playedListImageView);

            artistAndTime.setVisibility(View.INVISIBLE);
            music_image.setVisibility(View.VISIBLE);

            // TODO itemView onClick()
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "itemView) PlayedAdapter allSongsView click");
                    int position = getAdapterPosition();
                    // (1) 해당 곡 재생
                    // (2) 해당 곡 아이템 백그라운드 컬러 변경 (고를 때마다 해당 곡만)
                    if (position != RecyclerView.NO_POSITION) {
                        if (selected_position != position) {
                            oldPosition = selected_position;
                            Log.i(TAG, "changeColor position) onClick oldPosition : " + oldPosition);
                            selected_position = position;
                            Log.i(TAG, "changeColor position) onClick selected_position : " + selected_position);

                            notifyItemChanged(oldPosition);
                            notifyItemChanged(selected_position);
                        } // if

                        PlayedModel clickedItem = playedList.get(position);
                        String songName = clickedItem.getSong_name();

                        v.setBackgroundColor(Color.parseColor("#7878E1"));

//                        String fileTypeAdd = songName + ".mp3";

                        Intent intent = new Intent("com.example.playlist.PLAY_MUSIC");
                        Log.i(TAG, "checking playedItem onClick : " + songName);
                        intent.putExtra("selected_song", songName);

                        context.sendBroadcast(intent);
                    } // if
                } // onClick
            }); // itemView.setOnClickListener

        } // Constructor
    } // CLASS

    public void clearItems() {
        Log.i(TAG, "clearItems");
        playedList.clear();
    } // clearItems

} // Adapter
