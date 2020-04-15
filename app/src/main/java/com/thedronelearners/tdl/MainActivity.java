package com.thedronelearners.tdl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {

    private Button getbutton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private final int RC_SIGNIN =  990;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);
        final Intent startMapActivityIntent = new Intent(this, MapsActivity.class);

        getbutton  = findViewById(R.id.get_button);
        getbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(startMapActivityIntent);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mUser = mAuth.getCurrentUser();
        updateUI(mUser);
    }

    protected void updateUI(FirebaseUser user){
        if(user == null){
            List<AuthUI.IdpConfig> providers = Arrays.asList( new AuthUI.IdpConfig.PhoneBuilder().build());
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(), RC_SIGNIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGNIN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                mUser = mAuth.getCurrentUser();
                if(response.isNewUser()){
                    startActivity(new Intent(this , GetUserDetails.class));
                }
            } else {
                finish();
            }
        }
    }

}
