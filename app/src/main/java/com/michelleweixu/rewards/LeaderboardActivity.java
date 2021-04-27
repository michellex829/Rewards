package com.michelleweixu.rewards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.michelleweixu.rewards.apis.GetAllProfilesAPIRunnable;
import com.michelleweixu.rewards.userdata.UserProfile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity implements  View.OnClickListener {
    private final List<UserProfile> userList = new ArrayList<>();
    private RecyclerView recyclerView;
    UserProfileAdapter mAdapter;
    UserProfile loggedInUser;
    private String apiKey;
    public UserProfile user;
    public ProgressBar progressBar;
    private static final int ADD_REWARD_CODE = 567;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF781F")));
        getSupportActionBar().setTitle("Leaderboard");
        Intent intent = getIntent();
        progressBar = findViewById(R.id.progressBar5);
        progressBar.setVisibility(View.VISIBLE);
        if (intent.hasExtra("apiKey"))
            apiKey = (String) intent.getSerializableExtra("apiKey");
        if (intent.hasExtra("loggedInUser"))
            loggedInUser = (UserProfile) intent.getSerializableExtra("loggedInUser");
        mAdapter = new UserProfileAdapter(userList, this, loggedInUser);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GetAllProfilesAPIRunnable getAllProfilesAPIRunnable = new GetAllProfilesAPIRunnable(apiKey, this);
        new Thread(getAllProfilesAPIRunnable).start();

    }

    public void openAddRewardActivity(View v, UserProfile user) {
        progressBar.setVisibility(View.INVISIBLE);
        if (! loggedInUser.equals(user)) {
            Intent intent = new Intent(this, AddRewardActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("apiKey", apiKey);
            intent.putExtra("loggedInUser", loggedInUser);
            startActivityForResult(intent, ADD_REWARD_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressBar.setVisibility(View.VISIBLE);
        if (requestCode == ADD_REWARD_CODE) {
            if (resultCode == RESULT_OK) {
                Boolean text = data.getBooleanExtra("REWARD_ADDED", false);

                if (data.hasExtra("POINTS"))
                    loggedInUser.remainingPointsToAward -= data.getIntExtra("POINTS", 0);

                if (text) {
                    userList.clear();
                    GetAllProfilesAPIRunnable getAllProfilesAPIRunnable = new GetAllProfilesAPIRunnable(apiKey, this);
                    new Thread(getAllProfilesAPIRunnable).start();
                    return;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent(); // Used to hold results data to be returned to original activity
        data.putExtra("POINTS", loggedInUser.remainingPointsToAward);
        setResult(RESULT_OK, data);
        finish(); // This closes the current activity, returning us to the original activity
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void onClick(View v) {
        int position = recyclerView.getChildLayoutPosition(v);
        user = userList.get(position);
        openAddRewardActivity(v, user);
    }

    public void addUserToList(UserProfile user) {
        this.userList.add(user);
        Collections.sort(userList);
        mAdapter.notifyDataSetChanged();
    }
}