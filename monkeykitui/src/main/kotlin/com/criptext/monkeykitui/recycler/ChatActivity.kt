package com.criptext.monkeykitui.recycler

/**
 * Created by gesuwall on 4/5/16.
 */

interface ChatActivity {
    fun getMenberName(sessionId : String) : String

    fun getMemberColor(sessionId: String) : Int

    fun isGroupChat() : Boolean

    fun onFileDownloadRequested(position: Int, item: MonkeyItem)

    fun isOnline() : Boolean
    /* AUDIO */

    fun getPlayingAudio() : MonkeyItem?

    fun setPlayingAudio(item: MonkeyItem)

    fun getPlayingAudioProgress(): Int

    fun getPlayingAudioProgressText(): String

    fun isAudioPlaybackPaused() : Boolean


}
