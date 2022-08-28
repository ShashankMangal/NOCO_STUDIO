package com.sharkBytesLab.nocostudio.Screens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.sharkBytesLab.nocostudio.R;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;

public class IntroScreen extends MaterialIntroActivity
{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder().title("Welcome to NOCO Studio").description("\nNOCO Studio provides you best quality music and remix for your content on all social media platforms.").image(R.drawable.ic_welcome_intro).buttonsColor(R.color.darkRed).backgroundColor(R.color.theme1).build());

        addSlide(new SlideFragmentBuilder().title("Provided with Music Player").description("\nYou can listen to the music in NOCO Studio Online without any charges. NOCO Studio also has music player for Offline songs on your device.").image(R.drawable.ic_media_player_intro).buttonsColor(R.color.darkRed).backgroundColor(R.color.theme1).build());

        addSlide(new SlideFragmentBuilder().title("Download Songs from our Server").description("\nWe have best quality music and remix on our server waiting for creators like you, Just go to the particular Song and Download it for free.").image(R.drawable.ic_online_youtube_intro).buttonsColor(R.color.darkRed).backgroundColor(R.color.theme1).build());

        addSlide(new SlideFragmentBuilder().title("Music Wallpapers").description("\nNOCO Studio also has high quality wallpapers provided by PEXELS to customize your mobile background.").image(R.drawable.ic_mobile_wallaper_intro).buttonsColor(R.color.darkRed).backgroundColor(R.color.theme1).build());

    }
}