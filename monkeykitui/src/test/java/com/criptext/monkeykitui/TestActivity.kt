package com.criptext.monkeykitui

import android.support.v7.app.AppCompatActivity
import com.criptext.monkeykitui.conversation.ConversationsActivity
import com.criptext.monkeykitui.conversation.ConversationsList
import com.criptext.monkeykitui.conversation.MonkeyConversation
import com.criptext.monkeykitui.recycler.ChatActivity
import com.criptext.monkeykitui.recycler.GroupChat
import com.criptext.monkeykitui.recycler.MessagesList
import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by gesuwall on 12/21/16.
 */

class TestActivity: AppCompatActivity(), ConversationsActivity, ChatActivity {
    override fun onConversationDeleted(group: MonkeyConversation) {
    }

    override fun onLoadMoreConversations(loadedConversations: Int) {
    }

    override fun setConversationsFragment(conversationsFragment: MonkeyConversationsFragment?) {
    }

    override fun onRequestConversations() = ConversationsList()

    override fun deleteAllMessages(conversationId: String) {
    }

    override fun getGroupChat(conversationId: String, membersIds: String) = null
    override fun getInitialMessages(conversationId: String) = MessagesList("")

    override fun isOnline(): Boolean = true

    override fun onFileDownloadRequested(item: MonkeyItem) {
    }

    override fun onFileUploadRequested(item: MonkeyItem) {
    }

    override fun onLoadMoreMessages(conversationId: String, currentMessageCount: Int) {
    }

    override fun onMessageRemoved(item: MonkeyItem, unsent: Boolean) {
    }

    override fun onStartChatFragment(fragment: MonkeyChatFragment, conversationId: String) {
    }

    override fun onStopChatFragment(conversationId: String) {
    }

    override fun onConversationClicked(conversation: MonkeyConversation) {
    }

}