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
    val playingAudio : Boolean
    get() {
        try {
            return player.isPlaying
        } catch (ex : IllegalStateException){
            return false
        }
    }

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
        recycler = recyclerView
        this.adapter = monkeyAdapter
        adapter.audioHandler = this
        updateProgressEnabled = true

        handler = Handler()
        playerRunnable = object : Runnable {
            override fun run() {
                if (playingAudio) {
                    if(updateProgressEnabled) updateAudioSeekbar(recycler,
                            playbackProgress, playbackProgressText)
                    handler.postDelayed(this, 67)
                }
            }
        }

        player = MediaPlayer()
        player.setOnPreparedListener {
            startAudioHolderPlayer()
        }

        player.setOnCompletionListener {
            notifyPlaybackStopped()
        }

        adapter.audioListener = (object : AudioListener {
            override fun onPauseButtonClicked(position: Int, item: MonkeyItem) {
                //handler.removeCallbacks(playerRunnable);
                Log.d("AudioPlayback", "pause clicked")
                player.pause();
                adapter.notifyDataSetChanged();
            }

            override fun onPlayButtonClicked(position: Int, item: MonkeyItem) {
                Log.d("AudioPlayback", "play clicked")
                if ( currentlyPlayingItem?.item?.getMessageId().equals(item.getMessageId())) {//Resume playback
                    startAudioHolderPlayer()
                } else {//Start from beggining
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

            override fun onProgressManuallyChanged(position: Int, item: MonkeyItem, newProgress: Int) {
                player.seekTo(newProgress * player.duration / 100)
            }

        });
    }

    fun startAudioHolderPlayer(){
        player.start()
        playerRunnable.run()
        adapter.notifyDataSetChanged()
    }

    fun updateAudioSeekbar(recycler: RecyclerView, progress: Int, progressText: String){
        val audioHolder = recycler.findViewHolderForAdapterPosition(currentlyPlayingItem?.position ?: -1) as MonkeyAudioHolder?
        audioHolder?.updateAudioProgress(progress, progressText)
    }
    private fun notifyPlaybackStopped(){
        if(!playingAudio) {
            currentlyPlayingItem = null
            adapter.notifyDataSetChanged();
        }
    }

    fun releasePlayer(){
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
