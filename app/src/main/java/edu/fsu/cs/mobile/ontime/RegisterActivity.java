package edu.fsu.cs.mobile.ontime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

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

        ((CheckBox)findViewById(R.id.registerPhoneDisableCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {
                    ((EditText) findViewById(R.id.registerPhone)).setVisibility(View.GONE);
                }
                else
                {
                    ((EditText) findViewById(R.id.registerPhone)).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void SignUpClicked(View v) {

        if (((CheckBox) findViewById(R.id.registerPhoneDisableCheckBox)).isChecked()) {
            ((EditText) findViewById(R.id.registerPhone)).setText("");
        }

        final String usernameTyped = ((EditText) findViewById(R.id.registerUsername)).getText().toString();
        final String passwordTyped = ((EditText) findViewById(R.id.registerPassword)).getText().toString();
        final String confirmedPasswordTyped = ((EditText) findViewById(R.id.registerConfirmPassword)).getText().toString();
        final String phoneTyped = ((EditText) findViewById(R.id.registerPhone)).getText().toString();

        if (!passwordTyped.equals(confirmedPasswordTyped))
        {
            Toast.makeText(getApplicationContext(), "Passwords Don't Match", Toast.LENGTH_LONG).show();
        }
        else
        {
            if (!usernameTyped.equals("") && !passwordTyped.equals("") && !confirmedPasswordTyped.equals("") && (!phoneTyped.equals("") || ((CheckBox) findViewById(R.id.registerPhoneDisableCheckBox)).isChecked())) {

                mDatabase = FirebaseDatabase.getInstance().getReference("users");

                mDatabase.addValueEventListener(new ValueEventListener() {
                    boolean match = false;

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot next : snapshot.getChildren()) {


                            user = next.getValue(Users.class);

                            if (next.getKey().equals(usernameTyped)) {
                                match = true;
                            }

                        }


                        if (!match) {
                            Users newUser = new Users("5", "0.0", "0.0", passwordTyped, phoneTyped, "yes");
                            mDatabase.child(usernameTyped).setValue(newUser);
                            Intent intent = new Intent(getApplicationContext(), EditScheduleActivity.class);

                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("alertDistance", "5");
                            editor.putString("username", usernameTyped);
                            editor.putString("password", passwordTyped);
                            editor.putString("phone", phoneTyped);
                            editor.putString("visibility", "yes");
                            editor.putInt("notificationNumber", 0);
                            editor.apply();

                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Username already exsists", Toast.LENGTH_LONG).show();
                        }

                    }


                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
            }
            else
            {
                Toast.makeText(getApplicationContext(), "All Fields Were Not Filled In", Toast.LENGTH_LONG).show();
            }
        }
    }
}
