package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.sharkBytesLab.nocostudio.MainActivity;
import com.sharkBytesLab.nocostudio.databinding.ActivityAllPerksScreenBinding;

public class AllPerksScreen extends AppCompatActivity
{
    private ActivityAllPerksScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllPerksScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();

    }

    private void setListeners()
    {

        binding.perkImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("frag",3);
                startActivity(intent);

            }
        });
        binding.perk2CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AllPerksScreen.this, "Coming Soon !", Toast.LENGTH_SHORT).show();
            }
        });

        binding.perk1CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Perk1Screen.class));
            }
        });
    }
}