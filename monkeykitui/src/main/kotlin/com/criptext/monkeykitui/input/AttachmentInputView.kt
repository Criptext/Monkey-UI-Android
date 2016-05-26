package com.criptext.monkeykitui.input

import android.content.Context
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

    override fun setLeftButton(a : TypedArray) : SideButton?{
        val attachBtn = AttachmentButton(context, a)
        return SideButton(attachBtn, attachBtn.diameter)
    }

}
