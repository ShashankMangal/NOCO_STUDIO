package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sharkBytesLab.nocostudio.MainActivity;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivitySetWallpaperScreenBinding;

import java.io.IOException;

public class SetWallpaperScreen extends AppCompatActivity {

    private ActivitySetWallpaperScreenBinding binding;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivitySetWallpaperScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        intent = getIntent();
        String url = intent.getStringExtra("image");
        Glide.with(getApplicationContext()).load(url).into(binding.finalImage);


        binding.setWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Bitmap bitmap=((BitmapDrawable)binding.finalImage.getDrawable()).getBitmap();
                    wallpaperManager.setBitmap(bitmap);
                    Toast.makeText(SetWallpaperScreen.this, "Wallpaper Changed.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SetWallpaperScreen.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



}