package com.criptext.monkeykitui.recycler

import org.amshove.kluent.`should equal`
import org.junit.Test

/**
 * Created by gesuwall on 12/30/16.
 */

class `MonkeyItem Comparator Test` {

    fun newMonkeyItem(id: String, timestamp: Long): MonkeyItem {
        return object: MonkeyItem {
            override fun getAudioDuration(): Long {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getConversationId(): String {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getDeliveryStatus(): MonkeyItem.DeliveryStatus {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getFilePath(): String {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getFileSize(): Long {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getMessageId() = id

            override fun getMessageText(): String {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getMessageTimestamp(): Long {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getMessageTimestampOrder() = timestamp

            override fun getMessageType(): Int {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getOldMessageId(): String? {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getPlaceholderFilePath(): String {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun getSenderId(): String {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun isIncomingMessage(): Boolean {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }

    @Test
    fun `Should return -1 if left hand item has a lower timestamp than right hand item`() {
        val lhi = newMonkeyItem("0", 1L)
        val rhi = newMonkeyItem("1", 2L)
        MonkeyItem.Companion.defaultComparator.compare(lhi, rhi) `should equal` -1
    }

    @Test
    fun `Should return 1 if left hand item has a higher timestamp than right hand item`() {
        val lhi = newMonkeyItem("0", 2L)
        val rhi = newMonkeyItem("1", 1L)
        MonkeyItem.Companion.defaultComparator.compare(lhi, rhi) `should equal` 1
    }

    @Test
    fun `Should default to comparing id strings if both timestamps are equal`() {
        val lhi = newMonkeyItem("0", 1L)
        val rhi = newMonkeyItem("1", 1L)
        val stringCmpRes = lhi.getMessageId().compareTo(rhi.getMessageId())
        MonkeyItem.Companion.defaultComparator.compare(lhi, rhi) `should equal` stringCmpRes
    }
}