package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sharkBytesLab.nocostudio.MainActivity;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivityTermsScreenBinding;

public class TermsScreen extends AppCompatActivity {

    private ActivityTermsScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {

            binding.termsWebView.getSettings().setJavaScriptEnabled(true);
            binding.termsWebView.loadUrl("file:///android_asset/terms.html");
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.termsBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(TermsScreen.this, MainActivity.class);
                intent.putExtra("frag",5);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(TermsScreen.this, MainActivity.class);
        intent.putExtra("frag",5);
        startActivity(intent);
        finish();


    }


}