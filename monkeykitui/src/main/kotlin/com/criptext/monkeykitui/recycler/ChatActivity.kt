package com.criptext.monkeykitui.recycler

/**
 * Created by gesuwall on 4/5/16.
 */

interface ChatActivity {
    fun getMenberName(sessionId : String) : String

    fun getMemberColor(sessionId: String) : Int

    fun isGroupChat() : Boolean

    fun onMessageLongClicked(position : Int, item: MonkeyItem)

    fun onFileDownloadRequested(position: Int, item: MonkeyItem)

    fun getFilePath(position: Int, item: MonkeyItem) : String

    fun isOnline() : Boolean
}
