package com.criptext.monkeykitui.util

import com.criptext.monkeykitui.recycler.MonkeyItem

/**
 * Created by gesuwall on 12/30/16.
 */

class SimpleMonkeyItem(val id: String, val timestamp: Long): MonkeyItem {
    override fun getMessageId() = id
    override fun getMessageTimestampOrder() = timestamp

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

    override fun getMessageText(): String {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessageTimestamp(): Long {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessageType(): Int {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOldMessageId() = null

    override fun getPlaceholderFilePath(): String {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSenderId(): String {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isIncomingMessage() = false

}