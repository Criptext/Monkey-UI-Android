package com.criptext.monkeykitui.dialog

/**
 * Created by daniel on 5/9/16.
 */

abstract class DialogOption(label: String) {
    var label: String

    init {
        this.label = label
    }

    override fun toString(): String {
        return label
    }

    override fun equals(other: Any?): Boolean {
        if(other is DialogOption)
            return label == other.label
        return false
    }

    override fun hashCode(): Int{
        return label.hashCode()
    }

    abstract fun onOptionSelected()

}
