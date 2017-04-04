package edu.fsu.cs.mobile.ontime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;

public class SignInActivity extends Activity {

    private DatabaseReference mDatabase;
    private  Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        setTitle("Sign In");
    }

    public void LoginClicked(View v){


        final String usernameTyped = ((EditText) findViewById(R.id.signInUsername)).getText().toString();
        final String passwordTyped = ((EditText) findViewById(R.id.signInPassword)).getText().toString();

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot next : snapshot.getChildren()) {


                    user = next.getValue(Users.class);

                    if(next.getKey().equals(usernameTyped) && user.getPassword().equals(passwordTyped))
                    {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("username",usernameTyped);
                        editor.putString("password", passwordTyped);
                        editor.commit();

                        startActivity(intent);
                    }

                }
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }
}
