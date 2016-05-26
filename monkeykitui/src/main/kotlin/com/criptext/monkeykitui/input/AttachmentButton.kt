package com.criptext.monkeykitui.input

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.AttachmentOption
import com.criptext.monkeykitui.input.children.SideButton
import com.criptext.monkeykitui.input.listeners.CameraListener
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.MonkeyItem
import java.util.*

/**
 * Created by daniel on 5/10/16.
 */

open class AttachmentButton : ImageView{

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

    lateinit var cameraHandler: CameraHandler
    var inputListener: InputListener? = null

    var defaultCameraOptionLabel = "Take Picture"
    var defaultGalleryOptionLabel = "Choose Picture"

    private lateinit var attachmentsButtons : ArrayList<AttachmentOption>

    fun initialize(typedArray: TypedArray?) {
        cameraHandler = CameraHandler(context)

        attachmentsButtons = ArrayList(2)
        if (typedArray?.getBoolean(R.styleable.InputView_useDefaultCamera, true) ?: true)
            attachmentsButtons.add(object : AttachmentOption(defaultCameraOptionLabel) {
                override fun onOptionSelected() {
                    cameraHandler.takePicture()
                }
            })

        if (typedArray?.getBoolean(R.styleable.InputView_useDefaultGallery, true) ?: true)
            attachmentsButtons.add(object : AttachmentOption(defaultGalleryOptionLabel) {
                override fun onOptionSelected() {
                    cameraHandler.pickFromGallery()
                }
            })

        val customDrawable = typedArray?.getDrawable(R.styleable.InputView_attachmentDrawable)
        setImageDrawable(customDrawable ?:
                        ContextCompat.getDrawable(context, R.drawable.ic_action_attachment))

        val dp5 = BaseInputView.dpToPx(5, context)
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

            val adapter = ArrayAdapter<AttachmentOption>(context, android.R.layout.select_dialog_item, attachmentsButtons)
            val builder = AlertDialog.Builder(context)

            builder.setNegativeButton(resources.getString(R.string.text_cancel)) { dialog, which -> dialog.dismiss() }
            builder.setAdapter(adapter, { dialog, item ->
                val selectedOption = attachmentsButtons[item]
                selectedOption.onOptionSelected()
            }).show()
        })
    }

    fun triggerClickButtonAttachment(indice : Int){
        if(indice >= 0 && indice < attachmentsButtons.size){
            attachmentsButtons[indice].onOptionSelected()
        }
    }

    fun addNewAttachmentOption(attachmentButton: AttachmentOption){
        attachmentsButtons.add(attachmentButton)
    }

    open val diameter: Int
    get() = context.resources.getDimension(R.dimen.circle_button_diameter).toInt()

}