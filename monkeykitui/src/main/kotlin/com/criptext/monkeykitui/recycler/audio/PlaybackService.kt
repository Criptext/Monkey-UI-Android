package com.criptext.monkeykitui.recycler.audio

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

/**
 * Created by gesuwall on 10/27/16.
 */

class PlaybackService: Service()  {

    //media player
    private val player : DefaultVoiceNotePlayer by lazy {
        val newPlayer = DefaultVoiceNotePlayer(this)
        newPlayer.initPlayer()
        newPlayer
    }
    private var currentSong : Int = 0


    private val binder  = VoiceNoteBinder()


    override fun onBind(p0: Intent?): IBinder? {
        //Log.d("MusicService", "bind service ")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if(!player.isPlayingAudio) {
            player.releasePlayer()
            stopSelf()
        }
        return true

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isRunning = true
        player.initPlayer();
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }

    inner class VoiceNoteBinder : Binder() {
        fun getVoiceNotePlayer() : DefaultVoiceNotePlayer {
            return player;
        }
    }

    companion object {
        var isRunning: Boolean = false
    }
}

