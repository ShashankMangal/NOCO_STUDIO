package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Window;
import android.view.WindowManager;


import com.sharkBytesLab.nocostudio.MainActivity;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.splashTheme));
        }

        preferences = this.getSharedPreferences("splash", MODE_PRIVATE);
        editor = preferences.edit();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if(preferences.getBoolean("isMain", false))
                {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
                else
                {
                    editor.putBoolean("isMain", true);
                    editor.apply();

                    TaskStackBuilder.create(SplashScreen.this)
                            .addNextIntentWithParentStack(new Intent(SplashScreen.this, MainActivity.class))
                            .addNextIntent(new Intent(SplashScreen.this, IntroScreen.class))
                            .startActivities();
                }
            }
        }, 2000);


    }



}