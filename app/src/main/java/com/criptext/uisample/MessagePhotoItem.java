package com.criptext.uisample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.criptext.monkeykitui.recycler.MonkeyItem;

import org.jetbrains.annotations.NotNull;

/**
 * Created by gesuwall on 4/7/16.
 */
public class MessagePhotoItem implements MonkeyItem {

    private String senderSessionId, messageId;
    private long timestamp;
    private boolean isIncoming;
    private Context context;

    public MessagePhotoItem(Context context, String senderId, String messageId, long timestamp, boolean isIncoming){
        senderSessionId = senderId;
        this.messageId = messageId;
        this.timestamp = timestamp;
        this.isIncoming = isIncoming;
        this.context = context;
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
        return MonkeyItemType.photo.ordinal();
    }

    @NotNull
    @Override
    public Object getDataObject() {
        return null;
    }

    @NotNull
    @Override
    public String getMessageText() {
        return "";
    }

    @NotNull
    @Override
    public String getFilePath() {
        return "";
    }

    @NotNull
    @Override
    public Bitmap getImageBitmap() {
        return BitmapFactory.decodeResource(context.getResources(),R.drawable.mrbean);
    }

    @NotNull
    @Override
    public Bitmap getImageCoverBitmap() {
        return BitmapFactory.decodeResource(context.getResources(),R.drawable.mrbean_blur);
    }

    @Override
    public long getFileSize() {
        return 0;
    }
}
