package com.criptext.monkeykitui.input

import android.util.Log
import android.view.View

/**
 * Created by gesuwall on 4/26/16.
 */

class ViewDraggerFadeOut(view: View) : ViewDragger(view) {
    var fadeView : View? = null

    override fun drag(distance: Int): Boolean{
        val cociente = (distance * 0.6f).toFloat()/limit.toFloat()
        fadeView?.alpha = 1f - cociente
        return super.drag(distance)
    }

}
