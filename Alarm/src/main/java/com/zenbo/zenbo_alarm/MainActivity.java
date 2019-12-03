package com.zenbo.zenbo_alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    AlarmManager alarmManager;
    PendingIntent beforePresentationPendingIntent;
    PendingIntent beforeGroupDiscussionPendingIntent;
    PendingIntent beforeSharingPendingIntent;
    public int beforePresentationHour, beforePresentationMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent beforePresentationIntent = new Intent(this, PresentationActivity.class);
        beforePresentationPendingIntent = PendingIntent.getActivity(this, 0, beforePresentationIntent, 0);

        Intent beforeGroupDiscussion = new Intent(this, GroupDiscussionActivity.class);
        beforeGroupDiscussionPendingIntent = PendingIntent.getActivity(this, 0, beforeGroupDiscussion, 0);

        Intent beforeSharing = new Intent(this, SharingActivity.class);
        beforeSharingPendingIntent = PendingIntent.getActivity(this, 0, beforeSharing, 0);

        Button cancelAlarmButton = findViewById(R.id.cancelAlarmButton);
        cancelAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
                Toast.makeText(MainActivity.this, "Cancel ", Toast.LENGTH_SHORT).show(); // For example
            }
        });

        Button presentationTimeBt = findViewById(R.id.presentation_time_bt);
        final TextView presentationTimeText = findViewById(R.id.presentation_time_text);
        presentationTimeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        Log.d(TAG,"alarm time"+ timePicker);
                        presentationTimeText.setText(hourOfDay + ":" + minutes);
                        beforePresentationHour = hourOfDay;
                        beforePresentationMinute = minutes;
                        setAlarm();
                        new timeHttpPost().execute(hourOfDay,minutes);
                    }
                }, 0, 0, false);
                timePickerDialog.show();
            }
        });
    }

    private void setAlarm() {
        Log.d(TAG, "Time"+System.currentTimeMillis());

        cancelAlarm();

        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10, pendingIntent);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        String currentTime = cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE);
        String alarmTime = beforePresentationHour+":"+beforePresentationMinute;
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date date1 = format.parse(currentTime);
            Date date2 = format.parse(alarmTime);
            long periodTimeAlert = date2.getTime() - date1.getTime();
            Toast.makeText(MainActivity.this, "Will alert in "+periodTimeAlert/1000/60 + " minute", Toast.LENGTH_SHORT).show(); // For example
        }
        catch (Exception e) {
            Log.d(TAG, "period time alert error : "+e);
        }

        Calendar beforePresentationAlarm = Calendar.getInstance();
        beforePresentationAlarm.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), beforePresentationHour, beforePresentationMinute, 0);
        Calendar beforeGroupDiscussionAlarm = Calendar.getInstance();
        beforeGroupDiscussionAlarm.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), beforePresentationHour+1, beforePresentationMinute, 0);
        Calendar beforeSharingAlarm = Calendar.getInstance();
        beforeSharingAlarm.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), beforePresentationHour+2, beforePresentationMinute, 0);

        Log.d(TAG, "setAlarmTimeStamp: " + beforePresentationAlarm.getTimeInMillis());

        if(System.currentTimeMillis() < beforePresentationAlarm.getTimeInMillis())
            alarmManager.set(AlarmManager.RTC_WAKEUP, beforePresentationAlarm.getTimeInMillis(), beforePresentationPendingIntent);
        if(System.currentTimeMillis() < beforeGroupDiscussionAlarm.getTimeInMillis())
            alarmManager.set(AlarmManager.RTC_WAKEUP, beforeGroupDiscussionAlarm.getTimeInMillis(), beforeGroupDiscussionPendingIntent);
        if(System.currentTimeMillis() < beforeSharingAlarm.getTimeInMillis())
            alarmManager.set(AlarmManager.RTC_WAKEUP, beforeSharingAlarm.getTimeInMillis(), beforeSharingPendingIntent);

        // Enable BootReceiver Component
        setBootReceiverEnabled(PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    private void cancelAlarm() {
        Log.d(TAG, "PresentationAlarmReceiver cancelled");
        alarmManager.cancel(beforePresentationPendingIntent);
        alarmManager.cancel(beforeGroupDiscussionPendingIntent);
        alarmManager.cancel(beforeSharingPendingIntent);

        // Disable BootReceiver Component
        setBootReceiverEnabled(PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }

    private void setBootReceiverEnabled(int componentEnabledState) {
        ComponentName componentName = new ComponentName(this, MyReceiver.class);
        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(componentName, componentEnabledState, PackageManager.DONT_KILL_APP);
    }

    public static class timeHttpPost extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... parameters) {
            OkHttpClient client = new OkHttpClient();
            int hour_of_day = parameters[0];
            int minute = parameters[1];
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "hour_of_day="+hour_of_day+"&minute="+minute);
            Request request = new Request.Builder()
                    .url("http://classroom.pitchayakit.com/alarm/")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "dd209f4b-605c-4dcf-8977-c12fd47d5e0d")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
