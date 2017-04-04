package edu.fsu.cs.mobile.ontime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends Activity {
    public static final String PREFS_NAME = "userStuff";

    @Override
    public void onBackPressed()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if(getSharedPreferences(PREFS_NAME,0).getString("username", "") != "" && getSharedPreferences(PREFS_NAME,0).getString("password", "") != "")
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

    }

    public void RegisterClicked(View v){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void SignInClicked(View v){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}
