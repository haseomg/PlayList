package com.example.playlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private ArrayList<ChatListModel> chatRoomList;
    private SharedPreferences preferences;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    } // setOnItemClickListener END

    String TAG = "[ChatListAdapter]";

    ChatListAdapter(Context context, ArrayList<ChatListModel> chatRoomList) {
        Log.i(TAG, "ChatListAdapter constructor (context, arraylist)");
        this.context = context;
        this.chatRoomList = chatRoomList;
    } // constructor END

    public void addItem(ChatListModel item) {
        Log.i(TAG, "addItem Method");
        if (chatRoomList != null) {
            Log.i(TAG, "chatList != null : " + chatRoomList);
            chatRoomList.add(item);
        } // if END
        else {
            Log.i(TAG, "chatList == null : " + chatRoomList);
        } // else END
    } /// addItem Method END

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.chat_list_item, parent, false);
        Log.i(TAG, "view check : " + view);
        return new ChatRoomHolder(view);
    } // onCreateViewHolder END

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");
        if (holder instanceof ChatRoomHolder) {
            ChatListModel chatListModel = chatRoomList.get(position);
            ((ChatRoomHolder) holder).user_name.setText(chatRoomList.get(position).getThe_other());
            ((ChatRoomHolder) holder).last_msg.setText(chatRoomList.get(position).getMessage());
//            ((ChatRoomHolder) holder).red_circle.setVisibility(View.GONE); // 기본 상태가 안 보이게

//            if(chatListModel.getIs_read() == 1) {
//                ((ChatRoomHolder) holder).red_circle.setVisibility(View.VISIBLE);
//            } else {
//                ((ChatRoomHolder) holder).red_circle.setVisibility(View.GONE); // 기본 상태가 안 보이게
//            }

            String[] dateTimeSplit = chatListModel.getDate_time().split("_");

            // 오늘 날짜와 비교
            SimpleDateFormat dateFormat = new SimpleDateFormat("M.dd", Locale.getDefault());
            String today = dateFormat.format(new Date());
            String messageDate = dateTimeSplit[0];

            if (today.equals(messageDate)) {
                // 오늘 날짜일 경우에는 시간만 표시
                ((ChatRoomHolder) holder).last_time.setText(dateTimeSplit[1]);
            } else {
                // 다른 날짜일 경우에는 날짜만 표시
                ((ChatRoomHolder) holder).last_time.setText(messageDate);
            }


        } // if END
    } // onBindViewHolder END

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount Method");
        Log.i(TAG, "chatRoomList.size check : " + chatRoomList.size());
        return chatRoomList.size();
    } // getItemCount END

    private class ChatRoomHolder extends RecyclerView.ViewHolder {
        // 채팅 룸 목록 모델의 UI 들 정의
        public final TextView user_name;
        public final TextView last_msg;
        public final TextView last_time;
//        public final ImageView red_circle;
        public final ImageView profile_image;

        public ChatRoomHolder(View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.list_userName);
            last_msg = itemView.findViewById(R.id.list_lastMsg);
            last_time = itemView.findViewById(R.id.list_msgTime);
//            red_circle = itemView.findViewById(R.id.red_circle);
            profile_image = itemView.findViewById(R.id.profile_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(position);
                            Log.i(TAG, "itemView.setOnClickListener : " + position + "번 쨰");
                        } // if END
                    } // if END
                } // onClick END
            }); // itemView.setOnClickListener END
        } // constructor END
    } // ChatRoomHolder class END

    public ChatListAdapter(ArrayList<ChatListModel> chatRoomList) {
        this.chatRoomList = chatRoomList;
    }

    public ChatListModel getChatModel(int position) {
        return chatRoomList.get(position);
    }

} // ChatListAdapter CLASS END