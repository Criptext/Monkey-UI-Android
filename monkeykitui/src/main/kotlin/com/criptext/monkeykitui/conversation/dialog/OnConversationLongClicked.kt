package com.criptext.monkeykitui.conversation.dialog

import com.criptext.monkeykitui.conversation.MonkeyConversation

/**
 * Created by gesuwall on 9/1/16.
 */

abstract class OnConversationLongClicked(val label: String): (MonkeyConversation) -> Unit {

    override fun toString() = label

}