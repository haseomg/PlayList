package com.example.playlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class Intro extends AppCompatActivity {

    Button start;

    String nickNameFromShared;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    int num;

    public final String TAG = "[Intro Activity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Log.i(TAG, "onCreate()");


        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        Log.i("[Main Activity]", "acct : " + acct);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
        }

        shared = getSharedPreferences("nickname", MODE_PRIVATE);
        editor = shared.edit();

        nickNameFromShared = shared.getString("nickname", "LOG IN");

//        responseRandomNumbers();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (acct != null || !nickNameFromShared.equals("LOG IN")) {
                    Log.i("[Intro]", "nickNameFromShared가 default값이 아닐 때");

                    Intent intent = new Intent(Intro.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else if (nickNameFromShared.equals("LOG IN")) {
                    Log.i("[Intro]", "nickNameFromShared가 default값일 때");

                    Intent intent = new Intent(Intro.this, SignUp.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }

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

//    public void responseRandomNumbers() {
//        Log.i(TAG, "bringGetSongInfo Method");
//
//        OkHttpClient client = new OkHttpClient();
//
//        HttpUrl.Builder builder = HttpUrl.parse("http://54.180.155.66/create_random_numbers.php").newBuilder();
//        builder.addQueryParameter("ver", "1.0");
//        String url = builder.build().toString();
//        Log.i(TAG, "bringGet String url check : " + url);
//
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    final String result = response.body().string();
//                    Log.i("bringGet", "response check : " + result);
//                    num = Integer.parseInt(result);
//                    Log.i("bringGet", "num check : " + num);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // nothing
//                        }
//                    });
//                }
//            }
//        });
//
//        class GetNumber extends AsyncTask<String, Void, Integer> {
//
//            @Override
//            protected Integer doInBackground(String... urls) {
//                Integer number = null;
//                try {
//                    URL url = new URL(urls[0]);
//                    HttpURLConnection urlConnection
//                            = (HttpURLConnection) url.openConnection();
//                    try {
//                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                        number = Integer.parseInt(reader.readLine());
//                    } finally {
//                        urlConnection.disconnect();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return number;
//            }
//
//            protected void onPostExecute(Integer result) {
//                int number = result;
//                Log.i(TAG, "bringGet number check : " + number);
//            }
//        }
//    }


}