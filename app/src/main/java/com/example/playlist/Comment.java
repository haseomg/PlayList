package com.example.playlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Comment extends Activity {

    Button back, submit;
    EditText writeComment;
    TextView songName, selectedTime;
    String getUserName, getSongName, getSelectedTime, msg;
    ServerApi serverApi;

    // final = 변하지 않는 상수 - 다른 사람이 이 코드를 봐도 작성한 사람이 변하지 않게 설정한 것으로 알 수 있음
    public final String TAG = "[Comment Activity]";

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_comment);
        Log.i(TAG, "onCreate()");

        textViewInitial();


        serverApi = ApiClient.getApiClient().create(ServerApi.class);
        Intent intent = getIntent();
        getUserName = intent.getStringExtra("user_name");
        Log.i(TAG, "getIntent getUserName : " + getUserName);
        getSongName = intent.getStringExtra("song_name");
        Log.i(TAG, "getIntent getSongName : " + getSongName);
        getSelectedTime = intent.getStringExtra("selected_time");
        Log.i(TAG, "getIntent getSelectedTime : " + getSelectedTime);

        songName.setText(getSongName + " - ");
        selectedTime.setText(getSelectedTime);

        back = findViewById(R.id.commentBackButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        writeComment = findViewById(R.id.commentEditText);
        submit = findViewById(R.id.commentSubmitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // user_name, song_name, selected_time, msg to DB
                msg = writeComment.getText().toString();
                Log.i(TAG, "submit - getIntent getUserName : " + getUserName);
                Log.i(TAG, "submit - getIntent getSongName : " + getSongName);
                Log.i(TAG, "submit - getIntent getSelectedTime : " + getSelectedTime);
                Log.i(TAG, "submit - writeComment check : " + msg);

                // DB upload
                comment_insert(getUserName, getSongName, getSelectedTime, msg);
//                Intent commentIntent = new Intent();
//                commentIntent.putExtra("user_name", getUserName);
//                commentIntent.putExtra("song_name", getSongName);
//                commentIntent.putExtra("selected_time", getSelectedTime);
//                commentIntent.putExtra("msg", msg);
                finish();
            }
        });
    }

    void textViewInitial() {
        songName = findViewById(R.id.commentSongNameTextView);
        selectedTime = findViewById(R.id.commentSelectedTimeTextView);
    } // textViewInitial END

    public void comment_insert(String user, String song, String time, String msg) {
        Log.i(TAG, "comment_insert");
        Call<Void> call = serverApi.loadComment(user, song, time, msg);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "comment_insert Method onResponse()");

                if (response.isSuccessful()) {
                    Log.i(TAG, "comment_insert Method onResponse() isSuccessful");
                    Log.i(TAG, "Insert (response success)  and response.body check : " + response.body());

                } else {
                    Log.i(TAG, "comment_insert Method onResponse() !!!!!isSuccessful");
                    Log.i(TAG, "Insert (response not successful)  and response.body check : " + response.body());
                } // else
            } // onResponse

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i(TAG, "onFailure : " + t.getMessage());
            } // onFailure
        }); // call.enqueue
    } // comment_insert

    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }
} // CLASS