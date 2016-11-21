package com.criptext.monkeykitui.input

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.util.AttributeSet
import com.criptext.monkeykitui.input.attachment.AttachmentButton
import com.criptext.monkeykitui.input.attachment.CameraHandler
import com.criptext.monkeykitui.input.children.SideButton
import com.criptext.monkeykitui.input.listeners.InputListener

/**
 * Created by daniel on 4/22/16.
 */

open class AttachmentInputView : TextInputView {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val cameraHandler: CameraHandler
    get() = attachmentButton.cameraHandler

    open val attachmentButton: AttachmentButton
    get() = leftButtonView as AttachmentButton

    override var inputListener: InputListener?
        get() = super.inputListener
        set(value) {
            attachmentButton.inputListener = value
            super.inputListener = value
        }

    override fun setLeftButton(typedArray: TypedArray) : SideButton?{
        val attachBtn = AttachmentButton(context, typedArray)
        return SideButton(attachBtn, attachBtn.diameter)
    }

    override fun onRequestPermissionsResult(requestCode: Int, result: IntArray) {
        if(requestCode == AttachmentButton.REQUEST_CAMERA &&
                result[0] == PackageManager.PERMISSION_GRANTED &&
                result[1] == PackageManager.PERMISSION_GRANTED)
            attachmentButton.cameraHandler.takePicture()
        else if(requestCode == AttachmentButton.REQUEST_GALLERY &&
                result[0] == PackageManager.PERMISSION_GRANTED &&
                result[1] == PackageManager.PERMISSION_GRANTED)
            attachmentButton.cameraHandler.pickFromGallery()
    }
}
