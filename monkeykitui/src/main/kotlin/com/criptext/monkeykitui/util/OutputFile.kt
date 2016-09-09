package com.criptext.monkeykitui.util

import android.content.Context
import android.os.Environment
import java.io.File

/**
 * Created by gesuwall on 9/8/16.
 */
class OutputFile {

    companion object {
        /**
         * creates a new file in the Internal Storage
         * @param context A Context reference
         * @param dirName the name of the parent directory of the file. If it doesn't exist it
         * will be created.
         * @param suffix the suffix containing the extension of the file. To name the file the current
         * timestamp in seconds will be used. the naming scheme is <timestamp>suffix
         * A reference to a valid file in the internal storage.
         */
        fun create(context: Context, dirName: String, suffix: String): File {
            val state = Environment.getExternalStorageState()

            val mPhotoFileName = "$dirName/${System.currentTimeMillis() / 1000}$suffix"
            val dir: File
            if (Environment.MEDIA_MOUNTED == state) {
                dir = File(Environment.getExternalStorageDirectory(), dirName)
            } else {
                dir = File(context.filesDir, mPhotoFileName)
            }

            if(!dir.exists()) {
               if(!dir.mkdir())
                   dir.mkdirs()
            }

            return File(dir, "${System.currentTimeMillis() / 1000}$suffix")
        }
    }
}