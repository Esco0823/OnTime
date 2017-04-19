package edu.fsu.cs.mobile.ontime;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;
import static edu.fsu.cs.mobile.ontime.UserNotification.CLASS;
import static edu.fsu.cs.mobile.ontime.UserNotification.CLASS_INVITE;
import static edu.fsu.cs.mobile.ontime.UserNotification.FOOD;
import static edu.fsu.cs.mobile.ontime.UserNotification.FOOD_INVITE;
import static edu.fsu.cs.mobile.ontime.UserNotification.STUDY;
import static edu.fsu.cs.mobile.ontime.UserNotification.STUDY_INVITE;

public class NearbyFriends extends ListActivity {

    ArrayList<String> listItems = new ArrayList<>();
    ArrayList<String> newListItems = new ArrayList<>();
    ArrayList<String> foodLunchFriends = new ArrayList<>();
    ArrayList<UserDistance> nearbyFriends = new ArrayList<>();
    ArrayList<UserNotification> friendNotifications = new ArrayList<>();

    ArrayAdapter<String> listAdapter;
    CheckBox filter;

    int positionToSend = -1;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabase3;
    private DatabaseReference mDatabase4;

    private String minTime;
    boolean found;

    Users me;

    String notificationType;


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naerbyfreinds);

        filter = (CheckBox) findViewById(R.id.filterCB);

        minTime = intToTimePlusSP(getIntent().getIntExtra("minTime", 0));

        setTitle("Nearby Friends");

        try {
            int locationOff = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);
            if(locationOff == 0)
            {
                final AlertDialog.Builder gpsAlert = new AlertDialog.Builder(this);
                gpsAlert.setTitle("GPS is Off");
                gpsAlert.setMessage("Please Enable Location");
                gpsAlert.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface myInterface, int id)
                    {
                        Intent turnOnGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(turnOnGPS);
                    }
                });
                gpsAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface myDialog, int id)
                    {
                        myDialog.cancel();
                    }
                });
                gpsAlert.show();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newListItems);
        setListAdapter(listAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("friends/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                listItems.clear();

                for(DataSnapshot next : snapshot.getChildren())
                {
                    String myFriend = next.getValue(String.class);
                    listItems.add(myFriend);
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        mDatabase1 = FirebaseDatabase.getInstance().getReference("users");

        mDatabase1.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                me = snapshot.child(getSharedPreferences(PREFS_NAME, 0).getString("username","")).getValue(Users.class);
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        mDatabase2 = FirebaseDatabase.getInstance().getReference("users/");

        mDatabase2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                nearbyFriends.clear();
                for (DataSnapshot next : snapshot.getChildren()) {
                    for (int i = 0; i < listItems.size(); i++) {
                        if (next.getKey().equals(listItems.get(i))) {

                            Location myLocation = new Location("");
                            Location theyLocation = new Location("");


                            double lat = Double.parseDouble(me.getLatitude());
                            double lon = Double.parseDouble(me.getLongitude());


                            myLocation.setLatitude(lat);
                            myLocation.setLongitude(lon);

                            double theyLat = Double.parseDouble(next.getValue(Users.class).getLatitude());
                            double theyLon = Double.parseDouble(next.getValue(Users.class).getLongitude());

                            theyLocation.setLatitude(theyLat);
                            theyLocation.setLongitude(theyLon);

                            nearbyFriends.add(new UserDistance(next.getKey(), chop(myLocation.distanceTo(theyLocation) / (1609.344)), next.getValue(Users.class).getVisible(), next.getValue(Users.class).getPhone()));
                        }
                    }
                }

                Collections.sort(nearbyFriends, new Comparator<UserDistance>() {
                    @Override
                    public int compare(UserDistance distance1, UserDistance distance2) {
                        if(distance1.getVisible().equals("no"))
                        {
                            return -1;
                        }
                        else if(distance2.getVisible().equals("no"))
                        {
                            return 1;
                        }
                        else if(Double.parseDouble(distance1.getDistance()) < Double.parseDouble(distance2.getDistance()))
                        {
                            return -1;
                        }
                        else
                        {
                            return 1;
                        }
                    }
                });

                newListItems.clear();
                for(int j = 0; j < nearbyFriends.size(); j++)
                {
                    if (nearbyFriends.get(j).getVisible().equals("yes") && getSharedPreferences(PREFS_NAME, 0).getString("visibility", "").equals("yes")) {
                        newListItems.add(nearbyFriends.get(j).getUsername() + " (" + nearbyFriends.get(j).getDistance() + " miles away)");
                    } else {
                        newListItems.add(nearbyFriends.get(j).getUsername());
                    }
                }

                listAdapter.notifyDataSetChanged();

                filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b)
                        {
                            found = false;
                            for(int j = 0; j < newListItems.size(); j++)
                            {
                                if(getIntent().getStringExtra("notificationType").equals(FOOD))
                                {
                                    mDatabase4 = FirebaseDatabase.getInstance().getReference("food");
                                    mDatabase4.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for(DataSnapshot next : snapshot.getChildren())
                                            {
                                                found = false;

                                                for(DataSnapshot next2 : next.getChildren())
                                                {
                                                     if(minTime.trim().equals(next2.getValue(StartEnd.class).getStart().trim()) && next2.getValue(StartEnd.class).getDays().contains(TodayAsString()))
                                                    {
                                                        found = true;
                                                    }
                                                }

                                                if(found)
                                                {
                                                }
                                                else
                                                {
                                                    for(int p = 0; p < newListItems.size(); p++)
                                                    {
                                                        if(firstWord(newListItems.get(p)).equals(next.getKey()))
                                                        {
                                                            newListItems.remove(p);
                                                            listAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError firebaseError) {
                                            System.out.println("The read failed: " + firebaseError.getMessage());
                                        }
                                    });
                                }
                                else if(getIntent().getStringExtra("notificationType").equals(CLASS))
                                {
                                    mDatabase4 = FirebaseDatabase.getInstance().getReference("schedule");
                                    mDatabase4.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for(DataSnapshot next : snapshot.getChildren())
                                            {
                                                found = false;

                                                for(DataSnapshot next2 : next.getChildren())
                                                {
                                                    if(minTime.trim().equals(next2.getValue(Classes.class).getStartTime().trim()) && next2.getValue(Classes.class).getDays().contains(TodayAsString()) && next2.getValue(Classes.class).getBuilding().equals(getIntent().getStringExtra("building"))) {
                                                        found = true;
                                                    }
                                                }

                                                if(found)
                                                {
                                                }
                                                else
                                                {
                                                    for(int p = 0; p < newListItems.size(); p++)
                                                    {
                                                        if(firstWord(newListItems.get(p)).equals(next.getKey()))
                                                        {
                                                            newListItems.remove(p);
                                                            listAdapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError firebaseError) {
                                            System.out.println("The read failed: " + firebaseError.getMessage());
                                        }
                                    });
                                }
                                else if(getIntent().getStringExtra("notificationType").equals(STUDY))
                                {
                                    mDatabase4 = FirebaseDatabase.getInstance().getReference("schedule");
                                    mDatabase4.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for(DataSnapshot next : snapshot.getChildren())
                                            {
                                                found = false;

                                                for(DataSnapshot next2 : next.getChildren())
                                                {
                                                    if(next2.getValue(Classes.class).getCourse().equals(getIntent().getStringExtra("course"))) {
                                                        found = true;
                                                    }
                                                }

                                                if(found)
                                                {
                                                }
                                                else
                                                {
                                                    for(int p = 0; p < newListItems.size(); p++)
                                                    {
                                                        if(firstWord(newListItems.get(p)).equals(next.getKey()))
                                                        {
                                                            newListItems.remove(p);
                                                            listAdapter.notifyDataSetChanged();
                                                        }
                                                    }
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

                            listAdapter.notifyDataSetChanged();

                        }
                        else
                        {
                            newListItems.clear();
                            for(int j = 0; j < nearbyFriends.size(); j++)
                            {
                                if (nearbyFriends.get(j).getVisible().equals("yes") && getSharedPreferences(PREFS_NAME, 0).getString("visibility", "").equals("yes")) {
                                    newListItems.add(nearbyFriends.get(j).getUsername() + " (" + nearbyFriends.get(j).getDistance() + " miles away)");
                                } else {
                                    newListItems.add(nearbyFriends.get(j).getUsername());
                                }
                            }

                            listAdapter.notifyDataSetChanged();
                        }
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        registerForContextMenu(getListView());
    }


    public String firstWord(String listItem)
    {
        String parse = "";
        for(int i = 0; i < listItem.length() && listItem.charAt(i) != ' '; i++)
        {
            parse = parse + listItem.charAt(i);
        }
        return parse;
    }

    public String intToTimePlusSP(int time) {
        int hr = time / 100;
        int min = time %100;
        int sp = Integer.parseInt(getSharedPreferences(PREFS_NAME, 0).getString("alertDistance", "5"));

        min = min + sp;

        if(min > 59){
            hr++;
            min = min - 60;
        }

        if(min < 10)
            return (hr + ":0" + min);
        else
            return (hr + ":" + min);
    }

    public String TodayAsString(){
        return dayToInt(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) + "";
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, view, menuInfo);
        AdapterView.AdapterContextMenuInfo info =  (AdapterView.AdapterContextMenuInfo) menuInfo;
        positionToSend = info.position;
        menu.setHeaderTitle("Contact " + nearbyFriends.get(positionToSend).getUsername());

        if(!nearbyFriends.get(positionToSend).getPhone().equals(""))
        {
            menu.add("Through phone");
        }
        menu.add("Through notification");
    }

    public boolean onContextItemSelected(MenuItem item)
    {
        super.onContextItemSelected(item);

        if(item.getTitle().equals("Through phone"))
        {
            SmsManager smsManager = SmsManager.getDefault();
            if(getIntent().getStringExtra("notificationType").equals(FOOD))
            {
                smsManager.sendTextMessage(nearbyFriends.get(positionToSend).getPhone(), null, "Let's eat, lmk\n\n- " + getSharedPreferences(PREFS_NAME, 0).getString("username", "") + "\n(Sent From On Time)", null, null);
            }
            else if(getIntent().getStringExtra("notificationType").equals(CLASS))
            {
                smsManager.sendTextMessage(nearbyFriends.get(positionToSend).getPhone(), null, "Let's walk to class, lmk\n\n- " + getSharedPreferences(PREFS_NAME, 0).getString("username", "") + "\n(Sent From On Time)", null, null);

            }
            else if(getIntent().getStringExtra("notificationType").equals(STUDY))
            {
                smsManager.sendTextMessage(nearbyFriends.get(positionToSend).getPhone(), null, "Let's study, lmk\n\n- " + getSharedPreferences(PREFS_NAME, 0).getString("username", "") + "\n(Sent From On Time)", null, null);
            }
            Toast.makeText(getApplicationContext(), "Text Sent", Toast.LENGTH_LONG).show();
        }
        else if(item.getTitle().equals("Through notification"))
        {
            mDatabase3 = FirebaseDatabase.getInstance().getReference("notifications/" + nearbyFriends.get(positionToSend).getUsername());

            mDatabase3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    friendNotifications.clear();

                    if(getIntent().getStringExtra("notificationType").equals(FOOD))
                    {
                        notificationType = FOOD_INVITE;
                    }
                    else if(getIntent().getStringExtra("notificationType").equals(CLASS))
                    {
                        notificationType = CLASS_INVITE;
                    }
                    else if(getIntent().getStringExtra("notificationType").equals(STUDY))
                    {
                        notificationType = STUDY_INVITE;
                    }

                    for(DataSnapshot next : snapshot.getChildren())
                    {
                        friendNotifications.add(next.getValue(UserNotification.class));
                    }

                    friendNotifications.add(new UserNotification(notificationType, getSharedPreferences(PREFS_NAME, 0).getString("username", "")));
                    mDatabase3.setValue(friendNotifications);

                }
                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });

            Toast.makeText(getApplicationContext(), "Notification Sent", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    public String chop(double location){
        String myString = location + "00";
        String chopped = "";
        boolean decimalFound = false;

        int i;
        for (i = 0; i < myString.length() && !decimalFound; i++)
        {
            if(myString.charAt(i) == '.')
            {
                decimalFound = true;
            }
            chopped = chopped + myString.charAt(i);
        }

        chopped = chopped + myString.charAt(i);
        chopped = chopped + myString.charAt(i + 1);


        return chopped;
    }

    public int dayToInt(int day)
    {
        switch(day)
        {
            case Calendar.SUNDAY:
                return 0;
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
        }

        return -1;
    }
}
