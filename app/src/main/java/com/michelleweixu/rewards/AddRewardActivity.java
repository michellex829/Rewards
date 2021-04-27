package com.michelleweixu.rewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.michelleweixu.rewards.apis.AddRewardAPIRunnable;
import com.michelleweixu.rewards.userdata.UserProfile;

public class AddRewardActivity extends AppCompatActivity {
    private static final int MAX_LEN = 80;
    UserProfile user;
    UserProfile loggedInUser;
    private TextView commentTitle;
    private TextView fullName;
    private TextView pointsAwarded;
    private ImageView imageInAdd;
    private TextView department;
    private TextView position;
    private TextView story;

    private EditText pointsToSend, comment;
    private String apiKey, imageString64;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reward);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF781F")));
        Intent intent = getIntent();

        fullName = findViewById(R.id.fullname);
        pointsAwarded = findViewById(R.id.points);
        imageInAdd = findViewById(R.id.imageInAdd);
        department = findViewById(R.id.departmentInAdd);
        position = findViewById(R.id.posistionInAdd);
        story = findViewById(R.id.storyInAdd);
        pointsToSend = findViewById(R.id.pointsinAdd);
        comment = findViewById(R.id.commentInAdd);
        commentTitle = findViewById(R.id.commentTitleAdd);
        commentTitle.setText("Comment: (0 of 80)");
        setupEditText();

        if (intent.hasExtra("apiKey"))
            apiKey = (String) intent.getSerializableExtra("apiKey");
        if (intent.hasExtra("user")) {
            user = (UserProfile) intent.getSerializableExtra("user");
            setUserData();
            getSupportActionBar().setTitle(user.firstName + " " + user.lastName);
            comment.setHint("Add comment for " + user.firstName);
        }
        if (intent.hasExtra("loggedInUser"))
            loggedInUser = (UserProfile) intent.getSerializableExtra("loggedInUser");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // standard for options menu
        getMenuInflater().inflate(R.menu.save_in_add_menu, menu);
        return true;
    }

    public void setUserData() {
        fullName.setText(user.firstName + " " + user.lastName);
        pointsAwarded.setText(String.valueOf(user.pointsAwarded));
        department.setText(user.department);
        position.setText(user.position);
        story.setText(user.story);
        imageString64 = user.imageString64;
        textToImage();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  R.id.save_in_add_reward) {
            confirmAddRewardDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupEditText() {
        comment.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        comment.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        // This one executes upon completion of typing a character
                        int len = s.toString().length();
                        commentTitle.setText("Comment: (" + len + " of 80)");
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                        // Nothing to do here
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // Nothing to do here
                    }
                });
    }

    public void displayErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.mipmap.logo);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setMessage(message);
        builder.setTitle("Please try again");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void confirmAddRewardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.icon);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startAddRewardAPIRunnable();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setMessage("Add rewards for " + user.firstName + " " + user.lastName);
        builder.setTitle("Add Rewards Points?");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void startAddRewardAPIRunnable() {
        try {
            if(comment.getText().toString() != null && !comment.getText().toString().trim().isEmpty()) {
                AddRewardAPIRunnable runnable = new AddRewardAPIRunnable(this, user, loggedInUser,
                        apiKey, Integer.parseInt(pointsToSend.getText().toString()), comment.getText().toString());
                new Thread(runnable).start();
            }
            else {
                dialogueForInvalidInput("Please enter points to send and comment.\nAmount must be a positive integer value, 11 digit maximum.\nComment must be 80 characters maximum.");
            }
        } catch (Exception e) {
            dialogueForInvalidInput("Please enter points to send and comment.\nAmount must be a positive integer value, 11 digit maximum.\nComment must be 80 characters maximum.");
            e.printStackTrace();
        }
    }

    public void dialogueForInvalidInput(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.logo);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setMessage(message);
        builder.setTitle("Please try again");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void doneClicked() {
        Intent data = new Intent(); // Used to hold results data to be returned to original activity
        data.putExtra("REWARD_ADDED", true);
        data.putExtra("POINTS", Integer.parseInt(pointsToSend.getText().toString()));
        setResult(RESULT_OK, data);
        finish(); // This closes the current activity, returning us to the original activity
    }

    @Override
    public void onBackPressed() {
        // Pressing the back arrow closes the current activity, returning to the original activity
        Intent data = new Intent();
        data.putExtra("REWARD_ADDED", true);
        setResult(RESULT_OK, data);
        super.onBackPressed();
    }

    public void textToImage() {
        if (imageString64 == null) return;
        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        imageInAdd.setImageBitmap(bitmap);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("pointsToSend", pointsToSend.getText().toString().trim());
        outState.putString("comment", comment.getText().toString());
        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);
        pointsToSend.setText(savedInstanceState.getString("pointsToSend"));
        comment.setText(savedInstanceState.getString("comment"));
    }
}