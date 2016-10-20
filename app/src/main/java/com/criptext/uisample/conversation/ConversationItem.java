package com.criptext.uisample.conversation;

import com.criptext.monkeykitui.conversation.MonkeyConversation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by gesuwall on 8/11/16.
 */
public class ConversationItem implements MonkeyConversation {

    private String id;
    private String name;
    private String lastMessage;


    private String groupMembers;
    private long datetime;
    private int newMessages;
    private int status;

    public void setAvatarFilePath(String avatarFilePath) {
        this.avatarFilePath = avatarFilePath;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setNewMessages(int newMessages) {
        this.newMessages = newMessages;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    private String avatarFilePath;

    public ConversationItem(String id, String name, long datetime, int status){
        this.id = id;
        this.name = name;
        this.datetime = datetime;
        this.status = status;

        lastMessage = "";
        newMessages = 0;
    }

    @NotNull
    @Override
    public String getAvatarFilePath() {
        return avatarFilePath;
    }

    @NotNull
    @Override
    public String getConvId() {
        return id;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getDatetime() {
        return datetime;
    }

    @NotNull
    @Override
    public String getSecondaryText() {
        return lastMessage;
    }

    @Override
    public int getTotalNewMessages() {
        return newMessages;
    }

    @Override
    public boolean isGroup() {
        return id.startsWith("G:");
    }

    @Override
    public int getStatus() {
        return status;
    }

    @NotNull
    @Override
    public String getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(String groupMembers) {
        this.groupMembers = groupMembers;
    }

    @Nullable
    @Override
    public String getAdmins() {
        return null;
    }
}
