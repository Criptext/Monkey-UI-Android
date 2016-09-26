package com.criptext.monkeykitui.input.attachment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.criptext.monkeykitui.input.photoEditor.PhotoEditorActivity
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.criptext.monkeykitui.util.OutputFile
import com.soundcloud.android.crop.Crop
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by daniel on 4/21/16.
 */

class CameraHandler constructor(var context : Context){

    var inputListener: InputListener? = null

    internal var mPhotoFileName: String? = null
    internal var tempFile: File? = null
    internal var outputFile: File? = null

    val TEMP_PHOTO_FILE_NAME = "temp_photo.jpg"
    var photoDirName = "MonkeyKit/Sent Photos"
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

    fun startPhotoEditor(photoUri: Uri?){
        val intent = Intent(context, PhotoEditorActivity::class.java)
        outputFile = initOutputFile()
        if(outputFile != null) {
            intent.putExtra(PhotoEditorActivity.destinationPath, outputFile!!.absolutePath)
            intent.data = photoUri
            startActivity(intent, RequestType.editPhoto.requestCode)
        } else {
            inputListener?.onNewItemFileError(MonkeyItem.MonkeyItemType.photo.ordinal)
            Log.d("DefaultVoiceNoteRec", "output file is null")
        }
    }

    fun takePicture() {

        if(tempFile == null)
            initTemporaryPhotoFile()

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val mImageCaptureUri: Uri
            val state = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED == state) {
                mImageCaptureUri = Uri.fromFile(tempFile)
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

    fun handleFileFromIntent(fileFromIntent: File?, photoUri: Uri?){
        try {
            if(fileFromIntent != null) {
                val ei = ExifInterface(fileFromIntent.absolutePath)
                orientationImage = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            } else {
                inputListener?.onNewItemFileError(MonkeyItem.MonkeyItemType.photo.ordinal)
            }
            startPhotoEditor(photoUri)
        } catch (e: IOException) {
            Log.e("error", "Exif error")
            inputListener?.onNewItemFileError(MonkeyItem.MonkeyItemType.photo.ordinal)
        }

    }


    /**
     * Creates a temporary photo file in mPhotoFile. If the photo directory does not exist, it creates it.
     */
    private fun initTemporaryPhotoFile() {
        tempFile = OutputFile.createTemp(context, photoDirName, photoSuffix)
    }

    fun initOutputFile() = OutputFile.create(context, photoDirName, photoSuffix)

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
            RequestType.openGallery -> startPhotoEditor(data?.data)
            RequestType.takePicture -> {
                if(tempFile == null){ //temp file lost during config change, create a new reference to it
                    initTemporaryPhotoFile()
                }
                startPhotoEditor(Uri.fromFile(tempFile))
            }

            RequestType.editPhoto -> {
                
                val output = File(data!!.getStringExtra(PhotoEditorActivity.destinationPath))

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
                        return output.absolutePath
                    }

                    override fun getFileSize(): Long {
                        return output.length()
                    }

                    override fun getAudioDuration(): Long {
                        return 0
                    }

                    override fun getSenderId(): String {
                        return ""
                    }

                }

                inputListener?.onNewItem(monkeyItem)
                mPhotoFileName = null
                tempFile = null
                outputFile = null

            }
        }

        return
    }
}
