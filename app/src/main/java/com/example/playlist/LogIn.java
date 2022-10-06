package com.example.playlist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LogIn extends Activity {

    Button back;
    Button submit;

    EditText idEdit, pwEdit;

    String idStr, pwStr;

    SharedPreferences shared;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_log_in);

        shared = getSharedPreferences("signUp", MODE_PRIVATE);
        editor = shared.edit();

        idEdit = findViewById(R.id.idEditText);
        pwEdit = findViewById(R.id.passwordEditText);

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

                idStr = idEdit.getText().toString();
                pwStr = pwEdit.getText().toString();

                editor.putString("id", idStr);
                editor.putString("pw", pwStr);
                editor.commit();

                finish();
            }
        });
    }
}