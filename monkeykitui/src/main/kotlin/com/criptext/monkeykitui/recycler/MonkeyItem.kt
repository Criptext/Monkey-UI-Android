package com.criptext.monkeykitui.recycler

import android.graphics.Bitmap
import android.view.View

/**
 * Created by gesuwall on 4/4/16.
 */

interface MonkeyItem {

    /*COMMON*/

    fun getMessageTimestamp() : Long

    fun getMessageId() : String

    fun isIncomingMessage() : Boolean

    fun getOutgoingMessageStatus() : OutgoingMessageStatus

    fun getMessageType() : Int

    fun getDataObject() : Any

    /*TEXT*/

    fun  getMessageText() : String

    /*PHOTO*/

    fun getImageCoverBitmap() : Bitmap

    fun getItemClickListener() : View.OnClickListener

    /*AUDIO, PHOTO, FILE */

    fun getFilePath() : String

    fun getFileSize() : Long

    /* AUDIO */

    fun getAudioDuration() : String

    /*CONTACT */

    fun getContactSessionId() : String

    enum class OutgoingMessageStatus {
        sending, delivered, read
    }

    enum class MonkeyItemType() {
        text, audio, photo, contact, file
    }


}