package com.criptext.monkeykitui.input.recorder

import android.util.Log
import android.view.View

/**
 * Created by gesuwall on 4/26/16.
 */

class ViewDraggerFadeOut(view: View) : ViewDragger(view) {
    var fadeView : View? = null
    var textStartX : Float = -1f

    override fun drag(distance: Int): Boolean{
        val cociente = (distance * 0.6f).toFloat()/limit.toFloat()
        fadeView?.alpha = 1f - cociente
        if(textStartX > -1)
            fadeView?.x = textStartX - distance

        return super.drag(distance)
    }

    override fun reset() {
        fadeView?.x = textStartX
        super.reset()
    }

}
