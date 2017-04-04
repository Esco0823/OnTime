package edu.fsu.cs.mobile.ontime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class TimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent myIntent = new Intent(context, NotificationService.class);

        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(myIntent);
    }
}
