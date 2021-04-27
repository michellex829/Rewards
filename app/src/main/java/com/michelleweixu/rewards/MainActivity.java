package com.michelleweixu.rewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.michelleweixu.rewards.apis.GetStudentApiKeyRunnable;
import com.michelleweixu.rewards.apis.LoginAPIRunnable;
import com.michelleweixu.rewards.userdata.UserProfile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

// string -> points a list of characters String name = "Michelle Xu"
// string_buffer -> input stream and output _. a buffer for characters
// string_builder-> "", sb.toString()

// interface doesn't have concrete implementations, needs to be implemented
// abstract class can be extended (doesn't have data?); could have concrete methods

// array and arrayList
// arrayList automatically resizes.

// Perforce

// JUnit

public class MainActivity extends AppCompatActivity {
    private ImageView background;
    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private MyProjectSharedPreference myPrefs;
    private LocationManager locationManager;
    Geocoder geocoder;
    private Criteria criteria;
    private String finalLocation;
    private ProgressBar progressBar;
    private static int CREATE_PROFILE_CODE = 123;
    List<String> studentInfo;

    private TextView name;
    private TextView studentID;
    private TextView email;
    private TextView APIKey;

    private TextView pName;
    private TextView pWord;

    String studentFirstName = "", studentLastName = "", studentEmail = "", studentSID = "", UserAPIKey,
            personName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF781F")));

        background=findViewById(R.id.background);
        background.setAlpha((float)0.3);
        progressBar = findViewById(R.id.progressBar);
        // the first time the app runs, it should ask for location permission (before asking for the API Key)
        // Request API Key (Displayed the first time the app is run, or if the API Key is cleared)
        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        // use gps for location
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else {
            setLocation();
        }

        name      = findViewById(R.id.PersonFirstName);
        studentID = findViewById(R.id.PersonLastName);
        email     = findViewById(R.id.EmailAddress);
        APIKey    = findViewById(R.id.PersonID);
        pName = findViewById(R.id.PersonName);
        pWord = findViewById(R.id.Password);

        myPrefs = new MyProjectSharedPreference(this);
        UserAPIKey = myPrefs.getValue("apiKey");
        personName = myPrefs.getValue("username");
        password = myPrefs.getValue("password");

        pName.setText(personName);
        pWord.setText(password);

        if (UserAPIKey.trim().equals("null") | UserAPIKey.trim().isEmpty())
            requestUserInfo();
    }

    @SuppressLint("MissingPermission")
    private void setLocation() {

        String bestProvider = locationManager.getBestProvider(criteria, true);

        Location currentLocation = null;
        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        if (currentLocation != null) {
            try {
                List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                getCityState(addresses);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            finalLocation = "Location unavailable";
        }
    }

    private void getCityState(List<Address> addresses) {
        if (addresses.size() == 0) {
            finalLocation = "Location unavailable";
        }
        else {
            Address add = addresses.get(0);
            String city = String.format("%s", (add.getLocality() == null ? "" : add.getLocality()));
            String state = String.format("%s", (add.getAdminArea() == null ? "" : add.getAdminArea()));
            finalLocation = city + ", " + state;
        }
    }

    public void requestUserInfo() {   // Dialog with a layout
        // Inflate the dialog's layout
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams")
        final View view = inflater.inflate(R.layout.request_apikey_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("To continue, you need to request an API Key");
        builder.setTitle("API Key Needed");
        builder.setIcon(R.mipmap.logo);

        // Set the inflated view to be the builder's view
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText fn = view.findViewById(R.id.PersonFirstName);
                EditText ln = view.findViewById(R.id.PersonLastName);
                EditText em = view.findViewById(R.id.EmailAddress);
                EditText sid = view.findViewById(R.id.PersonID);

                if (fn.getText().length() != 0 && ln.getText().length() != 0 &&
                        em.getText().length() != 0 && sid.getText().length() != 0) {
                    studentFirstName = fn.getText().toString();
                    studentLastName = ln.getText().toString();
                    studentEmail = em.getText().toString(); // TODO: must contain "@" and end with .edu
                    studentSID = sid.getText().toString();

                    studentInfo = Arrays.asList(studentFirstName, studentLastName, studentEmail, studentSID);
                    requestAPIKey();
                    progressBar.setVisibility(View.VISIBLE);
                }
                else { // TODO: error handle when any of the values is null
                    invalidAPIRequestToast();
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(MainActivity.this, "You changed your mind!", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void invalidAPIRequestToast() {
        Toast.makeText(MainActivity.this, "Please fill all fields to proceed.\nEmail must contain “@” and end with “.edu”", Toast.LENGTH_LONG).show();
        requestUserInfo();
    }

    public void invalidApiCall(String message) {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(MainActivity.this, "Oops, API Key request failed." +
                message + "Please modify and try again.", Toast.LENGTH_LONG).show();
    }

    public void displayAPIKeyReceivedAndStoredDialog(String apiKey) {
            progressBar.setVisibility(View.INVISIBLE);
            this.UserAPIKey = apiKey;
            // Simple Ok dialog - no view used.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setIcon(R.mipmap.logo);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });
            String userInfo = "Name: " + studentFirstName + ' ' + studentLastName + '\n' + "Student ID: " +
                    studentSID + '\n' + "EMail: " + studentEmail + '\n' + "API Key: " + apiKey;
            builder.setMessage(userInfo);
            builder.setTitle("API Key Received And Stored");

            AlertDialog dialog = builder.create();
            dialog.show();
    }

    private void requestAPIKey() {
        GetStudentApiKeyRunnable getApiKeyRunnable = new GetStudentApiKeyRunnable(this, studentFirstName, studentLastName, studentEmail, studentSID);
        new Thread(getApiKeyRunnable).start();
    }

    public void clearSavedAPIKey(View v) {
        if (!UserAPIKey.trim().isEmpty()) {
            myPrefs.removeValue("apiKey");
            Toast.makeText(this, "API Key is cleared!", Toast.LENGTH_LONG).show();
            requestUserInfo();
        } else {
            Toast.makeText(this, "API Key was not saved!", Toast.LENGTH_LONG).show();
            requestUserInfo();
        }
    }

    public void saveSharedPreferences(String apiKey) {
        myPrefs.save("apiKey", apiKey);
        myPrefs.save("email", studentEmail);
        myPrefs.save("firstName", studentFirstName);
        myPrefs.save("lastName", studentLastName);
        myPrefs.save("sid", studentSID);
    }

    public void saveUsernamePassword(View v) {
        // TODO: add error handling for trying to save empty input
        personName = pName.getText().toString();
        password = pWord.getText().toString();
        myPrefs.save("username", personName);
        myPrefs.save("password", password);
    }

    public void openCreateProfileActivity(View v) {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        intent.putExtra("apiKey", UserAPIKey);
        intent.putExtra("location", finalLocation);
        startActivityForResult(intent, CREATE_PROFILE_CODE);
    }

    public void openViewProfileActivity(UserProfile user) {
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra("apiKey", UserAPIKey);
        intent.putExtra("user", user);
        startActivityForResult(intent, CREATE_PROFILE_CODE);
    }

    public void login(View v) {
        personName = pName.getText().toString();
        password = pWord.getText().toString();
        progressBar.setVisibility(View.VISIBLE);
        LoginAPIRunnable loginAPIRunnable = new LoginAPIRunnable(this, personName, password, UserAPIKey);
        new Thread(loginAPIRunnable).start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("username", pName.getText().toString());
        outState.putString("password", pWord.getText().toString().trim());
        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);
        pName.setText(savedInstanceState.getString("username"));
        pWord.setText(savedInstanceState.getString("password"));
    }

}