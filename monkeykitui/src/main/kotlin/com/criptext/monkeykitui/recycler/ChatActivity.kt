package com.criptext.monkeykitui.recycler

/**
 * Created by gesuwall on 4/5/16.
 */

interface ChatActivity {

    fun onFileDownloadRequested(position: Int, item: MonkeyItem)

    fun isOnline() : Boolean

    fun onLoadMoreData(loadedItems : Int)

}
