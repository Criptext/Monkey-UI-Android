package com.criptext.monkeykitui.recycler.listeners

import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by gesuwall on 4/13/16.
 */

abstract interface OnLongClickMonkeyListener {

    abstract fun onLongClick(position: Int, item: MonkeyItem)
}
