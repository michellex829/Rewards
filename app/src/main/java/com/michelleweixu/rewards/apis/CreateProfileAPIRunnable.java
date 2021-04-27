package com.michelleweixu.rewards.apis;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.michelleweixu.rewards.CreateProfileActivity;
import com.michelleweixu.rewards.MainActivity;
import com.michelleweixu.rewards.ViewProfileActivity;
import com.michelleweixu.rewards.userdata.UserProfile;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

public class CreateProfileAPIRunnable implements Runnable {
    private static final String TAG = "CreateProfileAPIRunnable";
    private static final String BASE_URL = "http://christopherhield.org/api/";
    private static int VIEW_PROFILE_CODE = 456;
    private CreateProfileActivity act;
    int remainingPointsToAward;
    String firstName, lastName, username, department, story,
            position, password, location, imageString64, apiKey;


    public CreateProfileAPIRunnable(CreateProfileActivity act, String firstName, String lastName,
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
//    http://christopherhield.org/api/Profile/CreateProfile?firstName=John&lastName=Smith&userName=jsnith123&department=Finance
// &story=Tell other users about yourself...&position=Sr. Accountant
// &password=MyAwesomePassword123&remainingPointsToAward=1000&location=Chicago, IL

    @Override
    public void run() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image", imageString64);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String endpointURL = BASE_URL + "Profile/CreateProfile?firstName=" + firstName
                    + "&lastName=" + lastName + "&userName=" + username + "&department="
                    + department + "&story=" + story +  "&position=" + position + "&password=" +
                    password + "&remainingPointsToAward=" + 1000 + "&location=" + location;
// TODO: do not hardcode points and location
            Uri.Builder buildURL = Uri.parse(endpointURL).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(imageString64);
            out.close();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();
            if (responseCode == HTTP_CREATED) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                UserProfile newUser = new UserProfile( firstName, lastName, username, department, story,
                        position, password, location, imageString64,1000, null);

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.openViewProfileActivity(newUser);
                    }
                });
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.requestFailDialog(result.toString());
                    }
                });
            }

            // mainActivity.saveSharedPreferences(apiKey);
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
