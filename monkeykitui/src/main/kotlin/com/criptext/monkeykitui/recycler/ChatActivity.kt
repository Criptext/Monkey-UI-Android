package com.criptext.monkeykitui.recycler

/**
 * Created by gesuwall on 4/5/16.
 */

interface ChatActivity {

    /**
     * If a file instantiated with MonkeyItem.getFilePath() does not exist, this method will be called
     * &nbsp\ to download the neccesary file. Once the download is complete the adapter should be notified
     * &nbsp\ to update the UI.
     * @param position the adapter position of the MonkeyItem with the missing file
     * @param item the MonkeyItem with the missing file.
     */
    fun onFileDownloadRequested(position: Int, item: MonkeyItem)

    /**
     * @return true if the device is connected to the internet, otherwise false
     */
    fun isOnline() : Boolean

    /**
     * When the user scrolls to the to the end, but there are still more messages to display, this
     * &nbsp\ method will be called to load the next batch of old messages. Once the messages are
     * &nbsp\ ready they should be added to the adapter.
     * @param loadedItems the number of messages that the adapter currently displays.
     */
    fun onLoadMoreData(loadedItems : Int)

}
