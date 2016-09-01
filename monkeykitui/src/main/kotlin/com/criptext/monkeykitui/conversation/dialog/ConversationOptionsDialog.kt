package com.criptext.monkeykitui.conversation.dialog

import com.criptext.monkeykitui.conversation.MonkeyConversation
import com.criptext.monkeykitui.dialog.AbstractDialog

/**
 * Created by gesuwall on 9/1/16.
 */

class ConversationOptionsDialog(options: MutableList<OnConversationLongClicked>,
     val conversation: MonkeyConversation) : AbstractDialog<OnConversationLongClicked>(options) {

    override fun executeCallback(selectedOption: OnConversationLongClicked) {
        selectedOption.invoke(conversation)
    }

}