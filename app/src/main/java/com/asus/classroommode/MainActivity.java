package com.asus.classroommode;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.MotionControl;
import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.RobotUtil;
import com.asus.robotframework.API.SpeakConfig;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends RobotActivity {
    RobotAPI mRobotAPI;
    static RobotAPI mRobotAPIStatic;
    public final static String TAG = "ZenboDialogSample";
    public final static String DOMAIN = "818EBC176FCC46FE82875EC3104DF20B";

    private static Button btPresentation;
    private static Button btGroupDiscussion;
    private static Button btAfterClass;
    private static Button btGoFront;

    private static Context googleClassroomAppContext;
    private static Context groupDiscussionContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRobotAPI = new RobotAPI(getApplicationContext(), robotCallback);
        mRobotAPIStatic = new RobotAPI(getApplicationContext(), robotCallback);
        Log.d(TAG, "onCreate: "+mRobotAPI);

        setContentView(R.layout.activity_main);

        googleClassroomAppContext = this;
        groupDiscussionContext = this;

        final Intent googleClassroomApp = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.classroom");

        btPresentation = findViewById(R.id.btPresentation);
        btPresentation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btPresentation.getText().toString());
                presentationActivity();
                //robotAPI.robot.speak("I’m going to in front of room");
                //robotAPI.motion.goTo("In front of room");
                robotAPI.motion.remoteControlHead(MotionControl.Direction.Head.UP);
                startActivity(googleClassroomApp);
            }
        });

        btGroupDiscussion = findViewById(R.id.btGroupDiscussion);
        btGroupDiscussion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btGroupDiscussion.getText().toString());
                groupDiscussionActivity();
                startActivity(new Intent(MainActivity.this, GroupDiscusstion.class));
                //mRobotAPIStatic.robot.setExpression(RobotFace.HAPPY);
            }
        });

        btAfterClass = findViewById(R.id.btAfterClass);
        btAfterClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btAfterClass.getText().toString());
                afterClassActivity();
                mRobotAPIStatic.robot.setExpression(RobotFace.HAPPY);
            }
        });

        btGoFront = findViewById(R.id.btGoogleClassroomApp);
        btGoFront.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btGoFront.getText().toString());
                robotAPI.robot.speak("I’m going to in front of room");
                robotAPI.motion.goTo("In front of room");
                //startActivity(googleClassroomApp);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Head up
        robotAPI.motion.remoteControlHead(MotionControl.Direction.Head.UP);

        // close faical
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);

        // jump dialog domain
        robotAPI.robot.jumpToPlan(DOMAIN, "lanuchHelloWolrd_Plan");

        // listen user utterance
        robotAPI.robot.speakAndListen("What is classroom mode right now", new SpeakConfig());
        //robotAPI.robot.speak("What is classroom mode right now");

    }


    @Override
    protected void onPause() {
        super.onPause();

        //stop listen user utterance
        robotAPI.robot.stopSpeakAndListen();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            String text;
            text = "onEventUserUtterance: " + jsonObject.toString();
            Log.d(TAG, text);
            String userUtterance = RobotUtil.queryListenResultJson(jsonObject, "user_utterance");
            Log.d(TAG, "user_utterance_log : "+userUtterance);
            new voiceHttpPost().execute(userUtterance);
        }

        @Override
        public void onResult(JSONObject jsonObject) {
            String text;
            text = ": " + jsonObject.toString();
            Log.d(TAG, text);

            String sIntentionID = RobotUtil.queryListenResultJson(jsonObject, "IntentionId");
            Log.d(TAG, "Intention Id = " + sIntentionID);

            final Intent googleClassroomBrowser = new Intent(Intent.ACTION_VIEW);
            googleClassroomBrowser.setData(Uri.parse("https://classroom.google.com/u/2/h"));

            if(sIntentionID.equals("lanuchHelloWolrd_Plan")) {
                String resultClassroomMode = RobotUtil.queryListenResultJson(jsonObject, "output_context", null);
                Log.d(TAG, "First result = " + resultClassroomMode);
            }
            if(sIntentionID.equals("helloWorld")) {
                String resultClassroomMode = RobotUtil.queryListenResultJson(jsonObject, "classroomMode", null);
                Intent googleClassroomApp = googleClassroomAppContext.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.classroom");
                Intent groupDiscussionActivity = new Intent(groupDiscussionContext, GroupDiscusstion.class);

                if(resultClassroomMode != null && !resultClassroomMode.equals("na") && (resultClassroomMode.equals("presentation_mode") || resultClassroomMode.equals("sharing_mode"))) {
                    presentationActivity();
                    //mRobotAPIStatic.motion.goTo("In front of room");
                    //mRobotAPIStatic.robot.speak("I’m going to in front of room");
                    mRobotAPIStatic.motion.remoteControlHead(MotionControl.Direction.Head.UP);
                    googleClassroomAppContext.startActivity(googleClassroomApp);
                }
                else if (resultClassroomMode != null && !resultClassroomMode.equals("na") && resultClassroomMode.equals("group_discussion_mode")) {
                    groupDiscussionActivity();
                    groupDiscussionContext.startActivity(groupDiscussionActivity);
                }
                else if (resultClassroomMode != null && !resultClassroomMode.equals("na") && resultClassroomMode.equals("after_class_mode")) {
                    afterClassActivity();
                }
                else if (resultClassroomMode != null && !resultClassroomMode.equals("na") && resultClassroomMode.equals("in_front")) {
                    mRobotAPIStatic.motion.goTo("In front of room");
                }
            }

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

    public static class voiceHttpPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... parameters) {
            OkHttpClient client = new OkHttpClient();
            String voice = parameters[0];
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "voice="+voice);
            Request request = new Request.Builder()
                    .url("http://classroom.pitchayakit.com/voices/")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Postman-Token", "dd209f4b-605c-4dcf-8977-c12fd47d5e0d")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return voice;
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

    public static class deviceHttpPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... parameters) {
            OkHttpClient client = new OkHttpClient();
            String lightPin = parameters[0];
            String lightResult = parameters[1];
            String resultLightValue;
            MediaType mediaType = MediaType.parse("text/csv");
            if(lightResult.equals("hight")) {
                resultLightValue = lightPin+",,1";
            }
            else
                resultLightValue = lightPin+",,0";
            RequestBody body = RequestBody.create(mediaType, resultLightValue);

            Request request = new Request.Builder()
                    .url("https://api.mediatek.com/mcs/v2/devices/DjhyExmq/datapoints.csv")
                    .post(body)
                    .addHeader("deviceKey", "UJDPigFqdMOECY10")
                    .addHeader("Content-Type", "text/csv")
                    .addHeader("cache-control", "no-cache")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return lightResult;
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

    public static void presentationActivity(){
        new deviceHttpPost().execute("2","low");
        new deviceHttpPost().execute("3","low");
        new deviceHttpPost().execute("4","hight");
    }
    public static void groupDiscussionActivity(){
        new deviceHttpPost().execute("2","hight");
        new deviceHttpPost().execute("3","hight");
        new deviceHttpPost().execute("4","hight");
    }
    public static void afterClassActivity(){
        new deviceHttpPost().execute("2","low");
        new deviceHttpPost().execute("3","low");
        new deviceHttpPost().execute("4","low");
    }
}

