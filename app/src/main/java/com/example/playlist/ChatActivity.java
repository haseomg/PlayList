package com.example.playlist;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChatActivity extends AppCompatActivity {


    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    private Button chatBack;
    private EditText chatMsg;
    private Button send;

    private boolean hasConn = false;
    private Socket chatSocket;
    private URI uri = URI.create("http://54.180.83.241:3000/");
    private IO.Options options;

    private ArrayList<ResponseModel> chatList = new ArrayList<>();
    private Adapter chatAdapter;
    private RecyclerView chat_recyclerView;

    private String TAG = "MainActivity";

    private OkHttpClient client;
    private WebSocket webSocket;

    String getUUID, getUsername, getYourname, getRoomName, getSharedUserName, getSharedUUID, makeUUID;
    static Context chatCtx;

    String uuidKey, me, you, from_idx, msg, date_time, image_idx, today;
    String insertUUID, insertMsg, insertTime, getToday, getTimeToTable, hourNminute;
    int msg_idx, readCheck;

    String message, timestamp, image, uuidForChat;
    int is_read = 1;
    String uuidFromSelect, getSharedMe, getSharedYou;

//    String putMessage, putTime, putToday;
//    int put_is_read;

    private boolean isKeyboardOpen;
    private int keyboardHeight;
    String uuid;
    private ServerApi serverApi;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initial();
        setSend();

    } // onCreate END









    void initial() {
        chatCtx = ChatActivity.this;

        shared = getSharedPreferences("USER", MODE_PRIVATE);
        editor = shared.edit();

        serverApi = ApiClient.getApiClient().create(ServerApi.class);

        Intent intent = getIntent();
        getUsername = intent.getStringExtra("username");
        getYourname = intent.getStringExtra("yourname");
        uuidFromSelect = intent.getStringExtra("uuid");
        Log.i(TAG, "[Shared]getUUID check : " + getUUID);

        Log.i(TAG, "getUsername check : " + getUsername);
        Log.i(TAG, "getYourname check : " + getYourname);
        Log.i(TAG, "getUuidFromSelect check : " + uuidFromSelect);

//        getUUIDFromTable(getUsername, getYourname);
        getSharedUserName = shared.getString("name", "");
        getSharedUUID = shared.getString("UUID", "");
        getSharedYou = shared.getString("the_other", "");
        Log.i(TAG, "getSharedUserName check : " + getSharedUserName);
        Log.i(TAG, "getSharedUUID check : " + getSharedUUID);
        Log.i(TAG, "getSharedYou check : " + getSharedYou);

        loadChatMessages(getSharedUUID);

//        if (!getSharedUUID.contains(getUsername) && !getSharedUUID.contains(getYourname)) {
//            getRoomName = getSharedUUID;
//            Log.i(TAG, "[Shared]getSharedUUID check 1 : " + getSharedUUID);
//        } else if (uuidForChat != null){
//            // 받아와야 되는데... DB uuid... db에서 받아온 게 없을 때 uuid 생성
//            extractingUUID(getUsername, getYourname);
//            getRoomName = uuid;
//            Log.i(TAG, "[Shared]getSharedUUID check 2 : " + uuid);
//        }
//        editor.commit();

        options = new IO.Options();
        Log.i(TAG, "options check : " + options);
        options.transports = new String[]{"websocket"};
        Log.i(TAG, "options.transports check : " + options.transports);
        chatSocket = IO.socket(uri, options);
        Log.i(TAG, "chatSocket IO.socket (url, options) check : " + chatSocket);
//        connect();


        editor.commit();

        chatAdapter = new Adapter(this, chatList);
        Log.i(TAG, "ChatAdapter initialize check : " + chatAdapter);

        chat_recyclerView = findViewById(R.id.recyclerView);
        chat_recyclerView.setAdapter(chatAdapter);
//        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//
//                int lastVisiblePosition = ((LinearLayoutManager) chat_recyclerView.getLayoutManager()).findLastVisibleItemPosition();
//
//                if (lastVisiblePosition == -1 || ((positionStart) >= (chatAdapter.getItemCount() -1 ) &&
//                        lastVisiblePosition == (positionStart -1))) {
//                    chat_recyclerView.scrollToPosition(positionStart);
//                } // if END
//            } // onItemRangeInserted END
//        }); // Observer END
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        // TODO. KEYBOARD NO TOUCH RECYCLERVIEW
//        isKeyboardOpen = false;
//        keyboardHeight = 0;
//        onWindowFocusChanged(isKeyboardOpen);
//        // 키보드 상태에 따른 화면 조정
//            if (isKeyboardOpen) {
//                chat_recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                    @Override
//                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                        layoutManager.setStackFromEnd(true);
//                    } // onLayoutChange END
//                }); // if END
//            } else {
//                chat_recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                    @Override
//                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                        layoutManager.setStackFromEnd(false);
//                    } // Listener END
//                }); // onLayoutChange END
//            } // else END
        layoutManager.setStackFromEnd(true);
        chat_recyclerView.setLayoutManager(layoutManager);
        chat_recyclerView.setHasFixedSize(true);
//        chat_recyclerView.setLayoutParams(new LinearLayout.LayoutParams
//                (ViewGroup.LayoutParams.MATCH_PARENT, getWindow().getDecorView().getRootView()
//                        .getHeight() - getWindow().getDecorView().getRootView().getRootView()
//                        .getSystemUiVisibility()));

//        final View activityRootView = findViewById(R.id.root_layout);
////        // activityRootView의 레이아웃 변화를 감지하는 리스너
//        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                Rect r = new Rect();
//                // 화면에서 보이는 영역의 정보를 Rect 객체인 r에 저장
//                activityRootView.getWindowVisibleDisplayFrame(r);
//                // 화면 전체 높이를 가져와 screenHeight에 저장
//                int screenHeight = activityRootView.getRootView().getHeight();
//                Log.i(TAG, "screenHeight check : " + screenHeight);
//
//                // 키보드가 올라온 경우 마지막 메시지로 이동
//
//                // 화면 전체 높이에서 보이는 영역의 높이를 뺀 것으로, 키보드 높이를 구한다.
//                int heightDifference = screenHeight - (r.bottom - r.top);
//                // 키보드 높이를 화면체 높이로 나누어 스케일 값을 구한다.
//                float keyboardHeight = (float) heightDifference / screenHeight;
//                Log.i(TAG, "keyboardHeight check : " + keyboardHeight);
//                if (keyboardHeight > 0.15) {
//                    int lastIndex = chatAdapter.getItemCount() - 1;
//                    Log.i(TAG, "item lastIndex check : " + lastIndex);
//                    if (lastIndex >= 0) {
//                        chat_recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
//                    } // if END
//                } // if END
//            } // onGlobalLayout END
//        }); // Listener END

        chatMsg = findViewById(R.id.chatMsg);
        chatMsg.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        send = findViewById(R.id.sendBtn);
        send.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
//        chatBack = findViewById(R.id.chatBackBtn);
//        chatBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        options = new IO.Options();
        Log.i(TAG, "options check : " + options);
        options.transports = new String[]{"websocket"};
        Log.i(TAG, "options.transports check : " + options.transports);
        chatSocket = IO.socket(uri, options);
        Log.i(TAG, "chatSocket IO.socket (url, options) check : " + chatSocket);

        connect();
        setChatSocket();
    }

    private void loadChatMessages(String uuid) {
        Log.i(TAG, "loadChatMessages Method");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.180.155.66/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);

        Call<List<ResponseModel>> call = serverApi.loadChat(getSharedUUID);

        call.enqueue(new Callback<List<ResponseModel>>() {
            @Override
            public void onResponse(Call<List<ResponseModel>> call, Response<List<ResponseModel>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Request failed : " + response.code());
                    return;
                } else {
                    Log.e(TAG, "Request Success! : " + response.code());
                    List<ResponseModel> responseModels = response.body();
                    Log.i(TAG, "getChatData response.body : " + responseModels);

                    chatList.clear();
//                    for (ResponseModel responseModel : responseModels) {
//                        String
//                    }
                    chatList.addAll(response.body());

                    chatAdapter.notifyDataSetChanged();
                } // else END
            } // onResponse END

            @Override
            public void onFailure(Call<List<ResponseModel>> call, Throwable t) {
                Log.e(TAG, "onFailure : " + t.getMessage());
            }
        });
    } // method END


    void connect() {
        try {
//            chatSocket = IO.socket("http://13.125.216.244:3000/");
            Log.i(TAG, "chatSocket check : " + chatSocket);
//            chatSocket.connect(); //위 주소로 연결

            Log.d("SOCKET", "Connection success : " + chatSocket.id());

            chatSocket.on("connect_user", new Emitter.Listener() {
                // 서버가 'connect_user' 이벤트를 일으킨 경우
                @Override
                public void call(Object... args) { // args에 서버가 보내온 json 데이터가 들어간다
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.i(TAG, "run");

                            try {
                                Log.i(TAG, "try");
                                JSONObject data = (JSONObject) args[0];
//                                tv_text_con.setText(data.getString("id"));// json에서 id라는 키의 값만 빼서 텍스트뷰에 출력

                            } catch (Exception e) {
                                Log.i(TAG, "catch 1 : " + e);
//                                Toast.makeText(getApplicationContext(), e.getMessage(),
//                                        Toast.LENGTH_LONG).show();
                            } // catch END
                        } // run END
                    }); // runOnUiThread END
                } // call Method END
            }); // chatSocket.on END

        } catch (Exception e) {
            Log.i(TAG, "catch 2 : " + e);
//            tv_text_con.setText("안됨");
//            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        } // catch END
    } // connect method END

    void setChatSocket() {
        try {
            chatSocket = IO.socket("http://54.180.83.241:3000/");
            Log.i(TAG, "setChatSocket IO.socket check : " + chatSocket);
            chatSocket.connect();

            Log.d("SOCKET", "Connection success : " + chatSocket.id());

            chatSocket.on("connect_user", onNewUser);
            chatSocket.on("chat_message", onNewMessage);
//            chatSocket.on("notify", onNewNotification);

            joinRoom();

        } catch (Exception e) {
            Log.i(TAG, "setChatSocket catch error 1 : " + e);
        } // catch END
        JSONObject userId = new JSONObject();

        getRoomName = getSharedUUID;
        insertUUID = getRoomName;

        try {
            Log.i(TAG, "setChatSocket try");
            Log.i(TAG, "username check ------- " + getUsername);
            userId.put("username", getUsername);
            userId.put("roomName", getRoomName);
            chatSocket.emit("connect_user", userId);
        } catch (JSONException e) {
            Log.i(TAG, "setChatSocket catch error 2 : " + e);
        } // catch END

        hasConn = true;

        send.setOnClickListener(v -> sendMessage());
//        send.setOnClickListener(v -> insertToTable());
    } // setChatSocket Method END


    private void getUUIDFromTable(String me, String you) {
        Log.i(TAG, "getUUIDFRomToTable Method");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.180.155.66/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);

        Call<ResponseModel> call = serverApi.getUUID(me, you);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    // 성공적인 응답 처리
//                    Toast.makeText(ChatActivity.this, "Data selected successfully", Toast.LENGTH_SHORT).show();
                    ResponseModel responseModel = response.body();
                    if (responseModel != null) {
                        String uuid = responseModel.getUUID();

                        Log.d(TAG, "uuid : " + uuid);
                        uuidForChat = uuid;
                        Log.d(TAG, "uuid 2 : " + uuidForChat);

                        editor.putString("UUID", uuidForChat);
                        editor.commit();
                    } else {
                        Log.d(TAG, "uuid : " + "응답 데이터가 null 입니다.");
                    }
                } else {
                    // 실패한 응답 처리
//                    Toast.makeText(ChatActivity.this, "Failed to select data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                // 에러 처리
//                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void joinRoom() {
        chatSocket.emit("join");
    }

    private void setSend() {
//        send.setOnClickListener(v -> sendMessage());
    } // setSend END

    private void sendMessage() {
        Log.i(TAG, "sendMessage Method");
        shared = getSharedPreferences("USER", Context.MODE_PRIVATE);

        getTime();

        String fromEditText = chatMsg.getText().toString();
        insertMsg = fromEditText;
        String[] getTodayCut = getToday.split("-");
        String second = getTodayCut[1];
        String third = getTodayCut[2];
//        insertTime = todaysel;
        if (second.contains("0")) {
            String[] monthCut = second.split("0");
            String month = monthCut[1];
            today = month + "." + third + "_" + getTimeToTable;
//            insertTime = month + "." + third;
        } else {
            today = second + "." + third + "_" + getTimeToTable;
//            insertTime = second + "." + third;
        }
        insertTime = today;
        insertToTable();

        String message = chatMsg.getText().toString().trim();
        Log.i(TAG, "message check : " + message);
        if (TextUtils.isEmpty(message)) {
            return;
        } // if END

        int is_read = 1;

//        ChatModel item = new ChatModel(shared.getString("name", ""), chatMsg.getText().toString(), "example", hourNminute);
//        chatAdapter.addItem(item);
//        chatAdapter.notifyDataSetChanged();

        chatMsg.setText("");
        // TODO getRoomName 2

//        if (getSharedUUID.contains(getSharedUserName) && getSharedUUID.contains(getSharedYou)) {
//        if (!uuid.equals(getSharedUUID)) {
        getRoomName = getSharedUUID;
        Log.i(TAG, "[Shared]getSharedUUID check 1 : " + getSharedUUID);
//        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", getUsername);
            Log.i("json put", "name check : " + getUsername);
            jsonObject.put("script", message);
            Log.i("json put", "message check : " + message);
            jsonObject.put("profile_image", "example");
            jsonObject.put("date_time", hourNminute);
            Log.i("json put", "date_time check : " + hourNminute);
            jsonObject.put("roomName", getRoomName);
            jsonObject.put("today", getToday);
            Log.i("json put", "today check : " + getToday);
            jsonObject.put("is_read", is_read);
        } catch (JSONException e) {
            e.printStackTrace();
        } // catch END
        chatSocket.emit("chat_message", jsonObject);

//        onNewMessage.call();
    } // sendMessage Method END

    private Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {

        Log.i(TAG, "onNewMessage Listener");

        int length = args.length;
        if (length == 0) {
            Log.i(TAG, "length == 0 : " + args);
            return;
        } // if END

        JSONObject data = (JSONObject) args[0];
//        JSONObject data = new JSONObject();
        Log.i(TAG, "data check : " + data);
        Log.i(TAG, "args check : " + args);
        String name, image, room_name, message, timestamp;
        int is_read = 1;

        try {
            Log.i(TAG, "try");
            name = data.getString("name");
            Log.i(TAG, "name check : " + name);
            message = data.getString("script");
            Log.i(TAG, "script check : " + message);
            image = data.getString("profile_image");
            Log.i(TAG, "profile_image check : " + image);
            timestamp = data.getString("date_time");
            Log.i(TAG, "date_time check : " + timestamp);
            room_name = data.getString("roomName");
            Log.i(TAG, "roomName check : " + room_name);
            is_read = data.getInt("is_read");

            ResponseModel format = new ResponseModel(name, message, timestamp, is_read, image);
//            ArrayList<ChatModel> chatList = chatAdapter.getDataList();
            chatAdapter.addItem(format);
            chatAdapter.notifyDataSetChanged();

//            Toast.makeText(getApplicationContext(), "✉️", Toast.LENGTH_SHORT).show();
//            int position = chatList.size() -1;
//            chatAdapter.notifyItemInserted(position);
            chat_recyclerView.scrollToPosition(chatList.size() - 1);

        } catch (JSONException e) {
            Log.i(TAG, "onNewMessage catch error : " + e);
        } // catch END
    }); // onNewMessage END

    private Emitter.Listener onNewUser = args -> runOnUiThread(() -> {
        int length = args.length;
        if (length == 0) {
            return;
        } // if END

        String username = args[0].toString();
        String roomName;
        try {
            JSONObject object = new JSONObject(username);
            username = object.getString("username");
            roomName = object.getString("roomName");
            Log.i(TAG, "username check : " + username);
            Log.i(TAG, "roomName check : " + roomName);
        } catch (JSONException e) {
            e.printStackTrace();
        } // catch END
    }); // onNewUser END

    private void insertData(String uuid, String me, String you, String from_idx, String msg, int msg_idx, String date_time, int is_read, String image_idx) {
        Log.i(TAG, "InsertData Method");
        Call<Void> call = serverApi.insertData(uuid, me, you, from_idx, msg, msg_idx, date_time, is_read, image_idx);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "InsertData Method onResponse()");
                if (response.isSuccessful()) {
                    Log.i(TAG, "InsertData Method onResponse() isSuccessful");
//                    Toast.makeText(ChatActivity.this, "Data inserted successfully", Toast.LENGTH_SHORT).show();
                    // response ok인데.... 서버 측에도 틀린 코드가 없는데 왜 테이블에 값이 안 들어갈까 ㅠㅠ 다른 서버랑 겹쳐서 였음... ㅂㄷㅂㄷ
                    Log.i(TAG, "Insert (response success)  and response.body check : " + response.body());
                } else {
                    Log.i(TAG, "InsertData Method onResponse() !isSuccessful");
//                    Toast.makeText(ChatActivity.this, "Failed to insert data", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Insert (response failed)  and response.body check : " + response.body());
                } // else END
            } // onResponse END

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            } // onFailure END
        }); // call.enqueque END
    } // insertData method END


    // TODO. Insert Chat_info To chat_messages table T T Why don't ......
    private void addData(String uuid, String me, String you, String from_idx, String msg, int msg_idx, String date_time, int is_read, String image_idx) {
        Log.i(TAG, "insert method addData");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.180.155.66/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi api = retrofit.create(ServerApi.class);
        Call<ResponseModel> call = api.addData(uuid, me, you, from_idx, msg, msg_idx, date_time, is_read, image_idx);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    // 성공적인 응답 처리
//                    Toast.makeText(ChatActivity.this, "Data insert successfully", Toast.LENGTH_SHORT).show();
                    ResponseModel responseModel = response.body();
                    String response_check = String.valueOf(response.body());
                    Log.e(TAG, "response.body check : " + responseModel);
                    Log.d(TAG, "response.body check : " + response_check);

                } else {
                    // 실패한 응답 처리
//                    Toast.makeText(ChatActivity.this, "Failed insert data", Toast.LENGTH_SHORT).show();
                } // else END
            } // onResponse END

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                // 에러 처리
//                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            } // onFailure END
        }); // call.enqueque END
    } // addData END

    void noti() {
        // 알림 관리자 객체를 가져옵니다.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Android Oreo(8.0) 이상에서는 알림 채널이 필요합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "default_notification_channel_id";
            CharSequence channelName = "Default Channel";
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // 알림을 빌드합니다. 이 예시에서는 아이콘과 제목, 내용만 설정하였습니다.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default_notification_channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // 아이콘 설정
                .setContentTitle("✉️") // 제목 설정
//                .setContentText(receivedMessage) // 내용 설정
                .setAutoCancel(true); // 사용자가 터치하면 자동으로 알림 제거

        // 알림을 표시하고 고유 ID를 부여합니다. 같은 ID를 사용할 경우 알림이 업데이트됩니다.
        notificationManager.notify(1, builder.build());
    }

    void msgFromServer() {
        Log.i(TAG, "chatSocket null check : " + chatSocket);
        if (chatSocket != null) {
            chatSocket.on("msg_to_client", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject data = (JSONObject) args[0];
                                Toast.makeText(getApplicationContext(), "Hi❕ " + data.getString("msg"), Toast.LENGTH_SHORT);
                            } catch (JSONException e) {
                                Log.e(TAG, "msgFromServer error check : " + e);
                            } // catch END
                        } // run END
                    }); // runOnUiThread END
                } // call END
            }); // msg_to_client Emitter.Listener END
        } // if chatSocket != null END
    } // msgFromServer Method END

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    } // onStart END

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    } // onResume END

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    } // onPause END

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    } // onStop END

    protected void onDestroy() { // 어플리케이션 종료시 실행
        super.onDestroy();
        // chatSocket.emit("disconnect",null);
        chatSocket.disconnect(); // 소켓을 닫는다
        editor.putString("room", "");
        editor.commit();
    } // onDestroy END

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            checkKeyboardHeight();
        } else {
            isKeyboardOpen = false;
        } // else END
    } // onWindowFocusChanged END

    // 키보드 높이 확인 메서드
    // 키보드 높이 확인 메서드
    private void checkKeyboardHeight() {
        // 뷰 전체에 대한 사이즈를 가진 Rect 객체 생성
        Rect r = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

        // 측정된 높이 계산
        int screenHeight = getWindow().getDecorView().getRootView().getHeight();
        int heightDifference = screenHeight - r.bottom;

        // 이전 높이와 다르다면 상태 변경
        if (keyboardHeight != heightDifference) {
            isKeyboardOpen = heightDifference > 100; //임계 (100픽셀)
            keyboardHeight = heightDifference;
        }
    }

    void insertToTable() {
        Log.i(TAG, "insert to chat_messages table");
        //TODO Insert Chat_Info To chat_messages
        if (uuidForChat != null) {
            uuidKey = uuidForChat;
            Log.i(TAG, "insert uuid : " + uuidKey);
        } else {
            uuidKey = insertUUID;
            Log.i(TAG, "insert uuid : " + insertUUID);
        }
        me = getUsername;
        Log.i(TAG, "insert me : " + getUsername);
        you = getYourname;
        Log.i(TAG, "insert you : " + getYourname);
        from_idx = getUsername;
        Log.i(TAG, "insert from_idx : " + getUsername);
        // msg 값 not yet
        msg = insertMsg;
        Log.i(TAG, "insert msg : " + msg);
        // 숫자 늘어나야 하눈디..
        msg_idx = 1;
        Log.i(TAG, "insert msg_idx : " + msg_idx);
        // 시간 받아오슈..
        date_time = insertTime;
        Log.i(TAG, "insert time : " + date_time);
        readCheck = is_read;
        Log.i(TAG, "insert readCheck : " + readCheck);
        image_idx = "image";
        Log.i(TAG, "insert imageCheck : " + image_idx);
//            addData(uuidKey, me, you, from_idx, msg, msg_idx, date_time, readCheck, image_idx);
        insertData(uuidKey, me, you, from_idx, msg, msg_idx, date_time, readCheck, image_idx);
    }

    void getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        getToday = sdf.format(date);

        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String getTime = sdfTime.format(date);
        String[] reTime = getTime.split(":");
        String hour = reTime[0];
        String minute = reTime[1];
        int hourToInt = Integer.parseInt(hour);

        if (hourToInt >= 12) {
            if (hourToInt > 12) {
                hourToInt -= 12;
            }
            String reHour = Integer.toString(hourToInt);
            hourNminute = "오후 " + reHour + ":" + minute;
            getTimeToTable = "오후 " + reHour + ":" + minute;
            Log.i(TAG, "insert hourNminute check : " + hourNminute);
            Log.i(TAG, "insert getTimeToTable check : " + getTimeToTable);

        } else {
            if (hour.equals("00")) {
                hour = "12";
            }
            hourNminute = "오전 " + hour + ":" + minute;
            getTimeToTable = "오전 " + hour + ":" + minute;
            Log.i(TAG, "insert hourNminute check : " + hourNminute);
            Log.i(TAG, "insert getTimeToTable check : " + getTimeToTable);
        } // else END
    } // getTime Method END


} // Main CLASS END