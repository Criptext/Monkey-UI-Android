package com.criptext.monkeykitui.recycler.audio

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.criptext.monkeykitui.recycler.MonkeyItem
import java.io.File
import java.io.IOException

/**
 * This implementation of VoiceNotePlayer uses a MediaPlayer object to play the audio files. During
 * playback it uses a handler to update the UI of the playing MonkeyItem at 15 fps.
 * Created by gesuwall on 4/15/16.
 */

open class DefaultVoiceNotePlayer(val ctx: Context) : VoiceNotePlayer(){
    val handler : Handler
    private var player : MediaPlayer
    lateinit var playerRunnable : Runnable
    var mediaDuration : Int
    var progressDuration : Int
    private set

    override val isPlayingAudio: Boolean
    get() {
        try {
            return player.isPlaying
        } catch (ex : IllegalStateException){
            return false
        }
    }


    override val playbackProgress : Int
    get(){
        if(mediaDuration > 0)
            try {
                return 100 * player.currentPosition / mediaDuration;
            }catch(ex : IllegalStateException){
                return 0
            }
        else
            return 0;
    }

    override  val playbackPosition : Int
    get(){
        try {
            return player.currentPosition;
        }catch(ex : IllegalStateException){
            return 0
        }
    }

    init {
        updateProgressEnabled = true
        player = MediaPlayer()
        handler = Handler()
        mediaDuration = 0
        progressDuration = 0
    }

    constructor(ctx: Context, uiUpdater: AudioUIUpdater): this(ctx){
        this.uiUpdater = uiUpdater
    }

    private fun restorePreviousPlayback(prevPlayingItem: PlayingItem){
        player.setDataSource(prevPlayingItem.item.getFilePath())
        player.setOnPreparedListener {
            player.seekTo(prevPlayingItem.lastPlaybackPosition)
            rebindCurrentAudioHolder()
        }
        player.prepareAsync()
    }

    private fun restorePreviousPlaybackAndPlay(prevPlayingItem: PlayingItem){
        player.setDataSource(prevPlayingItem.item.getFilePath())
        player.setOnPreparedListener {
            player.seekTo(prevPlayingItem.lastPlaybackPosition)
            player.start()
        }
        player.prepareAsync()
    }

    private fun createNewUIRunnable() = object : Runnable {
        override fun run() {
            if (isPlayingAudio) {
                if(updateProgressEnabled) uiUpdater?.updateAudioProgress(currentlyPlayingItem!!.item,
                        playbackProgress, player.currentPosition.toLong())
                handler.postDelayed(this, 67)
            }
        }
    }

    override fun initPlayer(){
        player = MediaPlayer()
        playerRunnable = createNewUIRunnable()

        val playingTrack = currentlyPlayingItem
        if(playingTrack != null)
            restorePreviousPlayback(playingTrack)

        player.setOnCompletionListener {
            notifyPlaybackStopped()
        }
    }

    override fun initPlayerWithFrontSpeaker(){
        player = MediaPlayer()
        player.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        playerRunnable = createNewUIRunnable()

        val playingTrack = currentlyPlayingItem
        if(playingTrack != null) {
            restorePreviousPlaybackAndPlay(playingTrack)
        }
        player.setOnCompletionListener {
            notifyPlaybackStopped()
        }
    }

    override fun onPauseButtonClicked() {
        //handler.removeCallbacks(playerRunnable);
        pauseAudioHolderPlayer()
    }

    override fun onPlayButtonClicked(item: MonkeyItem) {
        if ( currentlyPlayingItem?.item?.getMessageId().equals(item.getMessageId())) {
            //Resume playback
            startPlayback()
        } else {
            //Start from beggining
            safelyResetPlayer()
            try {
                player.setDataSource(ctx, Uri.fromFile(File(item.getFilePath())));
                startAudioHolderPlayer(PlayingItem(item))
            } catch (ex: IOException) {
                ex.printStackTrace();
            }
        }

    }

    override fun onProgressManuallyChanged(item: MonkeyItem, newPlaybackPosition: Int) {
            player.seekTo(newPlaybackPosition * player.duration / 100)
    }

    /**
     * Starts media playback and rebinds the currently playing item to its MonkeyAudioHolder so that
     * the holder can reflect the new playback status. If there was an audio item already playing audio,
     * that item will rebind as well to reflect its new state.
     * @param newPlayingItem the new MonkeyItem containing an audio file to play.
     */
    private fun startAudioHolderPlayer(newPlayingItem: PlayingItem){
        val lastPlayedItem = currentlyPlayingItem?.item
        currentlyPlayingItem = newPlayingItem
        startAudioHolderPlayer()
        if(lastPlayedItem != null)
            uiUpdater?.rebindAudioHolder(lastPlayedItem)
    }
    /**
     * Starts media playback and rebinds the currently playing item to its MonkeyAudioHolder so that
     * the holder can reflect the new playback status.
     */
    private fun startAudioHolderPlayer(){
        player.setOnPreparedListener {
            startPlayback()
        }
        player.prepareAsync()
    }


    private fun rebindCurrentAudioHolder(){
        val currentMonkeyItem = currentlyPlayingItem?.item ?: null
        if(currentMonkeyItem != null)
            uiUpdater?.rebindAudioHolder(currentMonkeyItem)
    }

    private fun startPlayback(){
        player.start()
        mediaDuration = player.duration
        try {
            playerRunnable.run()
        } catch (ex: UninitializedPropertyAccessException){
            throw IllegalStateException("This Player has not been initialized yet!\nYou should call initPlayer()" +
            " in your onStart() activity callback and call releasePlayer() on your onStop() activity callback." +
            "\nDon't play voice notes while your activity is stopped.")
        }
        rebindCurrentAudioHolder()

    }

    private fun pauseAudioHolderPlayer(){
        player.pause()
        currentlyPlayingItem?.lastPlaybackPosition = player.currentPosition
        rebindCurrentAudioHolder()
    }

    /**
     * If there is audio being actively played, notifies the adapter that all messages of type audio
     * should show the play button and have the seekbar at 0
     */
    protected fun notifyPlaybackStopped(){
        if(!isPlayingAudio && currentlyPlayingItem != null) {
            val lastPlayingItem = currentlyPlayingItem!!.item
            currentlyPlayingItem = null
            uiUpdater?.rebindAudioHolder(lastPlayingItem)
        }
    }

    private fun safelyResetPlayer(){
        try{
            player.reset()
        } catch(ex: IllegalStateException){

        }
    }

    override fun releasePlayer(){
        try{
            if(isPlayingAudio) {
                pauseAudioHolderPlayer()
            }
            player.release();
        }catch (ex: IllegalStateException) {
            ex.printStackTrace();
        }
    }


}
