package com.zenbo.zenbo_alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = MyReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive - Intent Action: " + intent.getAction());
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            new timeHttpGet(context).execute();
        }
    }

    public static class timeHttpGet extends AsyncTask<String, Void, String > {
        private WeakReference<Context> myReceiverWeakReference;
        timeHttpGet(Context context) {
            myReceiverWeakReference = new WeakReference<>(context);
        }
        @Override
        protected String doInBackground(String... parameters) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://classroom.pitchayakit.com/alarm/alarm_time.php")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Log.d(TAG, "timeHttpGet result : " + response.toString());
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "timeHttpGet error : " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Context context = myReceiverWeakReference.get();
            Log.d(TAG, "onPostExecute result : " + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
//                Log.d(TAG,"onPostExecute jsonObject : " + jsonObject);
//                Log.d(TAG,"onPostExecute jsonObject : " + jsonObject.get("hour_of_day"));
                int hour_of_day = Integer.parseInt(jsonObject.get("hour_of_day").toString());
                int minute = Integer.parseInt(jsonObject.get("minute").toString());
                Log.d(TAG,"onPostExecute jsonObject : " + hour_of_day);
                Log.d(TAG,"onPostExecute jsonObject : " + minute);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Intent beforePresentationIntent = new Intent(context, PresentationAlarmReceiver.class);
                PendingIntent beforePresentationPendingIntent = PendingIntent.getBroadcast(context, 0, beforePresentationIntent, 0);
                Intent beforeGroupDiscussion = new Intent(context, GroupDiscussionAlarmReceiver.class);
                PendingIntent beforeGroupDiscussionPendingIntent = PendingIntent.getBroadcast(context, 0, beforeGroupDiscussion, 0);
                Intent beforeSharing = new Intent(context, SharingAlarmReceiver.class);
                PendingIntent beforeSharingPendingIntent = PendingIntent.getBroadcast(context, 0, beforeSharing, 0);

                //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 5, pendingIntent);

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());

                Calendar beforePresentationAlarm = Calendar.getInstance();
                beforePresentationAlarm.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), hour_of_day, minute, 0);
                Calendar beforeGroupDiscussionAlarm = Calendar.getInstance();
                beforeGroupDiscussionAlarm.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), hour_of_day+1, minute, 0);
                Calendar beforeSharingAlarm = Calendar.getInstance();
                beforeSharingAlarm.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), hour_of_day+2, minute, 0);

                if(System.currentTimeMillis() < beforePresentationAlarm.getTimeInMillis())
                    alarmManager.set(AlarmManager.RTC_WAKEUP, beforePresentationAlarm.getTimeInMillis(), beforePresentationPendingIntent);
                else
                    alarmManager.cancel(beforePresentationPendingIntent);
                if(System.currentTimeMillis() < beforeGroupDiscussionAlarm.getTimeInMillis())
                    alarmManager.set(AlarmManager.RTC_WAKEUP, beforeGroupDiscussionAlarm.getTimeInMillis(), beforeGroupDiscussionPendingIntent);
                else
                    alarmManager.cancel(beforeGroupDiscussionPendingIntent);
                if(System.currentTimeMillis() < beforeSharingAlarm.getTimeInMillis())
                    alarmManager.set(AlarmManager.RTC_WAKEUP, beforeSharingAlarm.getTimeInMillis(), beforeSharingPendingIntent);
                else
                    alarmManager.cancel(beforeSharingPendingIntent);
            }catch (JSONException err){
                Log.d("onPostExecute Error", err.toString());
            }
        }
    }
}
