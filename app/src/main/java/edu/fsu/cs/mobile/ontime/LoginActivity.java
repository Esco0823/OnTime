package edu.fsu.cs.mobile.ontime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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


        if(!getSharedPreferences(PREFS_NAME,0).getString("username", "").equals("") && !getSharedPreferences(PREFS_NAME,0).getString("password", "").equals(""))
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
