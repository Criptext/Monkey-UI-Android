package com.criptext.monkeykitui.info.dialog

import com.criptext.monkeykitui.conversation.dialog.OnConversationOptionClicked
import com.criptext.monkeykitui.conversation.dialog.OnInfoOptionClicked
import com.criptext.monkeykitui.dialog.AbstractDialog
import com.criptext.monkeykitui.recycler.MonkeyInfo

/**
 * Created by hirobreak on 24/10/16.
 */
class InfoOptionsDialog(options: MutableList<OnInfoOptionClicked>,
                        val info: MonkeyInfo) : AbstractDialog<OnInfoOptionClicked>(options) {

    override fun executeCallback(selectedOption: OnInfoOptionClicked) {
        selectedOption.invoke(info)
    }
}