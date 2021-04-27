package com.michelleweixu.rewards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 2000;
    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF781F")));

        background=findViewById(R.id.backgroundSplash);
        background.setAlpha((float)0.3);

        // Handler is used to execute something in the future
        new Handler().postDelayed(() -> {
            // This method will be executed once the timer is over
            // Start your app main activity
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            // close this activity
            finish();
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onBackPressed() {
    }
}
