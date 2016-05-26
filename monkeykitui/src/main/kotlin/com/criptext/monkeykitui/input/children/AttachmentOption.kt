package com.criptext.monkeykitui.input.children

/**
 * Created by daniel on 5/9/16.
 */

abstract class AttachmentOption(label: String) {
    var label: String

    init {
        this.label = label
    }

    override fun toString(): String {
        return label
    }

    override fun equals(other: Any?): Boolean {
        if(other is AttachmentOption)
            return label == other.label
        return false
    }

    override fun hashCode(): Int{
        return label.hashCode()
    }

    abstract fun onOptionSelected()

}
