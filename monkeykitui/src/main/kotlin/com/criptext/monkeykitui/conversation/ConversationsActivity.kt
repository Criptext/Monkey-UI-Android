package com.criptext.monkeykitui.conversation

/**
 * Created by gesuwall on 8/11/16.
 */

interface ConversationsActivity {

    fun requestConversations()

    fun onConversationClicked(conversation: MonkeyConversation)
}