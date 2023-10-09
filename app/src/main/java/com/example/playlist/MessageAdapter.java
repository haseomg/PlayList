//package com.example.playlist;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.database.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
//    private List<Message> messageList = new ArrayList<>();
//
//    public void setMessageList(List<Message> messages) {
//        messageList = messages;
//        notifyDataSetChanged();
//    }
//
//    @NotNull
//    @Override
//    public MessageViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.message_item, parent, false);
//        return new MessageViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NotNull MessageViewHolder holder, int position) {
//        Message message = messageList.get(position);
//        holder.contentTextView.setText(message.content);
//        holder.senderTextView.setText(message.sender);
//    }
//
//    @Override
//    public int getItemCount() {
//        return messageList.size();
//    }
//
//    static class MessageViewHolder extends RecyclerView.ViewHolder {
//        TextView contentTextView;
//        TextView senderTextView;
//
//        MessageViewHolder(View itemView) {
//            super(itemView);
////            contentTextView = itemView.findViewById(R.id.contentTextView);
////            senderTextView = itemView.findViewById(R.id.senderTextView);
//        }
//    }
//}
