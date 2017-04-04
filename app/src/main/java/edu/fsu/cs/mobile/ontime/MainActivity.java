package edu.fsu.cs.mobile.ontime;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
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


public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase1;
    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabase3;
    private ArrayList<StartEnd> foodSchedule = new ArrayList<StartEnd>();
    private ArrayList<Classes> classSchedule = new ArrayList<Classes>();
    private ArrayList<Study> studySessionSchedule = new ArrayList<Study>();
    private GoogleApiClient googleApiClient;
    private Location Lastlocation;

    Users newUser;


    @Override
    public void onBackPressed()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Home");

        if(googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();

        mDatabase1 = FirebaseDatabase.getInstance().getReference("food/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot next : snapshot.getChildren())
                        {
                            StartEnd myStartEnd =  next.getValue(StartEnd.class);
                            foodSchedule.add(myStartEnd);
                        }

                        //THE MINIMUM STUFF IS BROKEN!!!!!!!!!!
                        Calendar calendar = Calendar.getInstance();

                        //SimpleDateFormat dateSetup = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                        //String date = dateSetup.format(calendar.getTime());

                        double time = calendar.get(Calendar.HOUR_OF_DAY) + (calendar.get(Calendar.MINUTE)/100.0);

                        double min = 2000;
                        String minString = "";
                        for(int i = 0; i < foodSchedule.size(); i++)
                        {
                            double current = timeToDouble(foodSchedule.get(i).getStart());
                            if((current - time) < min && (current - time) >= 0 && foodSchedule.get(i).getDays().contains(String.valueOf(dayToInt(calendar.get(Calendar.DAY_OF_WEEK)))))
                            {
                                min = current - time;
                                minString = foodSchedule.get(i).getStart() + "-" + foodSchedule.get(i).getEnd();

                                int t = ToCalendar(foodSchedule.get(i).getStart());
                                t = Minus5mins(t);

                                //NOTIFICATION STUFF FOR LUNCH TIME
                                Calendar cal = Calendar.getInstance();
                                Intent activate = new Intent(getApplicationContext(), TimeReceiver.class);
                                AlarmManager alarms;
                                PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, activate, 0);
                                alarms = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                cal.set(Calendar.HOUR_OF_DAY, (t/100));
                                cal.set(Calendar.MINUTE, (t%100));
                                cal.set(Calendar.SECOND, 00);

                                String s = "" + t;

                                if(cal.getTimeInMillis() == System.currentTimeMillis()){
                                    startService(activate);
                                }
                                ////////////////////////////////////////
                                alarms.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
                                ((TextView) findViewById(R.id.upcomingFoodBreak)).setText(minString);
                            }
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

                //SimpleDateFormat dateSetup = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                //String date = dateSetup.format(calendar.getTime());

                double time = calendar.get(Calendar.HOUR_OF_DAY) + (calendar.get(Calendar.MINUTE)/100.0);

                double min = 2000;
                String minString = "";
                for(int i = 0; i < classSchedule.size(); i++)
                {
                    double current = timeToDouble(classSchedule.get(i).getStartTime());
                    if((current - time) < min && (current - time) >= 0 && classSchedule.get(i).getDays().contains(String.valueOf(dayToInt(calendar.get(Calendar.DAY_OF_WEEK)))))
                    {
                        min = current - time;
                        minString = classSchedule.get(i).getCourse() + " " + classSchedule.get(i).getStartTime() + "-" + classSchedule.get(i).getEndTime() + " " + classSchedule.get(i).getBuilding() + " " + classSchedule.get(i).getRoom();
                    }
                }

                ((TextView) findViewById(R.id.upcomingClass)).setText(minString);
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

                //SimpleDateFormat dateSetup = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                //String date = dateSetup.format(calendar.getTime());

                double time = calendar.get(Calendar.HOUR_OF_DAY) + (calendar.get(Calendar.MINUTE)/100.0);

                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
                Calendar calendar2 = Calendar.getInstance();


                double min = 2000;
                String minString = "";
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
                        minString = studySessionSchedule.get(i).getStart() + "-" + studySessionSchedule.get(i).getEnd() + " " + studySessionSchedule.get(i).getCourse();
                    }
                }

      //          ((TextView) findViewById(R.id.upcomingStudySession)).setText(minString);
            }


            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

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
            case R.id.editScheduleOption:
                intent = new Intent(this, EditScheduleActivity.class);
                startActivity(intent);
                break;
            case R.id.editFriendsOption:
                intent = new Intent(this, EditFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.signOutOption:
                intent = new Intent(this, LoginActivity.class);

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("username", "");
                editor.putString("password", "");
                editor.commit();

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

    private int Minus5mins(int t){
        int min = t%100;
        int hr = t/100;
        if(min >= 5){
            min = min - 5;
        }
        else{
            min = 60 + min - 5;
            hr = hr - 1;
        }

        return (hr*100 + min);
    }

    @Override
    public void onConnected(Bundle bundle) throws SecurityException {
        Lastlocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        double lat = 0;
        double lon = 0;
        if(Lastlocation != null){
            lat = Lastlocation.getLatitude();
            lon = Lastlocation.getLongitude();
            String lata = Double.toString(lat);
            String longi = Double.toString(lon);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        newUser = new Users(lat + "", lon + "", getSharedPreferences(PREFS_NAME, 0).getString("password",""), "yes");

        mDatabase.setValue(newUser);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

