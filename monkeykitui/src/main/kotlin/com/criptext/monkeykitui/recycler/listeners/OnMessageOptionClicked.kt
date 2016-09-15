package com.criptext.monkeykitui.recycler.listeners

import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by gesuwall on 9/13/16.
 */
abstract class OnMessageOptionClicked(val label: String): (MonkeyItem) -> Unit {
    override fun toString() = label
}
