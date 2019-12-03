package com.zenbo.zenbo_alarm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.asus.robotframework.API.RobotUtil;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class PresentationActivity extends RobotActivity {
    private static TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mTextView = findViewById(R.id.textview_info);
        presentationActivity();

        Button btBack = findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // close faical
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);

        String robotSpeak = "Class will begin in few minutes. Don't for get to check in with me.";
        //robotAPI.robot.speakAndListen("What is classroom mode right now", new SpeakConfig());
        robotAPI.robot.speak(robotSpeak);

        mTextView.setText(robotSpeak);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

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
        }

        @Override
        public void onResult(JSONObject jsonObject) {
            String text;
            text = ": " + jsonObject.toString();
            Log.d(TAG, text);
        }

        @Override
        public void onRetry(JSONObject jsonObject) {

        }
    };


    public PresentationActivity() {
        super(robotCallback, robotListenCallback);
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

}
