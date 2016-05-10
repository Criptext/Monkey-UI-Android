package com.criptext.monkeykitui.input

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.support.v4.content.ContextCompat
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.AttachmentButton
import com.criptext.monkeykitui.input.children.SideButton
import com.criptext.monkeykitui.input.listeners.CameraListener
import com.criptext.monkeykitui.recycler.MonkeyItem
import java.util.*

/**
 * Created by daniel on 5/10/16.
 */

open class AttachmentHandler{

    var attachmentsButtons : ArrayList<AttachmentButton>? = null

    fun triggerClickButtonAttachment(indice : Int){
        if(indice >= 0){
            attachmentsButtons?.get(indice)?.clickButton()
        }
    }

    fun addNewAttachmentButton(attachmentButton: AttachmentButton){

        if(attachmentsButtons == null)
            attachmentsButtons = ArrayList()

        attachmentsButtons?.add(attachmentButton)
    }

    open fun getLeftButton(a : AttributeHandler, context: Context, textInputView: TextInputView, cameraHandler : CameraHandler?, resources: Resources) : SideButton? {

        val btn = ImageView(context)
        if (a.attachmentDrawableInputView != -1)
            btn.setImageDrawable(ContextCompat.getDrawable(context, a.attachmentDrawableInputView))
        else
            btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_attachment))

        val dp5 = BaseInputView.dpToPx(5, context)
        btn.setPadding(dp5, 0, dp5, 0)

        val diameter = context.resources.getDimension(R.dimen.circle_button_diameter)
        val params = FrameLayout.LayoutParams(diameter.toInt(), diameter.toInt())
        //params.leftMargin = dpToPx(4)

        btn.layoutParams = params
        btn.setOnClickListener({

            cameraHandler?.setCameraListen(object : CameraListener {
                override fun onNewItem(item: MonkeyItem) {
                    textInputView.inputListener?.onNewItem(item)
                }
            })

            val items = ArrayList<String>();
            if(a.useDefaultCameraInputView && !a.useDefaultGalleryInputView)
                items.add("Take a Photo")
            if(!a.useDefaultCameraInputView && a.useDefaultGalleryInputView)
                items.add("Choose Photo")
            if(a.useDefaultCameraInputView && a.useDefaultGalleryInputView) {
                items.add("Take a Photo")
                items.add("Choose Photo")
            }
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
                var totalOptionsDefault = 0
                if(a.useDefaultCameraInputView || a.useDefaultGalleryInputView)
                    totalOptionsDefault = 1;
                if(a.useDefaultCameraInputView && a.useDefaultGalleryInputView)
                    totalOptionsDefault = 2;

                if(totalOptionsDefault==2){
                    when (item){
                        0 -> {
                            cameraHandler?.takePicture()
                        }
                        1 -> {
                            cameraHandler?.pickFromGallery()
                        }
                    }
                }
                if(totalOptionsDefault==1 && a.useDefaultCameraInputView){
                    when (item){
                        0 -> {
                            cameraHandler?.takePicture()
                        }
                    }
                }
                if(totalOptionsDefault==1 && a.useDefaultGalleryInputView){
                    when (item){
                        0 -> {
                            cameraHandler?.pickFromGallery()
                        }
                    }
                }
                if(attachmentsButtons!=null){
                    triggerClickButtonAttachment(item-totalOptionsDefault)
                }
                dialog.dismiss()
            }.show()

        })
        return SideButton(btn, diameter.toInt())
    }

}