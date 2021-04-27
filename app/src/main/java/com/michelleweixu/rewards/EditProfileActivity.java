package com.michelleweixu.rewards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.michelleweixu.rewards.apis.UpdateProfileAPIRunnable;
import com.michelleweixu.rewards.userdata.UserProfile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import android.widget.ProgressBar;

public class EditProfileActivity extends AppCompatActivity {
    private static final int MAX_LEN = 360;
    public UserProfile user;
    private TextView uname, storyTitle;
    private EditText pword;
    private EditText fname;
    private EditText lname;
    private EditText dment;
    private EditText pos;
    private EditText sto;
    private ProgressBar progressBar;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String department;
    private String position;
    private String story;
    private String apiKey, storyTitleCount;

    private final int REQUEST_IMAGE_GALLERY = 1;
    private final int REQUEST_IMAGE_CAPTURE = 2;
    private final int REQUEST_IMAGE_CAPTURE_THUMB = 3;

    private File currentImageFile;
    private ImageView profileImageView;
    private String imageString64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF781F")));
        getSupportActionBar().setTitle("Edit Profile");

        progressBar = findViewById(R.id.progressBar3);
        profileImageView = findViewById(R.id.profileImage);
        uname = findViewById(R.id.edituserame);
        pword = findViewById(R.id.password);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        dment = findViewById(R.id.department);
        pos = findViewById(R.id.position);
        sto = findViewById(R.id.story);
        storyTitle = findViewById(R.id.storyTitleView);
        Intent intent = getIntent();
        setupEditText();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PERMISSION_GRANTED)
            requestPermission();
        if (intent.hasExtra("apiKey"))
            apiKey = (String) intent.getSerializableExtra("apiKey");
        if (intent.hasExtra("user")) {
            user = (UserProfile) intent.getSerializableExtra("user");
            setUserData();
        }
        if (! intent.hasExtra("user")) {
            profileImageView.setImageDrawable(getResources().getDrawable(R.mipmap.default_photo));
        }
    }

    public void setUserData() {
        uname.setText(user.username);
        pword.setText(user.password);
        fname.setText(user.firstName);
        lname.setText(user.lastName);
        dment.setText(user.department);
        pos.setText(user.position);
        sto.setText(user.story);
        imageString64 = user.imageString64;
        textToImage();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_IMAGE_CAPTURE_THUMB);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE_THUMB:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setupEditText() {

        sto.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(MAX_LEN) // Specifies a max text length
        });

        sto.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        // This one executes upon completion of typing a character
                        int len = s.toString().length();
                        storyTitle.setText("Your story: (" + len + " of 360)");
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


    public void textToImage() {
        if (imageString64 == null) return;

        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        profileImageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // standard for options menu
        getMenuInflater().inflate(R.menu.save_edit_profile_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==  R.id.save_in_edit) {
            UpdateProfile(null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void UpdateProfile(View v) {
        if (uname.getText().length() != 0 && pword.getText().length() != 0 &&
                fname.getText().length() != 0 && lname.getText().length() != 0 &&
                dment.getText().length() != 0 && pos.getText().length() != 0 &&
                sto.getText().length() != 0 && user.imageString64.length() != 0) {
            username = uname.getText().toString();
            password = pword.getText().toString();
            firstName = fname.getText().toString();
            lastName = lname.getText().toString();
            department = dment.getText().toString();
            position = pos.getText().toString(); // TODO: must contain "@" and end with .edu
            story = sto.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            UpdateProfileAPIRunnable updateProfileRunnable = new UpdateProfileAPIRunnable(this,
                    firstName, lastName, username, department, story, position,
                    password, 1000, user.location, imageString64, apiKey);
            new Thread(updateProfileRunnable).start();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        username = uname.getText().toString().trim();
        Intent intent = new Intent();
        if (username != null & ! username.isEmpty()) {
            builder.setTitle("Save Changes?");
            builder.setMessage("Your profile is not saved.\nDo you want to save changes?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    UpdateProfile(null);
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            Toast.makeText(this, "You didn't enter a username for your profile,\n\t\t\t\t\t\tnothing is saved! :( ", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    public void ProfilePictureDialog(View v) {
        // Simple Ok & Cancel dialog - no view used.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.mipmap.logo);

        builder.setPositiveButton("GALLERY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doGallery(v);
            }
        });
        builder.setNegativeButton("CAMERA", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doThumb(v);
            }
        });
        builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.setMessage("Take picture from:");
        builder.setTitle("Profile Picture");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void doCamera(View v) {
        try {
            currentImageFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoURI = FileProvider.getUriForFile(
                this, "com.example.android.fileprovider", currentImageFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    public void doThumb(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_THUMB);
    }

    public void doGallery(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                processFullCameraImage();
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE_THUMB && resultCode == RESULT_OK) {
            try {
                processCameraThumb(data.getExtras());
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "image+";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );
    }

    private void processFullCameraImage() {
        Uri selectedImage = Uri.fromFile(currentImageFile);
        profileImageView.setImageURI(selectedImage);
    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        profileImageView.setImageBitmap(selectedImage);
        toBase64();
    }

    private void processCameraThumb(Bundle extras) {
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        profileImageView.setImageBitmap(imageBitmap);
        profileImageView.setImageBitmap(imageBitmap);
        toBase64();
    }

    public void toBase64() {
        BitmapDrawable drawable = (BitmapDrawable) profileImageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos);
        byte[] byteArray = baos.toByteArray();
        imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static void makeCustomToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);

        TextView tv = new TextView(context);
        tv.setText(message);
        tv.setTextSize(18.0f);

        tv.setPadding(50, 25, 50, 25);
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.WHITE);
        toast.setView(tv);

        toast.show();
    }

    public void openViewProfileActivity(UserProfile user) {
        progressBar.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, ViewProfileActivity.class);
        intent.putExtra("apiKey", apiKey);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("password", pword.getText().toString().trim());
        outState.putString("firstname", fname.getText().toString());
        outState.putString("lastname", lname.getText().toString().trim());
        outState.putString("department", dment.getText().toString());
        outState.putString("position", pos.getText().toString().trim());
        outState.putString("story", sto.getText().toString());
        outState.putString("imageString64", imageString64);
        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);
        pword.setText(savedInstanceState.getString("password"));
        fname.setText(savedInstanceState.getString("firstname"));
        lname.setText(savedInstanceState.getString("lastname"));
        dment.setText(savedInstanceState.getString("department"));
        pos.setText(savedInstanceState.getString("position"));
        sto.setText(savedInstanceState.getString("story"));
        imageString64 = savedInstanceState.getString("imageString64");
        if (imageString64 == "") return;

        byte[] imageBytes = Base64.decode(imageString64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        profileImageView.setImageBitmap(bitmap);
    }
}
