package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.sharkBytesLab.nocostudio.MainActivity;
import com.sharkBytesLab.nocostudio.Perks.Perk1Screen;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivityAllPerksScreenBinding;

import java.util.concurrent.TimeUnit;

public class AllPerksScreen extends AppCompatActivity
{
    private ActivityAllPerksScreenBinding binding;
    private MaxInterstitialAd interstitialAd;
    private int retry=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllPerksScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createInterstitialAd();
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

        binding.perk1CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(interstitialAd.isReady())
                {
                    interstitialAd.showAd();
                }
                startActivity(new Intent(getApplicationContext(), Perk1Screen.class));
            }
        });

        binding.perk2CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AllPerksScreen.this, "Coming Soon !", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void createInterstitialAd()
    {
        interstitialAd = new MaxInterstitialAd(getResources().getString(R.string.applovin_inter_adId_forPerk1), this);
        MaxAdListener adListener = new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                Log.e("Inter Ad", "Loaded");

            }

            @Override
            public void onAdDisplayed(MaxAd ad) {
                Log.e("Inter Ad", "Displayed");

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                retry++;
                long delay = TimeUnit.SECONDS.toMillis((long)Math.pow(2, Math.min(6, retry)));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        interstitialAd.loadAd();
                    }
                }, delay);
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        };
        interstitialAd.setListener(adListener);
        interstitialAd.loadAd();
    }
}