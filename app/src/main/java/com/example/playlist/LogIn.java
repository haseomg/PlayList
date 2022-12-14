package com.example.playlist;

//import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogIn extends Activity {

    static Context ctx;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageView google_Btn;

    Button back;
    Button submit;
    Button signUp;

    EditText idEdit, pwEdit;

    String idStr, pwStr;

//    private RetrofitClient retrofitClient;
//    private pinni_api pinni_api;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    public final String TAG = "[LogIn Activity]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_log_in);

        Log.i(TAG, "onCreate()");


        ctx = this;


//        google_Btn = findViewById(R.id.google_Btn);
//        gso = new GoogleSignInOptions
//                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail().build();
//        gsc = GoogleSignIn.getClient(this, gso);
//
//        google_Btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signIn();
//            }
//        });


        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        idEdit = findViewById(R.id.idEditText);
        pwEdit = findViewById(R.id.passwordEditText);

        signUp = findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, SignUp.class);

                startActivity(intent);
            }
        });

        back = findViewById(R.id.logInBackButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit = findViewById(R.id.logInSubmitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
                    // EditText??? ????????????
                    if (idEdit.getText().toString().trim().length() > 0 ||
                            pwEdit.getText().toString().trim().length() > 0) {

                        // get ?????? ???????????? ??????
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://43.201.105.106/logIn.php").newBuilder();
                        urlBuilder.addQueryParameter("v", "1.0"); // ??????
                        String url = urlBuilder.build().toString();

                        // POST ???????????? ??????
                        RequestBody formBody = new FormBody.Builder()
                                .add("id", idEdit.getText().toString().trim())
                                .add("pw", pwEdit.getText().toString().trim())
                                .build();

                        // ??????
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(url)
                                .post(formBody)
                                .build();

                        // ?????? ??????
                        client.newCall(request).enqueue(new Callback() {

                            @Override
                            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                Log.e("[LogIn]", "" + e);
                            }

                            @Override
                            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                                // ?????? ????????? Ui ?????? ??? ?????? ??????
                                // ??????????????? Ui ??????
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            // ?????????????????? ???????????? ??????
//                                             findViewById(R.id.cpb).setVisibility(View.GONE);
                                            if (!response.isSuccessful()) {
                                                // ?????? ??????
                                                Log.i("tag", "?????? ??????");
                                                Toast.makeText(getApplicationContext(), "???????????? ?????? ??????", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // ?????? ??????
                                                Log.i("tag", "?????? ??????");
                                                final String responseData = response.body().string().trim();
                                                Log.i("tag", responseData);
                                                if (responseData.equals("1")) {
                                                    Log.i("[Main]", "responseData ??? 1??? ??? : " + responseData);
                                                    Toast.makeText(getApplicationContext(), "????????? ??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.i("[Main]", "responseData ??? 1??? ?????? ??? : " + responseData);
                                                    startActivityString(MainActivity.class, "nickname", responseData);
                                                    if (!responseData.equals(0)) {
                                                        editor.putString("id", responseData);
                                                        ((MainActivity) MainActivity.mainCtx).logIn.setText(responseData);
                                                        editor.commit();
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
                    }


                    idStr = idEdit.getText().toString();
                    pwStr = pwEdit.getText().toString();
                    hideKeyboard();

                    if (!idStr.equals("") || !pwStr.equals("")) {

                        editor.putString("id", idStr);
                        editor.putString("pw", pwStr);
                        editor.commit();

                        finish();

                        ((MainActivity) MainActivity.mainCtx).logIn.setText(idStr + "'S");

                        Toast.makeText(getApplicationContext(), idStr + "??? ???????????????!",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), "????????? ??? ???????????? ????????? ???????????????.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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


    // ???????????? ?????? ??????
    // ????????? ???????????? ????????????
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // ???????????? ??????????????? ?????????
        overridePendingTransition(0, 0);
    }

    // ????????? ????????? ?????? ??????
    public void startActivityString(Class c, String name, String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // ???????????? ??????????????? ?????????
        overridePendingTransition(0, 0);
    }


    //????????? ?????????
    private void hideKeyboard() {

        Log.i("[LogIn]", "hideKeyboard ????????? ??????");


        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(idEdit.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pwEdit.getWindowToken(), 0);
    }

    //?????? ?????? ??? ????????? ?????????
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        Log.i("[LogIn]", "dispatchTouchEvent ????????? ??????");


        View focusView = getCurrentFocus();
        if (focusView != null) {

            Log.i("[LogIn]", "focusView != null??? ???");

            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {

                Log.i("[LogIn]", "!rect.contains(x, y)??? ???");

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)

                    Log.i("[LogIn]", "imm != null??? ???");


                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            navigateToSecondActivity();
            try {
                task.getResult(ApiException.class);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void navigateToSecondActivity() {
        finish();
        Intent intent = new Intent(LogIn.this, MainActivity.class);
        startActivity(intent);
    }


//    // ?????? ????????? ??????
//    public void checkAutoLogin(String id) {
//
//        //Toast.makeText(this, id + "??? ???????????????.", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
//
//    }

    // ?????? ?????? ?????? 2??? ????????? ??????
//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//        backPressCloseHandler.onBackPressed();
//    }
}

