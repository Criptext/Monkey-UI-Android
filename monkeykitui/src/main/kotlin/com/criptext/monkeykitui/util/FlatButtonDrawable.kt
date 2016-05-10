package com.criptext.monkeykitui.util

import android.R
import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.util.Log
import android.util.TypedValue

/**
 * Created by gesuwall on 5/5/16.
 */

class FlatButtonDrawable {

    companion object {
        private fun getDarkerFactor(color: Int, darkFactor: Double): Int{
            var hsv = FloatArray(3)
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.75f;
            return Color.HSVToColor(hsv);
        }
        fun getDarkerColor(color: Int, darkFactor: Double): Int{
            var red=   color.shr(16).and(0xFF)
            var oldRed = red
            red = getDarkerFactor(red, darkFactor)
            Log.d("newDrawable","before: $oldRed after: $red")
            var green= color.shr(8).and(0xFF)
            green = getDarkerFactor(green, darkFactor)
            var blue=  color.shr(0).and(0xFF)
            blue = getDarkerFactor(blue, darkFactor)
            var alpha= color.shr(24).and(0xFF)

            return Color.argb(alpha, red, green, blue)


        }
        fun new(ctx: Context): StateListDrawable{

            val typedValue = TypedValue();
            val a = ctx.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimary))
            val color = a.getColor(0, 0);
            a.recycle();
            return new(color)
        }
        fun new(bgColor: Int) : StateListDrawable{
           return new(bgColor, getDarkerColor(bgColor, 80.0))
        }

        fun new(bgColor: Int, pressedColor: Int) : StateListDrawable{
            val drawable = StateListDrawable()
            val idleDrawable = ColorDrawable(bgColor)
            val pressedDrawable = ColorDrawable(pressedColor)
            Log.d("newDrawable", "idle: $bgColor pressed: $pressedColor")
            drawable.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
            drawable.addState(intArrayOf(), idleDrawable)
            return drawable
        }


    }
}
