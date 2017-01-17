package com.criptext.monkeykitui

import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

/**
 * Created by gesuwall on 1/12/17.
 */

@Implements(className="android.support.v7.app.ToolbarActionBar")
class ShadowActionBar {

    var affordanceDisplayed = false
    private set

    fun clear() {
        affordanceDisplayed = false
    }

    @Implementation
    fun setDisplayHomeAsUpEnabled(showHomeAsUp: Boolean) {
        affordanceDisplayed = showHomeAsUp
    }
}