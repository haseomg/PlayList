package com.example.playlist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeedCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<FeedCommentModel> feedCommentList;
    private OnItemClickListener onItemClickListener;
    private int position;
    String TAG = "[FeedCommentAdapter]";

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    } // setOnItemClickListener END

    FeedCommentAdapter(Context context, ArrayList<FeedCommentModel> feedCommentList) {
        Log.i(TAG, "constructor (context, arraylist)");
        this.context = context;
        this.feedCommentList = feedCommentList;
    } // Constructor

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        // TODO R.layout Change
        view = LayoutInflater.from(context).inflate(R.layout.feed_comment_item, parent, false);
        Log.i(TAG, "view check : " + view);
        return new FeedCommentHolder(view);
    } // onCreateViewHolder

    public int getPosition() {
        return position;
    } // getPosition

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");
        if (holder instanceof FeedCommentHolder) {
            FeedCommentModel feedCommentModel = feedCommentList.get(position);

            ((FeedCommentHolder) holder).msg.setText(feedCommentModel.getMsg() + " ✦ ");
            ((FeedCommentHolder) holder).time.setText(feedCommentModel.getSelected_time() + " ✦ ");
            ((FeedCommentHolder) holder).song.setText(feedCommentModel.getSong());

            Log.i(TAG, "fetchAndDisplayFeedComments onBindViewHolder : "
                    + feedCommentModel.getSong() + " / " + feedCommentModel.getSelected_time() + " / "
                    + feedCommentModel.getMsg());

            Log.i(TAG, "onBindViewHolder feedCommentModel : " + feedCommentList);
            // TODO setText
        } // if
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        return feedCommentList.size();
    } // getItemCount

    public class FeedCommentHolder extends RecyclerView.ViewHolder {

        public final TextView song;
        public final TextView time;
        public final TextView msg;

        public FeedCommentHolder(View itemView) {
            super(itemView);
            song = itemView.findViewById(R.id.feedCommentSong);
            time = itemView.findViewById(R.id.feedCommentSelectedTime);
            msg = itemView.findViewById(R.id.feedCommentMsg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "feedCommentClickEvent onClick");
                    int position = getAdapterPosition();
                    FeedCommentModel clickedItem = feedCommentList.get(position);

                    String feedCommentSongName = clickedItem.getSong();
                    Log.i(TAG, "feedCommentClickEvent song name : " + feedCommentSongName);
                    String feedCommentSelectedTime = clickedItem.getSelected_time();
                    Log.i(TAG, "feedCommentClickEvent selected time : " + feedCommentSelectedTime);

                    String[] parts = feedCommentSelectedTime.split(":");
                    int minutes = Integer.parseInt(parts[0]);
                    int seconds = Integer.parseInt(parts[1]);
                    int selectedTimeInMilliseconds = (minutes * 60 + seconds) * 1000;

                    String[] cutOtherWord = feedCommentSongName.split(" • ");
                    String realSongName = cutOtherWord[0];
                    Log.i(TAG, "feedCommentClickEvent realSongName check : " + realSongName);

                    Intent intent = new Intent("com.example.playlist.PLAY_MUSIC");
                    intent.putExtra("selected_song", realSongName);
                    intent.putExtra("selected_time", selectedTimeInMilliseconds);

                    context.sendBroadcast(intent);
                } // onClick
            }); // itemView.setOnClickListener
        } // FeedCommentHolder Constructor
    } // FeedCommentHolder

    public FeedCommentAdapter(ArrayList<FeedCommentModel> feedCommentList) {
        this.feedCommentList = feedCommentList;
    } // FeedCommentAdapter Constructor

} // Adapter
