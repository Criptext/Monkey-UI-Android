package com.criptext.monkeykitui.recycler.audio

import android.media.AudioManager
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
 * This implementation of VoiceNotePlayer uses a MediaPlayer object to play the audio files. During
 * playback it uses a handler to update the UI of the playing MonkeyItem at 15 fps.
 * Created by gesuwall on 4/15/16.
 */

open class DefaultVoiceNotePlayer(monkeyAdapter : MonkeyAdapter, recyclerView: RecyclerView) : VoiceNotePlayer(){
    val handler : Handler

    lateinit private var player : MediaPlayer
    lateinit var playerRunnable : Runnable
    private set

    var recycler : RecyclerView
    private set
    var adapter : MonkeyAdapter
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
        if(player.duration > 0)
            return 100 * player.currentPosition / player.duration;
        else
            return 0;
    }

    override  val playbackPosition : Int
    get() = player.currentPosition

    init {
        recycler = recyclerView
        this.adapter = monkeyAdapter
        adapter.audioHandler = this
        updateProgressEnabled = true
        //initPlayer()
        handler = Handler()


    }

    private fun restorePreviousPlayback(prevPlayingItem: PlayingItem){
        player.setDataSource(prevPlayingItem.item.getFilePath())
        player.setOnPreparedListener {
            player.seekTo(prevPlayingItem.lastPlaybackPosition)
        }
        player.prepareAsync()
        Log.d("AudioHandler", "set ${prevPlayingItem.item.getFilePath()}")
    }

    private fun restorePreviousPlaybackAndPlay(prevPlayingItem: PlayingItem){
        player.setDataSource(prevPlayingItem.item.getFilePath())
        player.setOnPreparedListener {
            player.seekTo(prevPlayingItem.lastPlaybackPosition)
            player.start()
        }
        player.prepareAsync()
        Log.d("AudioHandler", "set ${prevPlayingItem.item.getFilePath()}")
    }


    override fun initPlayer(){
        player = MediaPlayer()
        playerRunnable = object : Runnable {
            override fun run() {
                if (isPlayingAudio) {
                    if(updateProgressEnabled) updateAudioSeekbar(playbackProgress,
                            player.currentPosition.toLong())
                    handler.postDelayed(this, 67)
                }
            }
        }

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
        playerRunnable = object : Runnable {
            override fun run() {
                if (isPlayingAudio) {
                    if(updateProgressEnabled) updateAudioSeekbar(playbackProgress,
                            player.currentPosition.toLong())
                    handler.postDelayed(this, 67)
                }
            }
        }

        val playingTrack = currentlyPlayingItem
        if(playingTrack != null)
            restorePreviousPlaybackAndPlay(playingTrack)

        player.setOnCompletionListener {
            notifyPlaybackStopped()
        }
    }

    override fun onPauseButtonClicked() {
        //handler.removeCallbacks(playerRunnable);
        Log.d("AudioPlayback", "pause clicked")
        pauseAudioHolderPlayer()
    }

    override fun onPlayButtonClicked(item: MonkeyItem) {
        Log.d("AudioPlayback", "play clicked")
        if ( currentlyPlayingItem?.item?.getMessageId().equals(item.getMessageId())) {
            //Resume playback
            startPlayback()
        } else {
            //Start from beggining
            safelyResetPlayer()
            try {
                player.setDataSource(adapter.mContext, Uri.fromFile(File(item.getFilePath())));
                startAudioHolderPlayer(PlayingItem(item))
            } catch (ex: IOException) {
                ex.printStackTrace();
            }
        }

    }

    override fun onProgressManuallyChanged(item: MonkeyItem, newProgress: Int) {
            player.seekTo(newProgress * player.duration / 100)
    }

    /**
     * Starts media playback and rebinds the currently playing item to its MonkeyAudioHolder so that
     * the holder can reflect the new playback status.
     * @param newPlayingItem the new MonkeyItem containing an audio file to play.
     */
    private fun startAudioHolderPlayer(newPlayingItem: PlayingItem){
        currentlyPlayingItem = newPlayingItem
        startAudioHolderPlayer()
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

    private fun rebindAudioHolder(monkeyItem: MonkeyItem){
        val adapterPosition = adapter.getItemPositionByTimestamp(monkeyItem)
        val audioHolder = getAudioHolder(adapterPosition)
        if (audioHolder != null)
            adapter.onBindViewHolder(audioHolder, adapterPosition)
    }

    private fun rebindCurrentAudioHolder(){
        val currentMonkeyItem = currentlyPlayingItem?.item ?: null
        if(currentMonkeyItem != null)
            rebindAudioHolder(currentMonkeyItem)
    }

    private fun startPlayback(){
        player.start()
        playerRunnable.run()
        rebindCurrentAudioHolder()

    }

    private fun pauseAudioHolderPlayer(){
        player.pause()
        currentlyPlayingItem?.lastPlaybackPosition = player.currentPosition
        rebindCurrentAudioHolder()
    }

    private fun updateAudioSeekbar(percentage: Int, progress: Long){
        val audioHolder = getAudioHolder()
        audioHolder?.updateAudioProgress(percentage, progress)
    }

    /**
     * Returns a MonkeyAudioHolder object that holds the UI for the currently playing audio message.
     * @return if there is no item being currently playing or maybe it is not visible, null will be
     * returned. Otherwise, a valid MonkeyAudioHolder object is returned
     */
    open protected fun getAudioHolder(adapterPosition: Int):MonkeyAudioHolder?{
        return recycler.findViewHolderForAdapterPosition(adapterPosition) as MonkeyAudioHolder?
    }

    open protected fun getAudioHolder(): MonkeyAudioHolder? {
        val currentMonkeyItem = currentlyPlayingItem?.item ?: return null
        val adapterPosition = adapter.getItemPositionByTimestamp(currentMonkeyItem)
        return getAudioHolder(adapterPosition)
    }

    /**
     * If there is audio being actively played, notifies the adapter that all messages of type audio
     * should show the play button and have the seekbar at 0
     */
    protected fun notifyPlaybackStopped(){
        if(!isPlayingAudio) {
            val lastPlayingItem = currentlyPlayingItem!!.item
            currentlyPlayingItem = null
            rebindAudioHolder(lastPlayingItem)
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
                player.release();
                recycler.removeCallbacks(playerRunnable);
                notifyPlaybackStopped();
            }
        }catch (ex: IllegalStateException) {
            ex.printStackTrace();
        }
    }


}
