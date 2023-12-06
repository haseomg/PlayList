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
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChatActivity extends AppCompatActivity {

    private MyLifecycleObserver lifecycleObserver;
    private MutableLiveData<String> nameLiveData;

    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    private EditText chatMsg;
    private Button send;
    TextView logo, chatBack;
    String getToken, yourDeviceToken;

    private boolean hasConn = false;
    private Socket chatSocket;
    private URI uri = URI.create("http://13.209.70.232:3000/");
    private IO.Options options;

    private ArrayList<ResponseModel> chatList = new ArrayList<>();
    private Adapter chatAdapter;
    private RecyclerView chat_recyclerView;

    private String TAG = "[Chat Activity]";

    private OkHttpClient client;
    private WebSocket webSocket;

    String getUUID, getUsername, getYourname, getRoomName, getSharedUserName, getSharedUUID, makeUUID, fromEditText;
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
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initial();
        setSend();
        loadChatMessages(uuidFromSelect);
    } // onCreate END


    void initial() {

        chatCtx = ChatActivity.this;

        lifecycleObserver = new MyLifecycleObserver();
        getLifecycle().addObserver(lifecycleObserver);

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
        Log.i(TAG, "uuid - getUuidFromSelect check : " + uuidFromSelect);

//        getUUIDFromTable(getUsername, getYourname);
        getSharedUserName = shared.getString("name", "");
//        getSharedUUID = shared.getString("UUID", "");
        getSharedYou = shared.getString("the_other", "");
        Log.i(TAG, "getSharedUserName check : " + getSharedUserName);
//        Log.i(TAG, "getSharedUUID check : " + getSharedUUID);
        Log.i(TAG, "getSharedYou check : " + getSharedYou);

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setStackFromEnd(true);
        chat_recyclerView.setLayoutManager(layoutManager);
        chat_recyclerView.setHasFixedSize(true);

        logo = findViewById(R.id.chat_logo);
        logo.setText(getYourname);
        chatMsg = findViewById(R.id.chatMsg);
        chatMsg.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        send = findViewById(R.id.sendBtn);
        send.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        options = new IO.Options();
        Log.i(TAG, "options check : " + options);
        options.transports = new String[]{"websocket"};
        Log.i(TAG, "options.transports check : " + options.transports);
        chatSocket = IO.socket(uri, options);
        Log.i(TAG, "chatSocket IO.socket (url, options) check : " + chatSocket);

        setChatSocket();
        getTokenFromChatTable(getRoomName, getYourname);
        connect();
        Log.i(TAG, "getTokenFromChatTable getRoomName + getYourname : " + getRoomName + " / " + getYourname);
        setChatBack();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        // Log and toast
                        Log.i(TAG, "getTokenFromChatTable task.getResult : " + token);
                        System.out.println("token : " + token);
                        try {
                            if (yourDeviceToken != null || !yourDeviceToken.equals("")) {
                                getToken = yourDeviceToken;

//                                Log.i(TAG, "updateToken getRoomName : " + getRoomName);
//                                Log.i(TAG, "updateToken getToken : " + getToken);
//                                Log.i(TAG, "updateToken getUsername : " + getUsername);
//                                updateDeviceTokenToChatTable(getRoomName, getToken, getUsername);
                            } // if
                        } catch (NullPointerException e) {
                            Log.e(TAG, "getTokenFromChatTable updateToken onComplete NULL ERROR : " + e);
                            getToken = token;
                            Log.i(TAG, "getTokenFromChatTable updateToken getRoomName : " + getRoomName);
                            Log.i(TAG, "getTokenFromChatTable updateToken getToken : " + getToken);
                            Log.i(TAG, "getTokenFromChatTable updateToken getUsername : " + getUsername);
                            updateDeviceTokenToChatTable(getRoomName, token, getUsername);
                        } // catch

                    } // onComplete
                });
    } // initial

    void getTokenFromChatTable(String uuid, String you) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.124.239.85/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi getTokenApi = retrofit.create(ServerApi.class);
        Call<ResponseBody> call = getTokenApi.getTokens(uuid, you);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "getTokenFromChatTable fetched successfully.");

                    try {
                        String token = response.body().string().trim();
                        Log.i(TAG, "getTokenFromChatTable token : " + token);
                        yourDeviceToken = token;
                        Log.i(TAG, "getTokenFromChatTable yourDeviceToken : " + yourDeviceToken);
                    } catch (IOException e) {
                        Log.e(TAG, "getTokenFromChatTable onResponse ERROR : " + e);
                    } // catch

                } else {
                    Log.d(TAG, "getTokenFromChatTable Error: " + response.code());
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "getTokenFromChatTable onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueue
    } // getTokenFromChatTable

    void updateDeviceTokenToChatTable(String uuid, String token, String me) {
        Log.i(TAG, "getTokenFromChatTable after updateDeviceTokenToChatTable method");
        // String getToken 값을 chat_messages 테이블에 token 컬럼으로 추가 또는 업데이트 (replace?)
        // getRoomName (uuid 컬럼) 기준으로 조회해서 해당되는 모든 줄에 token 컬럼 추가
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.124.239.85/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi tokenApi = retrofit.create(ServerApi.class);
        Call<ResponseBody> call = tokenApi.updateDeviceToken(uuid, token, me);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "getTokenFromChatTable after onResponse");
                if (response.isSuccessful()) {
                    Log.d(TAG, "getTokenFromChatTable after updateToken successfully.");
                } else {
                    try {
                        Log.d(TAG, "getTokenFromChatTable after updateToken failed : " + response.errorBody().string());

                    } catch (IOException e) {
                        Log.e(TAG, "getTokenFromChatTable after updateToken onResponse ERROR : " + e);
                    } // catch
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "updateToken onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueue
    } // updateDeviceTokenToChatTable

    void setChatBack() {
        chatBack = findViewById(R.id.chatBackBtn);
        chatBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            } // onClick
        }); // setOnClickListener
    } // setChatBack

    private void loadChatMessages(String uuid) {
        Log.i(TAG, "loadChatMessages Method");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://13.124.239.85/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);

        Call<List<ResponseModel>> call = serverApi.loadChat(uuidFromSelect);

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

                    try {
                        chatAdapter.notifyDataSetChanged();
                    } catch (NullPointerException e) {
                        Log.i(TAG, "chatAdapter.notifyDataSetChanged(); null error check : " + e);
                    } // Null catch END
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
            chatSocket = IO.socket("http://13.209.70.232:3000/");
            Log.i(TAG, "setChatSocket IO.socket check : " + chatSocket);
            chatSocket.connect();

            Log.d("SOCKET", "Connection success : " + chatSocket.id());

            joinRoom();

        } catch (Exception e) {
            Log.i(TAG, "setChatSocket catch error 1 : " + e);
        } // catch END
        JSONObject userId = new JSONObject();

        getRoomName = uuidFromSelect;
        Log.i(TAG, "getTokenFromChatTable setChatSocket getRoomName Check : " + getRoomName);
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

        if (!chatMsg.getText().toString().isEmpty()) {
            Log.i(TAG, "if) chatMsg EditText check : " + chatMsg.getText().toString());
            send.setOnClickListener(v -> sendMessage());

        } else {
            Log.i(TAG, "else) chatMsg EditText check : " + chatMsg.getText().toString());

        }
//        send.setOnClickListener(v -> insertToTable());
    } // setChatSocket Method END


    private void joinRoom() {
        chatSocket.emit("join");
    }

    private void setSend() {
        send.setOnClickListener(v -> sendMessage());
    } // setSend END

    private void sendMessage() {
        Log.i(TAG, "sendMessage Method");
        shared = getSharedPreferences("USER", Context.MODE_PRIVATE);

        getTime();

//        Log.i(TAG, "updateToken getRoomName : " + getRoomName);
//        Log.i(TAG, "updateToken getToken : " + getToken);
//        Log.i(TAG, "updateToken getUsername : " + getUsername);
//        updateDeviceTokenToChatTable(getRoomName, getToken, getUsername);
        fromEditText = chatMsg.getText().toString();
        if (!fromEditText.equals("") || fromEditText.length() != 0) {

            insertMsg = fromEditText;
            String[] getTodayCut = getToday.split("-");
            String second = getTodayCut[1];
            String third = getTodayCut[2];
//        insertTime = todaysel;
            if (second.contains("0")) {
                try {
                    String[] monthCut = second.split("0");
                    String month = monthCut[1];
                    today = month + "." + third + "_" + getTimeToTable;
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                } // catch
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
            getRoomName = uuidFromSelect;
            Log.i(TAG, "[Shared]getSharedUUID check 1 : " + uuidFromSelect);
//        }
            JSONObject jsonObject = new JSONObject();
            try {
                Log.i(TAG, "chatSendMessage sendMsessage me : " + getYourname);
                jsonObject.put("me", getYourname);
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
                jsonObject.put("token", yourDeviceToken);
                Log.i(TAG, "getTokenFromChatTable getToken check : " + yourDeviceToken);

            } catch (JSONException e) {
                e.printStackTrace();
            } // catch END
            chatSocket.emit("chat_message", jsonObject);

//        onNewMessage.call();

        }
    } // sendMessage Method END

    private Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {

        if (fromEditText != null && !fromEditText.equals("") && fromEditText.length() != 0) {

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
            String name, image, room_name, message, timestamp, me;
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
                me = data.getString("me");
                Log.i(TAG, "chatSendMessage onNewMessage me : " + me);


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
        } else { // if END

        } // else

    }); // onNewMessage END

    private Emitter.Listener onNewUser = args -> runOnUiThread(() -> {

        try {

            if (!fromEditText.equals("") || fromEditText.length() != 0) {

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
            } else {

            }

        } catch (NullPointerException e) {
            Log.e(TAG, "chatMsg Null ERROR : " + e);
        }
    }); // onNewUser END

    // TODO. 채팅 데이터 insert 할 때, SQLlite 테이블에도 넣어줘야 한다. SQLlite 입력할 때 중복 제거
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
                    Log.i(TAG, "Insert (response not successful)  and response.body check : " + response.body());
                } // else END
            } // onResponse END

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
//                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            } // onFailure END
        }); // call.enqueque END
    } // insertData method END


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
        uuidKey = uuidFromSelect;
        Log.i(TAG, "insert uuid : " + uuidFromSelect);
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
        insertData(uuidFromSelect, me, you, from_idx, msg, msg_idx, date_time, readCheck, image_idx);
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
            String reHour = Integer.toString(hourToInt).replaceFirst("^0+", "");
            hourNminute = "오후 " + reHour + ":" + minute;
            getTimeToTable = "오후 " + reHour + ":" + minute;
            Log.i(TAG, "insert hourNminute check : " + hourNminute);
            Log.i(TAG, "insert getTimeToTable check : " + getTimeToTable);

        } else {
            if (hourToInt == 0) {
                hour = "12";
            } else {
                hour = Integer.toString(hourToInt).replaceFirst("^0+", "");
            }
            hourNminute = "오전 " + hour + ":" + minute;
            getTimeToTable = "오전 " + hour + ":" + minute;
            Log.i(TAG, "insert hourNminute check : " + hourNminute);
            Log.i(TAG, "insert getTimeToTable check : " + getTimeToTable);
        } // else END
    } // getTime Method END


    static class MyLifecycleObserver implements DefaultLifecycleObserver {
        String TAG = "[Lifecycle]";
        String bringGetSharedUUID;
        ChatActivity chatActivity;

        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {
            Log.d(TAG, "ChatActivity Lifecycle onCreate");
        } // onCreate END

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            Log.d(TAG, "ChatActivity Lifecycle onStart");
        } // onStart END

        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
            Log.d(TAG, "ChatActivity Lifecycle onResume");
        } // onResume END

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            Log.d(TAG, "ChatActivity Lifecycle onPause");
        } // onPause END

        @Override
        public void onStop(@NonNull LifecycleOwner owner) {
            Log.d(TAG, "ChatActivity Lifecycle onStop");
        } // onStop END

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            Log.d(TAG, "ChatActivity Lifecycle onDestroy");
        } // onDestroy END

        void initial() {
//            chatActivity = new ChatActivity();
//            bringGetSharedUUID = ((ChatActivity) ChatActivity.chatCtx).getSharedUUID;
        } // initial void END
    } // Lifecycle CLASS END

    void setFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Get  token failed", task.getException());
                        return;
                    } // if
                    String token = task.getResult();
                    Toast.makeText(ChatActivity.this, token, Toast.LENGTH_SHORT).show();
                    // 여기서 서버나 사용하려는 곳에 토큰 저장..
                }); // addOnCompleteListener
    } // setFCM


} // Main CLASS END