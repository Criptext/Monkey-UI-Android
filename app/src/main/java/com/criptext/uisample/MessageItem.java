package com.criptext.uisample;

import android.graphics.Bitmap;
import android.view.View;

import com.criptext.monkeykitui.recycler.MonkeyItem;

import org.jetbrains.annotations.NotNull;

/**
 * Created by gesuwall on 4/7/16.
 */
public class MessageItem  implements MonkeyItem {

    private String senderSessionId, messageId, messageContent;
    private long timestamp;
    private boolean isIncoming;
    private OutgoingMessageStatus status;
    private MonkeyItemType itemType;
    /*AUDIO*/
    private String duration;
    /*PHOTO*/
    private String placeHolderFilePath;

    public MessageItem(String senderId, String messageId, String messageContent, long timestamp,
                       boolean isIncoming, MonkeyItemType itemType){
        senderSessionId = senderId;
        this.messageId = messageId;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        this.isIncoming = isIncoming;
        this.itemType = itemType;
        this.placeHolderFilePath = "";
        this.duration = "0";
    }

    public void setStatus (OutgoingMessageStatus status){
        this.status = status;
    }

    public void setDuration(String durationText) {
        this.duration = durationText;
    }

    public void setPlaceHolderFilePath(String placeHolderFilePath) {
        this.placeHolderFilePath = placeHolderFilePath;
    }

    @NotNull
    @Override
    public String getContactSessionId() {
        return senderSessionId;
    }

    @Override
    public long getMessageTimestamp() {
        return timestamp;
    }

    @NotNull
    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public boolean isIncomingMessage() {
        return isIncoming;
    }

    @NotNull
    @Override
    public OutgoingMessageStatus getOutgoingMessageStatus() {
        return OutgoingMessageStatus.read;
    }

    @Override
    public int getMessageType() {
        return itemType.ordinal();
    }

    @NotNull
    @Override
    public Object getDataObject() {
        return null;
    }

    @NotNull
    @Override
    public String getMessageText() {
        return messageContent;
    }

    @NotNull
    @Override
    public String getFilePath() {
        return messageContent;
    }

    @NotNull
    @Override
    public String getPlaceholderFilePath() {
        return placeHolderFilePath;
    }

    @Override
    public long getFileSize() {
        return 0;
    }

    @Override
    public String getAudioDuration() {
        return duration;
    }
}
