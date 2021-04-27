package com.michelleweixu.rewards.apis;

import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.michelleweixu.rewards.AddRewardActivity;
import com.michelleweixu.rewards.CreateProfileActivity;
import com.michelleweixu.rewards.LeaderboardActivity;
import com.michelleweixu.rewards.userdata.UserProfile;
import com.michelleweixu.rewards.userdata.UserReward;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

public class GetAllProfilesAPIRunnable implements Runnable {
    private static final String TAG = "CreateProfileAPIRunnable";
    private static final String BASE_URL = "http://christopherhield.org/api/";
    private static int VIEW_PROFILE_CODE = 456;
    private LeaderboardActivity act;
    private AddRewardActivity addAct;
    private String apiKey;
    int remainingPointsToAward;
    String firstName, lastName, username, department, story,
            position, password, location, imageString64;


    public GetAllProfilesAPIRunnable(String apiKey, LeaderboardActivity act) {
        this.apiKey = apiKey;
        this.act = act;
    }

    public GetAllProfilesAPIRunnable(String apiKey, AddRewardActivity act, LeaderboardActivity leaderAct) {
        this.apiKey = apiKey;
        this.addAct = act;
    }

    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String endpointURL = BASE_URL + "Profile/GetAllProfiles";
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
            JSONArray jArray = new JSONArray(s);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jUser = (JSONObject) jArray.get(i);

                JSONArray jRecords = new JSONArray(jUser.getString("rewardRecordViews"));

                List<UserReward> rewardsList = new ArrayList<>();
                for (int j = 0; j < jRecords.length(); j++) {
                    JSONObject jRecord = (JSONObject) jRecords.get(j);
                    String giverName = jRecord.getString("giverName");
                    String note = jRecord.getString("note");
                    int amount = jRecord.getInt("amount");
                    String awardDate = jRecord.getString("awardDate");

                    UserReward reward = new UserReward(giverName, note, amount, awardDate);
                    rewardsList.add(reward);
                }

                UserProfile user = new UserProfile(jUser.getString("firstName"),
                        jUser.getString("lastName"), jUser.getString("userName"),
                        jUser.getString("department"), jUser.getString("story"),
                        jUser.getString("position"), "",""
                        , jUser.getString("imageBytes"),1000, rewardsList);

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.addUserToList(user);
                    }
                });

            }
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                act.progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }
}
