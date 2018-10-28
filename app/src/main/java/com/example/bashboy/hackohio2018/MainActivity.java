package com.example.bashboy.hackohio2018;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button button1;
    TextView text1;
    private ExtAudioRecorder requestRecorder = ExtAudioRecorder.getInstance(false);
    private boolean recording = false;
    private String filename = "file.wav";
    private String PATH_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + filename;
    private String[] reqPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET};
    private boolean haveAllPermissions = false;
    static String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Imk2bEdrM0ZaenhSY1ViMkMzbkVRN3N5SEpsWSIsImtpZCI6Imk2bEdrM0ZaenhSY1ViMkMzbkVRN3N5SEpsWSJ9.eyJhdWQiOiJodHRwczovL2FwaS5ib3RmcmFtZXdvcmsuY29tIiwiaXNzIjoiaHR0cHM6Ly9zdHMud2luZG93cy5uZXQvZDZkNDk0MjAtZjM5Yi00ZGY3LWExZGMtZDU5YTkzNTg3MWRiLyIsImlhdCI6MTU0MDczNDM0NSwibmJmIjoxNTQwNzM0MzQ1LCJleHAiOjE1NDA3MzgyNDUsImFpbyI6IjQyUmdZSGczODFPZjY2MmdWcGJXV3djdWJndjVEZ0E9IiwiYXBwaWQiOiI1ODA5ODkwOS1jN2JhLTRiMjMtOWUyYi0xZDU4ZDVlNTE2YjUiLCJhcHBpZGFjciI6IjEiLCJpZHAiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC9kNmQ0OTQyMC1mMzliLTRkZjctYTFkYy1kNTlhOTM1ODcxZGIvIiwidGlkIjoiZDZkNDk0MjAtZjM5Yi00ZGY3LWExZGMtZDU5YTkzNTg3MWRiIiwidXRpIjoiRVdiWWVzRnVPa0dOcU9DU3lrQXlBQSIsInZlciI6IjEuMCJ9.ixAJP7u7xl4AFXL8zup9rcQXBtFkhj9GSgEeAVnNjRjJv0M9uHoBNxClnbb-GuV1e8MdMiwUJJWnJgHF9Y6D0BOhrIZ2uoyZWv917KfagFLYgXV2z4bV-PbRxqk8NEdJawEFVwmSauCvF7tYUn7wZqiQ-t70iOM8wC1HhWjSO_kO7cc9ljtjRNAhjl77tAnsWMb0ixoIgdBISC1loadqqeZv8NFlsH8PY6EqT1eVed_bbbL3TF9E4d6Ot0KrMot8KwclHZ7Pmq5U8dV2M3AF1SoX03jrnvmTbTyGIi6yLqoBBF6xWmsbR4MtyxBD10Zlrn2HSd4rv1Pqgn9dqBldeA";
    static String convId = "8d75cd00-daba-11e8-b73c-43f34480215c";

    private Thread get;
    static boolean killThread = false;
    static JSONObject json = null;

    static final String LOG = "LOGBASE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.button1);
        button1.setOnTouchListener(button1Listener);

        text1 = findViewById(R.id.text1);
        text1.setOnTouchListener(text1Listener);
        text1.setText(Environment.getExternalStorageDirectory().getAbsolutePath());

        while(!haveAllPermissions) {
            haveAllPermissions = true;
            for (int i = 0; i < reqPermissions.length; i++) {
                if (ActivityCompat.checkSelfPermission(this, reqPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, reqPermissions, 10);
                    haveAllPermissions = false;
                }
            }
        }

        get = new Thread(new GetMethod(this));
        killThread = false;
        //get.start();

        /*
        requestRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        requestRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        requestRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        requestRecorder.setOutputFile(PATH_FILE);
        */

    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        killThread = true;
        try
        {
            get.join();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static synchronized void setRecieveJWT(String jwt)
    {
        MainActivity.jwt = jwt;
    }

    public static synchronized void setConvId(String convId)
    {
        MainActivity.convId = convId;
    }

    public static String getRecieveJWT()
    {
        return MainActivity.jwt;
    }

    public static String getConvId()
    {
        return MainActivity.convId;
    }


    public synchronized void speechToTextCallback(String response, JSONObject json)
    {
        try
        {
            if (response.equals("OK") && json.get("RecognitionStatus").equals("Success"))
            {
                MainActivity.json = json;
                new Thread(new AzureChatRequest(this, json.get("DisplayText").toString())).start();
            }
        }
        catch(Exception e)
        {
            Log.d(LOG, e.toString());
        }
    }

    public synchronized void textToChatCallback(String response, JSONObject json)
    {
        try
        {
            if (response.equals("OK"))
            {
                //this.text1.setText("Stuff");
            }

        } catch (Exception e)
        {
            Log.d(LOG, e.toString());
        }
    }


    public void getCallback(String message, JSONObject json)
    {
        try
        {
            if(message.equals("OK"))
                this.text1.setText(json.get("activities").toString());

        }
        catch(Exception e)
        {
            Log.d("CallBack", e.toString());
        }
    }

    private boolean onRecordButtonClicked()
    {
        requestRecorder.setOutputFile(PATH_FILE);
        try
        {
            requestRecorder.prepare();
        }
        catch (Exception e)
        {
            button1.setText(e.getMessage());
            recording = false;
            return false;
        }

        button1.setText("Started Recording!");
        requestRecorder.start();
        recording = true;
        return true;
    }

    private boolean onRecordButtonReleased()
    {
        if(recording)
        {
            requestRecorder.stop();
            recording = false;
            new Thread(new AzureAudioFileRequest(this,filename)).start();
        }
        requestRecorder.reset();
        button1.setText("Finished Recording!");
        if(!get.isAlive())
        {
            get.start();
        }
        return true;
    }

    private View.OnTouchListener button1Listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {

                return onRecordButtonClicked();
            }
            else if (event.getAction() == MotionEvent.ACTION_UP)
            {
                return onRecordButtonReleased();
            }
            return true;
        }
    };

    private View.OnTouchListener text1Listener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {

            try
            {
                if (json != null)
                {
                    text1.setText(MainActivity.json.get("DisplayText").toString());
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return true;
        }


    };


}
