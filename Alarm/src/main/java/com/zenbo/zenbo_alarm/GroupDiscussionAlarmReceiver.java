package com.zenbo.zenbo_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class GroupDiscussionAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        Log.d(TAG,"onReceive alarmReceiver");

        Intent i = new Intent();
        i.setClassName("com.zenbo.zenbo_alarm", "com.zenbo.zenbo_alarm.GroupDiscussionActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        //Toast.makeText(context, "Alarm, Alarm, Alarm!", Toast.LENGTH_LONG).show(); // For example
    }
}
