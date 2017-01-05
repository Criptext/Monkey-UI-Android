package com.criptext.monkeykitui

import android.graphics.Color
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/**
 * Created by hirobreak on 03/01/17.
 */
@Implements(Color::class)
class ShadowColor {

    companion object{

        @Implementation
        fun rgb(r : Int, g : Int, b : Int) : Int{
            return r + g + b;
        }

    }

}