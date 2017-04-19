package edu.fsu.cs.mobile.ontime;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;
import static edu.fsu.cs.mobile.ontime.UserNotification.FOOD;
import static edu.fsu.cs.mobile.ontime.UserNotification.STUDY;
import static edu.fsu.cs.mobile.ontime.UserNotification.CLASS;

public class NotificationService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    NotificationManager nm;
    int seconds;
    Timer t = new Timer();
    int maxSeconds;
    private GoogleApiClient googleApiClient;
    private Location Lastlocation;
    Users newUser;

    private DatabaseReference mDatabase;
    ArrayList<StartEnd> foodList = new ArrayList<StartEnd>();
    StartEnd food;
    int i;


    public static final int NOTIF_ID_FOOD = 1;
    public static final int NOTIF_ID_CLASS = 2;
    public static final int NOTIF_ID_STUDY = 3;
    public static final int NOTIF_ID_FOOD_INVITE = 4;
    public static final int NOTIF_ID_CLASS_INVITE = 5;
    public static final int NOTIF_ID_STUDY_INVITE = 6;
    public static final int NOTIF_ID_FOOD_INVITE_RESPONSE = 7;
    public static final int NOTIF_ID_CLASS_INVITE_RESPONSE = 8;
    public static final int NOTIF_ID_STUDY_INVITE_RESPONSE = 9;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onHandleIntent(Intent intent){
        maxSeconds = (Integer.parseInt(getSharedPreferences(PREFS_NAME, 0).getString("alertDistance", "5")) * 60);

        if(googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        googleApiClient.connect();

        if(intent.getStringExtra("notificationType").equals(FOOD))
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_v2)
                    .setContentTitle("Lunch Time!")
                    .setContentText("Its time to eat fam")
                    .setAutoCancel(false)
                    .setOngoing(true);

            Intent notifIntent = new Intent(this, NearbyFriends.class);
            notifIntent.putExtra("notificationType", intent.getStringExtra("notificationType"));
            notifIntent.putExtra("minTime", intent.getIntExtra("minTime", 0));

            PendingIntent contentIntent = PendingIntent.getActivity(this, 1, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setContentIntent(contentIntent);
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);



            nm.notify(NOTIF_ID_FOOD, builder.build());

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("notificationNumber", (getSharedPreferences(PREFS_NAME, 0).getInt("notificationNumber", 0) + 1) % Integer.MAX_VALUE);
            editor.apply();

            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIF_ID_FOOD);
                }
            }, maxSeconds * 1000);
        }
        else if(intent.getStringExtra("notificationType").equals(CLASS))
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_v2)
                    .setContentTitle("Class Time!")
                    .setContentText("Its time to go to class fam")
                    .setAutoCancel(false)
                    .setOngoing(true);

            Intent notifIntent = new Intent(this, NearbyFriends.class);
            notifIntent.putExtra("notificationType", intent.getStringExtra("notificationType"));
            notifIntent.putExtra("minTime", intent.getIntExtra("minTime", 0));
            notifIntent.putExtra("building", intent.getStringExtra("building"));

            PendingIntent contentIntent = PendingIntent.getActivity(this, 1, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setContentIntent(contentIntent);
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            nm.notify(NOTIF_ID_CLASS, builder.build());

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("notificationNumber", (getSharedPreferences(PREFS_NAME, 0).getInt("notificationNumber", 0) + 1) % Integer.MAX_VALUE);
            editor.apply();

            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIF_ID_CLASS);
                }
            }, maxSeconds * 1000);
        }
        else if(intent.getStringExtra("notificationType").equals(STUDY))
        {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_v2)
                    .setContentTitle("Study Time!")
                    .setContentText("Its time to study fam")
                    .setAutoCancel(false)
                    .setOngoing(true);

            Intent notifIntent = new Intent(this, NearbyFriends.class);
            notifIntent.putExtra("notificationType", intent.getStringExtra("notificationType"));
            notifIntent.putExtra("minTime", intent.getIntExtra("minTime", 0));
            notifIntent.putExtra("course", intent.getStringExtra("course"));

            PendingIntent contentIntent = PendingIntent.getActivity(this, 1, notifIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            builder.setContentIntent(contentIntent);
            nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            nm.notify(NOTIF_ID_STUDY, builder.build());

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("notificationNumber", (getSharedPreferences(PREFS_NAME, 0).getInt("notificationNumber", 0) + 1) % Integer.MAX_VALUE);
            editor.apply();

            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIF_ID_STUDY);
                }
            }, maxSeconds * 1000);
        }

        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onConnected(Bundle bundle) throws SecurityException {
        Lastlocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        double lat = 0;
        double lon = 0;
        if(Lastlocation != null){
            lat = Lastlocation.getLatitude();
            lon = Lastlocation.getLongitude();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users/" + getSharedPreferences(PREFS_NAME, 0).getString("username",""));

        newUser = new Users(getSharedPreferences(PREFS_NAME, 0).getString("alertDistance",""), lat + "", lon + "", getSharedPreferences(PREFS_NAME, 0).getString("password",""), getSharedPreferences(PREFS_NAME, 0).getString("phone",""), getSharedPreferences(PREFS_NAME, 0).getString("visibility",""));

        mDatabase.setValue(newUser);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}