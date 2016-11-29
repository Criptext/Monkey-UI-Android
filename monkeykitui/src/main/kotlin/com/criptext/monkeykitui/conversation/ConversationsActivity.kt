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
     * This callback is executed on the onStart() and onStop() callbacks of the Conversations
     * fragment, the purpose is to update the activity's reference to the Conversation Fragment.
     *
     * The activity should only have a reference to the fragment while it is active.
     */
    fun setConversationsFragment(conversationsFragment: MonkeyConversationsFragment?)

    /**
     * This method is called by the Conversations fragment when it is initializing to retrieve the
     * list of conversations. loading the conversations should be asynchronous, so when you are done
     * call the fragment's insertConversations() method to add the conversations to the fragment.
     */
    fun requestConversations()

    /**
     * Callback executed when the user clicks a conversation. This should open the chat.
     *
     * You should gather your messages without blocking the UI thread and when it is ready
     * instantiate a new MonkeyChatFragment object and display it using MonkeyFragmentManager's
     * setChatFragment() method.
     *
     * @param conversation the conversation that was clicked. Before you display this chat,
     * you should keep a reference to this conversation in your activity
     */
    fun onConversationClicked(conversation: MonkeyConversation)

    /**
     * Callback executed when the user scrolls to the bottom of the list conversations. This should
     * add more conversations to the fragment.
     *
     * You should gather the next conversation batch without blocking the UI thread and when it is
     * ready add them to the fragment using MonkeyConversationsFragment's addOldConversations()
     * method.
     *
     * @param loadedConversations the number of conversations that the fragment currently has. You
     * could use this number as an offset if fetching from a local database.
     */
    fun onLoadMoreConversations(loadedConversations: Int)

    /**
     * Callback executed when the fragment is about to be destroyed or removed from the screen so
     * that the activity can persist the conversations. When the fragment is recreated it will try
     * to get back the conversations using the requestConversations() method
     *
     * @param conversations The conversations that the fragment had before being dettached. You
     * should hold a reference to this list in your conversation but only to give it back the next
     * time requestConversations() is called. Never try to access the objects of this list if you
     * want to manipulate a conversation object, always fetch them from the ConversationsFragment or
     * your local database because this list may not be up to date with those two sources, thus
     * leading to data inconsistencies. This is only meant to be used as a temporary 'backup'.
     *
     */
    fun retainConversations(conversations: List<MonkeyConversation>)

    /**
     * Callback executed when the user has deleted a conversation. The conversation has been
     * removed from the fragment. You should delete this conversation from your local database.
     * Server should be also be notified about this so that the conversation can also be deleted on
     * the server and remove the user from any group if applicable.
     */
    fun onConversationDeleted(group: MonkeyConversation)
}