package com.criptext.monkeykitui.input

import android.util.Log
import android.view.View
import android.widget.RelativeLayout

/**
 * Created by gesuwall on 4/25/16.
 */

class ViewDragger(view: View, limit: Int) {
    var view : View
    val limit : Int

    val originalRightMargin: Int

    init {
        this.view = view
        val params = view.layoutParams as RelativeLayout.LayoutParams
        originalRightMargin = params.rightMargin
        this.limit = limit

    }

    fun drag(distance: Int): Boolean{
        val params = view.layoutParams as RelativeLayout.LayoutParams
        params.rightMargin = distance
        view.requestLayout()

        return distance > limit
    }

    fun reset(){
        Log.d("ViewDragger", "drag reset")
        val params = view.layoutParams as RelativeLayout.LayoutParams
        params.rightMargin = originalRightMargin
        view.requestLayout()
    }
}