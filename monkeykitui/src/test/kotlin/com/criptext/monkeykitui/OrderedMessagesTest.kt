package com.criptext.monkeykitui

import android.util.Log
import com.criptext.monkeykitui.recycler.MonkeyItem
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
class OrderedMessagesTest: AdapterTestCase() {

fun newTextMessage(timestamp: Long, id: String): MonkeyItem {
        return object : MonkeyItem {
            override fun getMessageTimestampOrder() = timestamp
            override fun getOldMessageId() = timestamp.toString()
            override fun getAudioDuration(): Long { throw UnsupportedOperationException() }
            override fun getContactSessionId(): String = "0"
            override fun getDeliveryStatus() = MonkeyItem.DeliveryStatus.delivered
            override fun getFilePath(): String { throw UnsupportedOperationException() }
            override fun getFileSize(): Long { throw UnsupportedOperationException() }
            override fun getMessageId() = id
            override fun getMessageTimestamp() = timestamp
            override fun getMessageText() = "Hello this is a text message"
            override fun getMessageType() = MonkeyItem.MonkeyItemType.text.ordinal
            override fun getPlaceholderFilePath(): String { throw UnsupportedOperationException() }
            override fun isIncomingMessage() = true
        }
    }

    fun assertThatListIsOrdered(list: ArrayList<MonkeyItem>){
        for(i in 1..(list.size-1))
            assert(adapter.itemCmp(list[i - 1], list[i]) != 1)
            //System.out.println("${list[i].getMessageTimestampOrder()}")
    }

    @Test
    @Throws (Exception::class)
    fun newMessagesAddedAreOrdered() {
        val time = System.currentTimeMillis()
        adapter.smoothlyAddNewItem(newTextMessage(time, "123"), recycler!!)
        adapter.smoothlyAddNewItem(newTextMessage(time + 1, "124"), recycler!!)
        adapter.smoothlyAddNewItem(newTextMessage(time + 2, "125"), recycler!!)
        adapter.smoothlyAddNewItem(newTextMessage(time + 4, "126"), recycler!!)

        adapter.smoothlyAddNewItem(newTextMessage(time + 3, "126"), recycler!!)
        adapter.smoothlyAddNewItem(newTextMessage(time + -1, "127"), recycler!!)
        adapter.smoothlyAddNewItem(newTextMessage(time + 3, "128"), recycler!!)

        val list = adapter.takeAllMessages()
        assertThatListIsOrdered(list as ArrayList<MonkeyItem>)

    }

    @Test
    @Throws (Exception::class)
    fun oldMessagesAddedAreOrdered() {
        val newPage = ArrayList<MonkeyItem>()
        val time = System.currentTimeMillis()
        newPage.add(newTextMessage(time + 1, "124"))
        newPage.add(newTextMessage(time + 2, "125"))
        newPage.add(newTextMessage(time + 2, "126"))
        newPage.add(newTextMessage(time + 3, "127"))
        newPage.add(newTextMessage(time + 4, "128"))
        newPage.add(newTextMessage(time + 2, "129"))
        newPage.add(newTextMessage(time + 1, "130"))

        adapter.addOldMessages(newPage, true, recycler!!)
        val list = adapter.takeAllMessages()
        assertThatListIsOrdered(list as ArrayList<MonkeyItem>)
    }

    @Test
    @Throws (Exception::class)
    fun newMessageBatchIsOrdered() {
        val newPage = ArrayList<MonkeyItem>()
        val time = System.currentTimeMillis()
        newPage.add(newTextMessage(time + 1, "124"))
        newPage.add(newTextMessage(time + 2, "125"))
        newPage.add(newTextMessage(time + 2, "126"))
        newPage.add(newTextMessage(time + 3, "127"))
        newPage.add(newTextMessage(time + 3, "127"))
        newPage.add(newTextMessage(time + 4, "128"))
        newPage.add(newTextMessage(time + 2, "129"))
        newPage.add(newTextMessage(time + 1, "130"))
        newPage.add(newTextMessage(time - 1, "131"))

        adapter.smoothlyAddNewItems(newPage, recycler!!)
        val list = adapter.takeAllMessages()
        assertThatListIsOrdered(list as ArrayList<MonkeyItem>)
    }
}
