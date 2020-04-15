package com.thedronelearners.tdl;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class GetUserDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_details);
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameEdit = findViewById(R.id.name_edit);
                String userName = nameEdit.getText().toString();
                if(userName.isEmpty()){
                    Toast toast = Toast.makeText(getApplicationContext(), "Enter valid name", Toast.LENGTH_SHORT);
                    toast.show();
                    nameEdit.setBackgroundColor(Color.RED);
                    return;
                }
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser mUser = mAuth.getCurrentUser();
                Task<Void> task = mUser.updateProfile(new UserProfileChangeRequest
                        .Builder()
                        .setDisplayName(userName)
                        .build());
                task.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Profile Created Successfully", Toast.LENGTH_SHORT);
                        toast.show();
                        finish();
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Can't create profile, Try Again", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
        });
    }
}
