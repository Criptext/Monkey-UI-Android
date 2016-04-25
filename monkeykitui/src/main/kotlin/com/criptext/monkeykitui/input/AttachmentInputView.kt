package com.criptext.monkeykitui.input

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import com.criptext.monkeykitui.input.listeners.OnAttachmentButtonClickListener
import com.criptext.monkeykitui.input.listeners.OnSendButtonClickListener
import com.criptext.monkeykitui.util.Utils

/**
 * Created by daniel on 4/22/16.
 */

open class AttachmentInputView : TextInputView {

    var onAttachmentButtonClickListener : OnAttachmentButtonClickListener? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var actionStrings : Array<String>? = null

    fun setActionString(actionStrings : Array<String>){
        this.actionStrings=actionStrings
    }

    override fun setLeftButton() : SideButton{
        val btn = ImageView(context)
        btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_action_attachment))
        btn.setPadding(dpToPx(5), 0, dpToPx(5), 0)

        val diameter = context.resources.getDimension(R.dimen.circle_button_diameter)
        val params = FrameLayout.LayoutParams(diameter.toInt(), diameter.toInt())
        //params.leftMargin = dpToPx(4)

        btn.layoutParams = params
        btn.setOnClickListener({

            if(actionStrings!=null){

                val items = actionStrings
                val adapter = ArrayAdapter<String>(context, android.R.layout.select_dialog_item, items)
                val builder = AlertDialog.Builder(context)

                builder.setNegativeButton(getResources().getString(R.string.text_cancel)) { dialog, which -> dialog.dismiss() }
                builder.setAdapter(adapter) { dialog, item ->
                    onAttachmentButtonClickListener?.onAttachmentButtonClickListener(item)
                    dialog.dismiss()
                }.show()

            }
            else{
                Log.e(Utils.TAG, "Please set your action strings via the following method: setActionString")
            }

        })
        return SideButton(btn, diameter.toInt())
    }

}
