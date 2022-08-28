package com.sharkBytesLab.nocostudio.Perks;

import static com.sharkBytesLab.nocostudio.R.color.theme1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sharkBytesLab.nocostudio.R;
import com.sharkBytesLab.nocostudio.Screens.AllPerksScreen;
import com.sharkBytesLab.nocostudio.databinding.ActivityPerk2ScreenBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import www.sanju.motiontoast.MotionToast;
import www.sanju.motiontoast.MotionToastStyle;

public class Perk2Screen extends AppCompatActivity {

    private ActivityPerk2ScreenBinding binding;
    private String channelName = "CHANNEL NAME";
    private String countView = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerk2ScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.perk2ImageBack.setOnClickListener(v ->
                {
                    startActivity(new Intent(getApplicationContext(), AllPerksScreen.class));
                    finish();
                }
        );

        binding.saveLayoutPerk2Button.setOnClickListener(v ->
                saveLayout()
        );

        binding.previewLayoutPerk2Button.setOnClickListener(v -> getEditTextAndSetTextView());

    }

    private void getEditTextAndSetTextView() {
        binding.saveLayoutPerk2Button.setEnabled(true);
        channelName = binding.perk2ChannelName.getText().toString();
        countView = binding.perk2ViewCount.getText().toString();

        binding.perk2TextChannelName.setText(channelName);
        binding.perk2TextViews.setText(countView);
    }

    private void saveLayout() {
        binding.perk2LayoutSave.setDrawingCacheEnabled(true);
        binding.perk2LayoutSave.buildDrawingCache();
        binding.perk2LayoutSave.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        Bitmap bitmap = binding.perk2LayoutSave.getDrawingCache();

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
            binding.perk2LayoutSave.setDrawingCacheEnabled(false);
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