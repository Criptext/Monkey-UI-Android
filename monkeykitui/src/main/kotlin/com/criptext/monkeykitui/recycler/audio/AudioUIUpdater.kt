package com.criptext.monkeykitui.recycler.audio

import android.support.v7.widget.RecyclerView
import com.criptext.monkeykitui.recycler.MonkeyAdapter
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.criptext.monkeykitui.recycler.holders.MonkeyAudioHolder

/**
 * Created by gesuwall on 8/10/16.
 */

class AudioUIUpdater(val recyclerView: RecyclerView) {
    val adapter: MonkeyAdapter

    init {
        adapter = recyclerView.adapter as MonkeyAdapter
    }
    fun rebindAudioHolder(monkeyItem: MonkeyItem){
        val adapterPosition = adapter.getItemPositionByTimestamp(monkeyItem)
        val audioHolder = getAudioHolder(adapterPosition)
        if (audioHolder != null)
            adapter.onBindViewHolder(audioHolder, adapterPosition)
    }

    /**
     * Returns a MonkeyAudioHolder object that holds the UI for the currently playing audio message.
     * @return if there is no item being currently playing or maybe it is not visible, null will be
     * returned. Otherwise, a valid MonkeyAudioHolder object is returned
     */
    open protected fun getAudioHolder(adapterPosition: Int): MonkeyAudioHolder?{
        return recyclerView.findViewHolderForAdapterPosition(adapterPosition) as MonkeyAudioHolder?
    }

    fun updateAudioProgress(monkeyItem: MonkeyItem, percentage: Int, progress: Long){
        val audioHolder = getAudioHolder(adapter.getItemPositionByTimestamp(monkeyItem))
        audioHolder?.updateAudioProgress(percentage, progress)
    }
}
