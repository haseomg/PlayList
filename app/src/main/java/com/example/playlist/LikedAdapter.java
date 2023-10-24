package com.example.playlist;

import android.content.Context;
import android.content.Intent;
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

public class LikedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<LikedModel> likedList;
    private OnItemClickListener onItemClickListener;

    private boolean applyGradient = true;
    private boolean isGradientEnabled = true;
    private int oldPosition;
    private int playing_position = -1;
    private int lastVisibleItemPosition;
    private int selected_position = -1;
    String songName;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    } // setOnItemClickListener END

    String TAG = "[LikedAdapter]";

    LikedAdapter(Context context, ArrayList<LikedModel> likedList) {
        Log.i(TAG, "LikedAdapter constructor (context, arraylist)");
        this.context = context;
        this.likedList = likedList;
    } // Constructor END

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.liked_user, parent, false);
        Log.i(TAG, "view check : " + view);
        return new LikedHolder(view);
    } // onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");

        ((LikedHolder) holder).user.setText(likedList.get(position).getUser());

        if (applyGradient && position == lastVisibleItemPosition && lastVisibleItemPosition != -1) {
            applyGradient(holder.itemView);

        } else {
            clearGradient(holder.itemView);
        } // else

        GradientDrawable drawable = (GradientDrawable) holder.itemView.getBackground();

        try {
            Log.i(TAG, "onBindViewHolder Gradient able : " + isGradientEnabled);
            Log.i(TAG, "onBindViewHolder Gradient position : " + position);
            Log.i(TAG, "onBindViewHolder Gradient lastVisibleItemPosition : " + lastVisibleItemPosition);
            if (isGradientEnabled ) {
                //  && position <= lastVisibleItemPosition
                drawable.setAlpha(255);
            } else {
                // else if (position > lastVisibleItemPosition) {
                drawable.setAlpha(0);
            } // else

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
        Log.i(TAG, "likedSongCheck) onBindViewHolder check : " + reRealName);
        String originalName = likedList.get(position).getSelected_song();
        Log.i(TAG, "likedSongCheck) song name : " + originalName);

        try {
            if (position == playing_position) {
                Log.i(TAG, "likedSongCheck) onBindViewHolder *if : " + playing_position + " / " + originalName + " / " + changeName + ".mp3");
                holder.itemView.setBackgroundColor(Color.parseColor("#B57878E1"));

            } else if (selected_position == position || likedList.get(position).getSelected_song().equals(changeName + ".mp3")) {
                Log.i(TAG, "likedSongCheck) onBindViewHolder *else if : " + selected_position + " / " + originalName + " / " + changeName + ".mp3");
                holder.itemView.setBackgroundColor(Color.parseColor("#A0B1FF"));

            } else {
                Log.i(TAG, "likedSongCheck) onBindViewHolder *else : " + selected_position + " / " + originalName + " / " + changeName + ".mp3");
                holder.itemView.setBackgroundColor(Color.parseColor("#B57878E1"));  // 원래 색상으로 설정
            } // else

        } catch (NullPointerException e) {
            e.printStackTrace();
        } // catch
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount Method");
        Log.i(TAG, "likedList.size check : " + likedList.size());
        return likedList.size();
    }

    public void clearItems() {
        Log.i(TAG, "clearItems");
        likedList.clear();
    } // clearItems END

    private class LikedHolder extends RecyclerView.ViewHolder {

        public final TextView user;
        public final ImageView heart;
        public final androidx.constraintlayout.widget.ConstraintLayout layout;

        public LikedHolder(View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.likedUser);
            heart = itemView.findViewById(R.id.likedHeart);
            layout = itemView.findViewById(R.id.likedUserLayout);

            // TODO itemView onClick()
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "itemView) LikedAdapter allSongsView click");
//                   (1) 해당 곡 재생
//                   (2) 해당 곡 아이템 백그라운드 컬러 변경 (고를 때마다 해당 곡만)
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        oldPosition = selected_position;
                        Log.i(TAG, "likedSongCheck position) onClick oldPosition : " + oldPosition);
                        selected_position = position;
                        Log.i(TAG, "likedSongCheck position) onClick selected_position : " + selected_position);

                        notifyItemChanged(oldPosition);
                        notifyItemChanged(selected_position);
                    } // if

                    if (playing_position != RecyclerView.NO_POSITION) {
                        notifyItemChanged(playing_position);
                    } // if

                    playing_position = position;

                    LikedModel clickedItem = likedList.get(position);
                    songName = clickedItem.getSelected_song();

                    v.setBackgroundColor(Color.parseColor("#AAB9FF"));

                    String[] fileTypeCut = songName.split(".mp3");
                    String selected_song = fileTypeCut[0];

                    Intent intent = new Intent("com.example.playlist.PLAY_MUSIC");
                    Log.i(TAG, "likedSongCheck playedItem onClick : " + selected_song);
                    intent.putExtra("selected_song", selected_song);

                    context.sendBroadcast(intent);
                    // 액티비터 시작
                    notifyItemChanged(position);
                } // onClick
            }); // itemView.setOnClickListener

            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    startFeed(position, likedList);
                }
            }); // user

            heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    startFeed(position, likedList);
                }
            }); // heart

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();
                    startFeed(position, likedList);
                }
            }); // layout
        } // LikedHolder Constructor
    } // LikedHolder CLASS

    public LikedModel getLikedModel(int position) {
        return likedList.get(position);
    } // LikedModel

    void startFeed(int positionFrom, ArrayList<LikedModel> likedList) {
        Log.i(TAG, "liked_user click");
        int position = positionFrom;
        if (position != RecyclerView.NO_POSITION) {
            LikedModel clickedItem = likedList.get(position);
            String user = clickedItem.getUser();

            // 새로운 액티비티로 이동 (여기서는 MainActivity라고 가정)
            Intent intent = new Intent(context, Feed.class);

            // 인탠트에 추가 데이터 넣기 (여기서는 선택한 곡의 이름)
            intent.putExtra("user", user);

            // 액티비터 시작
            context.startActivity(intent);
        } // if
    } // startFeed

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

    private void clearGradient(View itemView) {
        itemView.setBackground(null);
    } // clearGradient

    private void applyGradient(View itemView) {
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.TRANSPARENT, Color.parseColor("#AAB9FF")});

        itemView.setBackground(gradientDrawable);
    } // clearGradient

} // Adapter END
