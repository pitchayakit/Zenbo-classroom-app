package com.zenbo.zenbo_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PresentationAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = PresentationAlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Log.d(TAG,"onReceive alarmReceiver");

        Intent i = new Intent();
        i.setClassName("com.zenbo.zenbo_alarm", "com.zenbo.zenbo_alarm.PresentationActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        //Toast.makeText(context, "Alarm, Alarm, Alarm!", Toast.LENGTH_LONG).show(); // For example
    }
}