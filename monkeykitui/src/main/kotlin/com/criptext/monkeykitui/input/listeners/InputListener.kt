package com.criptext.monkeykitui.input.listeners

import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by daniel on 4/29/16.
 */

interface InputListener {
    fun onNewItem(item : MonkeyItem)

    fun onNewItemFileError(type: Int)
}
