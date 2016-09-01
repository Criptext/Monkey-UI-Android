package com.criptext.monkeykitui.input.attachment

import android.app.AlertDialog
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.BaseInputView
import com.criptext.monkeykitui.input.attachment.CameraHandler
import com.criptext.monkeykitui.dialog.DialogOption
import com.criptext.monkeykitui.dialog.SimpleDialog
import com.criptext.monkeykitui.input.listeners.CameraListener
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.MonkeyItem
import java.util.*

/**
 * Created by daniel on 5/10/16.
 */

open class AttachmentButton : ImageView {

    lateinit var cameraHandler: CameraHandler
    var inputListener: InputListener? = null

    var cameraOptionLabel = defaultCameraOptionLabel
    var galleryOptionLabel = defaultGalleryOptionLabel

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


    fun initialize(typedArray: TypedArray?) {
        cameraHandler = CameraHandler(context)

        attachmentOptions = ArrayList(2)
        cameraOptionLabel = typedArray?.getString(R.styleable.InputView_cameraOptionLabel) ?: defaultCameraOptionLabel
        if (typedArray?.getBoolean(R.styleable.InputView_useDefaultCamera, true) ?: true)
            attachmentOptions.add(object : DialogOption(cameraOptionLabel) {
                override fun onOptionSelected() {
                    cameraHandler.takePicture()
                }
            })

        galleryOptionLabel = typedArray?.getString(R.styleable.InputView_galleryOptionLabel) ?: defaultGalleryOptionLabel
        if (typedArray?.getBoolean(R.styleable.InputView_useDefaultGallery, true) ?: true)
            attachmentOptions.add(object : DialogOption(galleryOptionLabel) {
                override fun onOptionSelected() {
                    cameraHandler.pickFromGallery()
                }
            })

        val customDrawable = typedArray?.getDrawable(R.styleable.InputView_attachmentDrawable)
        setImageDrawable(customDrawable ?:
                        ContextCompat.getDrawable(context, R.drawable.ic_action_attachment))

        val dp5 = context.resources.getDimension(R.dimen.attach_button_padding).toInt()
        setPadding(dp5, 0, dp5, 0)
        val diameter = diameter
        val params = FrameLayout.LayoutParams(diameter, diameter)

        layoutParams = params
        setOnClickListener({
            cameraHandler.setCameraListen(object : CameraListener {
                override fun onNewItem(item: MonkeyItem) {
                    inputListener?.onNewItem(item)
                }
            })

            SimpleDialog(attachmentOptions).show(context)
        })
    }

    open val diameter: Int
    get() = context.resources.getDimension(R.dimen.circle_button_diameter).toInt()

    companion object {
        val defaultCameraOptionLabel = "Take Picture"
        val defaultGalleryOptionLabel = "Choose Picture"

    }
}