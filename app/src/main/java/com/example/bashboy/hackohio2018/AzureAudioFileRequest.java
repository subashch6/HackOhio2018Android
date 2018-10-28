package com.example.bashboy.hackohio2018;

import android.os.Environment;
import android.util.Log;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class AzureAudioFileRequest implements Runnable
{
    private String urlString = "https://eastus.stt.speech.microsoft.com/speech/recognition/conversation/cognitiveservices/v1?language=en-US";
    File file;
    byte[] bytes;
    MainActivity mainActivity;

    public AzureAudioFileRequest(MainActivity mainActivity, String fileName)
    {
        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + fileName);
        int size = (int) file.length();
        bytes = new byte[size];
        this.mainActivity = mainActivity;

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void run()
    {
        String inputResponse = "";
        JSONObject jsonInput = new JSONObject();
        try
        {
            URL url = new URL("https://eastus.stt.speech.microsoft.com/speech/recognition/conversation/cognitiveservices/v1?language=en-US");
            HttpURLConnection urlConnection;

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Ocp-Apim-Subscription-Key","8a2205d9ff074110b203bf35ad1c5fe7");
            urlConnection.setRequestProperty("Content-type","audio/wav; codec=audio/pcm; samplerate=16000");
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            BufferedOutputStream request = new BufferedOutputStream(urlConnection.getOutputStream());
            request.write(bytes);
            request.flush();
            request.close();


            String temp = "";
            String inputLine = "";

            while ((temp = urlConnection.getResponseMessage()) == null) {}
            inputResponse = temp;

            BufferedReader input = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while((temp = input.readLine()) != null)
            {
                inputLine += temp;
            }
            Log.d(MainActivity.LOG, inputResponse + ": " + inputLine);
            jsonInput = new JSONObject(inputLine);
            urlConnection.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        this.mainActivity.speechToTextCallback(inputResponse, jsonInput);
    }

}
