package com.criptext.monkeykitui.input

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.SideButton
import com.criptext.monkeykitui.input.listeners.CameraListener
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.criptext.monkeykitui.util.Utils

/**
 * Created by daniel on 4/22/16.
 */

open class MediaInputView : AudioInputView {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    open val cameraHandler: CameraHandler
    get() = attachmentButton.cameraHandler

    private val attachmentButton: AttachmentButton
    get() = leftButtonView as AttachmentButton

    override var inputListener: InputListener?
        get() = super.inputListener
        set(value) {
            attachmentButton.inputListener = value
            super.inputListener = value
        }

    override fun setLeftButton(a : TypedArray) : SideButton?{
        val attachBtn = AttachmentButton(context, a)
        attachBtn.inputListener = inputListener
        return SideButton(attachBtn, attachBtn.diameter)
    }

}
