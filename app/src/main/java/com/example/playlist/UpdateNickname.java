package com.example.playlist;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateNickname {

    String myId;

    static UpdateNickname ctx;

    public void selectUserTB(String nickname) {
        ctx = UpdateNickname.this;

        Log.i("updateNickname Class", "selectUserTB Method");

        HttpUrl.Builder builder = HttpUrl.parse("http://54.180.152.109/user_info.php").newBuilder();
        builder.addQueryParameter("ver", "1.0");
        String url = builder.build().toString();
        Log.i("updateNickname", "String url check : " + url);

        // nickname <-> nickname.getText.toString

        RequestBody formBody = new FormBody.Builder()
                .add("nickname", nickname)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.i("Update", "응답 실패 : " + response);
                } else {
                    Log.i("Update", "응답 성공 : " + response);
                    final String responseData = response.body().string();
                    if (responseData.equals("1")) {
                        Log.i("Update", "User Table match == 0");
                    } else {
                        Log.i("Update", "User Table match ok : " + responseData);
                        myId = responseData;
                    }

                }
            }
        });

    }                           

    public void updateUserTB(String id, String nickname) {

        id = ctx.myId;

        Log.i("updateNickname Class", "updateUserTB Method");

        HttpUrl.Builder builder = HttpUrl.parse("http://54.180.152.109/update_user.php").newBuilder();
        builder.addQueryParameter("ver", "1.0");
        String url = builder.build().toString();
        Log.i("updateNickname", "String url check : " + url);

        RequestBody formBody = new FormBody.Builder()
                .add("nickname", nickname)
                .add("id", id)
                .build();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.i("Update", "응답 실패 : " + response);
                } else {
                    Log.i("Update", "응답 성공 : " + response);
                    final String responseData = response.body().string();
                    if (responseData.equals("1")) {
                        Log.i("Update", "User Table match == 0");
                    } else {
                        Log.i("Update", "User Table match ok : " + responseData);
                    }

                }
            }
        });
    }
}
