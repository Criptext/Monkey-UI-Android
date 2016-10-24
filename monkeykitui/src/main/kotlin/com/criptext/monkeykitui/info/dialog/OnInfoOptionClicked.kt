package com.criptext.monkeykitui.conversation.dialog

import com.criptext.monkeykitui.recycler.MonkeyInfo

/**
 * Created by gesuwall on 9/1/16.
 */

abstract class OnInfoOptionClicked(val label: String): (MonkeyInfo) -> Unit {
    override fun toString() = label
}