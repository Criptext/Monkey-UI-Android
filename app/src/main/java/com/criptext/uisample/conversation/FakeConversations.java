package com.criptext.uisample.conversation;

import android.content.Context;

import com.criptext.monkeykitui.conversation.MonkeyConversation;
import com.criptext.uisample.FakeFiles;

import java.util.ArrayList;

/**
 * Created by gesuwall on 8/11/16.
 */
public class FakeConversations {
    public ArrayList<MonkeyConversation> getAll(Context ctx){
        ArrayList<MonkeyConversation>  conversations = new ArrayList<>();
        final String avatarPath = FakeFiles.defaultImageFilepath(ctx);
        long timestamp = System.currentTimeMillis();
        ConversationItem newConversation;

        newConversation = new ConversationItem("sdfgsdg54ed56", "Gianni Carlo",
                timestamp, MonkeyConversation.ConversationStatus.empty.ordinal());
        conversations.add(0, newConversation);

        timestamp += 1;
        newConversation = new ConversationItem("G:235", "Devlab",
                timestamp, MonkeyConversation.ConversationStatus.deliveredMessage.ordinal());
        newConversation.setLastMessage("Donde estan?");
        conversations.add(0, newConversation);

        timestamp += 1;
        newConversation = new ConversationItem("w34tef35y67", "Alberto Vera",
                timestamp, MonkeyConversation.ConversationStatus.sentMessageRead.ordinal());
        newConversation.setLastMessage("ok!");
        conversations.add(0, newConversation);

        timestamp += 1;
        newConversation = new ConversationItem("gsdfgsrtr5e2e", "Luis Loaiza",
                timestamp, MonkeyConversation.ConversationStatus.receivedMessage.ordinal());
        newConversation.setLastMessage("Sigues en la misma pantalla!?");
        conversations.add(0, newConversation);

        timestamp += 1;
        newConversation = new ConversationItem("gstehsfhty34vf", "Mayer Mizrachi",
                timestamp, MonkeyConversation.ConversationStatus.receivedMessage.ordinal());
        newConversation.setNewMessages(4);
        newConversation.setLastMessage("Vamos por donas!");
        newConversation.setAvatarFilePath(avatarPath);
        conversations.add(0, newConversation);
        return conversations;
    }
}
