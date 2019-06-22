package com.asus.classroommode;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
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

public class GroupDiscusstion extends RobotActivity {
    RobotAPI mRobotAPI;
    static RobotAPI mRobotAPIStatic;
    public final static String TAG = "ZenboDialogSample";
    public final static String DOMAIN = "818EBC176FCC46FE82875EC3104DF20B";

    private static Button btGroup1;
    private static Button btGroup2;
    private static Button btGroup3;
    private static Button btGroup4;
    private static Button btGroup5;
    private static Button btFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRobotAPI = new RobotAPI(getApplicationContext(), robotCallback);
        mRobotAPIStatic = new RobotAPI(getApplicationContext(), robotCallback);
        setContentView(R.layout.activity_group_discusstion);

        btGroup1 = findViewById(R.id.btGroup1);
        btGroup1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new MainActivity.clickHttpPost().execute(btGroup1.getText().toString());
                robotAPI.motion.goTo("Group one");
                robotAPI.robot.speak("I’m going to group one");
            }
        });

        btGroup2 = findViewById(R.id.btGroup2);
        btGroup2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new MainActivity.clickHttpPost().execute(btGroup2.getText().toString());
                robotAPI.motion.goTo("Group two");
                robotAPI.robot.speak("I’m going to group two");
            }
        });

        btGroup3 = findViewById(R.id.btGroup3);
        btGroup3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new MainActivity.clickHttpPost().execute(btGroup3.getText().toString());
                robotAPI.motion.goTo("Group three");
                robotAPI.robot.speak("I’m going to group three");
            }
        });

        btGroup4 = findViewById(R.id.btGroup4);
        btGroup4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new MainActivity.clickHttpPost().execute(btGroup4.getText().toString());
                robotAPI.motion.goTo("Group four");
                robotAPI.robot.speak("I’m going to group four");
            }
        });

        btGroup5 = findViewById(R.id.btGroup5);
        btGroup5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new MainActivity.clickHttpPost().execute(btGroup5.getText().toString());
                robotAPI.motion.goTo("Group five");
                robotAPI.robot.speak("I’m going to group five");
            }
        });

        btFront = findViewById(R.id.btFront);
        btFront.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new MainActivity.clickHttpPost().execute(btFront.getText().toString());
                robotAPI.motion.goTo("In front of room");
                robotAPI.robot.speak("I’m going to in front of room");
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
        robotAPI.robot.speakAndListen("Which group I have to go", new SpeakConfig());
        //robotAPI.robot.speak("What is classroom mode right now");

    }

    public static RobotCallback robotCallback = new RobotCallback() {
        @Override
        public void initComplete() {
            super.initComplete();

            Log.d("ZenboGoToLocation", "initComplete()");
        }

        @Override
        public void onResult(int cmd, int serial, RobotErrorCode err_code, Bundle result) {
            super.onResult(cmd, serial, err_code, result);
        }

        @Override
        public void onStateChange(int cmd, int serial, RobotErrorCode err_code, RobotCmdState state) {
            super.onStateChange(cmd, serial, err_code, state);
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
            String userUtterance = RobotUtil.queryListenResultJson(jsonObject, "user_utterance");
            new MainActivity.voiceHttpPost().execute(userUtterance);
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

                if(resultClassroomMode != null && !resultClassroomMode.equals("na") && (resultClassroomMode.equals("group_one") )) {
                    mRobotAPIStatic.motion.goTo("Group one");
                    mRobotAPIStatic.robot.speak("I’m going to Group one");
                }
                else if(resultClassroomMode != null && !resultClassroomMode.equals("na") && (resultClassroomMode.equals("group_two") )) {
                    mRobotAPIStatic.motion.goTo("Group two");
                    mRobotAPIStatic.robot.speak("I’m going to Group two");
                }
                else if(resultClassroomMode != null && !resultClassroomMode.equals("na") && (resultClassroomMode.equals("group_three") )) {
                    mRobotAPIStatic.motion.goTo("Group three");
                    mRobotAPIStatic.robot.speak("I’m going to Group three");
                }
                else if(resultClassroomMode != null && !resultClassroomMode.equals("na") && (resultClassroomMode.equals("group_four") )) {
                    mRobotAPIStatic.motion.goTo("Group four");
                    mRobotAPIStatic.robot.speak("I’m going to Group four");
                }
                else if(resultClassroomMode != null && !resultClassroomMode.equals("na") && (resultClassroomMode.equals("group_five") )) {
                    mRobotAPIStatic.motion.goTo("Group five");
                    mRobotAPIStatic.robot.speak("I’m going to Group five");
                }
                else if(resultClassroomMode != null && !resultClassroomMode.equals("na") && (resultClassroomMode.equals("in_front") )) {
                    mRobotAPIStatic.motion.goTo("In front of room");
                    mRobotAPIStatic.robot.speak("I’m going to in front of room");
                }
                Log.d(TAG, "Second result" + resultClassroomMode);
                mRobotAPIStatic.motion.remoteControlHead(MotionControl.Direction.Head.UP);
            }
        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };

    public GroupDiscusstion()  {
        super(robotCallback, robotListenCallback);
    }



}
