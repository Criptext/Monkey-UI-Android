package com.criptext.monkeykitui.conversation.dialog

import com.criptext.monkeykitui.conversation.MonkeyConversation
import com.criptext.monkeykitui.dialog.AbstractDialog

/**
 * Created by gesuwall on 9/1/16.
 */

class ConversationOptionsDialog(options: MutableList<OnConversationOptionClicked>,
                                val conversation: MonkeyConversation) : AbstractDialog<OnConversationOptionClicked>(options) {

    override fun executeCallback(selectedOption: OnConversationOptionClicked) {
        selectedOption.invoke(conversation)
    }

}