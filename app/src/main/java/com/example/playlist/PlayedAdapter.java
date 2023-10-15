package com.example.playlist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayedAdapter  extends RecyclerView.Adapter<PlayedAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PlayedModel> playedList;
    private OnItemClickListener onItemClickListener;
    private int position;
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
    } // onBindViewHolder

    public void addItem(PlayedModel playedModel) {
        playedList.add(playedModel);
    } // addItem

    public int getPosition() {
        return  position;
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
                    // (1) 해당 곡 재생
                    // (2) 해당 곡 아이템 백그라운드 컬러 변경 (고를 때마다 해당 곡만)

                } // onClick
            }); // itemView.setOnClickListener
        } // Constructor
    } // CLASS

    public void clearItems() {
        Log.i(TAG, "clearItems");
        playedList.clear();
    } // clearItems

} // Adapter
