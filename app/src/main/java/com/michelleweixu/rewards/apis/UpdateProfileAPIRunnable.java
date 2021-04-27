package com.michelleweixu.rewards.apis;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.michelleweixu.rewards.CreateProfileActivity;
import com.michelleweixu.rewards.EditProfileActivity;
import com.michelleweixu.rewards.ViewProfileActivity;
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

import static java.net.HttpURLConnection.HTTP_OK;

public class UpdateProfileAPIRunnable implements Runnable {
    private static final String TAG = "UpdateProfileAPIRunnable";
    private static final String BASE_URL = "http://christopherhield.org/api/";
    private EditProfileActivity act;
    int remainingPointsToAward;
    String firstName, lastName, username, department, story,
            position, password, location, imageString64, apiKey;


    public UpdateProfileAPIRunnable(EditProfileActivity act, String firstName, String lastName,
                                    String username, String department, String story, String position,
                                    String password, int remainingPointsToAward, String location,
                                    String imageString64, String apiKey) {
        this.act = act;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = password;
        this.remainingPointsToAward = remainingPointsToAward;
        this.location = location;
        this.apiKey = apiKey;
        this.imageString64 = imageString64;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String endpointURL = BASE_URL + "Profile/UpdateProfile?firstName=" + firstName
                    + "&lastName=" + lastName + "&userName=" + username + "&department="
                    + department + "&story=" + story +  "&position=" + position + "&password=" +
                    password + "&location=" + location;

            Uri.Builder buildURL = Uri.parse(endpointURL).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(imageString64);
            out.close();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();
            if (responseCode == HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                JSONObject jUser = new JSONObject(result.toString());
                JSONArray jRecords = new JSONArray(jUser.getString("rewardRecordViews"));

                List<UserReward> rewardsList = new ArrayList<>();
                for (int i = 0; i < jRecords.length(); i++) {
                    JSONObject jRecord = (JSONObject) jRecords.get(i);
                    String giverName = jRecord.getString("giverName");
                    String note = jRecord.getString("note");
                    int amount = jRecord.getInt("amount");
                    String awardDate = jRecord.getString("awardDate");

                    UserReward reward = new UserReward(giverName, note, amount, awardDate);
                    rewardsList.add(reward);
                }

                UserProfile updatedUser = new UserProfile(jUser.getString("firstName"),
                        jUser.getString("lastName"), jUser.getString("userName"), jUser.getString("department"),
                        jUser.getString("story"), jUser.getString("position"), jUser.getString("password"),
                        jUser.getString("location"), jUser.getString("imageBytes"), jUser.getInt("remainingPointsToAward"),
                        rewardsList);

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.openViewProfileActivity(updatedUser);
                    }
                });

            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }
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
}
