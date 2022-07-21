package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sharkBytesLab.nocostudio.MainActivity;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivityPolicyScreenBinding;

public class PolicyScreen extends AppCompatActivity {

    private ActivityPolicyScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPolicyScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {

            binding.policyWebView.getSettings().setJavaScriptEnabled(true);
            binding.policyWebView.loadUrl("file:///android_asset/policy.html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.policyBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PolicyScreen.this, MainActivity.class);
                intent.putExtra("frag",5);
                startActivity(intent);
                finish();

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(PolicyScreen.this, MainActivity.class);
        intent.putExtra("frag",5);
        startActivity(intent);
        finish();

    }
}

