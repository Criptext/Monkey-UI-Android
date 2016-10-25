package com.criptext.monkeykitui.recycler.holders

import android.view.View
import android.widget.Button
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.util.Utils

/**
 * Created by gesuwall on 4/18/16.
 */

open class MonkeyEndHolder : MonkeyHolder {

    constructor(view: View) : super(view){
    }

    fun adjustHeight(matchParentHeight: Boolean) {
        Utils.adjustHeight(itemView, matchParentHeight)
    }

    open fun setOnClickListener(listener: () -> Unit){

    }

}
