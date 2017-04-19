package edu.fsu.cs.mobile.ontime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class TimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent myIntent = new Intent(context, NotificationService.class);

        Calendar calFood = Calendar.getInstance();
        calFood.set(Calendar.HOUR_OF_DAY, (intent.getIntExtra("minFoodTime", 0) / 100));
        calFood.set(Calendar.MINUTE, (intent.getIntExtra("minFoodTime", 0) % 100));
        calFood.set(Calendar.SECOND, 00);

        Calendar calClass = Calendar.getInstance();
        calClass.set(Calendar.HOUR_OF_DAY, (intent.getIntExtra("minClassTime", 0) / 100));
        calClass.set(Calendar.MINUTE, (intent.getIntExtra("minClassTime", 0) % 100));
        calClass.set(Calendar.SECOND, 00);

        Calendar calStudy = Calendar.getInstance();
        calStudy.set(Calendar.HOUR_OF_DAY, (intent.getIntExtra("minStudyTime", 0) / 100));
        calStudy.set(Calendar.MINUTE, (intent.getIntExtra("minStudyTime", 0) % 100));
        calStudy.set(Calendar.SECOND, 00);

        if ((System.currentTimeMillis() < calFood.getTimeInMillis() + 1000*60*Integer.parseInt(context.getSharedPreferences("userStuff", 0).getString("alertDistance", ""))) && (System.currentTimeMillis() > calFood.getTimeInMillis()))
        {
            myIntent.putExtra("notificationType", intent.getStringExtra("notificationType1"));
            myIntent.putExtra("minTime", intent.getIntExtra("minFoodTime", 0));
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startService(myIntent);
        }
        else if ((System.currentTimeMillis() < calClass.getTimeInMillis() + 1000*60*Integer.parseInt(context.getSharedPreferences("userStuff", 0).getString("alertDistance", ""))) && (System.currentTimeMillis() > calClass.getTimeInMillis()))
        {
            myIntent.putExtra("notificationType", intent.getStringExtra("notificationType2"));
            myIntent.putExtra("minTime", intent.getIntExtra("minClassTime", 0));
            myIntent.putExtra("building", intent.getStringExtra("building"));
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startService(myIntent);
        }
        else if ((System.currentTimeMillis() < calStudy.getTimeInMillis() + 1000*60*Integer.parseInt(context.getSharedPreferences("userStuff", 0).getString("alertDistance", "")))  && (System.currentTimeMillis() > calStudy.getTimeInMillis()))
        {
            myIntent.putExtra("notificationType", intent.getStringExtra("notificationType3"));
            myIntent.putExtra("minTime", intent.getIntExtra("minStudyTime", 0));
            myIntent.putExtra("course", intent.getStringExtra("course"));
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startService(myIntent);
        }

    }
}
