package com.example.playlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.socket.emitter.Emitter;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ChatSelect extends AppCompatActivity {


    private SharedPreferences shared;
    private SharedPreferences.Editor editor;

    private UUIDDatabase uuidDatabase;

    String TAG = "[Chat Select CLASS]";
    Intent intent;
    String uuidFromDB, getUsername, getRoomName, myName, yourName, uuidForChat, uuid, you, uuidResponse;
    private ArrayList<String> uuidValues;
    Button enterButton;
    TextView name, list_name, list_msg, list_time;
    LinearLayoutManager layoutManager;
    EditText write_chat_person;
    ImageView profile_image, red_circle;

    ArrayList<ChatListModel> chatRoomList = new ArrayList<>();
    ChatListAdapter chatListAdapter;
    RecyclerView chat_list_recyclerView;

    String ourUUID;
    private static final String BASE_URL = "http://54.180.155.66/";

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_select);
        Log.i(TAG, "onCreate()");

        initial();
//        setUuidDatabase();
//        setSelectUUIDDatabase();
//        getUUIDFromTable(getUsername);
        getUUIDFromRoomDB(getUsername);
        // TODO uuid값 sql lite에서 조회
    } // onCreate END

    // chat_messages 테이블 me, you 컬럼에 내 이름이 들어가있다면
    // 가장 마지막 메시지를 가져오는데
    // me = 나 일땐 you = 너를 가져오고
    // me = 너 일땐 you = 나를 가져와야 해
    // 필요한 정보들은 (You, Last Message, Time, is_read)

    private void chat_room_check() {
        Log.i(TAG, "chat_room_check Method");
        String me = name.getText().toString();
        Log.i(TAG, "String me check : " + me);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);
        Call<List<ChatListModel>> call = serverApi.getChatMessages(me);

        call.enqueue(new Callback<List<ChatListModel>>() {
            @Override
            public void onResponse(Call<List<ChatListModel>> call, Response<List<ChatListModel>> response) {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Request failed : " + response.code());
                    return;

                } else {
                    Log.e(TAG, "Request Success! : " + response.code());
                    List<ChatListModel> responseModels = response.body();
                    Log.i(TAG, "getChatData response.body : " + responseModels);

                    for (ChatListModel chatListModel : responseModels) {
                        String the_other = chatListModel.getThe_other();
                        Log.i(TAG, "getChatData the_other : " + the_other);
                        String message = chatListModel.getMessage();
                        Log.i(TAG, "getChatData message : " + message);
                        String date_time = chatListModel.getDate_time();
                        Log.i(TAG, "getChatData date_time : " + date_time);
                        int is_read = chatListModel.getIs_read();
                        Log.i(TAG, "getChatData is_read : " + is_read);

                        Log.d(TAG, "getChatData The other: " + the_other + ", message: " + message + ", date_time: " + date_time + ", is_read: " + is_read);
                        chatRoomList.add(chatListModel);
                    } // for END
                    chatListAdapter.notifyDataSetChanged();
                } // else END
            } // onResponse END

            @Override
            public void onFailure(Call<List<ChatListModel>> call, Throwable t) {
                Log.e(TAG, "API call failure", t);
            } // onFailure END
        }); // call.enqueue END
    } // chat_room_check Method END


    void initial() {
        // 데이터베이스 인스턴스 생성
        uuidDatabase = Room.databaseBuilder(getApplicationContext(), UUIDDatabase.class, "uuid_db")
                .allowMainThreadQueries()
                .build();


        Log.i(TAG, "initial()");
        intent = getIntent();
        getUsername = intent.getStringExtra("username");
        Log.i(TAG, "getUsername check : " + getUsername);
        myName = getUsername;
        // and To ChatActivity send Intent

        shared = getSharedPreferences("USER", MODE_PRIVATE);
        editor = shared.edit();

        name = findViewById(R.id.userNameMain);
        name.setText(getUsername); // 상단바 내 이름 표시

        // DB에서 내용 가져와야 해
        list_name = findViewById(R.id.list_userName); // 채팅 목록 - 이름
        list_msg = findViewById(R.id.list_lastMsg); // 채팅 목록 - 마지막 메시지 내용
        list_time = findViewById(R.id.list_msgTime);  // 채팅 목록 - 마지막 메시지 시간
//        red_circle = findViewById(R.id.red_circle); // 아직 안 읽은 메시지 존재 시 Visible (= is_read가 1인 게 있으면)

        profile_image = findViewById(R.id.profile_image); // 우선 기본 이미지 설정
//        enterButton = findViewById(R.id.enterButton); // 채팅 목록에 없는 상대 적고 누르는 버튼
//        write_chat_person = findViewById(R.id.writeChatPersonEditText); // 채팅 목록에 없는 상대와 대화하고 싶을 때

        chat_list_recyclerView = findViewById(R.id.chatListRecyclerView);
        layoutManager = new LinearLayoutManager(this);
        chat_list_recyclerView.setLayoutManager(layoutManager);
        chat_list_recyclerView.setHasFixedSize(true);
        chatListAdapter = new ChatListAdapter(this, chatRoomList);
        chat_list_recyclerView.setAdapter(chatListAdapter);

        chatListAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Log.i(TAG, "Recyclerview itemclick position check : " + position);
                // 클릭한 아이템의 데이터 가져온다.
                ChatListModel clickedItem = chatListAdapter.getChatModel(position);
                you = chatRoomList.get(position).getThe_other();

                // uuid 넘겨
                getUUIDFromRoomDB(getUsername);
                String foundValue = null;

                for (String uuidValue : uuidValues) {
                    Log.i(TAG, "uuid - for");
                    if (uuidValue.contains(getUsername) && uuidValue.contains(you)) {
                        Log.i(TAG, "uuid - if");
                        foundValue = uuidValue;
                        break;
                    } // if END
                } // for END

                if (foundValue != null) {
                    Log.i(TAG, "Found value with me and you: " + foundValue);
                } else {
                    Log.i(TAG, "No value found with me and you.");
                } // else END

                Log.i(TAG, "uuid - you check : " + you);

                uuidForChat = foundValue;
                Log.i(TAG, "uuid ForChat ; " + uuidForChat);

                // 전달할 데이터를 인텐트에 추가
                Intent intent = new Intent(ChatSelect.this, ChatActivity.class);
                if (uuidForChat != null) {
                    Log.i(TAG, "if (uuidForChat != null)");
                    Log.i(TAG, "uuid ForChat ; " + uuidForChat);
                    intent.putExtra("uuid", uuidForChat);
                } else {
                    Log.i(TAG, "if (uuidForChat == null)");
                    Log.i(TAG, "uuid ForChat ; " + uuidForChat);
                    String uuidCheck = "none_uuid";
                    intent.putExtra("uuid", uuidCheck);
                } // else END

                Log.i(TAG, "yourname Check : " + you);
                Log.i(TAG, "username Check : " + getUsername);

                intent.putExtra("yourname", you);
                intent.putExtra("username", getUsername);

                editor.putString("room", getRoomName);
                editor.putString("name", getUsername);
                editor.putString("the_other", you);
                editor.commit();

                startActivity(intent);
            } // onItemClick END
        }); // setOnItemClickListener END

//        setEnterButton();
        setDeleteChatRoom();
    } // initial method END

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String message = args[0].toString();
                    // 새로운 메시지 데이터 추가 로직을 실행해야 합니다.
                }
            });
        }
    };

    private void getUUIDContainsYouMe(String mine, String yours) {
        Log.i(TAG, "uuid - getUUIDContainsYouMe Method");
        UUIDDatabase db = Room.databaseBuilder(getApplicationContext(), UUIDDatabase.class, "uuid")
                .addMigrations(MainActivity.MIGRATION_1_2)
                .build();

        UuidDao uuidDao = db.uuidDao();

        new Thread(() -> {
            List<Uuid> allUuids = (List<Uuid>) uuidDao.getAll();
            Log.i(TAG, "All UUIDs in the database: " + allUuids.toString());
        }).start();

        new Thread(() -> {
            Log.i(TAG, "uuid - new Thread");
            Log.i(TAG, "uuid - searching for mine: " + mine + " and yours: " + yours);
            List<Uuid> uuidList = new ArrayList<>();
            List<Uuid> tempList = uuidDao.getUuidByMeYou(mine, yours);

            if (tempList.size() > 0) {
                uuidList.clear();
                uuidList.addAll(tempList);
            }

            Log.i(TAG, "uuid - check : " + uuidList.size());
            if (uuidList != null && !uuidList.isEmpty()) {
                Log.i(TAG, "uuid - !uuidList.isEmpty");
                Log.i(TAG, "uuid - check : " + uuid);
                // 결과 중 첫 번째 uuid 값을 String 변수에 저장합니다.
                Log.i(TAG, "uuid - Entire uuidList: " + uuidList.toString());
                String tempUUID = uuidList.get(0).uuid;
                runOnUiThread(() -> {
                    Log.i(TAG, "uuid - new Thread2");
                    uuidFromDB = tempUUID;
                    Log.i(TAG, "uuid - uuidForChat : " + tempUUID);
                });
                // String 변수를 사용하여 필요한 작업을 수행하세요.
            } else {
                Log.i(TAG, "uuid - uuidList.isEmpty");
                runOnUiThread(() -> {
                    Log.i(TAG, "uuid - new Thread3");
                });
            }
        }).start(); // Thread END
    } // getUUIDContainsYouMe method END

    //    // TODO. UUID 가져오는 방식 변경 (레트로핏으로 서버에서 가져오다가, SQlite에서 가져오는 것으로)
    private void getUUIDFromRoomDB(String me) {
        Log.i(TAG, "uuid - getUUIDFromRoomDB Method");
        UUIDDatabase db = Room.databaseBuilder(getApplicationContext(), UUIDDatabase.class, "uuid")
                .addMigrations(MainActivity.MIGRATION_1_2)
                .build();
        UuidDao uuidDao = db.uuidDao();

        runOnUiThread(() -> {
            db.uuidDao().getAll().observe(ChatSelect.this, uuids -> {
                if (uuids.isEmpty()) {
//                    Toast.makeText(ChatSelect.this, "데이터베이스가 비어 있습니다.", Toast.LENGTH_SHORT).show();
                } else {

                    uuidValues = new ArrayList<>(); // 리스트 초기화

                    StringBuilder sb = new StringBuilder();
                    for (Uuid uuid : uuids) {
                        uuidValues.add(uuid.uuid); // 값 추가
                        sb.append("UUID : ").append(uuid.uuid).append("\n");
                    } // for END
                    Log.i(TAG, "UUID: " + sb.toString());
                } // else END
            }); // observer END
        }); // runOnUiThread END
    }

    private void getUUIDFromTable(String me) {
        Log.i(TAG, "getUUIDFRomToTable Method");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.180.155.66/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ServerApi serverApi = retrofit.create(ServerApi.class);

        Call<ResponseModel> call = serverApi.getUUID(me);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    // 성공적인 응답 처리
//                    Toast.makeText(ChatSelect.this, "Data selected successfully", Toast.LENGTH_SHORT).show();
                    ResponseModel responseModel = response.body();

                    // uuidFromResponse 전역 변수로 생성해서 값 넣어주면
                    // 변수의 값이 너무 늦게 들어가거나 안 들어가 = 통신해서 response 값 받아오는 게
                    // 원하는 생명주기 시점이 아니여서 != 채팅 목록 클릭 시
                    String uuiidFromResponse = responseModel.getUUID();
                    ;
                    if (responseModel != null) {

                        List<String> uuidsFromResponse = responseModel.getUUIDs();
                        if (uuidsFromResponse != null) {
                            // 밑에 코드에서 uuidsFromResponse를 사용하여 처리 수행
                            // 예 : 저장, 출력, 변수 저장 등
                        } else {
                            Log.d(TAG, "uuids : " + "응답 데이터가 null 입니다.");
                        }
                        Log.d(TAG, "uuid 1 : " + uuidsFromResponse);
                        // RoomDB에서 가져와서
                        // 나와 상대의 이름이 포함된 uuid 키 값을 변수에 넣어줘야 함
//                        uuidForChat = uuiidFromResponse;
                        Log.d(TAG, "uuid 2 : " + uuidForChat);
                        // 생성한 uuid
                        if (uuid == null) {
                            Log.d(TAG, "uuid 3 : " + uuidForChat);
                            editor.putString("UUID", uuidForChat);
                            editor.commit();
                        } else {
                            extractingUUID(getUsername, you);
                            Log.d(TAG, "uuid 4 : " + uuid);
                            Log.i(TAG, "uuid check : " + uuid);
                            editor.putString("UUID", uuid);
                            editor.commit();
                        }

                        UUIDDatabase db = Room.databaseBuilder(getApplicationContext(), UUIDDatabase.class, "uuid")
                                .allowMainThreadQueries()
                                .addMigrations(MainActivity.MIGRATION_1_2)
                                .build();
                        UuidDao uuidDao = db.uuidDao();

                        runOnUiThread(() -> {
                            db.uuidDao().getAll().observe(ChatSelect.this, uuids -> {
                                if (uuids.isEmpty()) {
//                                    Toast.makeText(ChatSelect.this, "데이터베이스가 비어 있습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    for (Uuid uuid : uuids) {
                                        sb.append("UUID : ").append(uuid.uuid).append("\n");
                                    } // for END
                                    Log.i(TAG, "UUID: " + sb.toString());
                                } // else END
                            }); // observer END
                        }); // runOnUiThread END

                    } else {
                        Log.d(TAG, "uuid : " + "응답 데이터가 null 입니다.");
                    }
                } else {
                    // 실패한 응답 처리
//                    Toast.makeText(ChatSelect.this, "Failed to select data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                // 에러 처리
                Toast.makeText(ChatSelect.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            } // onFailure END
        }); // call.enqueque END
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        chatRoomList.clear();
        chat_room_check();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    } // onDestroy END

    void setSelectUUIDDatabase() {
        uuidDatabase.uuidDao().getAll().observe(ChatSelect.this, new Observer<List<Uuid>>() {
            @Override
            public void onChanged(List<Uuid> uuids) {
                // DB가 비어있는 경우
                if (uuids.isEmpty()) {
                    Log.i("[UUID DATABASE]", "데이터베이스가 비어있습니다.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    for (Uuid uuid : uuids) {
//                        sb.append("").append(uuid.id).append("_");
                        sb.append("UUID : ").append(uuid.uuid).append("_");
//                        Log.i()
                    } // for END
                    Log.i(TAG, "UUID: " + sb.toString());
                } // else END
            } // onChanged END
        }); // observe END
    } // setSelectUUIDDatabase Method END

    private void extractingUUID(String user1, String user2) {
        // 생성할 고유 값의 개수
        int numValues = 1;

        // 중복을 제거한 고유 값 저장
        HashSet<String> uniqueValues = new HashSet<>();

        // 이름을 덧붙히면 중복될 일이 없지 않을까?
        while (uniqueValues.size() < numValues) {
            // 16비트 랜덤 정수 생성
            int randomInt = new Random().nextInt(65536);
            // 정수를 16진수 문자열로 변환
            String hexString = String.format("%04X", randomInt);

            // 오늘 날짜 가져오기
//            LocalDate today = LocalDate.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//            String dateString = today.format(formatter);

            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            String dateString = sdf.format(date);

            // 최종 고유 값 생성
            String uniqueValue = hexString + dateString + user1 + user2;

            // 생성된 고유 값을 HashSet에 추가 (중복 제거)
            uniqueValues.add(uniqueValue);

        } // while END

        // 생성된 고유 값 출력
        for (String value : uniqueValues) {
            System.out.println("생성된 고유 값 : " + value);
            uuid = value;
//            editor.putString("extractingUUID", uuid);
//            editor.commit();
        } // for END
//        makeUUID = shared.getString("extractingUUID", "");
//        Log.i(TAG, "uuid in extractingUUID : " + getSharedUUID);
//        System.out.println(one);
    }

    void setDeleteChatRoom() {
        chat_list_recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            } // onClick END
        }); // setOnClickListener END

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            } // onMove END

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition();
                Log.i(TAG, "position check : " + position);

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        if (!chatRoomList.isEmpty()) {
                            String getYourname = chatRoomList.get(position).getThe_other(); // 사실 이 부분임...
                            Log.i(TAG, "Delete - me, you check : " + getUsername + ", " + getYourname);

                            chatRoomList.remove(position);
                            chatListAdapter.notifyItemRemoved(position);

                            deleteData(getUsername, getYourname);
                            deleteData(getYourname, getUsername);

                        } else {

                        }
                } // switch END
            } // onSwiped END

            @Override
            public void onChildDraw(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView
                    , @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                                    boolean isCurrentlyActive) {

                Typeface font = Typeface.DEFAULT_BOLD;
                new RecyclerViewSwipeDecorator.Builder(canvas, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.parseColor("#7878E1"))
                        .addSwipeLeftLabel("delete")
                        .setSwipeLeftLabelColor(Color.parseColor("#CDD5FB"))
                        .setSwipeLeftLabelTypeface(font)
                        .create()
                        .decorate();

                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(chat_list_recyclerView);
        ;

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration
                (chat_list_recyclerView.getContext(), layoutManager.getOrientation());
        chat_list_recyclerView.addItemDecoration(dividerItemDecoration);

    } // setDeleteChatRoom Method END

    private void deleteData(String me, String you) {
        Log.i(TAG, "Delete method");
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://54.180.155.66/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ServerApi api = retrofit.create(ServerApi.class);
        Call<ResponseModel> call = api.deleteData(me, you);

        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                ResponseModel responseModel = response.body();
                String response_check = String.valueOf(response.body());

                if (response.isSuccessful()) {
                    // 성공적인 응답 처리
//                    Toast.makeText(ChatSelect.this, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Delete (response success)  and response.body check 1 : " + responseModel);
                    Log.i(TAG, "Delete (response success)  and response.body check 2 : " + response_check);
                } else {
                    // 실패한 응답 처리
//                    Toast.makeText(ChatSelect.this, "Failed to delete data", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Delete (response failed)  and response.body check 1 : " + responseModel);
                    Log.i(TAG, "Delete (response failed)  and response.body check 2 : " + response_check);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                // 에러 처리
                Toast.makeText(ChatSelect.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


} // Select CLASS END