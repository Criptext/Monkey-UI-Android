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
    private OutgoingMessageStatus status;

    public MessageItem(String senderId, String messageId, String messageContent, long timestamp, boolean isIncoming){
        senderSessionId = senderId;
        this.messageId = messageId;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        this.isIncoming = isIncoming;
    }

    public void setStatus (OutgoingMessageStatus status){
        this.status = status;
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
        return MonkeyItemType.text.ordinal();
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
}
