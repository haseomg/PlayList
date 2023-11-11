package com.example.playlist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class FollowingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<FollowingModel> followingList;
    private OnItemClickListener listener;
    private OnItemFollowDeleteButtonClickListener deleteListener;
    private OnItemFollowReAddButtonClickListener reAddListener;
    private OnItemMovingEventClickListener movingListener;
    private static final String BASE_URL = "http://13.124.239.85/";
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

    public interface OnItemMovingEventClickListener {
        void onFollowingListItemClick(FollowingModel followingModel);
    } // OnItemMovingEventClickListener

    public void setOnItemMovingEventClickListener(OnItemMovingEventClickListener listener) {
        this.movingListener = listener;
    } // setOnItemMovingEventClickListener

    public interface OnItemFollowDeleteButtonClickListener {
        void onItemFollowDeleteButtonClick(FollowingModel followingModel);
    } // OnItemClickListener

    public void setOnItemFollowDeleteClickListener(OnItemFollowDeleteButtonClickListener listener) {
        this.deleteListener = listener;
    } // void

    public interface OnItemFollowReAddButtonClickListener {
        void onItemFollowReAddButtonClick(FollowingModel followingModel);
    } // OnItemClickListener

    public void setOnItemFollowReAddClickListener(OnItemFollowReAddButtonClickListener listener) {
        this.reAddListener = listener;
    } // void


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

            try {
                String imagePath = BASE_URL + "/profile_image/" + followingModel.getUser_name() + "_profile_image.JPG";
                Log.i(TAG, "setFollowerListProfileImage onBindViewHolder (imagePath) *if : " + imagePath);
                Glide.with(context)
                        .load(imagePath)
                        .apply(new RequestOptions().circleCrop())
                        .error(R.drawable.gray_profile)
                        .into(((FollowingHolder) holder).profile);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } // catch

            ((FollowingHolder) holder).profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "followingClickEvent (adapter) profile onClick: ");
                    if (movingListener != null) {
                        Log.i(TAG, "followingClickEvent (adapter) profile *listener !null (if)");
                        movingListener.onFollowingListItemClick(followingModel);
                    } else {
                        Log.i(TAG, "followingClickEvent (adapter) profile *listener null (else)");

                    } // else
                } // onClick
            }); // setOnClickListener

            ((FollowingHolder) holder).user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "followingClickEvent (adapter) user onClick: ");
                    if (movingListener != null) {
                        Log.i(TAG, "followingClickEvent (adapter) user *listener !null (if)");
                        movingListener.onFollowingListItemClick(followingModel);
                    } else {
                        Log.i(TAG, "followingClickEvent (adapter) user *listener null (else)");

                    } // else

                } // onClick
            }); // setOnClickListener


            // TODO image setting
            try {
                ((FollowingHolder) holder).user.setText(followingModel.getUser_name());
                Log.i(TAG, "bringGetFeed onBindViewHolder : "
                        + followingModel.getUser_name());

            } catch (NullPointerException e) {
                e.printStackTrace();
            } // catch

            Log.i(TAG, "onBindViewHolder : " + followingList);

            // TODO - 팔로우 버튼 클릭 이벤트
            ((FollowingHolder) holder).follow_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String btnText = ((FollowingHolder) holder).follow_btn.getText().toString();
                    if ("팔로잉".equals(btnText)) {
                        Log.i(TAG, "followingSetButton (adapter) *btnTextStatus (if) : " + btnText);

                        new AlertDialog.Builder(context, R.style.AlertDialogCustom)
                                .setMessage("팔로우를 취소하시겠습니까?")
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 확인 버튼 클릭시 처리 로직
                                        ((FollowingHolder) holder).follow_btn.setText("팔로우");
                                        ((FollowingHolder) holder).follow_btn.setBackgroundResource(R.drawable.follower_readd_btn);
                                        ((FollowingHolder) holder).follow_btn.setTextColor(Color.WHITE);

                                        if (deleteListener != null) {
                                            Log.i(TAG, "followingSetButton (adapter) *deleteListener !null (if)");
                                            deleteListener.onItemFollowDeleteButtonClick(followingModel);
                                        } else {
                                            Log.i(TAG, "followingSetButton (adapter) *deleteListener null (else)");
                                        } // else
                                    } // onClick
                                }) // setPositiveButton
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 취소 버튼 클릭시 처리 로직
                                        dialog.dismiss();
                                    } // onClick
                                }) // setNegativeButton
                                .show();

                    } else if ("팔로우".equals(btnText)) {
                        Log.i(TAG, "followingSetButton (adapter) *btnTextStatus (else if) : " + btnText);

                        new AlertDialog.Builder(context)
                                .setMessage(followingModel.getUser_name() + "님을 팔로우하시겠습니까?")
                                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 확인 버튼 클릭시 처리 로직
                                        ((FollowingHolder) holder).follow_btn.setText("팔로잉");
                                        ((FollowingHolder) holder).follow_btn.setBackgroundResource(R.drawable.follower_delete_btn);
                                        ((FollowingHolder) holder).follow_btn.setTextColor(Color.parseColor("#5D5D9F"));

                                        if (reAddListener != null) {
                                            Log.i(TAG, "followingSetButton (adapter) *reAddListener !null (if)");
                                            reAddListener.onItemFollowReAddButtonClick(followingModel);
                                        } else {
                                            Log.i(TAG, "followingSetButton (adapter) *reAddListener null (else)");
                                        } // else
                                    } // onClick
                                }) // setPositiveButton
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 취소 버튼 클릭시 처리 로직
                                        dialog.dismiss();
                                    } // onClick
                                }) // setNegativeButton
                                .show();
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
