package com.asus.classroommode;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    private static Button btFanLevelThree;
    private static Button btFanLevelTwo;
    private static Button btFanLevelOne;
    private static Button btFanTurnOff;
    private static Button btFanRotate;
    private static Button btInFront;
    private static Button btGroup1;
    private static Button btGroup2;
    private static Button btGroup3;
    private static Button btGroup4;
    private static Button btGroup5;

    private static Context googleClassroomAppContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRobotAPI = new RobotAPI(getApplicationContext(), robotCallback);
        mRobotAPIStatic = new RobotAPI(getApplicationContext(), robotCallback);
        Log.d(TAG, "onCreate: "+mRobotAPI);

        setContentView(R.layout.activity_main);

        googleClassroomAppContext = this;

        final Intent googleClassroomApp = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.classroom");

        btPresentation = findViewById(R.id.btPresentation);
        btPresentation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btPresentation.getText().toString());
                presentationActivity();
                robotAPI.motion.remoteControlHead(MotionControl.Direction.Head.UP);
                startActivity(googleClassroomApp);
            }
        });

        btGroupDiscussion = findViewById(R.id.btGroupDiscussion);
        btGroupDiscussion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btGroupDiscussion.getText().toString());
                groupDiscussionActivity();
            }
        });

        btAfterClass = findViewById(R.id.btAfterClass);
        btAfterClass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btAfterClass.getText().toString());
                afterClassActivity();
            }
        });

        btFanLevelThree = findViewById(R.id.btFanLevelThree);
        btFanLevelThree.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btFanLevelThree.getText().toString());
                new deviceHttpPost().execute("5","hight");
                mRobotAPIStatic.robot.speak("OK. Fan tun on level three");
            }
        });

        btFanLevelTwo = findViewById(R.id.btFanLevelTwo);
        btFanLevelTwo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btFanLevelTwo.getText().toString());
                new deviceHttpPost().execute("6","hight");
                mRobotAPIStatic.robot.speak("OK. Fan tun on level two");
            }
        });

        btFanLevelOne = findViewById(R.id.btFanLevelOne);
        btFanLevelOne.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btFanLevelOne.getText().toString());
                new deviceHttpPost().execute("7","hight");
                mRobotAPIStatic.robot.speak("OK. Fan tun on level one");
            }
        });

        btFanTurnOff = findViewById(R.id.btFanTurnOff);
        btFanTurnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btFanTurnOff.getText().toString());
                new deviceHttpPost().execute("8","hight");
                mRobotAPIStatic.robot.speak("OK. Fan tun off");
            }
        });

        btFanRotate = findViewById(R.id.btFanRotation);
        btFanRotate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btFanRotate.getText().toString());
                new deviceHttpPost().execute("9","hight");
                mRobotAPIStatic.robot.speak("OK. Fan rotation");
            }
        });

        btInFront = findViewById(R.id.btInFront);
        btInFront.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btInFront.getText().toString());
                robotAPI.motion.goTo("In front of room");
                robotAPI.robot.speak("I’m going to in front of room");
            }
        });

        btGroup1 = findViewById(R.id.btGroup1);
        btGroup1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btGroup1.getText().toString());
                robotAPI.motion.goTo("Group one");
                robotAPI.robot.speak("I’m going to group one");
            }
        });

        btGroup2 = findViewById(R.id.btGroup2);
        btGroup2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btGroup2.getText().toString());
                robotAPI.motion.goTo("Group two");
                robotAPI.robot.speak("I’m going to group two");
            }
        });

        btGroup3 = findViewById(R.id.btGroup3);
        btGroup3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btGroup3.getText().toString());
                robotAPI.motion.goTo("Group three");
                robotAPI.robot.speak("I’m going to group three");
            }
        });

        btGroup4 = findViewById(R.id.btGroup4);
        btGroup4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btGroup4.getText().toString());
                robotAPI.motion.goTo("Group four");
                robotAPI.robot.speak("I’m going to group four");
            }
        });

        btGroup5 = findViewById(R.id.btGroup5);
        btGroup5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new clickHttpPost().execute(btGroup5.getText().toString());
                robotAPI.motion.goTo("Group five");
                robotAPI.robot.speak("I’m going to group five");
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
        robotAPI.robot.speakAndListen("You can control following these command", new SpeakConfig());
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
            else if(sIntentionID.equals("helloWorld")) {
                String resultClassroomMode = RobotUtil.queryListenResultJson(jsonObject, "classroomMode", null);
                Intent googleClassroomApp = googleClassroomAppContext.getPackageManager().getLaunchIntentForPackage("com.google.android.apps.classroom");

                if(resultClassroomMode != null && !resultClassroomMode.equals("na") && (resultClassroomMode.equals("presentation_mode") || resultClassroomMode.equals("sharing_mode"))) {
                    presentationActivity();
                    mRobotAPIStatic.motion.remoteControlHead(MotionControl.Direction.Head.UP);
                    googleClassroomAppContext.startActivity(googleClassroomApp);
                }
                else if (resultClassroomMode != null && !resultClassroomMode.equals("na") && resultClassroomMode.equals("group_discussion_mode")) {
                    groupDiscussionActivity();
                }
                else if (resultClassroomMode != null && !resultClassroomMode.equals("na") && resultClassroomMode.equals("after_class_mode")) {
                    afterClassActivity();
                }

                String resultGoToLocation = RobotUtil.queryListenResultJson(jsonObject, "goToLocation", null);
                if(resultGoToLocation != null && !resultGoToLocation.equals("na") && (resultGoToLocation.equals("in_front"))) {
                    mRobotAPIStatic.motion.goTo("in front of room");
                }
                else if(resultGoToLocation != null && !resultGoToLocation.equals("na") && (resultGoToLocation.equals("group_one"))) {
                    mRobotAPIStatic.motion.goTo("group one");
                }
                else if(resultGoToLocation != null && !resultGoToLocation.equals("na") && (resultGoToLocation.equals("group_two"))) {
                    mRobotAPIStatic.motion.goTo("group two");
                }
                else if(resultGoToLocation != null && !resultGoToLocation.equals("na") && (resultGoToLocation.equals("group_three"))) {
                    mRobotAPIStatic.motion.goTo("group three");
                }
                else if(resultGoToLocation != null && !resultGoToLocation.equals("na") && (resultGoToLocation.equals("group_four"))) {
                    mRobotAPIStatic.motion.goTo("group four");
                }
                else if(resultGoToLocation != null && !resultGoToLocation.equals("na") && (resultGoToLocation.equals("group_five"))) {
                    mRobotAPIStatic.motion.goTo("group five");
                }

                String resultFanControl = RobotUtil.queryListenResultJson(jsonObject, "fanControl", null);
                Log.d(TAG, "resultFanControl = " + resultFanControl);
                if(resultFanControl != null && !resultFanControl.equals("na") && (resultFanControl.equals("fan_level_three"))) {
                    new deviceHttpPost().execute("5","hight");
                    mRobotAPIStatic.robot.speak("OK. Fan tun on level three");
                }
                else if(resultFanControl != null && !resultFanControl.equals("na") && (resultFanControl.equals("fan_level_two"))) {
                    new deviceHttpPost().execute("6","hight");
                    mRobotAPIStatic.robot.speak("OK. Fan tun on level two");
                }
                else if(resultFanControl != null && !resultFanControl.equals("na") && (resultFanControl.equals("fan_level_one"))) {
                    new deviceHttpPost().execute("7","hight");
                    mRobotAPIStatic.robot.speak("OK. Fan tun on level one");
                }
                else if(resultFanControl != null && !resultFanControl.equals("na") && (resultFanControl.equals("fan_turn_off"))) {
                    new deviceHttpPost().execute("8","hight");
                    mRobotAPIStatic.robot.speak("OK. Fan turn off");
                }
                else if(resultFanControl != null && !resultFanControl.equals("na") && (resultFanControl.equals("fan_rotation"))) {
                    new deviceHttpPost().execute("9","hight");
                    mRobotAPIStatic.robot.speak("OK. Fan rotation");
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
}

