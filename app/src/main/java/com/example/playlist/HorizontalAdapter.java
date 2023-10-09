package com.example.playlist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    private Context context;
    private ArrayList<UpdateLikedModel> likedList;
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
        holder.textView.setText(likedList.get(position).getSong_name());
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount Method");
        Log.i(TAG, "likedList.size check : " + likedList.size());
        return likedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.likedItem);
        } // ViewHolder Constructor
    } // ViewHolder

    public UpdateLikedModel getUpdateLikedModel(int position) {
        return likedList.get(position);
    }
} // Adapter
