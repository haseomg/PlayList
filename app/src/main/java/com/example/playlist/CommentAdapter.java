package com.example.playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<CommentModel> commentList;
    private SharedPreferences preferences;

    String TAG = "[Comment Adapter]";

    CommentAdapter(Context context, ArrayList<CommentModel> commentList) {
        Log.i(TAG, "Comment Adapter constructor (context, arraylist)");
        this.context = context;
        this.commentList = commentList;
    } // constructor END

    public void addItem(CommentModel item) {
        Log.i(TAG, "addItem Method");
        if (commentList != null) {
            Log.i(TAG, "commentList != null : " + commentList);
            commentList.add(item);
        } // if END
        else {
//            Log.i(TAG, "commentList == null : " + commentList);
        } // else END
    } // addItem Method END


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        Log.i(TAG, "view check : " + view);
        return new CommentHolder(view);
    } // onCreateViewHolder로

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");
        if (holder instanceof CommentHolder) {
            CommentModel commentModel = commentList.get(position);

            ((CommentHolder) holder).comment_msg.setText(commentModel.getMsg());
            ((CommentHolder) holder).comment_user.setText(commentModel.getUser_name() + " ✦︎ ");
        } // if END
    } // onBindViewHolder

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount Method");
        Log.i(TAG, "commentList.size check : " + commentList.size());
        return commentList.size();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        public final TextView comment_msg;
        public final TextView comment_user;

        public CommentHolder(View itemView) {
            super(itemView);
            comment_msg = itemView.findViewById(R.id.commentMsg);
            comment_user = itemView.findViewById(R.id.commentUser);
        } // CommentHolder

    } // CommentHolder CLASS

    public CommentAdapter(ArrayList<CommentModel> commentList) {
        this.commentList = commentList;
    } // CommentAdapter END

    public CommentModel getCommentModel(int position) {
        return commentList.get(position);
    }

    public void clearItems() {
        Log.i(TAG, "clearItems");
        commentList.clear();
    } // clearItems
}
