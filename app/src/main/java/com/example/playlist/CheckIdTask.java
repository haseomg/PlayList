package com.example.playlist;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckIdTask extends AsyncTask<String, Void, String> {

    private final OkHttpClient client = new OkHttpClient();

    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(json,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String id = params[1];
        String json = "{\"id\": \"" + id + "\"}";
        try {
            String responseStr = post(url, json); // 서버에 HTTP 요청을 보냄
            JSONObject responseJson = new JSONObject(responseStr); // 응답을 JSON 객체로 파싱
            String status = responseJson.getString("status"); // 응답에서 status 필드를 가져옴
            Log.i("CheckIdTask", "status Check : " + status);
            return status;
        } catch (IOException e) {
            return "error";
        } catch (Exception e) {
            return "invalid_response";
        }
    } // doInBackGround End
}
