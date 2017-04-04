package edu.fsu.cs.mobile.ontime;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static edu.fsu.cs.mobile.ontime.LoginActivity.PREFS_NAME;

public class NotificationService extends IntentService {
    NotificationManager nm;
    int seconds;
    Timer t = new Timer();
    final int maxSeconds = 300;

    private DatabaseReference mDatabase;
    ArrayList<StartEnd> foodList = new ArrayList<StartEnd>();
    StartEnd food;
    int i;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onHandleIntent(Intent intent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Lunch Time!")
                .setContentText("Its time to eat fam")
                .setAutoCancel(true);

        final int NOTIF_ID = 1;
        Intent notifIntent = new Intent(this, NearbyFriends.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);

        builder.setContentIntent(contentIntent);
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nm.notify(NOTIF_ID, builder.build());

        t.schedule(new TimerTask() {
            @Override
            public void run() {
                // Your database code here
            }
        }, maxSeconds*1000);

        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
