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
import com.sharkBytesLab.nocostudio.databinding.ActivityPerksScreenBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Locale;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class Perk1Screen extends AppCompatActivity {

    private ActivityPerksScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerksScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.perk1ImageBack.setOnClickListener(v ->
                {
                    startActivity(new Intent(getApplicationContext(), AllPerksScreen.class));
                    finish();
                }
        );
        binding.saveLayoutPerkButton.setOnClickListener(v ->
                saveLayout()
        );
    }

    private void saveLayout() {
        binding.perkLayoutSave.setDrawingCacheEnabled(true);
        binding.perkLayoutSave.buildDrawingCache();
        binding.perkLayoutSave.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        Bitmap bitmap = binding.perkLayoutSave.getDrawingCache();

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
            binding.perkLayoutSave.setDrawingCacheEnabled(false);
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