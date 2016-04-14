package com.criptext.monkeykitui.recycler.listeners

import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by gesuwall on 4/13/16.
 */

abstract interface AudioListener {
    abstract fun onPlayButtonClicked(position: Int, item: MonkeyItem)

    abstract fun onPauseButtonClicked(position: Int, item: MonkeyItem)

    abstract fun onProgressManuallyChanged(position: Int, item: MonkeyItem, newProgress: Int)
}
