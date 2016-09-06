package com.criptext.monkeykitui.conversation

import java.util.*

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
     * conversation's view
     */
    fun getTotalNewMessages(): Int

    /**
     * @return true if this is a group conversation
     */
    fun isGroup(): Boolean

    /**
     * @return String with the monkey ID's of the group members separated by commas
     */
    fun getGroupMembers(): String?

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
        moreConversations, empty, receivedMessage, sendingMessage, deliveredMessage, sentMessageRead;
    }

    companion object {

        val defaultComparator = Comparator<MonkeyConversation> { t1, t2 ->
            if(t1.getDatetime() > t2.getDatetime()) {
                -1
            }else if (t1.getDatetime() < t2.getDatetime()) {
                1
            } else t1.getId().compareTo(t2.getId()) * (-1)

        }
        fun endItem(): MonkeyConversation = object : MonkeyConversation {
            override fun getAvatarFilePath(): String? = null

            override fun getDatetime() = 0L
            override fun getGroupMembers() = null
            override fun getId() = "0"
            override fun getName() = "0"
            override fun getStatus() = ConversationStatus.moreConversations.ordinal
            override fun getSecondaryText() = ""
            override fun getTotalNewMessages() = 0
            override fun isGroup() = false

        }

    }
}
