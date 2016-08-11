package com.criptext.monkeykitui.conversation

/**
 * Created by gesuwall on 8/11/16.
 */

interface MonkeyConversation{

    fun getId(): String

    fun getName(): String

    fun getDatetime(): Long

    fun getSecondaryText(): String

    fun getTotalNewMessages(): Int

    fun isGroup(): Boolean

    fun getAvatarFilePath(): String?

    fun getStatus(): Int

    enum class ConversationStatus {
        empty, receivedMessage, sentMessage, sentMessageRead;
    }
}
