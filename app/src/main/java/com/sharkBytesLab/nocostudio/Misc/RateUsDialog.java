package com.sharkBytesLab.nocostudio.Misc;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.sharkBytesLab.nocostudio.R;

public class RateUsDialog extends Dialog {

    private float userRate = 0;

    public RateUsDialog(@NonNull Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.rate_us_dialog);



        final AppCompatButton rate = findViewById(R.id.rateNowBtn);
        final AppCompatButton later = findViewById(R.id.laterBtn);
        final RatingBar ratingBar = findViewById(R.id.ratingBar);
        final ImageView ratingImage = findViewById(R.id.ratingImage);

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getContext().getPackageName());
                Intent i = new Intent(Intent.ACTION_VIEW, uri);

                try {
                    getContext().startActivity(i);
                } catch (Exception e) {

                    Toast.makeText(getContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                if(v <= 1)
                {
                    ratingImage.setImageResource(R.drawable.one_star);
                }
                else if(v <= 2)
                {
                    ratingImage.setImageResource(R.drawable.two_star);
                }
                else if(v <= 3)
                {
                    ratingImage.setImageResource(R.drawable.three_star);
                }
                else if(v <= 4)
                {
                    ratingImage.setImageResource(R.drawable.four_star);
                }
                else {
                    ratingImage.setImageResource(R.drawable.five_star);
                }

                animateImage(ratingImage);
                userRate = v;

            }
        });

    }

    private void animateImage(ImageView ratingImage)
    {
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);
    }


}
