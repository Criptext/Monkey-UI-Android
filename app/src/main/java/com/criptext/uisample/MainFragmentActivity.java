package com.criptext.uisample;

import android.content.Intent;
import android.os.Bundle;

import com.criptext.monkeykitui.MonkeyChatFragment;
import com.criptext.monkeykitui.MonkeyConversationsFragment;
import com.criptext.monkeykitui.conversation.ConversationsActivity;
import com.criptext.monkeykitui.conversation.MonkeyConversation;
import com.criptext.monkeykitui.recycler.MonkeyItem;
import com.criptext.monkeykitui.recycler.audio.DefaultVoiceNotePlayer;
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer;
import com.criptext.uisample.conversation.FakeConversations;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by gesuwall on 8/10/16.
 */
public class MainFragmentActivity extends BaseChatActivity implements ConversationsActivity {
    MonkeyChatFragment chatFragment;
    MonkeyConversationsFragment convFragment;
    VoiceNotePlayer vnPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        convFragment = (MonkeyConversationsFragment) getSupportFragmentManager().findFragmentById(R.id.monkey_fragment);
    }

    private void initChatFragment(){
        chatFragment = (MonkeyChatFragment) getSupportFragmentManager().findFragmentById(R.id.monkey_fragment);
        //set an input listener to the chat fragment so that the user can compose and send messages
        chatFragment.setInputListener(createInputListener());
        //instantiate an object to play voice notes and pass it to the fragment
        vnPlayer = new DefaultVoiceNotePlayer(this);
        chatFragment.setVoiceNotePlayer(vnPlayer);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    void rebindMonkeyItem(MonkeyItem message) {
        chatFragment.rebindMonkeyItem(message);
    }

    @Override
    void addOldMessages(ArrayList<MonkeyItem> messages, boolean hasReachedEnd) {
        chatFragment.addOldMessages(messages, hasReachedEnd);
    }

    @Override
    void smoothlyAddNewItem(MonkeyItem message) {
        chatFragment.smoothlyAddNewItem(message);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Activity must manually call chatFragments on activityResult. it's not automatic.
        //This makes sure that the inputListener receives the edited photos that the user wants to send
        if(chatFragment != null)
            chatFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConversationClicked(@NotNull MonkeyConversation conversation) {

    }

    @Override
    public void requestConversations() {
        convFragment.insertConversations(new FakeConversations().getAll(this));
    }
}
