package com.criptext.monkeykitui.photoview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.criptext.monkeykitui.R;

import java.io.File;
import java.lang.ref.WeakReference;

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
        loadBitmap(dir, mImageView);
    }

    public void closeViewer(View view) {
        onBackPressed();
    }

    public void loadBitmap(File file, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(file);
    }

    public class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(File... params) {
            return BitmapFactory.decodeFile(params[0].getAbsolutePath());
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

}
