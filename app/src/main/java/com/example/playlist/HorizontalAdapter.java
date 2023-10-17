package com.example.playlist;

import android.content.Context;
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
    private OnItemClickListener onItemClickListener;
    private int selected_position = -1;

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

//        if (selected_position == position) {
//            holder.itemView.setBackgroundColor(Color.parseColor("#7878E1"));
//            holder.likedButton.setTextColor(Color.parseColor("#AAB9FF"));
//        } else {
//            holder.itemView.setBackgroundColor(Color.parseColor("#AAB9FF"));
//            holder.likedButton.setTextColor(Color.parseColor("#7878E1"));
//        } // else
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (selected_position != getAdapterPosition()) {
                        notifyItemChanged(selected_position);
                        selected_position = getAdapterPosition();
                    } // if

                    if (position != RecyclerView.NO_POSITION) {
                        UpdateLikedModel clickedItem = likedList.get(position);
                        String songName = clickedItem.getSong_name();

//                        v.setBackgroundColor(Color.parseColor("#7878E1"));

                        String fileType = songName + ".mp3";
                        notifyItemChanged(position);
                    }
                } // onClick
            }); // itemView.setOnClickListener
        } // ViewHolder Constructor
    } // ViewHolder

    public UpdateLikedModel getUpdateLikedModel(int position) {
        return likedList.get(position);
    }
} // Adapter
