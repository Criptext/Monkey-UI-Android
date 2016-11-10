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

open class PlaybackService: Service()  {

    //media player
    private val player : VoiceNotePlayer by lazy {
        val newPlayer = newVoiceNotePlayerInstance()
        newPlayer.initPlayer()

        newPlayer
    }

    /**
    This controls whether the service should be killed after stopping playback. The service should
    only be killed if it is not playing anything while activity is dead.
    */
    var inForeground = false

    private var sensorHandler: SensorHandler? = null

    open protected fun newVoiceNotePlayerInstance(): VoiceNotePlayer {
        val newPlayer = DefaultVoiceNotePlayer(this)
        sensorHandler = SensorHandler(newPlayer, this)
        return newPlayer
    }


    override fun onBind(p0: Intent?): IBinder? {
        //Log.d("MusicService", "bind service ")
        player.onPlaybackStopped = null
        return VoiceNotePlayerBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if(!inForeground && !player.isPlayingAudio) {
            player.releasePlayer()
            stopSelf()
        } else {
            player.onPlaybackStopped = { item ->
                if(!inForeground)
                    stopSelf()
            }
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

        if(!(sensorHandler?.isProximityOn ?: false)) {
            sensorHandler?.onDestroy()
            sensorHandler = null
        }

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

        fun setIsInForeground(value: Boolean) {
            inForeground = value
        }

        /**
         * Posts a notification that can control voice note's playback if the player is playing audio.
         * If it isn't playing anything or is paused, it will attempt to remove any existing playback
         * notifications.
         * @param notification an instance of the notification to post
         */
        fun setupNotificationControl(notification: PlaybackNotification) {
            if(player.isPlayingAudio) {
            player.showNotification(notification);
        } else
            PlaybackNotification.Companion.removePlaybackNotification(this@PlaybackService);
        }

        /**
         * Removes the playback notification only if the supplied conversation ID matches the
         * conversation ID of the currently playing item.
         * @param conversationId the conversation ID to match
         */
        fun removeNotificationControl(conversationId: String) {
            if( player.isPlayingAudio) {
                val message = player.currentlyPlayingItem!!.item;
                if(message.getConversationId() == conversationId)
                    player.removeNotification()
            }
        }


    }

    companion object {
        var isRunning: Boolean = false
        val togglePlayback = "MonkeyKitUI.PlaybackService.togglePlayback"
    }
}

