package com.example.playlist;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    private Context context;
    private ArrayList<UpdateLikedModel> likedList;
    private int oldPosition;
    private int playing_position = 0;
    private int lastVisibleItemPosition;
    private int selected_position = -1;
    String songName;

    private OnItemClickListener onItemClickListener;

    String TAG = "[HorizontalAdapter]";

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    } // setOnItemClickListener END

    HorizontalAdapter(Context context, ArrayList<UpdateLikedModel> likedList) {
        Log.i(TAG, "AllSongListAdapter constructor (context, arraylist)");
        this.context = context;
        this.likedList = likedList;
    } // constructor END

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item, parent, false);
        Log.i(TAG, "view check : " + view);
        return new ViewHolder(view);
    } // onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");
        holder.likedButton.setText(likedList.get(position).getSong_name());
//        holder.

        // TODO (1)  to did - check "Selectable" 액티비티 시작 시 현재 메인에서 재생 중인 음악 아이템 체킹 (색상 변경) 되어있음
        // TODO (2) issue - 1번의 상태에서 다른 아이템 클릭 시 추가적으로 체킹 (색상 변경) 되는 상황
        // TODO (3) want - 체킹 (색상 변경) 아이템은 한 개만 되어 있어야 함
        // 현재 체킹되어있는 아이템의 포지션 값을 찾아야 해
        String playingSong = ((MainActivity) MainActivity.mainCtx).mainLogo.getText().toString();
        String[] cutPlayingSong = playingSong.split(" • ");
        String reRealName = cutPlayingSong[0];
        String changeName = reRealName.replace(" ", "_");
        Log.i(TAG, "likedSongCheck) onBindViewHolder check : " + reRealName);
        String originalName = likedList.get(position).getSong_name();
        Log.i(TAG, "likedSongCheck) song name : " + originalName);
//        if (position == playing_position) {
//            Log.i(TAG, "likedSongCheck onBindViewHolder *if : " + playing_position + " / " + originalName + " / " + changeName + ".mp3");
//            holder.likedButton.setBackgroundColor(Color.parseColor("#B57878E1"));
//            holder.likedButton.setTextColor(Color.parseColor("#BDC7F6"));
//
//        } else if (selected_position == position || likedList.get(position).getSong_name().equals(changeName + ".mp3")) {
//            Log.i(TAG, "likedSongCheck) onBindViewHolder *else if : " + selected_position + " / " + originalName + " / " + changeName + ".mp3");
//            holder.likedButton.setBackgroundColor(Color.parseColor("#B57878E1"));
//            holder.likedButton.setTextColor(Color.parseColor("#BDC7F6"));
//        } else {
//            Log.i(TAG, "likedSongCheck) onBindViewHolder *else : " + selected_position + " / " + originalName + " / " + changeName + ".mp3");
//            holder.likedButton.setBackgroundColor(Color.parseColor("#B57878E1"));
//            holder.likedButton.setTextColor(Color.parseColor("#BDC7F6"));
//        } // else

        holder.likedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick (onBindViewHolder)");
            } // onClick
        }); // setOnClickListener
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount Method");
        Log.i(TAG, "likedList.size check : " + likedList.size());
        return likedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button likedButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            likedButton = itemView.findViewById(R.id.likedItem);

            likedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick (ViewHolder Constructor)");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        if (selected_position != getAdapterPosition()) {
                            notifyItemChanged(selected_position);
                            selected_position = getAdapterPosition();
                        } // if

                        UpdateLikedModel clickedItem = likedList.get(position);
                        songName = clickedItem.getSong_name();
                        Log.i(TAG, "likedSongCheck) onClick songName : " + songName);

                        String[] fileTypeCut = songName.split(".mp3");
                        String selected_song = fileTypeCut[0];

                        Intent intent = new Intent("com.example.playlist.PLAY_MUSIC");
                        Log.i(TAG, "likedSongCheck) playedItem onClick : " + selected_song);
                        intent.putExtra("selected_song", selected_song);

                        context.sendBroadcast(intent);
                        // 액티비터 시작
//                        context.startActivity(intent);
                        notifyItemChanged(position);
                    } // if
                } // onClick
            }); // itemView.setOnClickListener
        } // ViewHolder Constructor
    } // ViewHolder

    public UpdateLikedModel getUpdateLikedModel(int position) {
        return likedList.get(position);
    }
} // Adapter
