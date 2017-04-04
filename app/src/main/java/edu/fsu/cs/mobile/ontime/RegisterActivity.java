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

public class RegisterActivity extends Activity {

    private DatabaseReference mDatabase;
    private  Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setTitle("Register");
    }

    public void SignUpClicked(View v){

        final String usernameTyped = ((EditText) findViewById(R.id.registerUsername)).getText().toString();
        final String passwordTyped = ((EditText) findViewById(R.id.registerPassword)).getText().toString();
        final String confirmedPasswordTyped = ((EditText) findViewById(R.id.registerConfirmPassword)).getText().toString();

        if(passwordTyped.equals(confirmedPasswordTyped) && !usernameTyped.equals("") && !passwordTyped.equals("") && !confirmedPasswordTyped.equals(""))
        {

            mDatabase = FirebaseDatabase.getInstance().getReference("users");

            mDatabase.addValueEventListener(new ValueEventListener() {
                boolean match = false;
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot next : snapshot.getChildren()) {


                        user = next.getValue(Users.class);

                        if(next.getKey().equals(usernameTyped))
                        {
                            match = true;
                        }

                    }

                    if(!match)
                    {
                        Users newUser = new Users("0.0", "0.0", passwordTyped, "yes");
                        mDatabase.child(usernameTyped).setValue(newUser);
                        Intent intent = new Intent(getApplicationContext(), EditScheduleActivity.class);

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("username",usernameTyped);
                        editor.putString("password", passwordTyped);
                        editor.commit();

                        startActivity(intent);
                    }

                }


                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        }
    }
}
