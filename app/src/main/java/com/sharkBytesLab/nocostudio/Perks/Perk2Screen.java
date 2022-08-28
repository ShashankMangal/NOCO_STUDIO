package com.sharkBytesLab.nocostudio.Perks;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sharkBytesLab.nocostudio.databinding.ActivityPerk2ScreenBinding;

public class Perk2Screen extends AppCompatActivity
{
    private ActivityPerk2ScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerk2ScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    }
}