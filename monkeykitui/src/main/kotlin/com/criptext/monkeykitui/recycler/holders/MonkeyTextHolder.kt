package com.criptext.monkeykitui.recycler.holders

import android.view.View
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.bubble.MonkeyView
import com.criptext.monkeykitui.bubble.TextMessageView

/**
 * Created by gesuwall on 4/12/16.
 */

class MonkeyTextHolder : MonkeyHolder {
    var messageTextView : TextView? = null

    constructor(view : View) : super(view) {
        messageTextView = view.findViewById(R.id.text_message) as TextView
    }

    constructor(view : MonkeyView, type : Int) : super(view, type) {
        val tmv = view as TextMessageView
        messageTextView = tmv.messageTextView
    }
}
