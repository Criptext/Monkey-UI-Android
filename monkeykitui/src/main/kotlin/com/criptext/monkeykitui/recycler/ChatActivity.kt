package com.criptext.monkeykitui.recycler

import android.content.Intent
import com.criptext.monkeykitui.MonkeyChatFragment

/**
 * Interface that host activities' of MonkeyChatFragment must implement. MonkeyAdapter also
 * casts its context reference to this interface to interact with the activity.
 * Created by Gabriel on 4/5/16.
 */

interface ChatActivity {

    /**
     * If a file instantiated with MonkeyItem.getFilePath() does not exist, this method will be called
     * &nbsp\ to download the neccesary file. Once the download is complete the adapter should be notified
     * &nbsp\ to update the UI.
     *
     * If you do start the download, make sure to set the status of the MonkeyItem as 'sending' so
     * that the user can see a progress bar while the file downloads.
     *
     * Monkey Adapter may call this method several times for the same file, you must make sure that
     * you only start the download operation once, although MonkeySDK already does this for you.
     *
     * @param position the adapter position of the MonkeyItem with the missing file
     * @param item the MonkeyItem with the missing file.
     */
    fun onFileDownloadRequested(item: MonkeyItem)

    /**
     * This method will be called when there is a file that had an error during its upload and the
     * user wants to retry the upload. Once the download is complete the adapter should be notified
     * &nbsp\ to update the UI.
     *
     * If you do start the upload, make sure to set the status of the MonkeyItem as 'sending' so
     * that the user can see a progress bar while the file uploads.
     *
     * Monkey Adapter may call this method several times for the same file, you must make sure that
     * you only start the upload operation once, although MonkeySDK already does it for you.
     */
    fun onFileUploadRequested(item: MonkeyItem)

    /**
     * Checks network status. The recommended way to implement this is using ConnectivityManager
     * @return true if the device is connected to the internet, otherwise false
     */
    fun isOnline() : Boolean

    /**
     * Callback executed when the user removes an item from the Chat. If message was of type file, such
     * as photo or voice note, you must delete the file from the SD dard.
     * @param item the removed MonkeyItem
     * @param unsend if true Application should request the server to remotely delete the message from all devices.
     */
    fun onMessageRemoved(item: MonkeyItem, unsent: Boolean)

    /**
     * Callback executed when the user scrolls to the top of the list of messages. This should
     * add older messages to the fragment.
     *
     * You should gather the next message batch without blocking the UI thread and when it is
     * ready add them to the fragment using MonkeyChatFragment's addOldMessages()
     * method.
     *
     * @param conversationId the id of the conversations whose messages are requested. This is useful
     * for querying the messages.
     * @param currentMessageCount the number of messages that the fragment currently has. You
     * could use this number as an offset if fetching from a local database.
     */

    fun onLoadMoreMessages(conversationId: String, currentMessageCount: Int)

    /**
     * MonkeyChatFragment will call this method to instantly retrieve the first messages to display.
     * You should return any cached messages that you have quickly available. if you need to access
     * a database or a server, do that on the onLoadMoreMessages() callback.
     *
     * @param conversationId unique identifier of the conversation of this chat activity
     * @return A collection of MonkeyItems that will be displayed in the chat as soon as it renders.
     * if there are no messages available returns null
     */
    fun getInitialMessages(conversationId: String): MessagesList?

    /**
     * MonkeyChatFragment will call this method to retrieve the current conversation's groupChat object.
     * @param conversationId unique identifier of the conversation of this chat activity
     * @return A GroupChat object that describes a group conversation that is currently active.
     */
    fun getGroupChat(conversationId: String, membersIds: String): GroupChat?

    /**
     * Callback executed on the chat fragment's onStart callback
     * @param fragment a reference to the MonkeyChatFragment that has started. Activity should keep
     * it, so that it can iteract with the fragment.
     * @param conversationId the conversation ID of the current chat. You should keep a reference
     * to this object, since it identifies the active conversation.
     */
    fun onStartChatFragment(fragment: MonkeyChatFragment, conversationId: String)

    /**
     * Callback executed on the chat fragment's onStop callback.
     *
     * When the chat closes you can do things like showing a playback notification if the user
     * is listening to a voice note.
     * @param conversationId the conversation ID of the current chat. You should clear any references
     * to this object, since it no longer identifies the active conversation.
     */
    fun onStopChatFragment(conversationId: String)

    /**
     * Callback executed when the user wishes to delete all messages from the conversation. You
     * should delete them from you local database.
     * @param conversationId The ID of the conversation whose messages are to be deleted.
     */
    fun deleteAllMessages(conversationId: String);

    /**
     * Callback executed when a class in MonkeyChatFragment wants to start a new activity for result
     * you should always call startActivityForResult here. You get this callback so that you can
     * execute any code that you need before switching activities.
     */
    fun startMonkeyActivityForResult(intent: Intent, requestCode: Int)

}
