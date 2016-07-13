package com.criptext.uisample;

import com.criptext.monkeykitui.recycler.MonkeyItem;

import org.jetbrains.annotations.NotNull;

/**
 * Created by gesuwall on 4/7/16.
 */
public class MessageItem  implements MonkeyItem {

    private String senderSessionId, messageId, messageContent;
    private long timestamp;
    private boolean isIncoming;
    private DeliveryStatus status;
    private MonkeyItemType itemType;
    /*AUDIO*/
    private long duration;
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
        this.duration = 0;
        this.status = DeliveryStatus.read;
    }

    public void setStatus (DeliveryStatus status){
        this.status = status;
    }

    public void setDuration(long durationText) {
        this.duration = durationText;
    }

    public void setPlaceHolderFilePath(String placeHolderFilePath) {
        this.placeHolderFilePath = placeHolderFilePath;
    }

    public void setMessageContent(String content) {
        this.messageContent = content;
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
    public DeliveryStatus getDeliveryStatus() {
        return this.status;
    }

    public void setDeliveryStatus(DeliveryStatus newStatus) {
        this.status = newStatus;
    }

    @Override
    public int getMessageType() {
        return itemType.ordinal();
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
    public long getAudioDuration() {
        return duration;
    }
}
