package com.example.bashboy.hackohio2018;

import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetMethod implements Runnable
{
    MainActivity mainActivity;

    public GetMethod(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run()
    {
        try
        {
            while (!MainActivity.killThread)
            {
                if (MainActivity.getConvId() != null && MainActivity.getRecieveJWT() != null)
                {
                    getPoll();
                }
                Thread.sleep(5000);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    private void getPoll()
    {
        String inputResponse = "";
        JSONObject jsonInput = new JSONObject();
        try
        {
            URL url = new URL("https://9fc08345.ngrok.io/v3/directline/conversations/" + MainActivity.getConvId() + "/activities");
            HttpURLConnection urlConnection;

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Authorization","Bearer " + MainActivity.getRecieveJWT());
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


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


        this.mainActivity.getCallback(inputResponse, jsonInput);
    }
}
