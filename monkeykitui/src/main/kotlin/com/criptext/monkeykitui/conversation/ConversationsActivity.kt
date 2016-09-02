package com.criptext.monkeykitui.conversation

import com.criptext.monkeykitui.MonkeyConversationsFragment

/**
 * Created by gesuwall on 8/11/16.
 */

interface ConversationsActivity {

    fun setConversationsFragment(conversationsFragment: MonkeyConversationsFragment?)

    fun requestConversations()

    /**
     * Callback executed when the user clicks a conversation. This should open the chat.
     */
    fun onConversationClicked(conversation: MonkeyConversation)

    /**
     * Callback executed when the user scrolls to the bottom of the list conversations. The
     * activity should load asynchronously more conversations.
     */
    fun onLoadMoreConversations(loadedConversations: Int)

    /**
     * Callback executed when the fragment is about to be destroyed so that the activity can
     * persist the conversations. When the fragment is recreated it will try to get back the
     * conversations usoing the requestConversations() method
     */
    fun retainConversations(conversations: List<MonkeyConversation>)

    /**
     * Callback executed when the user has deleted a conversation. Server should be notified
     * on this so that the conversation can also be deleted server side and remove the user from
     * any group if applicable
     */
    fun onConversationDeleted(group: MonkeyConversation)
}