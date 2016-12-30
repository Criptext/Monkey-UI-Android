package com.criptext.monkeykitui

import android.util.Log
import com.criptext.monkeykitui.recycler.MessagesList
import com.criptext.monkeykitui.recycler.MonkeyItem
import org.amshove.kluent.shouldThrow
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

/**
 * Created by gesuwall on 8/26/16.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class SortedMessagesTest : AdapterTestCase() {
    val messages = MessagesList("test")

    fun newTextMessage(timestamp: Long, id: String, oldId: String?, incoming: Boolean): MonkeyItem {
        return object : MonkeyItem {
            override fun getConversationId() = "0"
            override fun getMessageTimestampOrder() = timestamp
            override fun getOldMessageId() = oldId
            override fun getAudioDuration(): Long { throw UnsupportedOperationException() }
            override fun getSenderId(): String = "0"
            override fun getDeliveryStatus() = MonkeyItem.DeliveryStatus.delivered
            override fun getFilePath(): String { throw UnsupportedOperationException() }
            override fun getFileSize(): Long { throw UnsupportedOperationException() }
            override fun getMessageId() = id
            override fun getMessageTimestamp() = timestamp
            override fun getMessageText() = "Hello this is a text message"
            override fun getMessageType() = MonkeyItem.MonkeyItemType.text.ordinal
            override fun getPlaceholderFilePath(): String { throw UnsupportedOperationException() }
            override fun isIncomingMessage() = incoming
        }
    }

    fun newTextMessage(timestamp: Long, id: String) = newTextMessage(timestamp, id, null, true)
    fun newSentTextMessage(timestamp: Long, id: String) = newTextMessage(timestamp, id, null, false)
    fun newSentTextMessage(timestamp: Long, id: String, oldId: String) = newTextMessage(timestamp, id, oldId, false)

    fun assertThatListIsSorted(list: List<MonkeyItem>){
        for(i in 1..(list.size-1))
            assert(MonkeyItem.defaultComparator.compare(list[i - 1], list[i]) != 1)
            //System.out.println("${list[i].getMessageTimestampOrder()}")
    }

    fun assertThatMessagesAreNotRepeated(list: List<MonkeyItem>){

        for(i in 0..(list.size-2))
            for(j in (i+1)..(list.size-1)) {
                assert(list[i].getMessageId() != list[j].getMessageId())
                assert(list[i].getMessageId() != list[j].getOldMessageId())
                assert(list[i].getOldMessageId() != list[j].getMessageId())
            }
    }

    @Test
    @Throws (Exception::class)
    fun initiallyInsertedMessagesAreSorted() {
        val newPage = ArrayList<MonkeyItem>()
        val time = System.currentTimeMillis()
        newPage.add(newTextMessage(time + 1, "114"))
        newPage.add(newTextMessage(time + 2, "115"))
        newPage.add(newTextMessage(time + 2, "116"))
        newPage.add(newTextMessage(time + 3, "117"))
        newPage.add(newTextMessage(time + 4, "118"))
        newPage.add(newTextMessage(time + 2, "119"))
        newPage.add(newTextMessage(time + 1, "120"))

        messages.insertMessages(newPage, true)
        assertThatListIsSorted(messages)

        val duplicatedInsertion = { messages.insertMessages(newPage, true) }
        duplicatedInsertion shouldThrow  IllegalStateException::class
    }

    @Test
    @Throws (Exception::class)
    fun newMessagesAddedAreSorted() {
        val time = System.currentTimeMillis()
        messages.smoothlyAddNewItem(newTextMessage(time, "123"))
        messages.smoothlyAddNewItem(newTextMessage(time + 1, "124"))
        messages.smoothlyAddNewItem(newTextMessage(time + 2, "125"))
        messages.smoothlyAddNewItem(newTextMessage(time + 4, "126"))

        messages.smoothlyAddNewItem(newTextMessage(time + 3, "126"))
        messages.smoothlyAddNewItem(newTextMessage(time + -1, "127"))
        messages.smoothlyAddNewItem(newTextMessage(time + 3, "128"))

        assertThatListIsSorted(messages)
    }

    @Test
    @Throws (Exception::class)
    fun oldMessagesAddedAreSorted() {
        val newPage = ArrayList<MonkeyItem>()
        val time = System.currentTimeMillis()
        newPage.add(newTextMessage(time + 1, "124"))
        newPage.add(newTextMessage(time + 2, "125"))
        newPage.add(newTextMessage(time + 2, "126"))
        newPage.add(newTextMessage(time + 3, "127"))
        newPage.add(newTextMessage(time + 4, "128"))
        newPage.add(newTextMessage(time + 2, "129"))
        newPage.add(newTextMessage(time + 1, "130"))

        messages.addOldMessages(newPage, true)
        assertThatListIsSorted(messages)
    }

    @Test
    @Throws (Exception::class)
    fun newMessageBatchIsSorted() {
        val newPage = ArrayList<MonkeyItem>()
        val time = System.currentTimeMillis()
        newPage.add(newTextMessage(time + 1, "124"))
        newPage.add(newTextMessage(time + 2, "125"))
        newPage.add(newTextMessage(time + 2, "126"))
        newPage.add(newTextMessage(time + 3, "127"))
        newPage.add(newTextMessage(time + 3, "132"))
        newPage.add(newTextMessage(time + 4, "128"))
        newPage.add(newTextMessage(time + 2, "129"))
        newPage.add(newTextMessage(time + 1, "130"))
        newPage.add(newTextMessage(time - 1, "131"))

        messages.smoothlyAddNewItems(newPage)
        assertThatListIsSorted(messages)
        assertThatMessagesAreNotRepeated(messages)
    }


    @Test
    @Throws (Exception::class)
    fun messagesCantBeRepeated() {
        val time = System.currentTimeMillis()
        messages.smoothlyAddNewItem(newSentTextMessage(time, "151"))
        messages.smoothlyAddNewItem(newSentTextMessage(time + 1 , "152"))
        messages.smoothlyAddNewItem(newSentTextMessage(time + 2 , "-153"))

        val newPage = ArrayList<MonkeyItem>()
        newPage.add(newTextMessage(time - 5, "146"))
        newPage.add(newTextMessage(time - 4, "147"))
        newPage.add(newTextMessage(time - 3, "148"))
        newPage.add(newSentTextMessage(time - 2, "151"))
        newPage.add(newSentTextMessage(time - 1, "152"))
        newPage.add(newSentTextMessage(time, "154", "-153")) //message with old id

        messages.addOldMessages(newPage, true)
        assertThatListIsSorted(messages)
        assertThatMessagesAreNotRepeated(messages)
    }
}
