package com.criptext.monkeykitui.input.photoEditor

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import java.io.File
import java.lang.ref.WeakReference

/**
 * Created by gesuwall on 5/4/16.
 */

class PhotoEditTask(uri: Uri, filePath: String, resolver: ContentResolver) : AsyncTask<Int, Int, Int>() {
    private var listener : Runnable? = null

    var sourceUri : Uri
    var resolver : ContentResolver
    var destPath: String

    init {
        this.sourceUri = uri
        this.resolver = resolver
        this.destPath = filePath
    }

    fun setOnBitmapProcessedCallback(listener: Runnable){
        this.listener = listener
    }

    override fun doInBackground(vararg params: Int?): Int? {
        if(params[0] == null)
            throw IllegalArgumentException();
        var bitmap : Bitmap? = null
        try {
            val degrees = if(params.size == 0) 0 else params[0] ?: 0
            var bitmap = BitmapProcessing.getBitmapFromUri(sourceUri, resolver)
            if (bitmap != null) {
                bitmap = BitmapProcessing.rotateBitmap(bitmap, degrees)
                var destFile = File(destPath)
                destFile.delete()
                BitmapProcessing.saveBitmapToFile(bitmap, destFile, 85)
                return 1
            }
        } finally {
            bitmap?.recycle()
        }

        return 0

    }

    override fun onCancelled() {
        Log.d("RotationTask", "cancelled")
        listener = null
    }


    override fun onPostExecute(result: Int?) {
        Log.d("RotationTask", "result: $result")
        listener?.run()
    }

}
