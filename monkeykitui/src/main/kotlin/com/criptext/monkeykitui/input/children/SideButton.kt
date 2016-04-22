package com.criptext.monkeykitui.input.children

import android.view.View

/**
 * Created by gesuwall on 4/21/16.
 */

class SideButton(button : View, visibleWidth : Int) {
    val button : View
    val visibleWidth : Int

    init {
        this.button = button
        this.visibleWidth = visibleWidth
    }
}
