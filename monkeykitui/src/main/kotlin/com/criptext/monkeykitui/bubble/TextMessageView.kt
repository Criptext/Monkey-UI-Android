package com.criptext.monkeykitui.bubble

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView

import com.criptext.monkeykitui.*

/**
 * Created by gesuwall on 3/29/16.
 */
class TextMessageView : MonkeyView {

    override val inLayoutId : Int
        get() = R.layout.text_message_view_in
    override val outLayoutId : Int
        get() = R.layout.text_message_view_out

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    constructor(context: Context, incoming: Boolean) : super(context, incoming) {
    }


    override fun setBubbleBackground(resId: Int) {
    }

    val messageTextView: TextView
        get() = findViewById(R.id.text_message) as TextView


}
