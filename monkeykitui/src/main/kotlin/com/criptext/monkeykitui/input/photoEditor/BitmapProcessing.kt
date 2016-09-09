package com.criptext.monkeykitui.input.photoEditor

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import java.io.*

/**
 * Created by gesuwall on 5/4/16.
 */

class BitmapProcessing {
    companion object{
        /**
         * Recibe un uri y devuelve el bitmap encontrado
         * @param uri
         * *
         * @param resolver
         * *
         * @return
         * *
         * @throws FileNotFoundException
         * *
         * @throws IOException
         */
        @Throws(FileNotFoundException::class, IOException::class)
        fun getBitmapFromUri(uri: Uri, resolver: ContentResolver): Bitmap? {
            val THUMBNAIL_SIZE = 640.0
            var input: InputStream = resolver.openInputStream(uri)

            val onlyBoundsOptions = BitmapFactory.Options()
            onlyBoundsOptions.inJustDecodeBounds = true
            onlyBoundsOptions.inDither = true//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions)
            input.close()
            if (onlyBoundsOptions.outWidth == -1 || onlyBoundsOptions.outHeight == -1)
                return null

            val originalSize = if (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) onlyBoundsOptions.outHeight else onlyBoundsOptions.outWidth

            val ratio = if (originalSize > THUMBNAIL_SIZE) originalSize / THUMBNAIL_SIZE else 1.0

            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio)
            bitmapOptions.inDither = true//optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888//optional
            input = resolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions)
            input.close()
            return bitmap
        }

        private fun getPowerOfTwoForSampleRatio(ratio: Double): Int {
            val k = Integer.highestOneBit(Math.floor(ratio).toInt())
            if (k == 0)
                return 1
            else
                return k
        }

        /**
         * Rota un bitmap
         * @param source bitmap a rotar
         * *
         * @param degs grados a rotar
         * *
         * @return el bitma rotado grados
         */
        fun rotateBitmap(source: Bitmap, degs: Int): Bitmap {
            if(degs%360 != 0) {
                val matrix = Matrix()
                matrix.postRotate(degs.toFloat())
                //Bitmap scaledBitmap = Bitmap.createScaledBitmap(source,source.getWidth(),source.getHeight(),true);
                return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
            }

            return source
        }

        /**
         * stores a compressed bitamp to a file
         * @param source bitmap to save
         * *
         * @param dest reference to the file in which the bitmap will be saved
         * @return true if the bitmap was successfully compressed and stored in the requested file
         */
        fun saveBitmapToFile(source: Bitmap, dest: File, quality: Int): Boolean {
            var fout : FileOutputStream? = null
            fout = FileOutputStream(dest)
            val result = source.compress(Bitmap.CompressFormat.PNG, quality, fout)
            fout.flush()
            fout.close()
            return result
        }
    }
}
