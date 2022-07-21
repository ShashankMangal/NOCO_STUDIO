package com.sharkBytesLab.nocostudio.Screens;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivityOnlineMusicScreenBinding;

public class OnlineMusicScreen extends AppCompatActivity {

    private ActivityOnlineMusicScreenBinding binding;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnlineMusicScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mediaPlayer = new MediaPlayer();

        binding.onlinePlayerSeekBar.setMax(100);

        binding.onlinePlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mediaPlayer.isPlaying())
                {
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    binding.onlinePlayPause.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                }
                else
                {
                    mediaPlayer.start();
                    binding.onlinePlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                    updateSeekBar();
                }

            }
        });

        prepareMediaPlayer();
    }

    private void prepareMediaPlayer()
    {
        try
        {
            //mediaPlayer.setDataSource("http://infinityandroid.com/music/good_times.mp3");
            mediaPlayer.setDataSource("https://firebasestorage.googleapis.com/v0/b/rewardapp-1070c.appspot.com/o/Songs%2FEMIWAY_-_KA_KHA_GA_Ft._Yo_Yo_Honey_Singh_%26_Hommie_Dilliwala_(Music_Video)_Prod_By_Itsraaj(256k).mp3?alt=media&token=5a533e3e-07ce-42f6-b81c-4b8bcac8ff6b");
            mediaPlayer.prepare();
            binding.onlineTextTotalTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
        }
        catch(Exception exception)
        {
            Toast.makeText(this, "Error : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            binding.onlineTextCurrentTime.setText(milliSecondsToTimer(currentDuration));
        }
    };

    private void updateSeekBar()
    {
        if(mediaPlayer.isPlaying())
        {
            binding.onlinePlayerSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }


    private String milliSecondsToTimer(long ms)
    {
        String timerString = "";
        String secondsString;

        int hours = (int) (ms / (1000 * 60 * 60));
        int minutes = (int) (ms % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((ms % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if(hours > 0)
        {
            timerString = hours + ":";
        }

        if(seconds < 10)
        {
            secondsString = "0" + seconds;
        }

        else
        {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;

    }


}