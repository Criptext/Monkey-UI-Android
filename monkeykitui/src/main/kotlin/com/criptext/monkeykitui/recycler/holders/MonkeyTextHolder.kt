package com.criptext.monkeykitui.recycler.holders

import android.view.View
import android.widget.TextView
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 4/12/16.
 */

class MonkeyTextHolder : MonkeyHolder {
    var messageTextView : TextView? = null

    constructor(view : View) : super(view) {
        messageTextView = view.findViewById(R.id.text_message) as TextView
    }

}
