package com.criptext.monkeykitui.recycler.audio

import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.criptext.monkeykitui.recycler.MonkeyAdapter
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.criptext.monkeykitui.recycler.holders.MonkeyAudioHolder
import com.criptext.monkeykitui.recycler.listeners.AudioListener
import org.jetbrains.annotations.NotNull
import java.io.File
import java.io.IOException

/**
 * Created by gesuwall on 4/15/16.
 */

class AudioPlaybackHandler(monkeyAdapter : MonkeyAdapter, recyclerView: RecyclerView) {
    val handler : Handler
    val player : MediaPlayer
    var currentlyPlayingItem : PlayingItem?
    private set
    var recycler : RecyclerView
    private set
    var adapter : MonkeyAdapter
    private set
    var playingAudio : Boolean
    private set
    var updateProgressEnabled : Boolean


    val playerRunnable : Runnable

    val playbackProgress : Int
    get() = 100 * player.currentPosition / player.duration;

    val playbackProgressText : String
    get() {
        val progress = player.currentPosition/ 1000;
        var res = "00:";
        if(progress < 10)
            res += "0";
        return res + progress;
    }

    init {
        currentlyPlayingItem = null
        playingAudio = false
        recycler = recyclerView
        this.adapter = monkeyAdapter
        adapter.audioHandler = this
        updateProgressEnabled = true

        handler = Handler()
        playerRunnable = object : Runnable {
            override fun run() {
                val playingItem = currentlyPlayingItem
                if (playingAudio && playingItem != null) {
                    if(updateProgressEnabled) updateAudioSeekbar(recycler,
                            playingItem.position, playbackProgress, playbackProgressText)
                    handler.postDelayed(this, 67)
                }
            }
        }

        player = MediaPlayer()
        player.setOnPreparedListener {
            player.start();
            playingAudio = true;
            playerRunnable.run();
        }

        player.setOnCompletionListener {
            adapter.cachedAudioHolder = null
            notifyPlaybackStopped()
        }

        adapter.audioListener = (object : AudioListener {
            override fun onPauseButtonClicked(position: Int, item: MonkeyItem) {
                handler.removeCallbacks(playerRunnable);
                player.pause();
                playingAudio = false;
                adapter.notifyDataSetChanged();
            }

            override fun onPlayButtonClicked(position: Int, item: MonkeyItem) {
                if ( currentlyPlayingItem?.item?.getMessageId().equals(item.getMessageId())) {//Resume playback
                    player.start();
                    playingAudio = true;
                    playerRunnable.run();
                    adapter.notifyDataSetChanged();
                } else {//Start from beggining
                    adapter.cachedAudioHolder = null
                    player.reset();
                    currentlyPlayingItem = PlayingItem(position, item)
                    playingAudio = true;
                    adapter.notifyDataSetChanged();
                    try {
                        player.setDataSource(adapter.mContext, Uri.fromFile(File(item.getFilePath())));
                        player.prepareAsync();
                    } catch (ex: IOException) {
                        ex.printStackTrace();
                    }
                }
            }

            override fun onProgressManuallyChanged(position: Int, item: MonkeyItem, newProgress: Int) {
                player.seekTo(newProgress * player.duration / 100)
            }

        });
    }

    fun updateAudioSeekbar(recycler: RecyclerView, position: Int, progress: Int, progressText: String){
        fun getVisibleItemView(): View?{
            if(recycler.childCount > 0){
                val manager = recycler.layoutManager as LinearLayoutManager
                val start = manager.findFirstVisibleItemPosition()
                val end = manager.findLastVisibleItemPosition()
                if(position < start || position > end)
                    return null
                return recycler.getChildAt(position - start)
            }
            return null
        }

        val itemView = getVisibleItemView()
        val audioHolder = adapter.cachedAudioHolder
        if(audioHolder != null){
            audioHolder.updateAudioProgress(progress, progressText)
        } else if(itemView != null){
            Log.d("Seekbar", "update at $position with $progress")
            val holder = recycler.getChildViewHolder(itemView) as? MonkeyAudioHolder
            holder?.updateAudioProgress(progress, progressText)
            adapter.cachedAudioHolder = holder
        } //else cachedAudioHolder = null

    }
    private fun notifyPlaybackStopped(){
        playingAudio = false;
        currentlyPlayingItem = null
        adapter.notifyDataSetChanged();
    }

    fun releasePlayer(){
        try{
            adapter.cachedAudioHolder = null
            if(playingAudio) {
                player.release();
                recycler.removeCallbacks(playerRunnable);
                notifyPlaybackStopped();
            }
        }catch (ex: IllegalStateException) {
            ex.printStackTrace();
        }
    }


}
