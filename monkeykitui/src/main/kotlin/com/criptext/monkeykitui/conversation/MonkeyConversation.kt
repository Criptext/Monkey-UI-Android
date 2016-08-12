package com.criptext.monkeykitui.conversation

/**
 * Created by gesuwall on 8/11/16.
 */

interface MonkeyConversation{

    /**
     * @return an unique identifier for this conversation.
     */
    fun getId(): String

    /**
     * @return a name for this conversation
     */
    fun getName(): String

    /**
     * @return the timestamp of the last message of this conversation. This value is also used
     * to order the conversations
     */
    fun getDatetime(): Long

    /**
     * @return additional text to display about this conversation. It's usually the last messsage's text
     */
    fun getSecondaryText(): String

    /**
     * @return the total amount of new unread messages. This value is used to display a badge in the
     * conversation's UI
     */
    fun getTotalNewMessages(): Int

    /**
     * @return true if this is a group conversation
     */
    fun isGroup(): Boolean

    /**
     * @return a string with the filepath of this conversation's avatar. If it is null, then
     * a default image is used.
     */
    fun getAvatarFilePath(): String?

    /**
     * @return an integer with the status of the conversation. This influences how the conversation
     * is displayed. It must be a value of the ConversationStatus enum
     */
    fun getStatus(): Int

    enum class ConversationStatus {
        empty, receivedMessage, sendingMessage, deliveredMessage, sentMessageRead;
    }
}
