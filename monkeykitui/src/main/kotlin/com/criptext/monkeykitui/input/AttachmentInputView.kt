package com.criptext.monkeykitui.input

import android.content.Context
import android.util.AttributeSet
import com.criptext.monkeykitui.input.children.SideButton
/**
 * Created by daniel on 4/22/16.
 */

open class AttachmentInputView : TextInputView {

    var cameraHandler : CameraHandler? = null

    lateinit var attachmentHandler : AttachmentHandler

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setLeftButton(a : AttributeHandler) : SideButton?{
        cameraHandler = CameraHandler(context)
        attachmentHandler = AttachmentHandler()
        return attachmentHandler.getLeftButton(a, context, this, cameraHandler, resources)
    }

}
