package com.criptext.monkeykitui.input.photoEditor

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * Created by gesuwall on 5/4/16.
 */

class PhotoEditTask(uri: Uri, filePath: String, resolver: ContentResolver, callback: OnTaskFinished):
        AsyncTask<Int, Int, IOException>() {
    private var callbackRef: WeakReference<OnTaskFinished>
    var sourceUri : Uri
    var resolver : ContentResolver
    var destPath: String

    init {
        this.sourceUri = uri
        this.resolver = resolver
        this.destPath = filePath
        callbackRef = WeakReference(callback)
    }

    override fun doInBackground(vararg params: Int?): IOException? {
        if(params[0] == null)
            throw IllegalArgumentException();
        var bitmap : Bitmap? = null
        try {
            val degrees = if(params.size == 0) 0 else params[0] ?: 0
            bitmap = BitmapProcessing.getBitmapFromUri(sourceUri, resolver)
            if (bitmap != null) {
                bitmap = BitmapProcessing.rotateBitmap(bitmap, degrees)
                var destFile = File(destPath)
                destFile.delete()
                BitmapProcessing.saveBitmapToFile(bitmap, destFile, 85)
                return null
            }
        } catch (ex: IOException){
            return ex
        }
        finally {
            bitmap?.recycle()
        }

        //This should never happen
        return null

    }

    override fun onCancelled() {
        Log.d("RotationTask", "cancelled")
    }


    override fun onPostExecute(result: IOException?) {
        Log.d("RotationTask", "result: $result")
        callbackRef.get()?.invoke(result)
    }


    interface OnTaskFinished: (IOException?) -> Unit

}
