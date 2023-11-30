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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ResponseModel> chatList;
    private SharedPreferences preferences;

    String TAG = "[ChatAdapter]";


    Adapter(Context context, ArrayList<ResponseModel> chatList) {
        Log.i(TAG, "ChattingAdapter constructor (context, arraylist)");
        this.context = context;
        this.chatList = chatList;
    } // constructor END

//    public void addItem(ResponseModel item) {
//        Log.i(TAG, "ChattingAdapter addItem Method");
//        if (chatList != null) {
//            Log.i(TAG, "ChattingAdapter chatList != null : " + chatList);
//            chatList.add(item);
//        } // if END
//        else {
//            Log.i(TAG, "ChattingAdapter chatList == null : " + chatList);
//        } // else END
//    } // addItem Method END

    public void addItem(ResponseModel item) {
        Log.i(TAG, "ChattingAdapter addItem Method");
        if (chatList != null) {
            Log.i(TAG, "ChattingAdapter chatList != null : " + chatList);
            chatList.add(item);
        } else {
            Log.i(TAG, "ChattingAdapter chatList == null : " + chatList);
            chatList = new ArrayList<>(); // chatList가 null인 경우 새로운 ArrayList를 생성
            chatList.add(item); // 새로 생성된 ArrayList에 item 추가
        }
        notifyDataSetChanged(); // 데이터가 변경되었음을 알림
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "ChattingAdapter nCreateViewHolder");
        View view;
        // getItemViewType 에서 뷰타입 1을 리턴받았다면 내 채팅 레이아웃을 받은 Holder를 리턴
        if (viewType == 0) {
            Log.i(TAG, "ChattingAdapter onCreateViewHolder viewType : " + viewType);
            // TODO (1) - 첫번째 메시지 발신
            view = LayoutInflater.from(context).inflate(R.layout.chat_date_item, parent, false);
            return new DateHolder(view);

        } else if (viewType == 1) {
            Log.i(TAG, "ChattingAdapter onCreateViewHolder viewType : " + viewType);
            view = LayoutInflater.from(context).inflate(R.layout.my_chat_item, parent, false);
            Log.i(TAG, "view check : " + view);
            return new MyHolder(view);
        } // if END
        // getItemViewType 에서 뷰타입 2을 리턴받았다면 상대 채팅 레이아웃을 받은 Holder2를 리턴
        else {
            Log.i(TAG, "ChattingAdapter onCreateViewHolder viewType : " + viewType);
            view = LayoutInflater.from(context).inflate(R.layout.your_chat_item, parent, false);
            Log.i(TAG, "view check : " + view);
            return new YourHolder(view);
        } // else END
    } // onCreateViewHolder END

    @Override
    public int getItemCount() {
        Log.i(TAG, "ChattingAdapter getItemCount Method");
        Log.i(TAG, "ChattingAdapter chatList.size check : " + chatList.size());
        return chatList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "ChattingAdapter onBindViewHolder Method");
        // onCreateViewHolder에서 리턴받은 뷰홀더가 Holder라면 내채팅, item_my_chat의 뷰들을 초기화 해줌
        ResponseModel chatModel = chatList.get(position);
        ResponseModel currentItem = chatList.get(position);
        ResponseModel previousItem = position > 0 ? chatList.get(position - 1) : null;

        if (holder instanceof MyHolder) {
            Log.i(TAG, "ChattingAdapter holder instanceof MyHolder");
            ((MyHolder) holder).chat_Text.setText(chatList.get(position).getMsg());
//            ((MyHolder) holder).chat_Time.setText(chatList.get(position).getTimestamp());

            if (chatList.size() == 1 || position == chatList.size() - 1 || !chatList.get(position + 1).getTimestamp().equals(chatList.get(position).getTimestamp())) {
                // 리스트 사이즈가 1이거나 현재 아이템이 마지막 아이템일 경우
                // 또는 이전 아이템의 타임스탬프와 현재 아이템의 타임스탬프가 같지 않을 때
                // 현재 아이템의 타임스탬프를 표시한다.
                ((MyHolder) holder).chat_Time.setVisibility(View.VISIBLE);
                ((MyHolder) holder).chat_Time.setText(chatList.get(position).getTimestamp());

            } else {
                ((MyHolder) holder).chat_Time.setVisibility(View.GONE);
            } // else END

            if (((MyHolder) holder).chat_Time.getText().toString().equals("") || ((MyHolder) holder).chat_Time.getText().toString() == null || ((MyHolder) holder).chat_Time.getText().toString().isEmpty()) {
                // if
            } else {
//                ((MyHolder) holder).is_read.setVisibility(View.VISIBLE);
                ((MyHolder) holder).is_read.setVisibility(View.GONE);
            } // else END

            Log.i(TAG, "ChattingAdapter date chatList.get(position).getTimestamp() check : " + chatList.get(position).getTimestamp());
            String[] dateTimeSplit = chatModel.getTimestamp().split("_");

            // 오늘 날짜와 비교
            SimpleDateFormat dateFormat = new SimpleDateFormat("M.dd", Locale.getDefault());
            String today = dateFormat.format(new Date());
            String messageDate = dateTimeSplit[0];
            Log.i(TAG, "ChattingAdapter date check 0 : " + dateTimeSplit[0]);
            try {
                Log.i(TAG, "ChattingAdapter date check 1 : " + dateTimeSplit[1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.i(TAG, "ChattingAdapter ERROR : " + e);
            } // catch END

            if (today.equals(messageDate)) {
                // 오늘 날짜일 경우에는 시간만 표시
                ((MyHolder) holder).chat_Time.setText(dateTimeSplit[1]);
            } else {
                try {
//                    ((MyHolder) holder).chat_Time.setText(dateTimeSplit[1] + " (" + dateTimeSplit[0] + ")");
                    ((MyHolder) holder).chat_Time.setText(dateTimeSplit[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                } // catch END
            } // else END

        } else if (holder instanceof DateHolder) {
            Log.i(TAG, "ChattingAdapter holder instanceof DateHolder");
            String chat_Time = currentItem.getTimestamp();
            Log.i(TAG, "ChattingAdapter chat_Time check : " + chat_Time);

            if (chat_Time.length() > 10) {
                Log.i(TAG, "ChattingAdapter chat_Time length > 10");
                String[] cutTime = chat_Time.split("_");
                Log.i(TAG, "ChattingAdapter chat_Time split check : " + cutTime[0]);
                ((DateHolder) holder).chat_date.setText(cutTime[0]);
            } else if (chat_Time.length() > 0 && chat_Time.length() < 8) {
                Log.i(TAG, "ChattingAdapter chat_Time.length() > 0 && chat_Time.length() < 8");
                // else if
            } else {
//                if (previousItem != null && !currentItem.getTimestamp().substring(0, 5).equalsIgnoreCase(previousItem.getTimestamp().substring(0, 5))) {
//                    Log.i(TAG, "chat_Time if");
//                    String timestamp = chatModel.getTimestamp();
//                    String[] dateTimeSplit = timestamp.split("_");
//                    ((DateHolder) holder).chat_date.setText(dateTimeSplit[0]);
//                    ((DateHolder) holder).chat_date.setVisibility(View.GONE);
//                } else {
                Log.i(TAG, "ChattingAdapter chat_Time else");
                ((DateHolder) holder).chat_date.setVisibility(View.GONE);
//                } // else END
            }

//            String timestamp = currentItem.getTimestamp();
//            String date = timestamp.substring(0, 5);
//            ((DateHolder) holder).chat_date.setText(date);

        } else if (holder instanceof YourHolder) {
            Log.i(TAG, "holder instanceof YourHolder");
//            ((YourHolder) holder).chat_You_Image.setImageResource(R.mipmap.ic_launcher);

            if (chatList.size() == 1 || position == chatList.size() - 1 || !chatList.get(position + 1).getTimestamp().equals(chatList.get(position).getTimestamp())) {
                ((YourHolder) holder).your_chat_Time.setVisibility(View.VISIBLE);
                ((YourHolder) holder).your_chat_Time.setText(chatList.get(position).getTimestamp());
            } else {
                ((YourHolder) holder).your_chat_Time.setVisibility(View.GONE);
            } // else

            ((YourHolder) holder).chat_You_Name.setText(chatList.get(position).getMyName());
            ((YourHolder) holder).your_chat_Text.setText(chatList.get(position).getMsg());
            ((YourHolder) holder).your_chat_Time.setText(chatList.get(position).getTimestamp());

            Log.i(TAG, "ChattingAdapter date chatList.get(position).getTimestamp() check : " + chatList.get(position).getTimestamp());
            String[] dateTimeSplit = chatModel.getTimestamp().split("_");

            // 오늘 날짜와 비교
            SimpleDateFormat dateFormat = new SimpleDateFormat("M.dd", Locale.getDefault());
            String today = dateFormat.format(new Date());
            String messageDate = dateTimeSplit[0];
            try {
                Log.i(TAG, "ChattingAdapter date check 0 : " + dateTimeSplit[0]);
                Log.i(TAG, "ChattingAdapter date check 1 : " + dateTimeSplit[1]);

            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            } // catch

            if (today.equals(messageDate)) {
                // 오늘 날짜일 경우에는 시간만 표시
                ((YourHolder) holder).your_chat_Time.setText(dateTimeSplit[1]);
            } else {
                // 다른 날짜일 경우에는 날짜만 표시
//                ((YourHolder) holder).your_chat_Time.setText(messageDate);
//                ((DateHolder) holder).chat_date.setText(dateTimeSplit[0]);
                try {
//                    ((YourHolder) holder).your_chat_Time.setText(dateTimeSplit[1] + " (" + dateTimeSplit[0] + ")");
                    ((YourHolder) holder).your_chat_Time.setText(dateTimeSplit[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                } // catch
        } // else
        }
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
        Log.i(TAG, "ChattingAdapter getItemViewType Method");
        preferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);

        ResponseModel currentItem = chatList.get(position);
        ResponseModel previousItem = position > 0 ? chatList.get(position - 1) : null;

        // 만약 이전 날짜와 다르다면 날짜를 표시하는 뷰 타입을 리턴합니다.
        try {
            if (previousItem != null && !currentItem.getTimestamp().substring(0, 5).equalsIgnoreCase(previousItem.getTimestamp().substring(0, 5))) {
                return 0;
            }

        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        } // catch


        Log.i(TAG, "ChattingAdapter shared name check : " + preferences.getString("name", ""));
        // 내 아이디와 arraylist의 name이 같다면 내꺼 아니면 상대꺼
        if (chatList.get(position).me.equals(preferences.getString("name", ""))) {
            Log.i(TAG, "ChattingAdapter 내 아이디 == chatList.get(position).name : " + chatList.get(position).me);
            return 1;
        } else {
            Log.i(TAG, "ChattingAdapter 내 아이디 != chatList.get(position).name : " + chatList.get(position).me);
            return 2;
        } // else END
    } // getItemViewType END

    public ArrayList<ResponseModel> getDataList() {
        return chatList;
    }

    private class DateHolder extends RecyclerView.ViewHolder {
        // 친구목록 모델의 변수들 정의하는부분
        public final TextView chat_date;

        public DateHolder(View itemView) {
            super(itemView);
            Log.i(TAG, "ChattingAdapter DateHolder");
            chat_date = itemView.findViewById(R.id.chat_date_Text);
        } // constructor END
    } // MyHolder class END{
} // ChatAdapter END
