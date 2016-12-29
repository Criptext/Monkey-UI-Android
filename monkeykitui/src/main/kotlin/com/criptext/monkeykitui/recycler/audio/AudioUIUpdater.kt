package com.criptext.monkeykitui.recycler.audio

import android.support.v7.widget.RecyclerView
import com.criptext.monkeykitui.MonkeyChatFragment
import com.criptext.monkeykitui.recycler.MessagesList
import com.criptext.monkeykitui.recycler.MonkeyAdapter
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.criptext.monkeykitui.recycler.holders.MonkeyAudioHolder

/**
 * Created by gesuwall on 8/10/16.
 */

open class AudioUIUpdater(val recyclerView: RecyclerView) {
    val adapter: MonkeyAdapter
    init {
        adapter = recyclerView.adapter as MonkeyAdapter
    }

    open fun rebindAudioHolder(monkeyItem: MonkeyItem){
        adapter.rebindMonkeyItem(monkeyItem, recyclerView)
    }

    /**
     * Returns a MonkeyAudioHolder object that holds the UI for the currently playing audio message.
     * @return if there is no item being currently playing or maybe it is not visible, null will be
     * returned. Otherwise, a valid MonkeyAudioHolder object is returned
     */
    private fun getAudioHolder(adapterPosition: Int): MonkeyAudioHolder?{
        return recyclerView.findViewHolderForAdapterPosition(adapterPosition) as MonkeyAudioHolder?
    }

    open fun updateAudioProgress(monkeyItem: MonkeyItem, percentage: Int, progress: Long){
        val audioHolder = getAudioHolder(adapter.messages.getItemPositionByTimestamp(monkeyItem))
        audioHolder?.updateAudioProgress(percentage, progress)
    }
}
