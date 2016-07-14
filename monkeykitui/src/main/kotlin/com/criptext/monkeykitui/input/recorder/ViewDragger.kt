package com.criptext.monkeykitui.input.recorder

import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.criptext.monkeykitui.R

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
        this.limit = view.context.resources.getDimension(R.dimen.audio_btn_expanded_height).toInt()

    }

    open fun drag(distance: Int): Boolean{
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