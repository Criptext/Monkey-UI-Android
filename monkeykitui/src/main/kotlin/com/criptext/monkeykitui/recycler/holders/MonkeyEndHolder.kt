package com.criptext.monkeykitui.recycler.holders

import android.view.View
import android.widget.Button
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 4/18/16.
 */

open class MonkeyEndHolder : MonkeyHolder {

    constructor(view: View) : super(view){
    }

    open fun setOnClickListener(listener: () -> Unit){

    }

}
