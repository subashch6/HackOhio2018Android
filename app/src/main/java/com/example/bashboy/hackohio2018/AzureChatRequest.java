package com.example.bashboy.hackohio2018;

import android.util.Log;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AzureChatRequest implements Runnable
{
    MainActivity mainActivity;
    String text;

    public AzureChatRequest(MainActivity mainActivity, String text)
    {
        this.mainActivity = mainActivity;
        this.text = text;
    }

    @Override
    public void run()
    {

        while(MainActivity.getRecieveJWT() == null)
        {
            receiveJWT();
        }
        while(MainActivity.getConvId() == null)
        {
            receiveConvID();
        }
        chatRequest();
    }

    public void chatRequest()
    {
        String inputResponse = "";
        JSONObject jsonInput = new JSONObject();
        try
        {
            String tempString = "https://9fc08345.ngrok.io/v3/directline/conversations/" + MainActivity.getConvId() + "/activities";
            URL url = new URL(tempString);
            HttpURLConnection urlConnection;

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "Bearer " + MainActivity.getRecieveJWT());
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            BufferedOutputStream request = new BufferedOutputStream(urlConnection.getOutputStream());
            request.write(("{\"type\":\"message\",\"from\":{\"id\":\"user1\"},\"text\":"+text+"}").getBytes());
            request.flush();
            request.close();


            String temp = "";
            String inputLine = "";

            while ((temp = urlConnection.getResponseMessage()) == null) {}
            inputResponse = temp;
            Log.d(MainActivity.LOG, inputResponse);

            BufferedReader input = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((temp = input.readLine()) != null)
            {
                inputLine += temp;
            }
            Log.d(MainActivity.LOG, inputResponse + ": " + inputLine);
            jsonInput = new JSONObject(inputLine);


            urlConnection.disconnect();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        this.mainActivity.textToChatCallback(inputResponse, jsonInput);
    }


    public void receiveConvID() {
        String inputResponse = "";
        JSONObject jsonInput = new JSONObject();
        try
        {
            URL url = new URL("https://9fc08345.ngrok.io/v3/directline/conversations");
            HttpURLConnection urlConnection;

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "Bearer " + MainActivity.getRecieveJWT());
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

        try
        {
            //if(inputResponse.equals("Created"))
                MainActivity.setConvId(jsonInput.get("conversationId").toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void receiveJWT() {
        String inputResponse = "";
        JSONObject jsonInput = new JSONObject();
        try
        {
            URL url = new URL("https://login.microsoftonline.com/botframework.com/oauth2/v2.0/token");
            HttpURLConnection urlConnection;

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("client_id","58098909-c7ba-4b23-9e2b-1d58d5e516b5");
            urlConnection.setRequestProperty("client_secret","wwvpkMNR8!udGMCD3348$^=");
            urlConnection.setRequestProperty("grant_type","client_credentials");
            urlConnection.setRequestProperty("scope","https://api.botframework.com/.default");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);


            String temp = "";
            String inputLine = "";

            while ((temp = urlConnection.getResponseMessage()) == null) {}
            inputResponse = temp;
            Log.d(MainActivity.LOG, inputResponse);

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

        try
        {
            if(inputResponse.equals("OK"))
                MainActivity.setRecieveJWT(jsonInput.get("access_token").toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
