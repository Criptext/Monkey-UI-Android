package com.criptext.monkeykitui.dialog


/**
 * Created by gesuwall on 9/1/16.
 */

class SimpleDialog(options: MutableList<DialogOption>): AbstractDialog<DialogOption>(options) {
    override fun executeCallback(selectedOption: DialogOption) {
        (selectedOption).onOptionSelected()
    }

}