package com.example.playlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Gif_Play {

    static final String API_KEY = "vcXNF0Hr1MLK42lMUrDBiMgSJolmnvEO";
    static final String SEARCH_URL = "https://api.giphy.com/v1/gifs/search?q=sea&api_key=" + API_KEY;
    String TAG = "[Gif Class]";
    androidx.constraintlayout.widget.ConstraintLayout mainLayout;
    ImageView mainFull;


    public void playing() {
        mainLayout = ((MainActivity) MainActivity.mainCtx).mainLayout;
        mainFull = ((MainActivity) MainActivity.mainCtx).mainFull;

        new SearchGIF().execute(SEARCH_URL);
    }

    private class SearchGIF extends AsyncTask<String, Void, String> {
        // AsyncTask 클래스를 상속받은 클래스 정의 *AsyncTask 좀 더 공부
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                // GET 요청 보내고 InputStream 읽어들인다.
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                // Scanner를 사용하여 InputStream에서 문자열을 읽어들인다.
                Scanner scanner = new Scanner(inputStream, "UTF-8");
                String response = scanner.useDelimiter("\\A").next();

                // 닫고 닫고 문자열 반환
                scanner.close();
                inputStream.close();
                connection.disconnect();

                return response;
            } catch (Exception e) {
                Log.e(TAG, "Error while searching for GIF", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                // 검색 결과가 없을 경우 반환
                return;
            }
            try {
                JSONObject json = new JSONObject(response);
                JSONArray data = json.getJSONArray("data");

                if (data.length() == 0) {
                    return;
                }
                JSONObject gifObject = data.getJSONObject(0);
                JSONObject images = gifObject.getJSONObject("images");
                JSONObject original = images.getJSONObject("original");
                // GIF의 URL 추출
                String gifUrl = original.getString("url");

                // GIF 파일을 다운 받을 것
                new DownloadGIF().execute(gifUrl);
            } catch (Exception e) {
                Log.e(TAG, "Error while parsing JSON response", e);
            }
        }
    }


    private class DownloadGIF extends AsyncTask<String, Void, byte[]> {
        @Override
        protected byte[] doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                byte[] gifData = outputStream.toByteArray();
                outputStream.close();
                inputStream.close();
                connection.disconnect();

                return gifData;
            } catch (Exception e) {
                Log.e(TAG, "Error while downloading GIF", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(byte[] gifData) {
            if (gifData == null) {
                return;
            }

            Bitmap gifBitmap = BitmapFactory.decodeByteArray(gifData, 0, gifData.length);

            if (gifBitmap == null) {
                return;
            }

            mainFull.setImageBitmap(gifBitmap);
            mainFull.setScaleType(ImageView.ScaleType.FIT_XY);
//            mainFull.setAdjustViewBounds(true);
            mainFull.setAlpha(0.5f);
            mainFull.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mainFull.setImageBitmap(gifBitmap);

//                    mainLayout.addView(mainFull);
                }
            }, 100);


        }
    }
}
