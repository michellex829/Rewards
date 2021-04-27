package com.michelleweixu.rewards.apis;

import android.net.Uri;
import android.util.Log;

import com.michelleweixu.rewards.MainActivity;
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
import java.util.Date;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class LoginAPIRunnable implements Runnable {

    private static final String TAG = "LoginAPIRunnable";
    private static final String BASE_URL = "http://christopherhield.org/api/";
    private MainActivity mainActivity;
    String username, password, apiKey;


    public LoginAPIRunnable(MainActivity mainActivity, String username, String password, String apiKey) {
        this.mainActivity = mainActivity;
        this.username = username;
        this.password = password;
        this.apiKey = apiKey;
    }


    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        JSONObject jResult = null;
        // Example call: http://christopherhield.org/api/Profile/Login?userName=Owen.Miller&password=OweMil123
        try {
            String endpointURL = BASE_URL + "Profile/Login?userName=" + username
                    + "&password=" + password;

            Uri.Builder buildURL = Uri.parse(endpointURL).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
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
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.invalidApiCall(result.toString());
                    }
                });
                return;
            }

            process(result.toString());
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
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
    }

    private void process(String s) {
        try {
            JSONObject jUser = new JSONObject(s);
            JSONArray jRecords = new JSONArray(jUser.getString("rewardRecordViews"));

            List<UserReward> rewardsList = new ArrayList<>();
            for (int i = 0; i < jRecords.length(); i++) {
                JSONObject jRecord = (JSONObject) jRecords.get(i);
                String giverName = jRecord.getString("giverName");
                String note = jRecord.getString("note");
                int amount = jRecord.getInt("amount");
                String awardDate = jRecord.getString("awardDate");

                UserReward reward = new UserReward(giverName, note, amount, awardDate.substring(0, 10).replace('-', '/'));
                rewardsList.add(reward);
            }
            UserProfile user = new UserProfile(jUser.getString("firstName"),
                    jUser.getString("lastName"), jUser.getString("userName"), jUser.getString("department"),
                    jUser.getString("story"), jUser.getString("position"), jUser.getString("password"),
                    jUser.getString("location"), jUser.getString("imageBytes"), jUser.getInt("remainingPointsToAward"),
                    rewardsList);
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.openViewProfileActivity(user);
                }
            });

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

