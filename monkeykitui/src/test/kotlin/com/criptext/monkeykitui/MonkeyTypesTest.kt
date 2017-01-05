package com.criptext.monkeykitui


import android.view.View
import com.criptext.monkeykitui.recycler.AdapterTestCase
import com.criptext.monkeykitui.recycler.EndItem
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.criptext.monkeykitui.recycler.holders.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.runner.RunWith
import org.junit.Test

/**
 * Created by gesuwall on 7/19/16.
 */


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class MonkeyTypesTest: AdapterTestCase(){


    fun newTextMessage(incoming: Boolean, status: MonkeyItem.DeliveryStatus): MonkeyItem{
        val newTimestamp = System.currentTimeMillis()
        val monkeyId = if(incoming) contactSessionId else mySessionId
        return object : MonkeyItem {
            override fun getConversationId() = "0"

            override fun getMessageTimestampOrder() = newTimestamp

            override fun getOldMessageId() = newTimestamp.toString()

            override fun getAudioDuration(): Long { throw UnsupportedOperationException() }

            override fun getSenderId(): String = monkeyId

            override fun getDeliveryStatus() = status

            override fun getFilePath(): String { throw UnsupportedOperationException() }

            override fun getFileSize(): Long { throw UnsupportedOperationException() }

            override fun getMessageId() = newTimestamp.toString()

            override fun getMessageTimestamp() = newTimestamp

            override fun getMessageText() = "Hello this is a text message"

            override fun getMessageType() = MonkeyItem.MonkeyItemType.text.ordinal

            override fun getPlaceholderFilePath(): String { throw UnsupportedOperationException() }

            override fun isIncomingMessage() = true

        }
    }

    fun createViewHolder(newMessage: MonkeyItem): MonkeyHolder {
        adapter.messages.smoothlyAddNewItem(newMessage)
        return adapter.onCreateViewHolder(null, adapter.getItemViewType(adapter.itemCount - 1))!!
    }

    fun testThatMessageGetsAMonkeyTextHolder(incoming: Boolean, status: MonkeyItem.DeliveryStatus){
        val newItem = newTextMessage(incoming, status)
        var newHolder = createViewHolder(newItem)
        assert(newHolder is MonkeyTextHolder)
        val textHolder = newHolder as MonkeyTextHolder
        adapter.bindViewHolder(textHolder, adapter.itemCount - 1)
    }

    @Test
    @Throws (Exception::class)
    fun textMessagesShouldHaveAMonkeyTextHolder() {

        testThatMessageGetsAMonkeyTextHolder(incoming = true, status = MonkeyItem.DeliveryStatus.delivered)
        testThatMessageGetsAMonkeyTextHolder(incoming = false, status = MonkeyItem.DeliveryStatus.delivered)
        testThatMessageGetsAMonkeyTextHolder(incoming = true, status = MonkeyItem.DeliveryStatus.sending)
        testThatMessageGetsAMonkeyTextHolder(incoming = false, status = MonkeyItem.DeliveryStatus.sending)
        testThatMessageGetsAMonkeyTextHolder(incoming = true, status = MonkeyItem.DeliveryStatus.error)
        testThatMessageGetsAMonkeyTextHolder(incoming = false, status = MonkeyItem.DeliveryStatus.error)

    }

    fun newVoiceNote(incoming: Boolean, status: MonkeyItem.DeliveryStatus): MonkeyItem{
        val newTimestamp = System.currentTimeMillis()
        val monkeyId = if(incoming) contactSessionId else mySessionId
        return object : MonkeyItem {
            override fun getConversationId() = "0"

            override fun getMessageTimestampOrder() = newTimestamp

            override fun getOldMessageId() = newTimestamp.toString()

            override fun getAudioDuration(): Long = 5000L

            override fun getSenderId(): String = monkeyId

            override fun getDeliveryStatus() = status

            override fun getFilePath(): String = activity!!.cacheDir.absolutePath + "/" +FAKE_AUDIO

            override fun getFileSize(): Long = 1234456L

            override fun getMessageId() = newTimestamp.toString()

            override fun getMessageTimestamp() = newTimestamp

            override fun getMessageText(): String {throw UnsupportedOperationException() }

            override fun getMessageType() = MonkeyItem.MonkeyItemType.audio.ordinal

            override fun getPlaceholderFilePath(): String { throw UnsupportedOperationException() }

            override fun isIncomingMessage() = incoming

        }
    }

    fun testThatMessageGetsAMonkeyAudioHolder(incoming: Boolean, status: MonkeyItem.DeliveryStatus) {
        val newItem = newVoiceNote(incoming, status)
        adapter.messages.smoothlyAddNewItem(newItem)
        var newHolder = createViewHolder(newItem)
        assert(newHolder is MonkeyAudioHolder)
    }

    @Test
    @Throws (Exception::class)
    fun voiceNotesShouldHaveAMonkeyAudioHolder() {

        testThatMessageGetsAMonkeyAudioHolder(incoming = true, status = MonkeyItem.DeliveryStatus.delivered)
        testThatMessageGetsAMonkeyAudioHolder(incoming = false, status = MonkeyItem.DeliveryStatus.delivered)
        testThatMessageGetsAMonkeyAudioHolder(incoming = true, status = MonkeyItem.DeliveryStatus.sending)
        testThatMessageGetsAMonkeyAudioHolder(incoming = false, status = MonkeyItem.DeliveryStatus.sending)
        testThatMessageGetsAMonkeyAudioHolder(incoming = true, status = MonkeyItem.DeliveryStatus.error)
        testThatMessageGetsAMonkeyAudioHolder(incoming = false, status = MonkeyItem.DeliveryStatus.error)

    }

    fun newPhoto(incoming: Boolean, status: MonkeyItem.DeliveryStatus): MonkeyItem{
        val newTimestamp = System.currentTimeMillis()
        val monkeyId = if(incoming) contactSessionId else mySessionId
        return object : MonkeyItem {
            override fun getConversationId() = "0"

            override fun getMessageTimestampOrder() = newTimestamp

            override fun getOldMessageId() = newTimestamp.toString()

            override fun getAudioDuration(): Long {throw UnsupportedOperationException() }

            override fun getSenderId(): String = monkeyId

            override fun getDeliveryStatus() = status

            override fun getFilePath(): String = activity!!.cacheDir.absolutePath + FAKE_PHOTO

            override fun getFileSize(): Long = 1234456L

            override fun getMessageId() = newTimestamp.toString()

            override fun getMessageTimestamp() = newTimestamp

            override fun getMessageText(): String {throw UnsupportedOperationException() }

            override fun getMessageType() = MonkeyItem.MonkeyItemType.photo.ordinal

            override fun getPlaceholderFilePath(): String = activity!!.cacheDir.absolutePath + FAKE_PHOTO

            override fun isIncomingMessage() = incoming

        }
    }

    fun testThatMessageGetsAMonkeyImageHolder(incoming: Boolean, status: MonkeyItem.DeliveryStatus) {
        val newItem = newPhoto(incoming, status)
        var newHolder = createViewHolder(newItem)
        assert(newHolder is MonkeyImageHolder)
        adapter.onBindViewHolder(newHolder, adapter.itemCount - 1)
    }

    @Test
    @Throws (Exception::class)
    fun photosShouldHaveAMonkeyImageHolder() {

        testThatMessageGetsAMonkeyImageHolder(incoming = true, status = MonkeyItem.DeliveryStatus.delivered)
        testThatMessageGetsAMonkeyImageHolder(incoming = false, status = MonkeyItem.DeliveryStatus.delivered)
        testThatMessageGetsAMonkeyImageHolder(incoming = true, status = MonkeyItem.DeliveryStatus.sending)
        testThatMessageGetsAMonkeyImageHolder(incoming = false, status = MonkeyItem.DeliveryStatus.sending)
        testThatMessageGetsAMonkeyImageHolder(incoming = true, status = MonkeyItem.DeliveryStatus.error)
        testThatMessageGetsAMonkeyImageHolder(incoming = false, status = MonkeyItem.DeliveryStatus.error)

    }

    fun newFileMessage(incoming: Boolean, status: MonkeyItem.DeliveryStatus): MonkeyItem{
        val newTimestamp = System.currentTimeMillis()
        val monkeyId = if(incoming) contactSessionId else mySessionId
        return object : MonkeyItem {
            override fun getConversationId() = "0"

            override fun getMessageTimestampOrder() = newTimestamp

            override fun getOldMessageId() = newTimestamp.toString()

            override fun getAudioDuration(): Long {throw UnsupportedOperationException() }

            override fun getSenderId(): String = monkeyId

            override fun getDeliveryStatus() = status

            override fun getFilePath(): String = activity!!.cacheDir.absolutePath + "/" + FAKE_FILE

            override fun getFileSize(): Long = 1234456L

            override fun getMessageId() = newTimestamp.toString()

            override fun getMessageTimestamp() = newTimestamp

            override fun getMessageText() = "filename" 

            override fun getMessageType() = MonkeyItem.MonkeyItemType.file.ordinal

            override fun getPlaceholderFilePath(): String { throw UnsupportedOperationException() }

            override fun isIncomingMessage() = incoming

        }
    }

    fun testThatMessageGetsAMonkeyFileHolder(incoming: Boolean, status: MonkeyItem.DeliveryStatus) {
        val newItem = newFileMessage(incoming, status)
        var newHolder = createViewHolder(newItem)
        assert(newHolder is MonkeyFileHolder)
        adapter.onBindViewHolder(newHolder, adapter.itemCount - 1)
        val fileHolder = newHolder as MonkeyFileHolder

        if(incoming) {
            if (status == MonkeyItem.DeliveryStatus.sending) {
                assert(fileHolder.downloadProgressBar!!.visibility == View.VISIBLE)
                assert(fileHolder.fileLogoImageView!!.visibility != View.VISIBLE)
            } else if(status == MonkeyItem.DeliveryStatus.error){
                assert(fileHolder.downloadProgressBar!!.visibility != View.VISIBLE)
                assert(fileHolder.fileLogoImageView!!.visibility == View.VISIBLE)
            }
        } else {
            if (status == MonkeyItem.DeliveryStatus.sending) {
                assert(fileHolder.sendingProgressBar!!.visibility == View.VISIBLE)
                assert(fileHolder.fileLogoImageView!!.visibility != View.VISIBLE)
            } else if(status == MonkeyItem.DeliveryStatus.error){
                assert(fileHolder.sendingProgressBar!!.visibility != View.VISIBLE)
                assert(fileHolder.fileLogoImageView!!.visibility == View.VISIBLE)
            }
        }
    }

    @Test
    @Throws (Exception::class)
    fun filesShouldHaveAMonkeyFileHolder() {

        testThatMessageGetsAMonkeyFileHolder(incoming = true, status = MonkeyItem.DeliveryStatus.delivered)
        testThatMessageGetsAMonkeyFileHolder(incoming = false, status = MonkeyItem.DeliveryStatus.delivered)
        testThatMessageGetsAMonkeyFileHolder(incoming = true, status = MonkeyItem.DeliveryStatus.sending)
        testThatMessageGetsAMonkeyFileHolder(incoming = false, status = MonkeyItem.DeliveryStatus.sending)
        testThatMessageGetsAMonkeyFileHolder(incoming = true, status = MonkeyItem.DeliveryStatus.error)
        testThatMessageGetsAMonkeyFileHolder(incoming = false, status = MonkeyItem.DeliveryStatus.error)

    }

    @Test
    @Throws (Exception::class)
    fun loadingViewShouldHaveMoreMessagesHolder() {
        val newItem = EndItem()
        var newHolder = createViewHolder(newItem)
        assert(newHolder is MonkeyEndHolder)
        adapter.bindViewHolder(newHolder, adapter.itemCount - 1)

    }

}