package com.michelleweixu.rewards.apis;

import com.michelleweixu.rewards.MainActivity;

import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public class GetStudentApiKeyRunnable implements Runnable {
    private static final String TAG = "GetStudentApiKeyRunnable";
    private static final String BASE_URL = "http://christopherhield.org/api/";
    private MainActivity mainActivity;
    private List<String> studentInfo;
    String firstName, lastName, email, sid, api;


    public GetStudentApiKeyRunnable(MainActivity mainActivity, String fn, String ln, String email, String sid) {
        this.mainActivity = mainActivity;
        this.studentInfo = studentInfo;
        this.firstName = fn;
        this.lastName = ln;
        this.email = email;
        this.sid = sid;
        this.api = null;
    }


    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String endpointURL = BASE_URL + "Profile/GetStudentApiKey?firstName=" + firstName
                    + "&lastName=" + lastName + "&studentId=" + sid
                    + "&email=" + email;

            Uri.Builder buildURL = Uri.parse(endpointURL).buildUpon();
            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
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
                        mainActivity.requestUserInfo();
                    }
                });
            }
            JSONObject jResult = new JSONObject(result.toString());
            String apiKey = jResult.getString("apiKey");

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.displayAPIKeyReceivedAndStoredDialog(apiKey);
                    mainActivity.saveSharedPreferences(apiKey);
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
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
    }
}