package com.michelleweixu.rewards.apis;

import android.net.Uri;
import com.michelleweixu.rewards.AddRewardActivity;
import com.michelleweixu.rewards.EditProfileActivity;
import com.michelleweixu.rewards.userdata.UserProfile;
import com.michelleweixu.rewards.userdata.UserReward;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_CREATED;

public class AddRewardAPIRunnable implements Runnable {
    private static final String BASE_URL = "http://christopherhield.org/api/";
    private AddRewardActivity act;
    UserProfile user, loggedInUser;
    String apiKey, comment;
    int points;

    public AddRewardAPIRunnable(AddRewardActivity act, UserProfile user, UserProfile loggedInUser,
                                String apiKey, int points, String comment) {
        this.act = act;
        this.user = user;
        this.loggedInUser = loggedInUser;
        this.apiKey = apiKey;
        this.points = points;
        this.comment = comment;
    }

    @Override
    public void run() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String endpointURL = BASE_URL + "Rewards/AddRewardRecord?receiverUser=" + user.username
                    + "&giverUser=" + loggedInUser.username + "&giverName=" + loggedInUser.firstName + " "
                    + loggedInUser.lastName+ "&amount=" + points + "&note=" + comment;

            Uri.Builder buildURL = Uri.parse(endpointURL).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            int responseCode = connection.getResponseCode();

            StringBuilder result = new StringBuilder();
            if (responseCode == HTTP_CREATED) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                process(result.toString());
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        act.dialogueForInvalidInput("Add reward request failed. " + result.toString());
                    }
                });
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

                }
            }
        }
    }

    private void process(String s) {
        try {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    act.doneClicked();
                }
            });

        } catch (Exception e) {

        }
    }
}