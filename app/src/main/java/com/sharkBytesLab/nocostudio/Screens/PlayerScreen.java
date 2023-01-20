package com.sharkBytesLab.nocostudio.Screens;

import static com.sharkBytesLab.nocostudio.Adapters.AlbumDetailsAdapter.albumFiles;
import static com.sharkBytesLab.nocostudio.Adapters.MusicAdapter.mFiles;
import static com.sharkBytesLab.nocostudio.Fragments.MusicFragment.*;
import static com.sharkBytesLab.nocostudio.Misc.MyApplication.ACTION_NEXT;
import static com.sharkBytesLab.nocostudio.Misc.MyApplication.ACTION_PLAY;
import static com.sharkBytesLab.nocostudio.Misc.MyApplication.ACTION_PREVIOUS;
import static com.sharkBytesLab.nocostudio.Misc.MyApplication.CHANNEL_ID_2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sharkBytesLab.nocostudio.Misc.ActionPlaying;
import com.sharkBytesLab.nocostudio.Misc.MusicFiles;
import com.sharkBytesLab.nocostudio.Misc.MusicService;
import com.sharkBytesLab.nocostudio.Misc.NotificationReceiver;
import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.databinding.ActivityPlayerScreenBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class PlayerScreen extends AppCompatActivity implements ActionPlaying, ServiceConnection {
    private ActivityPlayerScreenBinding binding;
    private int position = -1;
    public static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    private static Uri uri;
    //private static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentMethod();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            WindowManager.LayoutParams winParams = window.getAttributes();
            winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            window.setAttributes(winParams);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicService != null && fromUser) {
                    musicService.seekTo(progress * 1000);
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
            public void run() {
                if (musicService != null) {
                    int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                    binding.seekBar.setProgress(mCurrentPosition);
                    binding.durationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 100);
            }
        });

        binding.idShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBoolean) {
                    shuffleBoolean = false;
                    binding.idShuffle.setImageResource(R.drawable.ic_shuffle_off);
                } else {
                    shuffleBoolean = true;
                    binding.idShuffle.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        binding.idRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatBoolean) {
                    repeatBoolean = false;
                    binding.idRepeat.setImageResource(R.drawable.ic_repeat_off);
                } else {
                    repeatBoolean = true;
                    binding.idRepeat.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });

        binding.playerBack.setOnClickListener(v -> onBackPressed());

    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                binding.playPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            playPauseBtnClicked();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        playThread.start();
    }

    public void playPauseBtnClicked() throws IOException {
        if (musicService.isPlaying()) {
            binding.playPause.setImageResource(R.drawable.playnow_icon);
            musicService.showNotification(R.drawable.playnow_icon);
            musicService.pause();
            binding.seekBar.setMax(musicService.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
        } else {
            binding.playPause.setImageResource(R.drawable.ic_pause);
            musicService.showNotification(R.drawable.ic_pause);
            musicService.start();
            binding.seekBar.setMax(musicService.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                binding.idNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            nextBtnClicked();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnClicked() throws IOException {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            binding.songName.setText(listSongs.get(position).getTitle());
            binding.songArtist.setText("Artist : " + listSongs.get(position).getArtist());

            binding.seekBar.setMax(musicService.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.ic_pause);
            binding.playPause.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            binding.songName.setText(listSongs.get(position).getTitle());
            binding.songArtist.setText("Artist : " + listSongs.get(position).getArtist());

            binding.seekBar.setMax(musicService.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            musicService.onCompleted();
            musicService.showNotification(R.drawable.playnow_icon);
            binding.playPause.setBackgroundResource(R.drawable.playnow_icon);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                binding.idPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnClicked() {
        if (musicService.isPlaying()) {
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            binding.songName.setText(listSongs.get(position).getTitle());
            binding.songArtist.setText("Artist : " + listSongs.get(position).getArtist());

            binding.seekBar.setMax(musicService.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            musicService.onCompleted();
            try {
                musicService.showNotification(R.drawable.ic_pause);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            binding.playPause.setBackgroundResource(R.drawable.ic_pause);
            musicService.start();
        } else {
            musicService.stop();
            musicService.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(listSongs.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (listSongs.size() - 1) : (position - 1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            binding.songName.setText(listSongs.get(position).getTitle());
            binding.songArtist.setText("Artist : " + listSongs.get(position).getArtist());

            binding.seekBar.setMax(musicService.getDuration() / 1000);

            PlayerScreen.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (musicService != null) {
                        int mCurrentPosition = musicService.getCurrentPosition() / 1000;
                        binding.seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            musicService.onCompleted();
            try {
                musicService.showNotification(R.drawable.playnow_icon);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            binding.playPause.setBackgroundResource(R.drawable.playnow_icon);
        }
    }

    private String formattedTime(int mCurrentTime) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentTime % 60);
        String minutes = String.valueOf(mCurrentTime / 60);

        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;

        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");

        if (sender != null && sender.equals("albumDetails")) {
            listSongs = albumFiles;
            binding.playingText.setText("Playing from " + albumFiles.get(position).getAlbum() + " Album ");
        } else {
            listSongs = mFiles;
        }
        if (listSongs != null) {
            binding.playPause.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("servicePosition", position);
        startService(intent);


    }

    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        binding.durationTotal.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null) {
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            imageAnimation(this, binding.coverArt, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        //binding.imageViewGradient.setBackgroundResource(R.drawable.cover_art_shadow);
                        binding.mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{swatch.getRgb(), 0x00000000});
                        //binding.imageViewGradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{swatch.getRgb(), 0x00000000});
                        binding.mContainer.setBackground(gradientDrawableBg);
                        binding.songName.setTextColor(swatch.getTitleTextColor());
                        binding.songArtist.setTextColor(swatch.getBodyTextColor());
                    } else {
                        //binding.imageViewGradient.setBackgroundResource(R.drawable.cover_art_shadow);
                        binding.mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0x00000000});
                        //binding.imageViewGradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0xff000000});
                        binding.mContainer.setBackground(gradientDrawableBg);
                    }
                }
            });
        } else {
            Glide.with(this).asBitmap().load(R.drawable.player_cover).into(binding.coverArt);
            //binding.imageViewGradient.setBackgroundResource(R.drawable.cover_art_shadow);
            binding.mContainer.setBackgroundResource(R.drawable.player_gradient);
        }
    }

    public void imageAnimation(Context context, ImageView imageView, Bitmap bitmap) {
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
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
        musicService.setCallback(this);
        binding.seekBar.setMax(musicService.getDuration() / 1000);
        metaData(uri);
        binding.songName.setText(listSongs.get(position).getTitle());
        binding.songArtist.setText("Artist : " + listSongs.get(position).getArtist());
        musicService.onCompleted();

        try {
            musicService.showNotification(R.drawable.ic_pause);
        } catch (Exception e) {
            Toast.makeText(this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.v("Player error : ", e.getMessage());
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }

}