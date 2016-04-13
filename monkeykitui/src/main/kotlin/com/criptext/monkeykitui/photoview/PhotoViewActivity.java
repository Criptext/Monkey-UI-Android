package com.criptext.monkeykitui.photoview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.criptext.monkeykitui.R;

import java.io.File;

/**
 * Created by daniel
 */
public class PhotoViewActivity extends Activity {

    protected TouchImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_photo);
        Intent intent = getIntent();
        String data_path = intent.getStringExtra("data_path");

        File dir = new File(data_path);
        mImageView = (TouchImageView) findViewById(R.id.photo);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(dir.getAbsolutePath()));
    }

    public void closeViewer(View view) {
        onBackPressed();
    }
}
