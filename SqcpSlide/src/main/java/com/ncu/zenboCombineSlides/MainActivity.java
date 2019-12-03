package com.ncu.zenboCombineSlides;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.asus.robotframework.API.MotionControl;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.robot.asus.robotactivity.RobotActivity;

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

public class MainActivity extends RobotActivity {
    public final static String TAG = "Zenbo combine slide";
    AlarmManager alarmManager;
    PendingIntent alertPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent alertIntent = new Intent(this, AlertActivity.class);
        alertPendingIntent = PendingIntent.getActivity(this, 0, alertIntent, 0);

        setContentView(R.layout.activity_main);
        robotAPI.motion.remoteControlHead(MotionControl.Direction.Head.UP);
        new sheetUrlGet().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new sheetUrlGet().execute();
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);

    }

    public class sheetUrlGet extends AsyncTask<String, Void, String > {
        @Override
        protected String doInBackground(String... parameters) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://classroom.pitchayakit.com/sheet_url/")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                Log.d(TAG, "sheetUrlGet result : " + response.toString());
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "sheetUrlGet error : " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);

                String urlGroup1 = jsonObject.get("group1").toString();
                Button btGroup1 = findViewById(R.id.btGroup1);
                setupGroupButton(btGroup1,urlGroup1);

                String urlGroup2 = jsonObject.get("group2").toString();
                Button btGroup2 = findViewById(R.id.btGroup2);
                setupGroupButton(btGroup2,urlGroup2);

                String urlGroup3 = jsonObject.get("group3").toString();
                Button btGroup3 = findViewById(R.id.btGroup3);
                setupGroupButton(btGroup3,urlGroup3);

                String urlGroup4 = jsonObject.get("group4").toString();
                Button btGroup4 = findViewById(R.id.btGroup4);
                setupGroupButton(btGroup4,urlGroup4);

                String urlGroup5 = jsonObject.get("group5").toString();
                Button btGroup5 = findViewById(R.id.btGroup5);
                setupGroupButton(btGroup5,urlGroup5);

                String urlFirstHourSlide = jsonObject.get("firstHourSlide").toString();
                Button btFirstHourSlide = findViewById(R.id.btFirstHourSlide);
                setupGroupButton(btFirstHourSlide,urlFirstHourSlide);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void setupGroupButton (final Button button, final String url){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button != findViewById(R.id.btFirstHourSlide))
                    setAlarm();
                new clickHttpPost().execute(button.getText().toString());
                Uri uri = Uri.parse(url);
                presentationActivity();
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
    }

    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
        }

        @Override
        public void initComplete() {
            super.initComplete();

        }
    };

    public static RobotCallback.Listen robotListenCallback = new RobotCallback.Listen() {
        @Override
        public void onFinishRegister() {

        }

        @Override
        public void onVoiceDetect(JSONObject jsonObject) {

        }

        @Override
        public void onSpeakComplete(String s, String s1) {

        }

        @Override
        public void onEventUserUtterance(JSONObject jsonObject) {

        }

        @Override
        public void onResult(JSONObject jsonObject) {

        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };

    public MainActivity() {
        super(robotCallback, robotListenCallback);
    }

    public static class clickHttpPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... parameters) {
            OkHttpClient client = new OkHttpClient();
            String click = parameters[0];
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "click="+click);
            Request request = new Request.Builder()
                    .url("http://classroom.pitchayakit.com/clicks/")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "dd209f4b-605c-4dcf-8977-c12fd47d5e0d")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return click;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //modeStatus.setText("Presentation");
        }
    }

    private void cancelAlarm() {
        Log.d(TAG, "PresentationAlarmReceiver cancelled");
        alarmManager.cancel(alertPendingIntent);

    }

    private void setAlarm() {
        Log.d(TAG, "Time"+System.currentTimeMillis());

        cancelAlarm();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        Calendar beforePresentationAlarm = Calendar.getInstance();
        beforePresentationAlarm.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)+10, 0);

        if(System.currentTimeMillis() < beforePresentationAlarm.getTimeInMillis())
            alarmManager.set(AlarmManager.RTC_WAKEUP, beforePresentationAlarm.getTimeInMillis(), alertPendingIntent);

    }

}
