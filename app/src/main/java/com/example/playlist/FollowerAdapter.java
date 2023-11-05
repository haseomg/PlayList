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

public class FollowerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<FollowerModel> followerList;
    private OnItemClickListener onItemClickListener;
    private int position;
    String TAG = "FollowAdapter";

    public FollowerAdapter(ArrayList<FollowerModel> followerList) {
        this.followerList = followerList;
    } // Constructor

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    } // setOnItemClickListener

    FollowerAdapter(Context context, ArrayList<FollowerModel> followerList) {
        Log.i(TAG, "constructor (context, arraylist)");
        this.context = context;
        this.followerList = followerList;
    } // Constructor

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        // TODO R.layout Change
        view = LayoutInflater.from(context).inflate(R.layout.follower_item, parent, false);
        Log.i(TAG, "view check : " + view);
        return new FollowerHolder(view);
    } // onCreateViewHolder

    public int getPosition() {
        return position;
    } // getPosition

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");
        if (holder instanceof FollowerHolder) {
            FollowerModel followerModel = followerList.get(position);

            // image setting
            ((FollowerHolder) holder).user_name.setText(followerModel.getUser_name());

            Log.i(TAG, "onBindViewHolder : "
                    + followerModel.getUser_name());

            Log.i(TAG, "onBindViewHolder : " + followerList);
        } // if
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        return followerList.size();
    } // getItemCount

    private class FollowerHolder extends RecyclerView.ViewHolder {

        public final ImageView profile_image;
        public final TextView user_name;
        public final TextView delete_btn;

        public FollowerHolder(View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.followerProfileImage);
            user_name = itemView.findViewById(R.id.followerUserName);
            delete_btn = itemView.findViewById(R.id.followerDeleteBtn);
        } // FollowerHolder Constructor
    } // FollowerHolder CLASS END

    public void clearItems() {
        followerList.clear();
    }

} // Adapter END
