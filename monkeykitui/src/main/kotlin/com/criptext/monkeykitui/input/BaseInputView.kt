package com.criptext.monkeykitui.input

import android.content.Context
import android.graphics.Color
import android.text.InputType
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.SideButton

/**
 * Created by gesuwall on 4/21/16.
 */

open class BaseInputView : FrameLayout {
    protected lateinit var editText : EditText

    constructor(context: Context?) : super(context){
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    open protected fun init(){
        editText = EditText(context);
        editText.maxLines = 4
        editText.hint = context.resources.getString(R.string.text_message_write_hint)
        editText.setTextColor(Color.BLACK)
        editText.setEms(10)
        editText.inputType = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        val params = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER_VERTICAL

        editText.layoutParams = params
        addView(editText)

        val leftBtn = setLeftButton()
        if(leftBtn != null) {
            params.leftMargin = leftBtn.visibleWidth
            (leftBtn.button.layoutParams as LayoutParams).gravity = Gravity.LEFT or Gravity.BOTTOM
            addView(leftBtn.button)
        }

        val rightBtn = setRightButton()
        if(rightBtn != null) {
            params.rightMargin = rightBtn.visibleWidth
            (rightBtn.button.layoutParams as LayoutParams).gravity = Gravity.RIGHT or Gravity.BOTTOM
            addView(rightBtn.button)
        }

    }

    open protected fun setLeftButton() : SideButton? = null
    open protected fun setRightButton() : SideButton? = null

    fun View.dpToPx(dp : Int) : Int{
        val displayMetrics = context.resources.displayMetrics;
        val px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;

    }

    fun clearText(){
        editText.text.clear()
        //val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //imm.hideSoftInputFromWindow(editText.windowToken, 0);
    }


}
