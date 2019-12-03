package com.ncu.ZenboFaceRecognition;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.asus.robotframework.API.RobotAPI;
import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.VisionConfig;
import com.asus.robotframework.API.results.DetectFaceResult;
import com.asus.robotframework.API.results.DetectPersonResult;
import com.asus.robotframework.API.results.GesturePointResult;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

import java.util.List;

public class MainActivity extends RobotAppCompatActivity {
    private Button mBtnDetectFace;
    private static Button btTakePhoto,cancelDetectFace;
    private static Integer countFaceDetect = 0;
    private static RobotAPI mRobotApiStatic;
    public final static String TAG = "Zenbo face recognition";
    private CountDownTimer showExpression;
    private String ZenboFace = "hideFace";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRobotApiStatic = new RobotAPI(getApplicationContext(), robotCallback);

        stopDetectFace();
        startDetectFace();

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerCamera2API, Camera2BasicFragment.newInstance())
                    .commit();
        }

        mBtnDetectFace = findViewById(R.id.btDetectFace);
        mBtnDetectFace.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetectFace();
            }
        });

        showExpression = new CountDownTimer(5000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if(ZenboFace == "hideFace"){
                    robotAPI.robot.setExpression(RobotFace.DEFAULT_STILL);
                    ZenboFace = "DEFAULT_STILL";
                }
                else {
                    robotAPI.robot.setExpression(RobotFace.HIDEFACE);
                    ZenboFace = "hideFace";
                    startDetectFace();
                }
                start();
            }
        };
        showExpression.start();

        btTakePhoto = findViewById(R.id.btTakePhoto);
        btTakePhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button takePhoto = findViewById(R.id.picture);
                takePhoto.performClick();
                showExpression.cancel();
                stopDetectFace();
                robotAPI.robot.setExpression(RobotFace.DEFAULT_STILL);
                robotAPI.robot.speak("Hi");
            }
        });

        cancelDetectFace = findViewById(R.id.cancelDetectFace);
        cancelDetectFace.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDetectFace();
            }
        });
    }

    private void startDetectFace() {
        // start detect face
        VisionConfig.FaceDetectConfig config = new VisionConfig.FaceDetectConfig();
        config.enableDebugPreview = true;  // set to true if you need preview screen
        config.intervalInMS = 1000;
        config.enableDetectHead = false;
        robotAPI.vision.requestDetectFace(config);
    }

    private void stopDetectFace() {
        // stop detect face
        robotAPI.vision.cancelDetectFace();
    }


    @Override
    protected void onPause() {
        super.onPause();

        robotAPI.vision.cancelDetectFace();
        showExpression.cancel();

    }


    @Override
    protected void onResume() {
        super.onResume();

        //robotAPI.vision.cancelDetectFace();
        // close faical
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);
        //robotAPI.robot.setExpression(RobotFace.DEFAULT_STILL);
        startDetectFace();
        showExpression.start();
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
        public void onDetectPersonResult(List<DetectPersonResult> resultList) {
            super.onDetectPersonResult(resultList);

        }

        @Override
        public void onGesturePoint(GesturePointResult result) {
            super.onGesturePoint(result);
        }

        @Override
        public void onDetectFaceResult(List<DetectFaceResult> resultList) {
            super.onDetectFaceResult(resultList);

            Log.d(TAG, "onDetectFaceResult: " + resultList.get(0));

            //use toast to show detected faces
            //String toast_result = "Detect Face";
            //Toast toast = Toast.makeText(context, toast_result, Toast.LENGTH_SHORT);
            //toast.show();
            countFaceDetect++;
            if(countFaceDetect % 2 != 0) {
                btTakePhoto.performClick();
            }
            mRobotApiStatic.robot.setExpression(RobotFace.DEFAULT_STILL);
            mRobotApiStatic.vision.cancelDetectFace();


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
}