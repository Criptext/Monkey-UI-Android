package com.criptext.monkeykitui.input

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.criptext.monkeykitui.input.listeners.CameraListener
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.soundcloud.android.crop.Crop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by daniel on 4/21/16.
 */

class CameraHandler constructor(ctx : Context){

    var cameraListener : CameraListener? = null

    internal var mPhotoFileName: String? = null
    internal var mPhotoFile: File? = null

    var context : Context? = ctx

    val TEMP_PHOTO_FILE_NAME = "temp_photo.jpg"
    val CONTENT_URI = Uri.parse("content://com.criptext.uisample/")
    var defaultActionStrings : Array<String>? = arrayOf("Take a Photo", "Choose Photo")

    var orientationImage: Int = 0

    enum class RequestType {
        openGallery, takePicture, editPhoto, cropPhoto
    }

    private fun initTemporaryPhotoFile() {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            mPhotoFile = File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME)
        } else {
            mPhotoFile = File(context?.getFilesDir(), TEMP_PHOTO_FILE_NAME)
        }
    }

    fun setCameraListen(cameraList : CameraListener){
        cameraListener=cameraList
    }

    fun takePicture() {

        if(mPhotoFile == null)
            initTemporaryPhotoFile()

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val mImageCaptureUri: Uri
            val state = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED == state) {
                mImageCaptureUri = Uri.fromFile(mPhotoFile)
            } else {
                /*
				 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
				 */
                mImageCaptureUri = CONTENT_URI
            }
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
            intent.putExtra("return-data", true)
            (context as Activity).startActivityForResult(intent, RequestType.takePicture.ordinal)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }

    }

    fun pickFromGallery() {
        Crop.pickImage(context as Activity)
    }

    fun getTempFile(): File {

        val state = Environment.getExternalStorageState()

        if (mPhotoFileName == null)
            mPhotoFileName = "${System.currentTimeMillis() / 1000}$TEMP_PHOTO_FILE_NAME"

        if (Environment.MEDIA_MOUNTED == state) {
            return File(Environment.getExternalStorageDirectory(), mPhotoFileName)
        } else {
            return File(context!!.getFilesDir(), mPhotoFileName)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var requestCode = requestCode

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == Crop.REQUEST_PICK)
            requestCode = RequestType.openGallery.ordinal
        if (requestCode == Crop.REQUEST_CROP)
            requestCode = RequestType.cropPhoto.ordinal

        when (RequestType.values()[requestCode]) {
            RequestType.openGallery -> {
                try {
                    val ei = ExifInterface(getTempFile().absolutePath)
                    orientationImage = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                } catch (e: IOException) {
                    Log.e("error", "Exif error")
                }

                Crop.of(data?.data, Uri.fromFile(getTempFile())).start(context as Activity)
            }
            RequestType.takePicture -> {
                try {
                    val ei = ExifInterface(mPhotoFile!!.absolutePath)
                    orientationImage = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                } catch (e: IOException) {
                    Log.e("error", "Exif error")
                }

                Crop.of(Uri.fromFile(mPhotoFile), Uri.fromFile(getTempFile())).start(context as Activity)
            }
            RequestType.cropPhoto -> {

                var rotation = 0
                if (orientationImage == 0) {
                    try {
                        val ei = ExifInterface(getTempFile().absolutePath)
                        orientationImage = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    } catch (e: IOException) {
                        Log.e("error", "Exif error")
                    }

                }
                if (orientationImage != 0) {
                    when (orientationImage) {
                        3 -> {
                            // ORIENTATION_ROTATE_180
                            rotation = 180
                        }
                        6 -> {
                            // ORIENTATION_ROTATE_90
                            rotation = 90
                        }
                        8 -> {
                            // ORIENTATION_ROTATE_270
                            rotation = 270
                        }
                    }
                }
                if (rotation != 0) {
                    val bmp = BitmapFactory.decodeFile(getTempFile().absolutePath)
                    val matrix = Matrix()
                    matrix.postRotate(rotation.toFloat())
                    val rotatedImg = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
                    bmp.recycle()

                    try {
                        val bos = ByteArrayOutputStream()
                        rotatedImg.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        val bitmapdata = bos.toByteArray()
                        val fos = FileOutputStream(getTempFile())
                        fos.write(bitmapdata)
                        fos.flush()
                        fos.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                var monkeyItem = object : com.criptext.monkeykitui.recycler.MonkeyItem{

                    override fun getMessageTimestamp(): Long {
                        return System.currentTimeMillis()
                    }

                    override fun getMessageId(): String {
                        return "" + (System.currentTimeMillis())
                    }

                    override fun isIncomingMessage(): Boolean {
                        return false
                    }

                    override fun getOutgoingMessageStatus(): MonkeyItem.OutgoingMessageStatus {
                        throw UnsupportedOperationException()
                    }

                    override fun getMessageType(): Int {
                        return MonkeyItem.MonkeyItemType.photo.ordinal
                    }

                    override fun getMessageText(): String {
                        return getTempFile().absolutePath
                    }

                    override fun getPlaceholderFilePath(): String {
                        return ""
                    }

                    override fun getFilePath(): String {
                        return ""
                    }

                    override fun getFileSize(): Long {
                        return 0
                    }

                    override fun getAudioDuration(): Long {
                        return 0
                    }

                    override fun getContactSessionId(): String {
                        return ""
                    }

                }

                cameraListener?.onNewItem(monkeyItem)

            }
        }

        return
    }
}
