package com.criptext.monkeykitui.util

import android.content.Context
import android.os.Environment
import android.util.Log
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
         * @return A reference to a valid file in the internal storage. Null if the file could
         * not be created
         */
        fun create(context: Context, dirName: String, suffix: String, temp: Boolean): File? {
            val state = Environment.getExternalStorageState()

            val mPhotoFileName = "$dirName/${System.currentTimeMillis() / 1000}$suffix"
            val dir: File
            if (Environment.MEDIA_MOUNTED == state) {
                dir = File(Environment.getExternalStorageDirectory(), dirName)
            } else {
                dir = File(context.filesDir, mPhotoFileName)
            }

            var dirExists = dir.exists()
            if (!dirExists) {
                dirExists = dir.mkdir()
                if(dir.exists() && !dirExists) dir.delete() //delete corrupted
            }
            if (!dirExists) dirExists = dir.mkdirs()
            if (!dirExists) return null

            val result = File(dir, "${System.currentTimeMillis() / 1000}$suffix")

            if(temp && result.exists())
                return result

            if(result.createNewFile()) //check that unique file is successfully created for non-temp files
                return result

            return null
        }

        fun create(context: Context, dirName: String, suffix: String) = create(context, dirName, suffix, false)
        fun createTemp(context: Context, dirName: String, suffix: String) = create(context,dirName, suffix, true)

    }
}