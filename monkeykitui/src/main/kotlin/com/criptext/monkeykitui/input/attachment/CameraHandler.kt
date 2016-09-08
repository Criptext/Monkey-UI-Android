package com.criptext.monkeykitui.input.attachment

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
import com.criptext.monkeykitui.input.photoEditor.PhotoEditorActivity
import com.criptext.monkeykitui.input.listeners.CameraListener
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.soundcloud.android.crop.Crop
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Created by daniel on 4/21/16.
 */

class CameraHandler constructor(ctx : Context){

    var inputListener: InputListener? = null

    internal var mPhotoFileName: String? = null
    internal var mPhotoFile: File? = null

    var context : Context? = ctx

    val TEMP_PHOTO_FILE_NAME = "temp_photo.jpg"
    var photoDirName = "MonkeyKit Sent Photos"
    var photoSuffix = "mk_photo.jpg"
    val CONTENT_URI = Uri.parse("content://com.criptext.uisample/")

    var orientationImage: Int = 0

    enum class RequestType {
        openGallery, takePicture, editPhoto, cropPhoto;

        companion object {
            val DEFAULT_REQUEST_CODE = 8000

            fun fromCode(requestCode: Int) = values()[requestCode - DEFAULT_REQUEST_CODE]
        }
        val requestCode: Int
        get() = this.ordinal + DEFAULT_REQUEST_CODE

    }

    private fun startActivity(intent: Intent, requestCode: Int){
        (context as? Activity)?.startActivityForResult(intent, requestCode)
    }

    /**
     * Creates a temporary photo file in mPhotoFile. If the photo directory does not exist, it creates it.
     */
    private fun initTemporaryPhotoFile() {
        val state = Environment.getExternalStorageState()
        val tempDir: File

        if (Environment.MEDIA_MOUNTED == state)
            tempDir = File(Environment.getExternalStorageDirectory(), photoDirName)
         else
            tempDir = File(context?.filesDir, photoDirName)

        if(!tempDir.exists())
            tempDir.mkdir()

        mPhotoFile = File(tempDir, TEMP_PHOTO_FILE_NAME)
    }

    fun startPhotoEditor(photoUri: Uri?){
        val intent = Intent(context, PhotoEditorActivity::class.java)
        intent.putExtra(PhotoEditorActivity.destinationPath, getOutputFile().absolutePath)
        intent.data = photoUri
        startActivity(intent, RequestType.editPhoto.requestCode)
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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
            intent.putExtra("return-data", true)
            startActivity(intent, RequestType.takePicture.requestCode)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }

    }

    fun pickFromGallery() {
        Crop.pickImage(context as Activity)
    }

    fun getOutputFile(): File {

        val state = Environment.getExternalStorageState()

        if (mPhotoFileName == null)
            mPhotoFileName = "$photoDirName/${System.currentTimeMillis() / 1000}$photoSuffix"

        if (Environment.MEDIA_MOUNTED == state) {
            return File(Environment.getExternalStorageDirectory(), mPhotoFileName)
        } else {
            return File(context!!.filesDir, mPhotoFileName)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        val newReqCode: Int
        if (requestCode == Crop.REQUEST_PICK)
            newReqCode = RequestType.openGallery.requestCode
        else if (requestCode == Crop.REQUEST_CROP)
            newReqCode = RequestType.cropPhoto.requestCode
        else
            newReqCode = requestCode

        when (RequestType.fromCode(newReqCode)) {
            RequestType.openGallery -> {
                try {
                    val ei = ExifInterface(getOutputFile().absolutePath)
                    orientationImage = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                } catch (e: IOException) {
                    Log.e("error", "Exif error")
                }

                //Crop.of(data?.data, Uri.fromFile(getOutputFile())).start(context as Activity)
                startPhotoEditor(data?.data)


            }
            RequestType.takePicture -> {
                try {
                    val ei = ExifInterface(mPhotoFile!!.absolutePath)
                    orientationImage = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                } catch (e: IOException) {
                    Log.e("error", "Exif error")
                }

                startPhotoEditor(Uri.fromFile(mPhotoFile))
                //Crop.of(Uri.fromFile(mPhotoFile), Uri.fromFile(getOutputFile())).start(context as Activity)
            }
            RequestType.editPhoto -> {
                
                //Log.d("CameraHandler", "decoding: " + getOutputFile().absolutePath)
                val bmp = BitmapFactory.decodeFile(getOutputFile().absolutePath)
                var exception: Exception? = null

                try {
                    val bos = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                    val bitmapdata = bos.toByteArray()
                    val fos = FileOutputStream(getOutputFile())
                    fos.write(bitmapdata)
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("CameraHandler", e.message)
                    exception = e
                } catch (e: NullPointerException){
                    Log.e("CameraHandler", "Edited bitmap is null!")
                    exception = e
                } finally {
                    bmp?.recycle()
                }

                var monkeyItem = object : MonkeyItem {

                    override fun getMessageTimestampOrder(): Long {
                        return System.currentTimeMillis()
                    }

                    override fun getOldMessageId(): String {
                        return "-" + System.currentTimeMillis()
                    }

                    override fun getMessageTimestamp(): Long {
                        return System.currentTimeMillis()/1000
                    }

                    override fun getMessageId(): String {
                        return "" + (System.currentTimeMillis())
                    }

                    override fun isIncomingMessage(): Boolean {
                        return false
                    }

                    override fun getDeliveryStatus(): MonkeyItem.DeliveryStatus {
                        throw UnsupportedOperationException()
                    }

                    override fun getMessageType(): Int {
                        return MonkeyItem.MonkeyItemType.photo.ordinal
                    }

                    override fun getMessageText(): String {
                        return ""
                    }

                    override fun getPlaceholderFilePath(): String {
                        return ""
                    }

                    override fun getFilePath(): String {
                        return getOutputFile().absolutePath
                    }

                    override fun getFileSize(): Long {
                        return getOutputFile().length()
                    }

                    override fun getAudioDuration(): Long {
                        return 0
                    }

                    override fun getSenderId(): String {
                        return ""
                    }

                }

                if(exception != null)
                    inputListener?.onNewItemFileError(getOutputFile().absolutePath, exception)
                else
                    inputListener?.onNewItem(monkeyItem)
                mPhotoFileName = null

            }
        }

        return
    }
}
