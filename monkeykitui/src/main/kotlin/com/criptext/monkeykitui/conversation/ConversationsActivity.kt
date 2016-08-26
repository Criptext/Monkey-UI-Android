package com.criptext.monkeykitui.conversation

import com.criptext.monkeykitui.MonkeyConversationsFragment

/**
 * Created by gesuwall on 8/11/16.
 */

interface ConversationsActivity {

    fun setConversationsFragment(conversationsFragment: MonkeyConversationsFragment?)

    fun requestConversations()

    fun onConversationClicked(conversation: MonkeyConversation)

    fun onLoadMoreConversations(loadedConversations: Int)

    fun retainConversations(conversations: Collection<MonkeyConversation>)
}