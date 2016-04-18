package com.criptext.monkeykitui.recycler.audio

import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by gesuwall on 4/15/16.
 */

class PlayingItem(position : Int, item : MonkeyItem) {
    val item : MonkeyItem
    val position : Int

    init{
        this.item = item
        this.position = position
    }
}
