package com.criptext.monkeykitui.input

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.SideButton
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * A InputView Implementation that only sends text messages. Overrides setRightButton to insert
 * a simple button for sending text messages written with the EditText on click.
 * Created by gesuwall on 4/21/16.
 */

open class TextInputView : BaseInputView {

    open var inputListener : InputListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setRightButton(a : AttributeHandler) : SideButton? {
        val diameter = context.resources.getDimension(R.dimen.default_inputview_height)
        val btn = newCirclularSendButton(diameter, a)

        initSendTextButton(btn)
        return SideButton(btn, diameter.toInt())
    }

    /**
     * Creates an ImageView with the size needed to fit the InputView
     */
    private fun newCirclularSendButton(diameter: Float, a: AttributeHandler): ImageView{
        val btn = ImageView(context)
        val dp5 = dpToPx(5, context)
        if (a.sendTextDrawableInputView != -1)
            btn.setImageDrawable(ContextCompat.getDrawable(context, a.sendTextDrawableInputView))
        else
            btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_send_now))
        btn.setPadding(dp5, 0, dp5, 0)

        val params = FrameLayout.LayoutParams(diameter.toInt(), diameter.toInt())
        val dp4 = dpToPx(4, context)
        params.rightMargin = dp4

        btn.layoutParams = params


        return btn
    }

    /**
     * Creates a view to be used as button for sending text messages written with the InputView's
     * EditText. On click, the InputListener's onNewItem() method is called with the new text message.
     */
    open protected fun initSendTextButton(btn: View){
        btn.setOnClickListener({
            val inputText = editText.text.trim()
            if(!inputText.isEmpty()) {

                var monkeyItem = object : com.criptext.monkeykitui.recycler.MonkeyItem{

                    override fun getMessageTimestamp(): Long {
                        return System.currentTimeMillis()
                    }

                    override fun getMessageId(): String {
                        return "" + System.currentTimeMillis()
                    }

                    override fun isIncomingMessage(): Boolean {
                        return false
                    }

                    override fun getOutgoingMessageStatus(): MonkeyItem.OutgoingMessageStatus {
                        throw UnsupportedOperationException()
                    }

                    override fun getMessageType(): Int {
                        return MonkeyItem.MonkeyItemType.text.ordinal
                    }

                    override fun getMessageText(): String {
                        return inputText.toString()
                    }

                    override fun getPlaceholderFilePath(): String {
                        throw UnsupportedOperationException()
                    }

                    override fun getFilePath(): String {
                        throw UnsupportedOperationException()
                    }

                    override fun getFileSize(): Long {
                        throw UnsupportedOperationException()
                    }

                    override fun getAudioDuration(): Long {
                        throw UnsupportedOperationException()
                    }

                    override fun getContactSessionId(): String {
                        throw UnsupportedOperationException()
                    }

                }

                inputListener?.onNewItem(monkeyItem)

                clearText()
            }
        })
    }

}
