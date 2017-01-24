package com.criptext.monkeykitui.input.attachment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.BaseInputView
import com.criptext.monkeykitui.input.attachment.CameraHandler
import com.criptext.monkeykitui.dialog.DialogOption
import com.criptext.monkeykitui.dialog.SimpleDialog
import com.criptext.monkeykitui.input.listeners.CameraListener
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import java.io.File
import java.util.*
import java.util.regex.Pattern

/**
 * Created by daniel on 5/10/16.
 */

open class AttachmentButton : ImageView {

    lateinit var cameraHandler: CameraHandler
    var inputListener: InputListener? = null
        set(value) {
            cameraHandler.inputListener = value
            field = value
        }

    val LOAD_FILE = 777
    lateinit var cameraOptionLabel: String
    lateinit var galleryOptionLabel: String
    lateinit var fileOptionLabel : String

    lateinit var attachmentOptions: ArrayList<DialogOption>
    private set

    constructor(context: Context): super(context){
        initialize(null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet){
        val a = context.theme.obtainStyledAttributes(attributeSet, R.styleable.InputView, 0, 0)
        initialize(a)
    }

    constructor(context: Context, typedArray: TypedArray) : super(context){
        initialize(typedArray)
    }

    fun hasPermissionsToTakePicture(ctx: Context) =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
    fun hasPermissionsToOpenGallery(ctx: Context) =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED

    fun initialize(typedArray: TypedArray?) {
        cameraHandler = CameraHandler(context)

        attachmentOptions = ArrayList(3)
        cameraOptionLabel = typedArray?.getString(R.styleable.InputView_cameraOptionLabel) ?:
                resources.getString(R.string.mk_take_picture)
        if (typedArray?.getBoolean(R.styleable.InputView_useDefaultCamera, true) ?: true)
            attachmentOptions.add(object : DialogOption(cameraOptionLabel) {
                override fun onOptionSelected() {
                    if(hasPermissionsToTakePicture(context))
                        cameraHandler.takePicture()
                    else
                        ActivityCompat.requestPermissions(context as Activity,
                                arrayOf(Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA)

                }
            })

        galleryOptionLabel = typedArray?.getString(R.styleable.InputView_galleryOptionLabel) ?:
                resources.getString(R.string.mk_choose_picture)
        if (typedArray?.getBoolean(R.styleable.InputView_useDefaultGallery, true) ?: true)
            attachmentOptions.add(object : DialogOption(galleryOptionLabel) {
                override fun onOptionSelected() {
                    if(hasPermissionsToOpenGallery(context))
                        cameraHandler.pickFromGallery()
                    else
                        ActivityCompat.requestPermissions(context as Activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_GALLERY)
                }
            })

        fileOptionLabel = resources.getString(R.string.mk_choose_file)
        attachmentOptions.add(object : DialogOption(fileOptionLabel) {
            override fun onOptionSelected() {
                MaterialFilePicker().withActivity(context as Activity)
                        .withRequestCode(LOAD_FILE)
                        .start()
            }
        })


        val customDrawable = typedArray?.getDrawable(R.styleable.InputView_attachmentDrawable)
        setImageDrawable(customDrawable ?:
                        ContextCompat.getDrawable(context, R.drawable.ic_attach_file))
        setColorFilter(context.resources.getColor(R.color.mk_icon_unfocus_tint))
        alpha = 0.7f
        val dp5 = context.resources.getDimension(R.dimen.attach_button_padding).toInt()
        setPadding(dp5, 0, dp5, 0)
        val diameter = diameter
        val params = FrameLayout.LayoutParams(diameter, diameter)

        layoutParams = params
        setOnClickListener({
            SimpleDialog(attachmentOptions).show(context)
        })

        cameraHandler.inputListener = inputListener
    }

    open val diameter: Int
    get() = context.resources.getDimension(R.dimen.default_inputview_icon_height).toInt()


    companion object {
        val REQUEST_CAMERA = 5001
        val REQUEST_GALLERY = 5002

    }

    fun  onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val output = File(data!!.getStringExtra(FilePickerActivity.RESULT_FILE_PATH))
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
                return MonkeyItem.MonkeyItemType.file.ordinal
            }

            override fun getMessageText(): String {
                return output.name
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

            override fun getConversationId(): String {
                return ""
            }

        }

        inputListener?.onNewItem(monkeyItem)

    }

}