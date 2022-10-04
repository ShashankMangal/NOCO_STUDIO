package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sharkBytesLab.nocostudio.databinding.ActivityPlayerScreenBinding;

public class PlayerScreen extends AppCompatActivity
{
    private ActivityPlayerScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}