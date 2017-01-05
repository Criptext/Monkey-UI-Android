package com.criptext.monkeykitui.conversation.holder

import com.criptext.monkeykitui.conversation.ConversationsList

/**
 * Created by gesuwall on 11/28/16.
 */

interface ConversationListUI {

    fun notifyConversationInserted(position: Int)

    fun notifyConversationMoved(oldPosition: Int, newPosition: Int)

    fun notifyConversationChanged(position: Int)

    fun notifyConversationRangeInserted(start: Int, end: Int)

    fun notifyConversationRemoved(position: Int)

    fun refresh()

    fun removeLoadingView()

    fun scrollToPosition(position: Int)
}