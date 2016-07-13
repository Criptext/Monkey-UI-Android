package com.criptext.monkeykitui.recycler

/**
 * Created by gesuwall on 4/5/16.
 */

interface ChatActivity {

    /**
     * If a file instantiated with MonkeyItem.getFilePath() does not exist, this method will be called
     * &nbsp\ to download the neccesary file. Once the download is complete the adapter should be notified
     * &nbsp\ to update the UI.
     * Monkey Adapter may call this method several times for the same file, you must make sure that
     * you only start the download operation once.
     *
     * @param position the adapter position of the MonkeyItem with the missing file
     * @param item the MonkeyItem with the missing file.
     */
    fun onFileDownloadRequested(item: MonkeyItem)

    /**
     * This method will be called when there is a file that had an error during its upload and the
     * user wants to retry the upload.`
     * Monkey Adapter may call this method several times for the same file, you must make sure that
     * you only start the upload operation once.
     */
    fun onFileUploadRequested(item: MonkeyItem)

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
