package com.ncu.ZenboFaceRecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.asus.robotframework.API.RobotCallback;
import com.asus.robotframework.API.RobotCmdState;
import com.asus.robotframework.API.RobotErrorCode;
import com.asus.robotframework.API.RobotFace;
import com.robot.asus.robotactivity.RobotActivity;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroupsActivity extends RobotActivity {
    public final static String TAG = "Zenbo face recognition";
    private String timeStamp;
    private String uploadPhotoFileName;
    private Intent checkInCompleteActivity;
    private String latestGroup;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        checkInCompleteActivity = new Intent(GroupsActivity.this, CheckInCompleteActivity.class);

        File imgFile = new File("/storage/emulated/0/Android/data/com.ncu.zenbofacerecognition/files/pic.jpg");

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = findViewById(R.id.capturePhoto);
            myImage.setImageBitmap(myBitmap);
        }

        final Button btGroup1 = findViewById(R.id.btGroup1);
        setupOnclickGroupButton(btGroup1);

        final Button btGroup2 = findViewById(R.id.btGroup2);
        setupOnclickGroupButton(btGroup2);

        final Button btGroup3 = findViewById(R.id.btGroup3);
        setupOnclickGroupButton(btGroup3);

        final Button btGroup4 = findViewById(R.id.btGroup4);
        setupOnclickGroupButton(btGroup4);

        final Button btGroup5 = findViewById(R.id.btGroup5);
        setupOnclickGroupButton(btGroup5);

        final Button btBack = findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        backToMainActivity();

        robotAPI.robot.speak("Please choose your group number");

    }

    public GroupsActivity() {
        super(robotCallback, robotListenCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        robotAPI.robot.setExpression(RobotFace.HIDEFACE);
    }

    private void backToMainActivity() {
        // 30 seconds countdown timer
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                finish();
            }
        }.start();
    }

    private void setupOnclickGroupButton (final Button bt) {
        bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                latestGroup = bt.getText().toString();
                new imageHttpPost().execute(latestGroup);
                startActivity(checkInCompleteActivity);
            }
        });
    }
    private class imageHttpPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... parameters) {
            OkHttpClient client = new OkHttpClient();
            String group = parameters[0];
            MediaType mediaType = MediaType.parse("image/jpg");
            String yourFilePath = "/storage/emulated/0/Android/data/com.ncu.zenbofacerecognition/files/pic.jpg";
            File file = new File( yourFilePath );
            Long timeStampLong = System.currentTimeMillis()/1000;
            timeStamp = timeStampLong.toString();
            uploadPhotoFileName = timeStamp+"_"+group.replace(' ', '_')+".jpg";
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("fileToUpload",uploadPhotoFileName, RequestBody.create(mediaType, file)).build();

            Request request = new Request.Builder()
                    .url("http://classroom.pitchayakit.com/hci_photo_check_in/photos/")
                    .post(body)
                    .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
                    .addHeader("User-Agent", "PostmanRuntime/7.17.1")
                    .addHeader("Accept", "*/*")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Postman-Token", "8ce2c7fc-7451-4e8f-986a-62e874459264,01419cc7-2ea6-497a-a0ce-2761bf5c7746")
                    .addHeader("Host", "classroom.pitchayakit.com")
                    .addHeader("Content-Type", "multipart/form-data; boundary=--------------------------030655545707739229909339")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Content-Length", "11213")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("cache-control", "no-cache")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "imageHttpPost onPostExecute: "+result);
            //finish();
            new attendanceSheetPost().execute(latestGroup);
        }
    }

    private class attendanceSheetPost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... parameters) {
            OkHttpClient client = new OkHttpClient();
            String group = parameters[0];
            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
            RequestBody body = RequestBody.create(mediaType, "url=http://classroom.pitchayakit.com/hci_photo_check_in/photos/upload/"+uploadPhotoFileName+"&group="+group);
            Request request = new Request.Builder()
                    .url("http://classroom.pitchayakit.com/hci_photo_check_in/attendance_sheet/")
                    .post(body)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("User-Agent", "PostmanRuntime/7.18.0")
                    .addHeader("Accept", "*/*")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Postman-Token", "1ccc717b-095d-4d1d-903e-4749bd9f2c59,fdfb3225-3097-4551-9920-2c56076f6043")
                    .addHeader("Host", "classroom.pitchayakit.com")
                    .addHeader("Accept-Encoding", "gzip, deflate")
                    .addHeader("Content-Length", "16")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("cache-control", "no-cache")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "attendanceSheetPost onPostExecute: "+result);
            //finish();
        }
    }


}
