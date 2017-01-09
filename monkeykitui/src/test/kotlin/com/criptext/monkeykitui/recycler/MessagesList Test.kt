package com.criptext.monkeykitui.recycler

import com.criptext.monkeykitui.util.SimpleMonkeyItem
import org.amshove.kluent.`should equal`
import org.junit.Test

/**
 * Created by gesuwall on 12/30/16.
 */

class `MessagesList Test` {

    @Test
    fun `get() should never return an EndItem object`() {
        val list = mutableListOf(SimpleMonkeyItem("7", 123), SimpleMonkeyItem("8", 124),
                SimpleMonkeyItem("9", 125))
        val messagesList = MessagesList("test")
        messagesList.insertMessages(list, false)

        (messagesList[0] is EndItem) `should equal` false
        messagesList[0].getMessageId() `should equal` "7"
        messagesList[1].getMessageId() `should equal` "8"
        messagesList[2].getMessageId() `should equal` "9"

        messagesList.hasReachedEnd = true

        (messagesList[0] is EndItem) `should equal` false
        messagesList[0].getMessageId() `should equal` "7"
        messagesList[1].getMessageId() `should equal` "8"
        messagesList[2].getMessageId() `should equal` "9"
    }

    @Test
    fun `getItemAt(0) will return an EndItem object if hasReachedEnd equals true`() {
        val list = mutableListOf(SimpleMonkeyItem("7", 123), SimpleMonkeyItem("8", 124),
                SimpleMonkeyItem("9", 125))
        val messagesList = MessagesList("test")
        messagesList.insertMessages(list, true)

        (messagesList.getItemAt(0) is EndItem) `should equal` false

        messagesList.hasReachedEnd = false

        (messagesList.getItemAt(0) is EndItem) `should equal` true
    }

    @Test
    fun `size val should ignore any EndItem objects`() {
        val list = mutableListOf(SimpleMonkeyItem("7", 123), SimpleMonkeyItem("8", 124),
                SimpleMonkeyItem("9", 125))
        val messagesList = MessagesList("test")
        messagesList.insertMessages(list, false)

        messagesList.size `should equal` 3

        messagesList.hasReachedEnd = true

        messagesList.size `should equal` 3
    }

    @Test
    fun `actualSize val should always take into account any EndItem objects`() {
        val list = mutableListOf(SimpleMonkeyItem("7", 123), SimpleMonkeyItem("8", 124),
                SimpleMonkeyItem("9", 125))
        val messagesList = MessagesList("test")
        messagesList.insertMessages(list, true)

        messagesList.actualSize `should equal` 3

        messagesList.hasReachedEnd = false

        messagesList.actualSize `should equal` 4
    }

    fun `addOldMessages() removes loading view correctly even with empty conversation`() {
        val messagesList = MessagesList("test")

    }
}
