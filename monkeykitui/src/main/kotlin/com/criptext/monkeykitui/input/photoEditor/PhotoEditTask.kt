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

class PhotoEditTask(uri: Uri, filePath: String, resolver: ContentResolver, callback: OnTaskFinished):
        AsyncTask<Int, Int, Boolean>() {
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

    override fun doInBackground(vararg params: Int?): Boolean? {
        if(params[0] == null)
            throw IllegalArgumentException();
        var bitmap : Bitmap? = null
        val degrees = if(params.size == 0) 0 else params[0] ?: 0
        bitmap = BitmapProcessing.getBitmapFromUri(sourceUri, resolver)
        if (bitmap != null) {
            bitmap = BitmapProcessing.rotateBitmap(bitmap, degrees)
            var destFile = File(destPath)
            destFile.delete()
            val res = BitmapProcessing.saveBitmapToFile(bitmap, destFile, 85)
            bitmap.recycle()
            return res
        }
       return false
    }

    override fun onPostExecute(result: Boolean?) {
        callbackRef.get()?.invoke(result!!)
    }

    interface OnTaskFinished: (Boolean) -> Unit

}
