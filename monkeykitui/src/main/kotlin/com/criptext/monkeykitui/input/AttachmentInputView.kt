package com.criptext.monkeykitui.input

import android.app.AlertDialog
import android.content.Context
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.AttachmentButton
import com.criptext.monkeykitui.input.children.SideButton
import com.criptext.monkeykitui.input.listeners.CameraListener
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.input.listeners.OnAttachmentButtonClickListener
import com.criptext.monkeykitui.recycler.MonkeyItem
import java.util.*

/**
 * Created by daniel on 4/22/16.
 */

open class AttachmentInputView : TextInputView {

    var cameraHandler : CameraHandler? = null

    var attachmentsButtons : ArrayList<AttachmentButton>? = null
    
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun addNewAttachmentButton(attachmentButton: AttachmentButton){

        if(attachmentsButtons == null)
            attachmentsButtons = ArrayList()

        attachmentsButtons?.add(attachmentButton)
    }

    fun triggerClickButtonAttachment(indice : Int){
        if(indice >= 0){
            attachmentsButtons?.get(indice)?.clickButton()
        }
    }

    override fun setLeftButton(a : AttributeHandler) : SideButton{

        val btn = ImageView(context)
        if (a.attachmentDrawableInputView != -1)
            btn.setImageDrawable(ContextCompat.getDrawable(context, a.attachmentDrawableInputView))
        else
            btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_attachment))

        val dp5 = dpToPx(5, context)
        btn.setPadding(dp5, 0, dp5, 0)

        val diameter = context.resources.getDimension(R.dimen.circle_button_diameter)
        val params = FrameLayout.LayoutParams(diameter.toInt(), diameter.toInt())
        //params.leftMargin = dpToPx(4)

        btn.layoutParams = params
        btn.setOnClickListener({

            cameraHandler = CameraHandler(context)
            cameraHandler?.setCameraListen(object : CameraListener {
                override fun onNewItem(item: MonkeyItem) {
                    inputListener?.onNewItem(item)
                }
            })

            val items = cameraHandler?.defaultActionStrings
            if(attachmentsButtons!=null){
                attachmentsButtons?.let {
                    for (button in it) {
                        items?.add(button.getTitle())
                    }
                }
            }

            val adapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item, items)
            val builder = AlertDialog.Builder(context)

            builder.setNegativeButton(resources.getString(R.string.text_cancel)) { dialog, which -> dialog.dismiss() }
            builder.setAdapter(adapter) { dialog, item ->
                when (item){
                    0 -> {
                        cameraHandler?.takePicture()
                    }
                    1 -> {
                        cameraHandler?.pickFromGallery()
                    }
                }
                if(attachmentsButtons!=null){
                    triggerClickButtonAttachment(item-2)
                }
                dialog.dismiss()
            }.show()

        })
        return SideButton(btn, diameter.toInt())
    }

}
