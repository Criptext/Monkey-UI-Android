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
import com.innovative.circularaudioview.CircularAudioView
import org.jetbrains.annotations.NotNull
import java.io.File
import java.io.IOException

/**
 * Created by gesuwall on 4/15/16.
 */

open class AudioPlaybackHandler(monkeyAdapter : MonkeyAdapter, recyclerView: RecyclerView) {
    val handler : Handler
    var player : MediaPlayer
    var currentlyPlayingItem : PlayingItem?
    private set
    var recycler : RecyclerView
    private set
    var adapter : MonkeyAdapter
    private set
    val playingAudio : Boolean
    get() {
        try {
            return player.isPlaying
        } catch (ex : IllegalStateException){
            return false
        }
    }

    var updateProgressEnabled : Boolean


    var playerRunnable : Runnable

    open val playbackProgress : Int
    get(){
        if(player.duration > 0)
            return 100 * player.currentPosition / player.duration;
        else
            return 0;
    }

    init {
        currentlyPlayingItem = null
        recycler = recyclerView
        this.adapter = monkeyAdapter
        adapter.audioHandler = this
        updateProgressEnabled = true

        handler = Handler()
        player = MediaPlayer()
        playerRunnable = object : Runnable {
            override fun run() {
                if (playingAudio) {
                    if(updateProgressEnabled) updateAudioSeekbar(recycler,
                            playbackProgress, player.currentPosition.toLong())
                    handler.postDelayed(this, 67)
                }
            }
        }

        player.setOnPreparedListener {
            startAudioHolderPlayer()
        }

        player.setOnCompletionListener {
            notifyPlaybackStopped()
        }

    }

    open fun onPauseButtonClicked(position: Int, item: MonkeyItem) {
        //handler.removeCallbacks(playerRunnable);
        Log.d("AudioPlayback", "pause clicked")
        player.pause();
        adapter.notifyDataSetChanged();
    }

    open fun onPlayButtonClicked(position: Int, item: MonkeyItem) {
        Log.d("AudioPlayback", "play clicked $position")
        if ( currentlyPlayingItem?.item?.getMessageId().equals(item.getMessageId())) {
            //Resume playback
            startAudioHolderPlayer()
        } else {
            //Start from beggining
            player.reset();
            currentlyPlayingItem = PlayingItem(position, item)
            try {
                player.setDataSource(adapter.mContext, Uri.fromFile(File(item.getFilePath())));
                player.prepareAsync();
            } catch (ex: IOException) {
                ex.printStackTrace();
            }
        }

    }

    open fun onProgressManuallyChanged(position: Int, item: MonkeyItem, newProgress: Int) {
        player.seekTo(newProgress * player.duration / 100)
    }


    fun startAudioHolderPlayer(){
        player.start()
        playerRunnable.run()
        adapter.notifyDataSetChanged()
    }

    fun updateAudioSeekbar(recycler: RecyclerView, percentage: Int, progress: Long){
        val audioHolder = recycler.findViewHolderForAdapterPosition(currentlyPlayingItem?.position ?: -1) as MonkeyAudioHolder?
        audioHolder?.updateAudioProgress(percentage, progress)
    }

    fun getAudioSeekBar():CircularAudioView?{
        val audioHolder = recycler.findViewHolderForAdapterPosition(currentlyPlayingItem?.position ?: -1) as MonkeyAudioHolder?
        return audioHolder?.circularAudioView
    }

    fun getAudioHolder():MonkeyAudioHolder{
        return recycler.findViewHolderForAdapterPosition(currentlyPlayingItem?.position ?: -1) as MonkeyAudioHolder
    }

    fun createNewPlayer(){
        player = MediaPlayer()
    }

    fun restartListeners(){
        player.setOnPreparedListener {
            startAudioHolderPlayer()
        }

        player.setOnCompletionListener {
            notifyPlaybackStopped()
        }
    }

    fun notifyPlaybackStopped(){
        if(!playingAudio) {
            currentlyPlayingItem = null
            adapter.notifyDataSetChanged();
        }
    }

    open fun releasePlayer(){
        try{
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
