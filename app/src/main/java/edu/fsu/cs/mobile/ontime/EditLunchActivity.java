package edu.fsu.cs.mobile.ontime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;

public class EditLunchActivity extends Activity {
    Spinner shours, sminutes, sampm, ehours, eminutes, eampm;
    CheckBox mon, tues, wed, thurs,fri;
    int i;
    private DatabaseReference mDatabase;

    @Override
    public void onBackPressed()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_lunch);

        setTitle("Edit Food Breaks");

        shours = (Spinner) findViewById(R.id.sHoursSpinner);
        sminutes = (Spinner) findViewById(R.id.sMinutesSpinner);
        sampm = (Spinner) findViewById(R.id.sAmpmSpinner);
        ehours = (Spinner) findViewById(R.id.eHoursSpinner);
        eminutes = (Spinner) findViewById(R.id.eMinutesSpinner);
        eampm = (Spinner) findViewById(R.id.eAMPMSpinner);

        mon = (CheckBox) findViewById(R.id.mCB);
        tues = (CheckBox) findViewById(R.id.tCB);
        wed = (CheckBox) findViewById(R.id.wCB);
        thurs = (CheckBox) findViewById(R.id.thCB);
        fri = (CheckBox) findViewById(R.id.fCB);
    }

    public void SkipFoodTime(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void AddFoodTime(View v){
        String days = "";
        if(mon.isChecked()) days = "1";
        if(tues.isChecked()) days = days + "2";
        if(wed.isChecked()) days = days + "3";
        if(thurs.isChecked()) days = days + "4";
        if(fri.isChecked()) days = days + "5";

        String start = "";
        int sh = Integer.parseInt(shours.getSelectedItem().toString().trim());
        if(sampm.getSelectedItem().toString().trim() == "PM"){
            sh = sh + 12;
        }
        start = start + sh + ":" + sminutes.getSelectedItem().toString().trim();

        String end = "";
        int eh = Integer.parseInt(ehours.getSelectedItem().toString().trim());
        if(eampm.getSelectedItem().toString().trim() == "PM"){
            eh = eh + 12;
        }
        end = end + eh + ":" + eminutes.getSelectedItem().toString().trim();

        StartEnd newLunchTime = new StartEnd(days, start, end);

        mDatabase = FirebaseDatabase.getInstance().getReference("food/" + getSharedPreferences(PREFS_NAME, 0).getString("username", ""));

        i = 0;

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot next : dataSnapshot.getChildren()){
                    i++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child(i+"").setValue(newLunchTime);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}