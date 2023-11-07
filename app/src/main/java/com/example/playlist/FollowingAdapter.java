package com.example.playlist;

import android.content.Context;
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

public class FollowingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<FollowingModel> followingList;
    private OnItemClickListener listener;
    private int position;
    String TAG = "FollowingAdapter";

    public FollowingAdapter(ArrayList<FollowingModel> followingList) {
        this.followingList = followingList;
    } // Constructor

    FollowingAdapter(Context context, ArrayList<FollowingModel> followingList) {
        Log.i(TAG, "constructor (context, arraylist)");
        this.context = context;
        this.followingList = followingList;
    } // Constructor

    public interface OnItemClickListener {
        void onItemFollowButtonClick(FollowingModel followingModel);
    } // OnItemClickListener

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
    } // setOnItemClickListener

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        // TODO R.layout Change
        view = LayoutInflater.from(context).inflate(R.layout.following_item, parent, false);
        Log.i(TAG, "view check : " + view);
        return new FollowingHolder(view);
    } // onCreateViewHolder

    public int getPosition() {
        return position;
    } // getPosition

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");
        if (holder instanceof FollowingHolder) {
            FollowingModel followingModel = followingList.get(position);

            // TODO image setting
            try {
                ((FollowingHolder) holder).user.setText(followingModel.getUser_name());
                Log.i(TAG, "bringGetFeed onBindViewHolder : "
                        + followingModel.getUser_name());

            } catch (NullPointerException e) {
                e.printStackTrace();
            } // catch

            Log.i(TAG, "onBindViewHolder : " + followingList);

            ((FollowingHolder) holder).follow_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String btnText = ((FollowingHolder) holder).follow_btn.getText().toString();
                    if ("팔로잉".equals(btnText)) {
                        ((FollowingHolder) holder).follow_btn.setText("팔로우");
                        ((FollowingHolder) holder).follow_btn.setBackgroundResource(R.drawable.follow_button);
                        ((FollowingHolder) holder).follow_btn.setTextColor(Color.WHITE);


                    } else if ("팔로우".equals(btnText)) {
                        ((FollowingHolder) holder).follow_btn.setText("팔로잉");
                        ((FollowingHolder) holder).follow_btn.setBackgroundResource(R.drawable.feed_button);
                        ((FollowingHolder) holder).follow_btn.setTextColor(Color.parseColor("#CD6CAC6C"));

                    } // else

//                    if (listener != null) {
//                        Log.i(TAG, "followingSetButton (adapter) onClick *if");
//                        listener.onItemFollowButtonClick(followingModel);
//
//                    } else {
//                        Log.i(TAG, "followingSetButton (adapter) onClick *else");
//                    } // else
                } // onClick
            }); // setOnClickListener
        } // if
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        return followingList.size();
    } // getItemCount

    private class FollowingHolder extends RecyclerView.ViewHolder {

        public final ImageView profile;
        public final TextView user;
        public final TextView follow_btn;

        public FollowingHolder(View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.followingProfileImage);
            user = itemView.findViewById(R.id.followingUserName);
            follow_btn = itemView.findViewById(R.id.followingBtn);
        } // Constructor
    } // FollowingHolder

    public void clearItems() {
        followingList.clear();
    }

} // Adapter
