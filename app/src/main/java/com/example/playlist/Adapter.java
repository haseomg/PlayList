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

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ResponseModel> chatList;
    private SharedPreferences preferences;

    String TAG = "[ChatAdapter]";

    Adapter(Context context, ArrayList<ResponseModel> chatList) {
        Log.i(TAG, "Adapter constructor (context, arraylist)");
        this.context = context;
        this.chatList = chatList;
    } // constructor END

//    ChatAdapter(Context context, ArrayList arrayList, RecyclerView.Adapter adapter) {
//
//    }

//    ChatAdapter() {
//
//    }

    public void addItem(ResponseModel item) {
        Log.i(TAG, "addItem Method");
        if (chatList != null) {
            Log.i(TAG, "chatList != null : " + chatList);
            chatList.add(item);
        } // if END
        else {
            Log.i(TAG, "chatList == null : " + chatList);
        } // else END
    } // addItem Method END

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");
        View view;
        // getItemViewType 에서 뷰타입 1을 리턴받았다면 내 채팅 레이아웃을 받은 Holder를 리턴
        if (viewType == 1) {
            Log.i(TAG, "viewType == 1 : " + viewType);
            view = LayoutInflater.from(context).inflate(R.layout.my_chat_item, parent, false);
            Log.i(TAG, "view check : " + view);
            return new MyHolder(view);
        } // if END
        // getItemViewType 에서 뷰타입 2을 리턴받았다면 상대 채팅 레이아웃을 받은 Holder2를 리턴
        else {
            Log.i(TAG, "viewType != 1 : " + viewType);
            view = LayoutInflater.from(context).inflate(R.layout.your_chat_item, parent, false);
            Log.i(TAG, "view check : " + view);
            return new YourHolder(view);
        } // else END
    } // onCreateViewHolder END

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount Method");
        Log.i(TAG, "chatList.size check : " + chatList.size());
        return chatList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Method");
        // onCreateViewHolder에서 리턴받은 뷰홀더가 Holder라면 내채팅, item_my_chat의 뷰들을 초기화 해줌
        if (holder instanceof MyHolder) {
            Log.i(TAG, "holder instanceof MyHolder");
            ((MyHolder) holder).chat_Text.setText(chatList.get(position).getMsg());
            ((MyHolder) holder).chat_Time.setText(chatList.get(position).getTimestamp());
            ((MyHolder) holder).is_read.setVisibility(View.GONE);
//            try {
//                if (chatList.get(position).getDate_time().contains(chatList.get(position).getDate_time())) {
//                    // 메시지 보낸 시간이 같은 시간이면 같은 시간 메시지 중 맨 마지막 아이템만 시간을 표시해준다.
//                    ((MyHolder) holder).chat_Time.setText("");
//                    ((MyHolder) holder).chat_Time.setText(chatList.get(getItemCount()).getDate_time());
//                } // if END
//            } catch (IndexOutOfBoundsException e) {
//                Log.i(TAG, "Error check : " + e);
//            } // catch END
        } // if END
        // onCreateViewHolder에서 리턴받은 뷰홀더가 Holder2라면 상대의 채팅, item_your_chat의 뷰들을 초기화 해줌
        else if (holder instanceof YourHolder) {
            Log.i(TAG, "holder instanceof YourHolder");
//            ((YourHolder) holder).chat_You_Image.setImageResource(R.mipmap.ic_launcher);
            ((YourHolder) holder).chat_You_Name.setText(chatList.get(position).getMyName());
            ((YourHolder) holder).your_chat_Text.setText(chatList.get(position).getMsg());
            ((YourHolder) holder).your_chat_Time.setText(chatList.get(position).getTimestamp());
        } // else if END
    } // onBindViewHolder END

    // 내가 친 채팅 뷰홀더
    public class MyHolder extends RecyclerView.ViewHolder {
        // 친구목록 모델의 변수들 정의하는부분
        public final TextView chat_Text;
        public final TextView chat_Time;
        public final TextView is_read;

        public MyHolder(View itemView) {
            super(itemView);
            chat_Text = itemView.findViewById(R.id.chat_Text);
            chat_Time = itemView.findViewById(R.id.chat_Time);
            is_read = itemView.findViewById(R.id.is_read_me);
        } // constructor END
    } // MyHolder class END

    // 상대가 친 채팅 뷰홀더
    public class YourHolder extends RecyclerView.ViewHolder {
        // 친구목록 모델의 변수들 정의하는부분
//        public final ImageView chat_You_Image;
        public final TextView chat_You_Name;
        public final TextView your_chat_Text;
        public final TextView your_chat_Time;


        public YourHolder(@NonNull View itemView) {
            super(itemView);
//            chat_You_Image = itemView.findViewById(R.id.chat_You_Image);
            chat_You_Name = itemView.findViewById(R.id.chat_You_Name);
            your_chat_Text = itemView.findViewById(R.id.your_chat_Text);
            your_chat_Time = itemView.findViewById(R.id.your_chat_Time);
        } // constructor END
    } // YourHolder class END

    @Override
    public int getItemViewType(int position) {
        Log.i(TAG, "getItemViewType Method");
        preferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);

        Log.i(TAG, "shared name check : " + preferences.getString("name", ""));
        // 내 아이디와 arraylist의 name이 같다면 내꺼 아니면 상대꺼
        if (chatList.get(position).me.equals(preferences.getString("name", ""))) {
            Log.i(TAG, "내 아이디 == chatList.get(position).name : " + chatList.get(position).me);
            return 1;
        } else {
            Log.i(TAG, "내 아이디 != chatList.get(position).name : " + chatList.get(position).me);
            return 2;
        } // else END
    } // getItemViewType END

    public ArrayList<ResponseModel> getDataList() {
        return  chatList;
    }

} // ChatAdapter END
