package com.sharkBytesLab.nocostudio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sharkBytesLab.nocostudio.Fragments.InfoFragment;
import com.sharkBytesLab.nocostudio.Fragments.LibraryFragment;
import com.sharkBytesLab.nocostudio.Fragments.MusicFragment;
import com.sharkBytesLab.nocostudio.Fragments.SettingsFragment;
import com.sharkBytesLab.nocostudio.Fragments.WallpaperFragment;
import com.sharkBytesLab.nocostudio.Misc.MyMediaPlayer;
import com.sharkBytesLab.nocostudio.Misc.RateUsDialog;
import com.sharkBytesLab.nocostudio.databinding.ActivityMainBinding;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int frag = 3;
    private int versionCode;
    private FirebaseRemoteConfig remoteConfig;
    private FirebaseAnalytics firebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppLovinSdk.getInstance( this ).setMediationProvider( "max" );
        AppLovinSdk.initializeSdk( this, new AppLovinSdk.SdkInitializationListener() {
            @Override
            public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
            {

            }
        } );

        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                frag = extras.getInt("frag");
                Log.d("Fragment in Main: ",String.valueOf(frag));
            }

            internet();

            firebaseAnalytics = FirebaseAnalytics.getInstance(this);

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });


        }catch (Exception e) {
            e.printStackTrace();
        }



        switch (frag)
        {
            case 1:
                replaceFragment(new LibraryFragment());
                binding.bottomNavigationView.setSelectedItemId(R.id.library);
                break;

            case 2:
                replaceFragment(new WallpaperFragment());
                binding.bottomNavigationView.setSelectedItemId(R.id.notify);
                break;

            case 3:
                replaceFragment(new MusicFragment());
                binding.bottomNavigationView.setSelectedItemId(R.id.play);
                break;

            case 4:
                replaceFragment(new InfoFragment());
                binding.bottomNavigationView.setSelectedItemId(R.id.info);
                break;
//
            case 5:
                replaceFragment(new SettingsFragment());
                binding.bottomNavigationView.setSelectedItemId(R.id.setting);
                break;

        }



        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {




                switch (item.getItemId())

                {
                    case R.id.library:

                        replaceFragment(new LibraryFragment());
                        break;

                    case R.id.play:


                        break;
//
                    case R.id.info:

                        replaceFragment(new InfoFragment());
                        break;
//
//
                    case R.id.notify:

                        replaceFragment(new WallpaperFragment());
                        break;
//
                    case R.id.setting:

                        replaceFragment(new SettingsFragment());
                        break;
                }
                return true;
            }


        });




        versionCode = getCurrentVersionCode();
        remoteConfig = FirebaseRemoteConfig.getInstance();

        checkUpdate();
        maintenanceBreakUpdate();
        rateUsDialogCheck();



        binding.music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.bottomNavigationView.setSelectedItemId(R.id.play);
                replaceFragment(new MusicFragment());

            }
        });





    }


    private void internet()
    {

        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable())
        {

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.no_internet_dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

            Button tryButton = dialog.findViewById(R.id.internetTryAgain);
            tryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    recreate();

                }
            });

            dialog.show();
        }

    }


    private int getCurrentVersionCode()
    {
        PackageInfo packageInfo = null;
        try{
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

        }catch(Exception e)
        {
            Toast.makeText(this, "Application error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return packageInfo.versionCode;
    }


    private void replaceFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.mainFrameLayout, fragment);
        transaction.commit();

    }

    private void checkUpdate() {

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(5).build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                if(task.isSuccessful())
                {
                    final String new_version_code = remoteConfig.getString("version_ns");
                    if(Integer.parseInt(new_version_code) > getCurrentVersionCode())
                    {
                        showUpdateDialog();

                    }

                }



            }
        });

    }

    private void showUpdateDialog()
    {

        Dialog dialog;
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.update_app);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.update_dialog_bg));

        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.show();

        Button update = dialog.findViewById(R.id.update_app);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                    Intent in = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(in);

                }catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void maintenanceBreakUpdate() {

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(5).build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                if(task.isSuccessful())
                {
                    final String maintenance_break_code = remoteConfig.getString("maintenance_ns");
                    if(Integer.parseInt(maintenance_break_code) > 0)
                    {
                        maintenanceDialog();

                    }

                }



            }
        });

    }

    private void maintenanceDialog()
    {
        Dialog dialog;
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.maintenance_break);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.maintenance_dialog_bg));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.show();

    }


    private void rateUsDialogCheck() {

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(5).build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.fetchAndActivate().addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                if(task.isSuccessful())
                {
                    final String rate_us = remoteConfig.getString("rate_ns");

                    if(Integer.parseInt(rate_us) > 0)
                    {
                        rateUsDialog();

                    }

                }



            }
        });

    }


    private void rateUsDialog()
    {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String todaysDate = year + "" + month + "" + day;


        SharedPreferences sharedPreferences = getSharedPreferences("RATE", Context.MODE_PRIVATE);
        boolean currentDay = sharedPreferences.getBoolean(todaysDate, false);

        if(!currentDay)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(todaysDate, true);
            editor.apply();
            RateUsDialog rateUsDialog = new RateUsDialog(MainActivity.this);
            rateUsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            rateUsDialog.setCancelable(false);
            rateUsDialog.show();
        }


    }



}