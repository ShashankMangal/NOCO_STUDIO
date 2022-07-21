package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.sharkBytesLab.nocostudio.MainActivity;
import com.sharkBytesLab.nocostudio.Misc.MyMediaPlayer;
import com.sharkBytesLab.nocostudio.Models.AudioModel;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivityMusicPlayerScreenBinding;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerScreen extends AppCompatActivity {

    private ActivityMusicPlayerScreenBinding binding;
    private ArrayList<AudioModel> songsList;
    private AudioModel currentSong;
    private MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    private int x=0;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMusicPlayerScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.titleTv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        binding.titleTv.setSelected(true);



        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                name = extras.getString("TITLE");
                binding.titleTv.setText(name);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        setResourcesWithMusic();

        MusicPlayerScreen.this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {

                if(mediaPlayer!=null)
                {

                    binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    binding.currentTime.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying())
                    {
                        binding.pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                       // binding.musicIconBig.setRotation(x++);
                    }
                    else
                    {
                        binding.pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                       // binding.musicIconBig.setRotation(x);
                    }

                }

                new Handler().postDelayed(this, 100);

            }
        });


        binding.leftArrowMusicPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("frag",3);
                startActivity(intent);

            }
        });


        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser)
            {

                if(mediaPlayer!=null && fromUser)
                {
                    mediaPlayer.seekTo(i);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {



            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {



            }
        });




    }

    void setResourcesWithMusic()
    {

        currentSong = songsList.get(MyMediaPlayer.currentIndex);


        binding.totalTime.setText(convertToMMSS(currentSong.getDuration()));

        binding.pausePlay.setOnClickListener(v-> pausePlay());
        binding.next.setOnClickListener(v-> playNextSong());
        binding.previous.setOnClickListener(v-> playPreviousSong());

        playMusic();


    }

    private void playMusic()
    {

        mediaPlayer.reset();
        try{
        mediaPlayer.setDataSource(currentSong.getPath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        binding.seekBar.setProgress(0);
        binding.seekBar.setMax(mediaPlayer.getDuration());
        }
        catch(Exception e)
            {
                e.printStackTrace();
            }

    }

    private void playNextSong()
    {

        if(MyMediaPlayer.currentIndex == songsList.size()-1)
            return;

        MyMediaPlayer.currentIndex +=1;
        mediaPlayer.reset();
        setResourcesWithMusic();

    }

    private void playPreviousSong()
    {

        if(MyMediaPlayer.currentIndex == 0)
            return;

        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourcesWithMusic();

    }

    private void pausePlay()
    {

        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();


    }

    public static String convertToMMSS(String duration)
    {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

}