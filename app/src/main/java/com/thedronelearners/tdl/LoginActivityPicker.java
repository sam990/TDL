package com.thedronelearners.tdl;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivityPicker extends AppCompatActivity implements View.OnClickListener {

    private Button signin;
    private Button signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_picker);
        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        signin.setOnClickListener(this);
        signup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == signup){

        }
        else if(v == signin){

        }
    }
}
