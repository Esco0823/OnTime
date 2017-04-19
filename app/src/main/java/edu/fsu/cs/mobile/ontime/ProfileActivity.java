package edu.fsu.cs.mobile.ontime;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;

public class ProfileActivity extends Activity {


    private DatabaseReference mDatabase1;
    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabase3;
    private DatabaseReference mDatabase4;
    private ArrayList<StartEnd> foodSchedule = new ArrayList<StartEnd>();
    private ArrayList<Classes> classSchedule = new ArrayList<Classes>();
    private ArrayList<Study> studySessionSchedule = new ArrayList<Study>();
    private ArrayList<String> friendList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("Profile");

        ((TextView) findViewById(R.id.profileName)).setText(getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase1 = FirebaseDatabase.getInstance().getReference("food/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String listOfFoodBreaks = "";
                for(DataSnapshot next2 : snapshot.getChildren())
                {
                    StartEnd myStartEnd =  next2.getValue(StartEnd.class);
                    foodSchedule.add(myStartEnd);
                    listOfFoodBreaks = listOfFoodBreaks + intToDay(myStartEnd.getDays()) + " " + militaryToNormal(myStartEnd.getStart()) + " - " + militaryToNormal(myStartEnd.getEnd()) + "\n";
                }

                ((TextView) findViewById(R.id.eatingTimeListing)).setText(listOfFoodBreaks);
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        mDatabase2 = FirebaseDatabase.getInstance().getReference("schedule/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String listOfClasses = "";
                for(DataSnapshot next : snapshot.getChildren())
                {
                    Classes myClasses =  next.getValue(Classes.class);
                    classSchedule.add(myClasses);
                    listOfClasses = listOfClasses + myClasses.getCourse() + " " + intToDay(myClasses.getDays()) + " " + militaryToNormal(myClasses.getStartTime()) + " - " + militaryToNormal(myClasses.getEndTime()) + " " + myClasses.getBuilding() + "" + myClasses.getRoom() + "\n";
                }

                ((TextView) findViewById(R.id.scheduleListing)).setText(listOfClasses);
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        mDatabase3 = FirebaseDatabase.getInstance().getReference("study/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String listOfStudySessions = "";
                for(DataSnapshot next : snapshot.getChildren())
                {
                    Study myStudySessions =  next.getValue(Study.class);
                    studySessionSchedule.add(myStudySessions);
                    listOfStudySessions = listOfStudySessions + myStudySessions.getCourse() + " " + myStudySessions.getDate() + " " + militaryToNormal(myStudySessions.getStart()) + " - " + militaryToNormal(myStudySessions.getEnd()) + "\n";
                }

                ((TextView) findViewById(R.id.studyListing)).setText(listOfStudySessions);
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        mDatabase4 = FirebaseDatabase.getInstance().getReference("friends/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String listOfFriends = "";
                for(DataSnapshot next : snapshot.getChildren())
                {
                    String myFriend = next.getValue(String.class);
                    friendList.add(myFriend);
                    listOfFriends = listOfFriends + myFriend + "\n";
                }

                ((TextView) findViewById(R.id.friendsListing)).setText(listOfFriends);
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public String militaryToNormal(String military)
    {
        String normal = "";
        String ammpm = "";
        int colonLocation = military.indexOf(":");
        int hour = Integer.parseInt(military.substring(0, colonLocation));
        int minutes = Integer.parseInt(military.substring(colonLocation + 1, military.length()));

        if(hour < 12)
        {
            if(hour == 0)
            {
                hour = 12;
            }
            ammpm = "AM";
        }
        else
        {
            if(hour > 12)
            {
                hour = hour - 12;
            }
            ammpm = "PM";
        }


        normal = hour + ":";

        if(minutes < 10)
        {
            normal = normal + "0";
        }

        normal = normal + minutes + " " + ammpm;

        return normal;
    }


    public String intToDay(String daysString)
    {
        String solution = "";

        if(daysString.contains("0"))
        {
            solution = solution + "SU";
        }
        if(daysString.contains("1"))
        {
            solution = solution + "M";
        }
        if(daysString.contains("2"))
        {
            solution = solution + "T";
        }
        if(daysString.contains("3"))
        {
            solution = solution + "W";
        }
        if(daysString.contains("4"))
        {
            solution = solution + "TR";
        }
        if(daysString.contains("5"))
        {
            solution = solution + "F";
        }
        if(daysString.contains("6"))
        {
            solution = solution + "SA";
        }

        return solution;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch(item.getItemId()) {
            case R.id.mainActivityOption2:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.FriendRequests2:
                intent = new Intent(this, FriendInvites.class);
                startActivity(intent);
                break;
            case R.id.editScheduleOption2:
                intent = new Intent(this, EditScheduleActivity.class);
                startActivity(intent);
                break;
            case R.id.editFriendsOption2:
                intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.settingsOption2:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.signOutOption2:
                intent = new Intent(this, LoginActivity.class);

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", "");
                editor.putString("password", "");

                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
                editor.putInt("notificationNumber", 0);
                editor.apply();

                stopService(new Intent(this, NotificationService.class));
                stopService(new Intent(this, NotificationReceivedService.class));

                startActivity(intent);
                break;
        }
        return true;
    }
}
