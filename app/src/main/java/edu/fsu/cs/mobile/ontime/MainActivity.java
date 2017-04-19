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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;
import static edu.fsu.cs.mobile.ontime.UserNotification.CLASS;
import static edu.fsu.cs.mobile.ontime.UserNotification.FOOD;
import static edu.fsu.cs.mobile.ontime.UserNotification.STUDY;


public class MainActivity extends Activity
{
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase0;
    private DatabaseReference mDatabase1;
    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabase3;
    private ArrayList<StartEnd> foodSchedule = new ArrayList<StartEnd>();
    private ArrayList<Classes> classSchedule = new ArrayList<Classes>();
    private ArrayList<Study> studySessionSchedule = new ArrayList<Study>();
    private ArrayList<UserNotification> putBack1 = new ArrayList<UserNotification>();
    private ArrayList<UserNotification> putBack2 = new ArrayList<UserNotification>();


    @Override
    public void onBackPressed()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Home");

        Intent myIntent = new Intent(this, NotificationReceivedService.class);
        startService(myIntent);


        mDatabase1 = FirebaseDatabase.getInstance().getReference("food/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot next : snapshot.getChildren()) {
                    StartEnd myStartEnd = next.getValue(StartEnd.class);
                    foodSchedule.add(myStartEnd);
                }

                Calendar calendar = Calendar.getInstance();

                double time = calendar.get(Calendar.HOUR_OF_DAY) + (calendar.get(Calendar.MINUTE) / 100.0);

                double min = 2000;
                String minString = "";
                int minIndex = -1;
                for (int i = 0; i < foodSchedule.size(); i++) {
                    double current = timeToDouble(foodSchedule.get(i).getStart());
                    if ((current - time) < min && (current - time) >= 0 && foodSchedule.get(i).getDays().contains(String.valueOf(dayToInt(calendar.get(Calendar.DAY_OF_WEEK))))) {
                        min = current - time;
                        minString = militaryToNormal(foodSchedule.get(i).getStart()) + " - " + militaryToNormal(foodSchedule.get(i).getEnd());
                        minIndex = i;


                    }
                }

                if(minIndex >= 0) {
                    int t = ToCalendar(foodSchedule.get(minIndex).getStart());
                    t = MinusXMinutes(t, getSharedPreferences(PREFS_NAME, 0).getString("alertDistance", ""));

                    Intent activate = new Intent(getApplicationContext(), TimeReceiver.class);
                    activate.putExtra("notificationType1", FOOD);
                    activate.putExtra("minFoodTime", t);
                    sendBroadcast(activate);

                    ((TextView) findViewById(R.id.upcomingFoodBreak)).setText(minString);
                }
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
                for(DataSnapshot next : snapshot.getChildren())
                {
                    Classes myClasses =  next.getValue(Classes.class);
                    classSchedule.add(myClasses);
                }

                Calendar calendar = Calendar.getInstance();

                double time = calendar.get(Calendar.HOUR_OF_DAY) + (calendar.get(Calendar.MINUTE)/100.0);

                double min = 2000;
                String minString = "";
                int minIndex = -1;
                for(int i = 0; i < classSchedule.size(); i++)
                {
                    double current = timeToDouble(classSchedule.get(i).getStartTime());
                    if((current - time) < min && (current - time) >= 0 && classSchedule.get(i).getDays().contains(String.valueOf(dayToInt(calendar.get(Calendar.DAY_OF_WEEK)))))
                    {
                        min = current - time;
                        minString = classSchedule.get(i).getCourse() + " " + militaryToNormal(classSchedule.get(i).getStartTime()) + " - " + militaryToNormal(classSchedule.get(i).getEndTime()) + " " + classSchedule.get(i).getBuilding() + " " + classSchedule.get(i).getRoom();
                        minIndex = i;
                    }
                }
                if(minIndex >= 0)
                {
                    int t = ToCalendar(classSchedule.get(minIndex).getStartTime());
                    t = MinusXMinutes(t, getSharedPreferences(PREFS_NAME, 0).getString("alertDistance", ""));

                    Intent activate = new Intent(getApplicationContext(), TimeReceiver.class);
                    activate.putExtra("notificationType2", CLASS);
                    activate.putExtra("minClassTime", t);
                    activate.putExtra("building", classSchedule.get(minIndex).getBuilding());
                    sendBroadcast(activate);


                    ((TextView) findViewById(R.id.upcomingClass)).setText(minString);

                }

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
                for(DataSnapshot next : snapshot.getChildren())
                {
                    Study myStudySessions =  next.getValue(Study.class);
                    studySessionSchedule.add(myStudySessions);
                }

                Calendar calendar = Calendar.getInstance();

                double time = calendar.get(Calendar.HOUR_OF_DAY) + (calendar.get(Calendar.MINUTE)/100.0);

                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
                Calendar calendar2 = Calendar.getInstance();


                double min = 2000;
                String minString = "";
                int minIndex = -1;
                for(int i = 0; i < studySessionSchedule.size(); i++)
                {
                    try
                    {
                        date = dateFormat.parse(studySessionSchedule.get(i).getDate());
                    }
                    catch(ParseException parseException)
                    {
                        parseException.printStackTrace();
                    }

                    calendar2.setTime(date);

                    Boolean matchingDay = calendar2.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH) && calendar2.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && calendar2.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
                    double current = timeToDouble(studySessionSchedule.get(i).getStart());
                    if((current - time) < min && (current - time) >= 0 && matchingDay)
                    {
                        min = current - time;
                        minString = militaryToNormal(studySessionSchedule.get(i).getStart()) + " - " + militaryToNormal(studySessionSchedule.get(i).getEnd()) + " " + studySessionSchedule.get(i).getCourse();
                        minIndex = i;
                    }
                }
                if(minIndex >= 0)
                {
                    int t = ToCalendar(studySessionSchedule.get(minIndex).getStart());
                    t = MinusXMinutes(t, getSharedPreferences(PREFS_NAME, 0).getString("alertDistance", ""));

                    Intent activate = new Intent(getApplicationContext(), TimeReceiver.class);
                    activate.putExtra("notificationType3", STUDY);
                    activate.putExtra("minStudyTime", t);
                    activate.putExtra("course", studySessionSchedule.get(minIndex).getCourse());
                    sendBroadcast(activate);

                    ((TextView) findViewById(R.id.upcomingStudySession)).setText(minString);

                }

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

    public double timeToDouble(String time)
    {
        int colonLocation = time.indexOf(":");
        double hour = Integer.parseInt(time.substring(0, colonLocation));
        double minutes = Integer.parseInt(time.substring(colonLocation + 1, time.length()));

        return hour + (minutes/100.0);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch(item.getItemId()) {
            case R.id.profileOption:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.FriendRequests:
                intent = new Intent(this, FriendInvites.class);
                startActivity(intent);
                break;
            case R.id.editScheduleOption:
                intent = new Intent(this, EditScheduleActivity.class);
                startActivity(intent);
                break;
            case R.id.editFriendsOption:
                intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.settingsOption:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.signOutOption:
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

    private int ToCalendar(String time){

        int t = 0;
        int colonLocation = time.indexOf(":");
        t = (Integer.parseInt(time.substring(0, colonLocation)) * 100) + (Integer.parseInt(time.substring(colonLocation + 1, time.length())));
        return t;
    }

    private int MinusXMinutes(int t, String m){
        if (m.equals("")){
            m = "5";
        }
        int min = t%100;
        int hr = t/100;
        if(min >= Integer.parseInt(m)){
            min = min - Integer.parseInt(m);
        }
        else{
            min = 60 + min - Integer.parseInt(m);
            hr = hr - 1;
        }

        return (hr*100 + min);
    }
}

