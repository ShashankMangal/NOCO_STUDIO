package com.sharkBytesLab.nocostudio.Screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.icu.number.Scale;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.sharkBytesLab.nocostudio.MainActivity;
import com.sharkBytesLab.nocostudio.Models.InfoModel;
import com.sharkBytesLab.nocostudio.Models.PromotionWebViewModel;
import com.sharkBytesLab.nocostudio.Models.SliderModel;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivityDownloadSongScreenBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class DownloadSongScreen extends AppCompatActivity {


    private ActivityDownloadSongScreenBinding binding;
    private FirebaseFirestore firestore;
    private SliderModel model;
    private String songTitle;
    private String songImage;
    private String songLink;
    private String songId;
    private Boolean reward;
    private String videoLink;
    private InterstitialAd mInterstitialAd;
    private PromotionWebViewModel promoModel;
    private String promoUrl;
    private AdView mAdView;
    private InfoModel infoModel;
    private int retry = 0;
    private ArrayList<String> favLists;
    private boolean favListIsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadSongScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

//        createBannerAd();
//        createInterstitialAd();
        getSliderImages();
        getInfo();


        binding.downloadImage.setEnabled(false);
        binding.downloadImage.setAlpha(0.3F);
        long dur = TimeUnit.SECONDS.toMillis(41);
        favLists = new ArrayList<>();

        new CountDownTimer(dur, 1000) {

            @Override
            public void onTick(long l) {

                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d", TimeUnit.MILLISECONDS.toMinutes(l), TimeUnit.MILLISECONDS.toSeconds(l) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));

                binding.timerDownloadSong.setText("Preparing File : " + sDuration.toString());


            }

            @Override
            public void onFinish() {

                binding.timerDownloadSong.setVisibility(View.GONE);
                binding.skipTimer.setVisibility(View.GONE);
                binding.downloadImage.setEnabled(true);
                binding.downloadImage.setAlpha(1.0F);

            }
        }.start();

        promotionWebViewData();

        binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("https://play.google.com/store/search?q=pub%3AShark%20Bytes%20Lab&c=apps");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);

                try {
                    startActivity(i);
                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.downloadSongFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                if(!favListIsChecked)
                {
                    if(!favLists.contains(songTitle))
                    {
                        favLists.add(songTitle);
                    }
                    saveData();
                    binding.downloadSongFav.setImageResource(R.drawable.ic_round_favorite_24);
                    favListIsChecked = true;
                }
                else
                {
                    binding.downloadSongFav.setImageResource(R.drawable.ic_round_favorite_border_24);
                    favLists.remove(songTitle);
                    saveData();
                    favListIsChecked = false;
                }

            }
        });


        binding.checkoutYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/c/NoCopyrightSongsHindi"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");
                startActivity(intent);

            }
        });


        binding.downloadThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkPermission();

            }
        });

        binding.enquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("https://instagram.com/only_ncshindi?igshid=YmMyMTA2M2Y=");
                Intent i = new Intent(Intent.ACTION_VIEW, uri);

                try {
                    startActivity(i);
                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(promoUrl));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.google.android.youtube");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.telegramChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/NCSHindiChatStation"));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(DownloadSongScreen.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.shareAppDownloadSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareBody = "Hey, I'm using NOCO Studio songs for my social platforms without any copyright. Join using this link below." + " \n" +
                        "Download from Play Store\n" + "https://play.google.com/store/apps/details?id=" + getPackageName();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(intent);

            }
        });

        binding.downloadSongImageBack.setOnClickListener(v -> onBackPressed());

        binding.subscribeYt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/c/NoCopyrightSongsHindi"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.google.android.youtube");
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(DownloadSongScreen.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


        reward = false;

        binding.skipTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if ( mInterstitialAd !=null )
//                {
//                   mInterstitialAd.show(DownloadSongScreen.this);
//
//                   mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                       @Override
//                       public void onAdDismissedFullScreenContent() {
//                           super.onAdDismissedFullScreenContent();
//
//                           binding.timerDownloadSong.setVisibility(View.GONE);
//                           binding.skipTimer.setVisibility(View.GONE);
//                           binding.downloadImage.setEnabled(true);
//                           binding.downloadImage.setAlpha(1.0F);
//
//
//                       }
//                   });
//
//                }

                try {
//                    if (interstitialAd.isReady()) {
//                        interstitialAd.showAd();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


        firestore = FirebaseFirestore.getInstance();

        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                songTitle = extras.getString("title");
                songImage = extras.getString("image");
                songLink = extras.getString("link");
                songId = extras.getString("videoId");
                addToFavList(songTitle);

            }

//            ArrayList<SlideModel> slideModels2 = new ArrayList<>();
//
//            slideModels2.add(new SlideModel(songImage, ScaleTypes.FIT));
//
//            binding.imageSlider2.setImageList(slideModels2, ScaleTypes.FIT);

            Glide.with(getApplicationContext()).load(songImage).thumbnail(Glide.with(getApplicationContext()).load(R.drawable.spinner)).into(binding.songThumbnail);


        } catch (Exception e) {
            e.printStackTrace();
        }


        binding.songNameFlowingText.setText(songTitle);
        binding.songNameFlowingText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        binding.songNameFlowingText.setSelected(true);


        try {

        } catch (Exception e) {
            Log.d("Youtube Player Error : ", e.getMessage());
        }


        binding.ytSongPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    videoLink = "https://youtu.be/" + songId;

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoLink));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setPackage("com.google.android.youtube");
                    startActivity(intent);


                } catch (Exception e) {
                    Log.d("Error : ", e.getMessage());
                    Toast.makeText(DownloadSongScreen.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


        binding.downloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {

                    if (!reward) {

                        downloadCount();
                        reward = true;

                    }


                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(songLink));
                    startActivity(intent);

                } catch (Exception e) {
                    Log.d("Mega WebView Error : ", e.getMessage());
                }


            }
        });

    }

    private void showToast(String s) {
        MotionToast.Companion.createColorToast(this,
                s,
                "Click on SKIP TIMER",
                MotionToastStyle.INFO,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, R.font.helvetica_regular));

    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            saveImage();
                        } else {
                            Toast.makeText(DownloadSongScreen.this, "Please allow all permission!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();
    }

    private void addToFavList(String songTitle) {

        readData();
        if (favLists.contains(songTitle.toString()))
        {
            binding.downloadSongFav.setImageResource(R.drawable.ic_round_favorite_24);
            favListIsChecked = true;
        }
        else
        {
            binding.downloadSongFav.setImageResource(R.drawable.ic_round_favorite_border_24);
            favListIsChecked = false;
        }

    }

    private void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("fav", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favLists);
        editor.putString("favList", json);
        editor.apply();
    }

    private void readData()
    {

        SharedPreferences sharedPreference = getSharedPreferences("fav", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreference.getString("favList", null);
        Type type = new TypeToken<ArrayList<String>> (){}.getType();
        favLists = gson.fromJson(json, type);

        if(favLists == null)
        {
            favLists = new ArrayList<>();
        }

    }

    private void saveImage() {
        FileOutputStream fileOutputStream = null;
        File file = getDisc();

        if (!file.exists() && !file.mkdirs()) {
            file.mkdirs();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmsshhmmss");
        String date = simpleDateFormat.format(new Date());
        String name = "IMG" + date + ".jpg";
        String fileName = file.getAbsolutePath() + "/" + name;
        File new_file = new File(fileName);

        try {

            BitmapDrawable draw = (BitmapDrawable) binding.songThumbnail.getDrawable();
            Bitmap bitmap = draw.getBitmap();
            fileOutputStream = new FileOutputStream(new_file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            MotionToast.Companion.createColorToast(this,
                    "Thumbnail Saved In Gallery",
                    "Successfully",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular));
            fileOutputStream.flush();
            fileOutputStream.close();


        } catch (FileNotFoundException e) {
            MotionToast.Companion.createColorToast(this,
                    "ERROR ☹️ ",
                    e.getMessage().toString(),
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular));

        } catch (IOException e) {
            Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            MotionToast.Companion.createColorToast(this,
                    "ERROR ☹️ ",
                    e.getMessage().toString(),
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular));
        }

        refreshGallery(new_file);
    }

    void getInfo() {
        firestore.collection("DownloadCount").document("count").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                infoModel = documentSnapshot.toObject(InfoModel.class);

                assert infoModel != null;
                int count = infoModel.getSubs();
                binding.countAnimationTextView.setDecimalFormat(new DecimalFormat("###,###,###")).setAnimationDuration(5000).countAnimation(0, count);

            }
        });
    }

    private void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        getApplicationContext().sendBroadcast(intent);
    }


    private File getDisc() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(file, "NOCO Studio");
    }

    void getSliderImages() {
        firestore.collection("Slider").document("images").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                model = documentSnapshot.toObject(SliderModel.class);

                ArrayList<SlideModel> slideModels = new ArrayList<>();

                slideModels.add(new SlideModel("https://firebasestorage.googleapis.com/v0/b/rewardapp-1070c.appspot.com/o/noco%20new%20banner.jpeg?alt=media&token=7118afd6-a87d-41de-b66e-48a57b9552c4", ScaleTypes.FIT));
                slideModels.add(new SlideModel(model.getImage1(), ScaleTypes.FIT));
                slideModels.add(new SlideModel(model.getImage2(), ScaleTypes.FIT));
                slideModels.add(new SlideModel(model.getImage3(), ScaleTypes.FIT));
                slideModels.add(new SlideModel(model.getImage4(), ScaleTypes.FIT));
                slideModels.add(new SlideModel(model.getImage5(), ScaleTypes.FIT));


                binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);


            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.putExtra("frag",1);
//        startActivity(intent);
//
//    }

    private void downloadCount() {

        firestore.collection("DownloadCount").document("count").update("num", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void setAds() {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-5127713321341585/9330800154", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                        mInterstitialAd = interstitialAd;
                        Toast.makeText(DownloadSongScreen.this, "You can skip the timer.", Toast.LENGTH_SHORT).show();
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        Toast.makeText(DownloadSongScreen.this, "Error : No Video Ads Available.", Toast.LENGTH_LONG).show();
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void promotionWebViewData() {
        firestore.collection("Promotion").document("promotion").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                promoModel = documentSnapshot.toObject(PromotionWebViewModel.class);
                promoUrl = promoModel.getChannelUrl();
                binding.channelNameTv.setText(promoModel.getChannelName());
                binding.channelNameTv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                binding.channelNameTv.setSelected(true);
                Glide.with(getApplicationContext()).load(promoModel.getChannelImage()).thumbnail(Glide.with(getApplicationContext()).load(R.drawable.spinner)).into(binding.youtubeLogo);

            }
        });
    }

//    private void createBannerAd() {
//        adView = new MaxAdView(getResources().getString(R.string.applovin_banner_adId), this);
//        adView.setListener(new MaxAdViewAdListener() {
//            @Override
//            public void onAdExpanded(MaxAd ad) {
//                Log.d("Banner Ad", "Expanded");
//            }
//
//            @Override
//            public void onAdCollapsed(MaxAd ad) {
//
//            }
//
//            @Override
//            public void onAdLoaded(MaxAd ad) {
//                Log.d("Banner Ad", "Loaded");
//            }
//
//            @Override
//            public void onAdDisplayed(MaxAd ad) {
//                Log.d("Banner Ad", "Displayed");
//            }
//
//            @Override
//            public void onAdHidden(MaxAd ad) {
//
//            }
//
//            @Override
//            public void onAdClicked(MaxAd ad) {
//
//            }
//
//            @Override
//            public void onAdLoadFailed(String adUnitId, MaxError error) {
//                createBannerAd();
//            }
//
//            @Override
//            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
//
//            }
//        });
//
//        int width = ViewGroup.LayoutParams.MATCH_PARENT;
//        int heightPx = getResources().getDimensionPixelSize(R.dimen.banner_height);
//
//        adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx, Gravity.BOTTOM));
//        adView.setBackgroundColor(Color.WHITE);
//        LinearLayout adLayout = findViewById(R.id.adLayout);
//        adLayout.addView(adView);
//        adView.loadAd();
//    }
//
//    private void createInterstitialAd() {
//        interstitialAd = new MaxInterstitialAd(getResources().getString(R.string.applovin_inter_adId), this);
//        MaxAdListener adListener = new MaxAdListener() {
//            @Override
//            public void onAdLoaded(MaxAd ad) {
//                Log.e("Inter Ad", "Loaded");
//                showToast("You can skip the timer");
//            }
//
//            @Override
//            public void onAdDisplayed(MaxAd ad) {
//                binding.timerDownloadSong.setVisibility(View.GONE);
//                binding.skipTimer.setVisibility(View.GONE);
//                binding.downloadImage.setEnabled(true);
//                binding.downloadImage.setAlpha(1.0F);
//
//                Log.e("Inter Ad", "Displayed");
//
//            }
//
//            @Override
//            public void onAdHidden(MaxAd ad) {
//
//            }
//
//            @Override
//            public void onAdClicked(MaxAd ad) {
//
//            }
//
//            @Override
//            public void onAdLoadFailed(String adUnitId, MaxError error) {
//                retry++;
//                long delay = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retry)));
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        interstitialAd.loadAd();
//                    }
//                }, delay);
//            }
//
//            @Override
//            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
//
//            }
//        };
//        interstitialAd.setListener(adListener);
//        interstitialAd.loadAd();
//    }

}