package com.thedronelearners.tdl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailid;
    private EditText username;
    private EditText mobnumber;
    private EditText password;
    private EditText cpassword;
    private Button sButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        emailid = findViewById(R.id.edit_email);
        username = findViewById(R.id.edit_name);
        mobnumber = findViewById(R.id.edit_mob);
        password = findViewById(R.id.edit_pass);
        cpassword = findViewById(R.id.edit_pass_c);
        sButton = findViewById(R.id.submit_button);
        sButton.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        sButton.setEnabled(false);
        String useremail = emailid.getText().toString();
        String uname = username.getText().toString();
        String umobilenum = mobnumber.getText().toString();
        String upassword = password.getText().toString();
        String ucpassword = cpassword.getText().toString();
        Boolean allOkay = true;

        if(useremail.isEmpty()){
            emailid.setBackgroundColor(Color.RED);
            allOkay = false;
        }

        if(uname.isEmpty()){
            username.setBackgroundColor(Color.RED);
            allOkay = false;
        }

        if(umobilenum.isEmpty()){
            mobnumber.setBackgroundColor(Color.RED);
            allOkay = false;
        }
        if(upassword.isEmpty()|| !upassword.equals(ucpassword)){
            password.setText("");
            cpassword.setText("");
            password.setBackgroundColor(Color.RED);
            cpassword.setBackgroundColor(Color.RED);
            allOkay = false;
        }
        if(allOkay){
            mAuth.createUserWithEmailAndPassword(useremail, ucpassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                            }
                            else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Unable to create account" , Toast.LENGTH_LONG);
                                toast.show();
                                sButton.setEnabled(true);
                            }
                        }
                    });
        }
    }
}
