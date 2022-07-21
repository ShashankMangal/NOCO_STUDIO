package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;


import com.sharkBytesLab.nocostudio.MainActivity;
import com.sharkBytesLab.nocostudio.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        startThread();


    }

    private void startThread() {

        Thread thread = new Thread(){
            public void run(){

                try{
                    sleep(2000);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {

                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        intent.putExtra("frag",3);
                        startActivity(intent);

                }
            }
        };
        thread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();

        startThread();

    }
}