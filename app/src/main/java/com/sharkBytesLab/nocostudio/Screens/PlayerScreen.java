package com.sharkBytesLab.nocostudio.Screens;

import static com.sharkBytesLab.nocostudio.Fragments.MusicFragment.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import com.bumptech.glide.Glide;
import com.sharkBytesLab.nocostudio.Misc.MusicFiles;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivityPlayerScreenBinding;
import java.util.ArrayList;
import java.util.Random;

public class PlayerScreen extends AppCompatActivity implements MediaPlayer.OnCompletionListener
{
    private ActivityPlayerScreenBinding binding;
    private int position = -1;
    private static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    private static Uri uri;
    private static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentMethod();
        binding.songName.setText(listSongs.get(position).getTitle());
        binding.songArtist.setText("Artist : " + listSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);

        binding.songName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        binding.songName.setSelected(true);

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(mediaPlayer != null && fromUser)
                {
                    mediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerScreen.this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if(mediaPlayer!=null)
                {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    binding.seekBar.setProgress(mCurrentPosition);
                    binding.durationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 100);
            }
        });

        binding.idShuffle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(shuffleBoolean)
                {
                    shuffleBoolean = false;
                    binding.idShuffle.setImageResource(R.drawable.ic_shuffle_off);
                }
                else
                {
                    shuffleBoolean = true;
                    binding.idShuffle.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        binding.idRepeat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(repeatBoolean)
                {
                    repeatBoolean = false;
                    binding.idRepeat.setImageResource(R.drawable.ic_repeat_off);
                }
                else
                {
                    repeatBoolean = true;
                    binding.idRepeat.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });

        binding.backBtn.setOnClickListener(v-> onBackPressed());

    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    private void playThreadBtn()
    {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                binding.playPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPauseBtnClicked()
    {
        if(mediaPlayer.isPlaying())
        {
            binding.playPause.setImageResource(R.drawable.playnow_icon);
            mediaPlayer.pause();
            binding.seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
        }
        else
        {
            binding.playPause.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            binding.seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
        }
    }

    private void nextThreadBtn()
    {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                binding.idNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextBtnClicked()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            binding.songName.setText(listSongs.get(position).getTitle());
            binding.songArtist.setText("Artist : " + listSongs.get(position).getArtist());

            binding.seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            binding.playPause.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            binding.songName.setText(listSongs.get(position).getTitle());
            binding.songArtist.setText("Artist : " + listSongs.get(position).getArtist());

            binding.seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            binding.playPause.setBackgroundResource(R.drawable.playnow_icon);
        }
    }

    private int getRandom(int i)
    {
        Random random = new Random();
        return random.nextInt(i+1);
    }

    private void prevThreadBtn()
    {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                binding.idPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevBtnClicked()
    {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) :  (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            binding.songName.setText(listSongs.get(position).getTitle());
            binding.songArtist.setText("Artist : " + listSongs.get(position).getArtist());

            binding.seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            binding.playPause.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) :  (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            binding.songName.setText(listSongs.get(position).getTitle());
            binding.songArtist.setText("Artist : " + listSongs.get(position).getArtist());

            binding.seekBar.setMax(mediaPlayer.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(mediaPlayer!=null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            binding.playPause.setBackgroundResource(R.drawable.playnow_icon);
        }
    }

    private String formattedTime(int mCurrentTime)
    {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentTime%60);
        String minutes = String.valueOf(mCurrentTime/60);

        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;

        if(seconds.length() == 1)
        {
            return totalNew;
        }
        else
        {
            return totalOut;
        }
    }

    private void getIntentMethod()
    {
        position = getIntent().getIntExtra("position", -1);
        listSongs = musicFiles;
        if(listSongs != null)
        {
            binding.playPause.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }

        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        else
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }

        binding.seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);

    }

    private void metaData(Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        binding.durationTotal.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if(art != null)
        {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            imageAnimation(this, binding.coverArt, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if(swatch!=null)
                    {
                        binding.imageViewGradient.setBackgroundResource(R.drawable.cover_art_shadow);
                        binding.mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        binding.imageViewGradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
                        binding.mContainer.setBackground(gradientDrawableBg);
                        binding.songName.setTextColor(swatch.getTitleTextColor());
                        binding.songArtist.setTextColor(swatch.getBodyTextColor());
                    }
                    else
                    {
                        binding.imageViewGradient.setBackgroundResource(R.drawable.cover_art_shadow);
                        binding.mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0x00000000});
                        binding.imageViewGradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0xff000000});
                        binding.mContainer.setBackground(gradientDrawableBg);
                    }
                }
            });
        }
        else
        {
            Glide.with(this).asBitmap().load(R.drawable.player_cover).into(binding.coverArt);
            binding.imageViewGradient.setBackgroundResource(R.drawable.cover_art_shadow);
            binding.mContainer.setBackgroundResource(R.drawable.main_bg);
        }
    }

    public void imageAnimation(Context context, ImageView imageView, Bitmap bitmap)
    {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onCompletion(MediaPlayer mp)
    {
           nextBtnClicked();
           if(mediaPlayer != null)
           {
               mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
               mediaPlayer.start();
               mediaPlayer.setOnCompletionListener(this);
           }
    }
}