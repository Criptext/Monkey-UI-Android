package com.criptext.monkeykitui.recycler.audio

import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by gesuwall on 5/19/16.
 */

abstract class VoiceNotePlayer {

    /**
     * Contains the monkeyitem that is currently being played. If there is no item being played or paused
     * it is null.
     */
    var currentlyPlayingItem : PlayingItem? = null

    /**
     * Boolean flag that if set to true, the seekbar of the MonkeyHolder of the playing item
     * will update itself periodically. This should be true during playback so that the user can see
     * that the audio is being played, but should be set to false when the user is manipulating the
     * seekbar so that it doesn't move while the user is dragging it.
     */
    var updateProgressEnabled : Boolean = false

    abstract val isPlayingAudio : Boolean
    abstract val playbackProgress : Int
    abstract  val playbackPosition : Int

    /**
     * Initializes the media player. It should be called on the onStart() callback of your activity.
     */
    abstract  fun initPlayer()
     /**
     * Initializes the media player with audio stream type STREAM_VOICE_CALL. It should be called
     * on the onStart() callback of your activity.
     */
    abstract fun initPlayerWithFrontSpeaker()
    /**
     * Callback that pauses media playback after the user clicks on the play button.
     */
    abstract fun onPauseButtonClicked()
    /**
     * Callback that starts or resumes media playback after the user clicks on the play button.
     * @param item the monkeyItem with the audio file to play
     */
    abstract fun onPlayButtonClicked(item: MonkeyItem)
    /**
     * Callback that updates media playback after the user has changed the playback position with a
     * touch gesture in the UI.
     * @param newPlaybackPosition the new playback position of the audio
     */
    abstract fun onProgressManuallyChanged(item: MonkeyItem, newPlaybackPosition: Int)
    /**
     * Releases the MediaPlayer's resources. This should be called on the onStop() callback of your activity.
     */
    abstract fun releasePlayer()
}