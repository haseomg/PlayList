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
    private OnItemClickListener listener;
    private OnFeedMovingEventListener onFeedMovingEventListener;
    private int position;
    String TAG = "FollowAdapter";

    public FollowerAdapter(ArrayList<FollowerModel> followerList) {
        this.followerList = followerList;
    } // Constructor

    public interface OnFeedMovingEventListener {
        void onFeedItemClick(FollowerModel followerModel);
    } // OnFeedMovingEventListener

    public void setOnFeedMovingEventListener(OnFeedMovingEventListener onFeedMovingEventListener) {
        this.onFeedMovingEventListener = onFeedMovingEventListener;
    }

    public interface OnItemClickListener {
        void onItemDeleteButtonClick(FollowerModel followerModel);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
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

            ((FollowerHolder) holder).profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "followerClickEvent (adapter) profile_image onClick");
                    if (onFeedMovingEventListener != null) {
                        Log.i(TAG, "followerClickEvent (adapter) profile_image *onFeedMovingEventListener !null (if)");
                        onFeedMovingEventListener.onFeedItemClick(followerModel);
                    } else {
                        Log.i(TAG, "followerClickEvent (adapter) profile_image *onFeedMovingEventListener null (else)");

                    } // else
                } // onClick
            }); //setOnClickListener

            ((FollowerHolder) holder).user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "followerClickEvent (adapter) user_name onClick");
                    if (onFeedMovingEventListener != null) {
                        Log.i(TAG, "followerClickEvent (adapter) user_name *onFeedMovingEventListener !null (if)");
                        onFeedMovingEventListener.onFeedItemClick(followerModel);
                    } else {
                        Log.i(TAG, "followerClickEvent (adapter) user_name *onFeedMovingEventListener null (else)");

                    } // else
                } // onClick
            }); //setOnClickListener

            // image setting
            ((FollowerHolder) holder).user_name.setText(followerModel.getUser_name());
            Log.i(TAG, "onBindViewHolder : " + followerModel.getUser_name());
            Log.i(TAG, "onBindViewHolder : " + followerList);

            ((FollowerHolder) holder).delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        Log.i(TAG, "followerSetDeleteBtn (adapter) onClick *if");
                        listener.onItemDeleteButtonClick(followerModel);
                    } else {
                        Log.i(TAG, "followerSetDeleteBtn (adapter) onClick *else");
                    } // else
                } // onClick
            }); // setOnClickListener
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
