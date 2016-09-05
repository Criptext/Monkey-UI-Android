package com.criptext.monkeykitui.conversation

import com.criptext.monkeykitui.MonkeyConversationsFragment

/**
 * Interface that host activities' of MonkeyConversationsFragment must implement.
 * MonkeyConversationsAdapter also casts its context reference to this interface to interact with
 * the activity.
 * Created by Gabriel on 8/11/16.
 */

interface ConversationsActivity {

    /**
     * This callback is executed on the onAttach() and onDetach() callbacks of the Conversations
     * fragment, the purpose is to update the activity's reference to the Conversation Fragment.
     * The activity should only have a reference to the fragment while it is attached.
     */
    fun setConversationsFragment(conversationsFragment: MonkeyConversationsFragment?)

    /**
     * This method is called by the Conversations fragment when it is initializing to retrieve the
     * list of conversations. loading the conversations should be asynchronous, so when you are done
     * call the fragment's insertConversations() method.
     */
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