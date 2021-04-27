package com.michelleweixu.rewards.apis;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.michelleweixu.rewards.MainActivity;
import com.michelleweixu.rewards.ViewProfileActivity;
import com.michelleweixu.rewards.userdata.UserProfile;
import com.michelleweixu.rewards.userdata.UserReward;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class DeleteProfileAPIRunnable  implements Runnable {

    private static final String BASE_URL = "http://christopherhield.org/api/";
    private ViewProfileActivity act;
    String username, password, apiKey;


    public DeleteProfileAPIRunnable(ViewProfileActivity act, String username, String apiKey) {
        this.act = act;
        this.username = username;
        this.apiKey = apiKey;
    }


    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        // Example Call:
        //http://christopherhield.org/api/Profile/DeleteProfile?userName=Emma.Walker
        try {
            String endpointURL = BASE_URL + "Profile/DeleteProfile?userName=" + username;

            Uri.Builder buildURL = Uri.parse(endpointURL).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();
            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    act.deleteSuccessAndQuit();
                }
            });
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }
}