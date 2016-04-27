package com.criptext.monkeykitui.input

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.SideButton
import com.criptext.monkeykitui.input.listeners.OnSendButtonClickListener

/**
 * Created by gesuwall on 4/21/16.
 */

open class TextInputView : BaseInputView {
    var onSendButtonClickListener : OnSendButtonClickListener? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setRightButton() : SideButton? {
        val diameter = context.resources.getDimension(R.dimen.circle_button_diameter)
        val btn = newCirclularSendButton(diameter)

        return SideButton(btn, diameter.toInt())
    }

    private fun newCirclularSendButton(diameter: Float): ImageView{
        val btn = ImageView(context)
        val dp5 = dpToPx(5, context)
        btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_send_now))
        btn.setPadding(dp5, 0, dp5, 0)

        val params = FrameLayout.LayoutParams(diameter.toInt(), diameter.toInt())
        val dp4 = dpToPx(4, context)
        params.rightMargin = dp4

        btn.layoutParams = params


        return btn
    }

    protected fun initSendTextButton(btn: View){
        btn.setOnClickListener({
            val inputText = editText.text.trim()
            if(!inputText.isEmpty()) {
                onSendButtonClickListener?.onSendButtonClick(inputText.toString())
                clearText()
            }
        })
    }




}
