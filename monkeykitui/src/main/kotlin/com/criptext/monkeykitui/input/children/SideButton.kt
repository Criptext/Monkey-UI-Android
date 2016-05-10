package com.criptext.monkeykitui.input.children

import android.view.View

/**
 * A class used to hold a view that will be placed either to the left or right of an InputView and
 * the distance that should be between the InputView's EditText and the screen to make enough room
 * for the view. Since InputView uses a FrameLayout, this distance needs to be declared explicitly to
 * avoid having the view draw on top of the EditText.
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
