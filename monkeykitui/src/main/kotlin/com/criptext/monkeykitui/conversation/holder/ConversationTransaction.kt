package com.criptext.monkeykitui.conversation.holder

import com.criptext.monkeykitui.conversation.MonkeyConversation

/**
 * Created by gesuwall on 8/30/16.
 */

interface ConversationTransaction {

    fun updateConversation(conversation: MonkeyConversation)
}