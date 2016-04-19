package com.criptext.monkeykitui.recycler.holders

import android.view.View
import android.widget.Button
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 4/18/16.
 */

class MonkeyEndHolder : MonkeyHolder {
    var loadMoreButton : Button

    constructor(view: View) : super(view){
        loadMoreButton = view.findViewById(R.id.load_more_btn) as Button
    }

    fun setOnClickListener(listener: () -> Unit){
        loadMoreButton.setOnClickListener({
            listener.invoke()
        })
    }

}
