package com.criptext.monkeykitui.input

import android.util.Log
import android.view.View
import android.widget.RelativeLayout

/**
 * Created by gesuwall on 4/25/16.
 */

open class ViewDragger(view: View) {
    var view : View
    val limit : Int
    private var startX : Float

    val originalRightMargin: Int

    init {
        this.view = view
        startX = view.x
        val params = view.layoutParams as RelativeLayout.LayoutParams
        originalRightMargin = params.rightMargin
        this.limit = BaseInputView.dpToPx(100, view.context)

    }

    open fun drag(distance: Int): Boolean{
        //val params = view.layoutParams as RelativeLayout.LayoutParams
        //params.rightMargin = distance
        //view.requestLayout()

        view.x = startX - distance
        return distance > limit
    }

    open fun reset(){
        Log.d("ViewDragger", "drag reset")
        val params = view.layoutParams as RelativeLayout.LayoutParams
        params.rightMargin = originalRightMargin
        view.requestLayout()
    }
}