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

public class PlayedAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<AllSongsModel> playedList;
    private OnItemClickListener onItemClickListener;
    private int position;
    String TAG = "[PlayedAdapter]";

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    } // setOnItemClickListener END

    PlayedAdapter(Context context, ArrayList<AllSongsModel> playedList) {
        Log.i(TAG, "LikedAdapter constructor (context, arraylist)");
        this.context = context;
        this.playedList = playedList;
    } // Constructor END

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.played_item, parent, false);
        Log.i(TAG, "view check : " + view);
        return new PlayedHolder(view);
    } // onCreateViewHolder END


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");
        if (holder instanceof PlayedHolder) {
            AllSongsModel playedModel = playedList.get(position);
            Log.i(TAG, "playedModel onBindViewHolder : " + playedModel);

            // TODO. Now songName null

            try {
                String songName = playedModel.getName();
                Log.i(TAG, "setPlayed onBindViewHolder: " + songName);

                if (songName.contains("_")) {
                    String realName = songName.replace("_", " ");
                    ((PlayedHolder) holder).song_name.setText(realName);

                } else {
                    ((PlayedHolder) holder).song_name.setText(songName);
                } // else

            } catch (NullPointerException e) {
                Log.e(TAG, "onBindViewHolder Null " + e);
            } // catch
        } // if
    } // onBindViewHolder

    public void addItem(AllSongsModel allSongsModel) {
        playedList.add(allSongsModel);
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

    public PlayedAdapter(ArrayList<AllSongsModel> playedList) {
        this.playedList = playedList;
    } // Constructor

    @Override
    public int getItemCount() {
        return playedList.size();
    }

    private class PlayedHolder extends RecyclerView.ViewHolder {

        public final TextView song_name;
        public final TextView artistAndTime;
        public final ImageView music_image;
        public final ImageView played_list;

        public PlayedHolder(View itemView) {
            super(itemView);
            song_name = itemView.findViewById(R.id.played_song_name);
            song_name.setSelected(true);
            artistAndTime = itemView.findViewById(R.id.played_artist_time);
            artistAndTime.setSelected(true);
            music_image = itemView.findViewById(R.id.played_album);
            played_list = itemView.findViewById(R.id.playedListImageView);

            artistAndTime.setVisibility(View.INVISIBLE);
            music_image.setVisibility(View.VISIBLE);
        } // Constructor
    } // CLASS

    public void clearItems() {
        Log.i(TAG, "clearItems");
        playedList.clear();
    } // clearItems

} // Adapter
