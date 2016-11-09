package com.criptext.monkeykitui.recycler.audio

import android.app.Activity
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by gesuwall on 10/27/16.
 */

class PlaybackService: Service()  {

    //media player
    private val player : DefaultVoiceNotePlayer by lazy {
        val newPlayer = DefaultVoiceNotePlayer(this)
        sensorHandler = SensorHandler(newPlayer, this)
        newPlayer.initPlayer()

        newPlayer
    }

    private var sensorHandler: SensorHandler? = null


    override fun onBind(p0: Intent?): IBinder? {
        //Log.d("MusicService", "bind service ")
        player.onPlaybackStopped = null
        return VoiceNotePlayerBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if(!player.isPlayingAudio) {
            player.releasePlayer()
            stopSelf()
        } else {
            player.onPlaybackStopped = { it: MonkeyItem ->
                stopSelf()
            }
        }

        if(!(sensorHandler?.isProximityOn ?: false)) {
            sensorHandler?.onDestroy()
            sensorHandler = null
        }

        return true

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isRunning = true

        if(intent.getBooleanExtra(togglePlayback, false)) {
            if(player.isPlayingAudio)
                player.onPauseButtonClicked()
            else
                player.onPlayButtonClicked(player.currentlyPlayingItem!!.item)
        } else
            player.initPlayer();

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    inner class VoiceNotePlayerBinder : Binder() {
        fun playVoiceNote(item: MonkeyItem) {
            player.onPlayButtonClicked(item)
        }

        fun pauseVoiceNote() {
            player.onPauseButtonClicked()
        }

        val isPlayingAudio: Boolean
        get() = player.isPlayingAudio

        val currentlyPlayingItem: MonkeyItem?
        get() = player.currentlyPlayingItem?.item

        var updateProgressEnabled: Boolean
        set(value) {
          player.updateProgressEnabled = value
        }
        get() = player.updateProgressEnabled

        val playbackProgress: Int
        get() = player.playbackProgress

        val playbackPosition: Int
            get() = player.playbackPosition

        fun setPlaybackProgress(item: MonkeyItem, progress: Int) {
            player.onProgressManuallyChanged(item, progress)
        }

        fun setUiUpdater(updater: AudioUIUpdater?) {
            player.uiUpdater = updater
        }

        fun showNotification(notification: PlaybackNotification) {
            player.showNotification(notification)
        }

    }

    companion object {
        var isRunning: Boolean = false
        val togglePlayback = "MonkeyKitUI.PlaybackService.togglePlayback"
    }
}

