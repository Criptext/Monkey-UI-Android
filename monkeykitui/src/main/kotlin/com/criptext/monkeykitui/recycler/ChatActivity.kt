package com.criptext.monkeykitui.recycler

import com.criptext.monkeykitui.MonkeyChatFragment

/**
 * Created by gesuwall on 4/5/16.
 */

interface ChatActivity {

    fun setChatFragment(chatFragment: MonkeyChatFragment?)
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
     * &nbsp\ ready they should be added to the adapter using the addOldMessages() method.
     * @param loadedItems the number of messages that the adapter currently displays.
     */
    fun onLoadMoreData(loadedItems : Int)

    /**
     * MonkeyChatFragment will call this method to instantly retrieve the first messages to display.
     * @param conversationId unique identifier of the conversation of this chat activity
     * @return A collection of MonkeyItems that will be displayed in the chat as soon as it renders.
     * if there are no messages available returns null
     */
    fun getInitialMessages(conversationId: String): Collection<MonkeyItem>?

    /**
     * MonkeyChatFragment will call this method in the onDestroy callback. chat activity should try
     * to persist the messages of the conversations to retain state on configuration change
     * @param conversationId unique identifier of the chat's conversation
     * @param messages list of messages to retain
     */
    fun retainMessages(conversationId: String, messages: Collection<MonkeyItem>)

    /**
     * MonkeyChatFragment will call this method to retrieve the groupChat.
     * @param conversationId unique identifier of the conversation of this chat activity
     * @return A GroupChat to handle group functions
     */
    fun getGroupChat(conversationId: String, membersIds: String): GroupChat?

}
