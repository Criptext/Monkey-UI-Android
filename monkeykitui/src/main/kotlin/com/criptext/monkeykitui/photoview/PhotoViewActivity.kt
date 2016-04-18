package com.criptext.monkeykitui.photoview

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ImageView

import com.criptext.monkeykitui.R

import java.io.File
import java.lang.ref.WeakReference

/**
 * Created by daniel
 */
class PhotoViewActivity : Activity() {

    protected lateinit var mImageView: TouchImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_view_photo)
        val intent = intent
        val data_path = intent.getStringExtra(IMAGE_DATA_PATH)

        val dir = File(data_path)
        mImageView = findViewById(R.id.photo) as TouchImageView
        loadBitmap(dir, mImageView)
    }

    fun closeViewer(view: View) {
        onBackPressed()
    }

    fun loadBitmap(file: File, imageView: ImageView) {
        val task = BitmapWorkerTask(imageView)
        task.execute(file)
    }

    inner class BitmapWorkerTask(imageView: ImageView) : AsyncTask<File, Void, Bitmap>() {

        private val imageViewReference: WeakReference<ImageView>?

        init {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = WeakReference(imageView)
        }

        // Decode image in background.
        override fun doInBackground(vararg params: File): Bitmap {
            return BitmapFactory.decodeFile(params[0].absolutePath)
        }

        // Once complete, see if ImageView is still around and set bitmap.
        override fun onPostExecute(bitmap: Bitmap?) {
            if (imageViewReference != null && bitmap != null) {
                val imageView = imageViewReference.get()
                imageView?.setImageBitmap(bitmap)
            }
        }
    }

    companion object {
        var IMAGE_DATA_PATH = "PhotoViewActivity.DataPath"
    }

}
