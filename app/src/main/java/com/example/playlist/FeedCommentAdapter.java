package com.example.playlist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeedCommentAdapter extends RecyclerView.Adapter<FeedCommentAdapter.ViewHolder> {

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        // TODO R.layout Change
        view = LayoutInflater.from(context).inflate(R.layout.played_item, parent, false);
        Log.i(TAG, "view check : " + view);
        return new ViewHolder(view);
    } // onCreateViewHolder

    public int getPosition() {
        return position;
    } // getPosition

    @Override
    public void onBindViewHolder(@NonNull FeedCommentAdapter.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");
        FeedCommentModel feedCommentModel = feedCommentList.get(position);
        Log.i(TAG, "onBindViewHolder feedCommentModel : " + feedCommentList);
    } // onBindViewHolder

    public FeedCommentAdapter(ArrayList<FeedCommentModel> feedCommentList) {
        this.feedCommentList = feedCommentList;
    } // FeedCommentAdapter Constructor

    @Override
    public int getItemCount() {
        return feedCommentList.size();
    } // getItemCount

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View view) {
            super(view);
            // TODO UI 세팅
        } // ViewHolder Constructor
    } // ViewHolder

} // Adapter
