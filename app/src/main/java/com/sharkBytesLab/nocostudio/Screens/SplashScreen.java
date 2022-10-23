package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import android.view.View;
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

//        if (Build.VERSION.SDK_INT >= 21) {
//            Window window = this.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(this.getResources().getColor(R.color.splashTheme));
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            Window window = getWindow();
            WindowManager.LayoutParams winParams = window.getAttributes();
            winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            window.setAttributes(winParams);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
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
        }, 4000);


    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


}