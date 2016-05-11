package com.criptext.monkeykitui.recycler.audio

import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by gesuwall on 4/15/16.
 */

class PlayingItem(item : MonkeyItem) {
    val item : MonkeyItem
    var lastPlaybackPosition: Int

    init{
        this.item = item
        lastPlaybackPosition = 0
    }
}
