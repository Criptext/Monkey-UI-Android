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
 * This class plays audio files from MonkeyItem messages and updates the UI of the items in the
 * RecyclerView.
 * Created by gesuwall on 4/15/16.
 */

open class AudioPlaybackHandler(monkeyAdapter : MonkeyAdapter, recyclerView: RecyclerView) {
    val handler : Handler

    lateinit private var player : MediaPlayer
    lateinit var playerRunnable : Runnable
    var currentlyPlayingItem : PlayingItem? = null
    private set

    var recycler : RecyclerView
    private set
    var adapter : MonkeyAdapter
    private set
    val isPlayingAudio: Boolean
    get() {
        try {
            return player.isPlaying
        } catch (ex : IllegalStateException){
            return false
        }
    }

    var updateProgressEnabled : Boolean

    open val playbackProgress : Int
    get(){
        if(player.duration > 0)
            return 100 * player.currentPosition / player.duration;
        else
            return 0;
    }

    open val playbackPosition : Int
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

    /**
     * Initializes the media player. It should be called on the onStart() callback of your activity.
     */
    open fun initPlayer(){
        player = MediaPlayer()
        playerRunnable = object : Runnable {
            override fun run() {
                if (isPlayingAudio) {
                    if(updateProgressEnabled) updateAudioSeekbar(recycler,
                            playbackProgress, player.currentPosition.toLong())
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

    /**
     * Callback that pauses media playback after the user clicks on the play button.
     */
    open fun onPauseButtonClicked(item: MonkeyItem) {
        //handler.removeCallbacks(playerRunnable);
        Log.d("AudioPlayback", "pause clicked")
        pauseAudioHolderPlayer()
    }

    /**
     * Callback that starts or resumes mediaplayback after the user clicks on the play button.
     */
    open fun onPlayButtonClicked(item: MonkeyItem) {
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

    /**
     * Callback that updates media playback after the user has changed the playback position with a
     * touch gesture in the UI.
     * @param position the adapter position of the MonkeyItem of the playing audio
     * @param item the MonkeyItem of the playing audio
     */
    open fun onProgressManuallyChanged(position: Int, item: MonkeyItem, newProgress: Int) {
        player.seekTo(newProgress * player.duration / 100)
    }

    /**
     * Starts media playback and rebinds the currently playing item to its MonkeyAudioHolder so that
     * the holder can reflect the new playback status.
     * @param newPlayingItem the new MonkeyItem containing an audio file to play.
     */
    fun startAudioHolderPlayer(newPlayingItem: PlayingItem){
        currentlyPlayingItem = newPlayingItem
        startAudioHolderPlayer()
    }
    /**
     * Starts media playback and rebinds the currently playing item to its MonkeyAudioHolder so that
     * the holder can reflect the new playback status.
     */
    fun startAudioHolderPlayer(){
        player.setOnPreparedListener {
            startPlayback()
        }
        player.prepareAsync()
    }

    private fun rebindAudioHolder(){
        val timeStamp = currentlyPlayingItem?.item?.getMessageTimestamp() ?: null
        if(timeStamp != null) {
            val adapterPosition = adapter.getItemPositionByTimestamp(timeStamp)
            val audioHolder = getAudioHolder(adapterPosition)
            if (audioHolder != null) {
                adapter.onBindViewHolder(audioHolder, adapterPosition)
            }
        }
    }

    private fun startPlayback(){
        player.start()
        playerRunnable.run()
        adapter.notifyDataSetChanged()

    }

    /**
     * Pauses media playback and rebinds the currently playing item to its MonkeyAudioHolder so that
     * the holder can reflect the new playback status.
     */
    fun pauseAudioHolderPlayer(){
        player.pause()
        currentlyPlayingItem?.lastPlaybackPosition = player.currentPosition
        rebindAudioHolder()
    }

    private fun updateAudioSeekbar(recycler: RecyclerView, percentage: Int, progress: Long){
        val audioHolder = getAudioHolder()
        audioHolder?.updateAudioProgress(percentage, progress)
    }

    /**
     * Returns a MonkeyAudioHolder object that holds the UI for the currently playing audio message.
     * @return if there is no item being currently playing or maybe it is not visible, null will be
     * returned. Otherwise, a valid MonkeyAudioHolder object is returned
     */
    fun getAudioHolder(adapterPosition: Int):MonkeyAudioHolder?{
        return recycler.findViewHolderForAdapterPosition(adapterPosition) as MonkeyAudioHolder?
    }

    fun getAudioHolder(): MonkeyAudioHolder? {
        val timeStamp = currentlyPlayingItem?.item?.getMessageTimestamp() ?: return null
        val adapterPosition = adapter.getItemPositionByTimestamp(timeStamp)
        return getAudioHolder(adapterPosition)
    }

    private fun notifyPlaybackStopped(){
        if(!isPlayingAudio) {
            currentlyPlayingItem = null
            adapter.notifyDataSetChanged();
        }
    }

    private fun safelyResetPlayer(){
        try{
            player.reset()
        } catch(ex: IllegalStateException){

        }
    }

    /**
     * Releases the MediaPlayer's resources. This should be called on the onStop() callback of your activity.
     */
    open fun releasePlayer(){
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
