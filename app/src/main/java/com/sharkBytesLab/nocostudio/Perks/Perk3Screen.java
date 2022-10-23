package com.sharkBytesLab.nocostudio.Perks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.Screens.AllPerksScreen;
import com.sharkBytesLab.nocostudio.databinding.ActivityPerk3ScreenBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class Perk3Screen extends AppCompatActivity
{

    private ActivityPerk3ScreenBinding binding;
    private String channelName = "CHANNEL NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerk3ScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.perk3ImageBack.setOnClickListener(v ->
                {
                    startActivity(new Intent(getApplicationContext(), AllPerksScreen.class));
                    finish();
                }
        );

        binding.saveLayoutPerk3Button.setOnClickListener(v ->
                saveLayout()
        );

        binding.previewLayoutPerk3Button.setOnClickListener(v -> getEditTextAndSetTextView());

    }

    private void getEditTextAndSetTextView() {
        binding.saveLayoutPerk3Button.setEnabled(true);
        channelName = binding.perk3ChannelName.getText().toString();

        binding.perk3TextChannelName.setText(channelName);
    }

    private void saveLayout() {
        binding.perk3LayoutSave.setDrawingCacheEnabled(true);
        binding.perk3LayoutSave.buildDrawingCache();
        binding.perk3LayoutSave.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        Bitmap bitmap = binding.perk3LayoutSave.getDrawingCache();

        save(bitmap);
    }

    private void save(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(root + "/Download");
        Date d = new Date();
        CharSequence s = DateFormat.format("MM-dd-yy hh-mm-ss", d.getTime());
        String fileName = "NOCO_Perks" + s.toString() + ".jpg";
        File myFile = new File(file, fileName);
        Log.e("Bitmap", bitmap.toString());

        if (myFile.exists()) {
            myFile.delete();
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(myFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            MotionToast.Companion.createColorToast(this,
                    "Perk Saved Successfully !",
                    "SHARE YOUR PERK WITH OTHERS",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular));
            binding.perk3LayoutSave.setDrawingCacheEnabled(false);
        } catch (Exception e) {
            Toast.makeText(this, "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), AllPerksScreen.class));
        finish();
    }
}