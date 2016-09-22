package com.criptext.uisample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.criptext.monkeykitui.MonkeyChatFragment;
import com.criptext.monkeykitui.MonkeyConversationsFragment;
import com.criptext.monkeykitui.conversation.ConversationsActivity;
import com.criptext.monkeykitui.conversation.MonkeyConversation;
import com.criptext.monkeykitui.input.listeners.InputListener;
import com.criptext.monkeykitui.recycler.GroupChat;
import com.criptext.monkeykitui.recycler.MonkeyItem;
import com.criptext.monkeykitui.recycler.audio.DefaultVoiceNotePlayer;
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer;
import com.criptext.monkeykitui.util.MonkeyFragmentManager;
import com.criptext.monkeykitui.util.Utils;
import com.criptext.uisample.conversation.FakeConversations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by gesuwall on 8/10/16.
 */
public class MainFragmentActivity extends BaseChatActivity implements ConversationsActivity {
    MonkeyChatFragment chatFragment;
    MonkeyConversationsFragment convFragment;
    VoiceNotePlayer vnPlayer;
    InputListener inputListener;
    MonkeyFragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = new MonkeyFragmentManager(this);
        fragmentManager.setConversationsTitle("UI Sample");
        fragmentManager.setContentLayout(savedInstanceState);

        fragmentManager.showStatusNotification(Utils.ConnectionStatus.connecting);
        //Simulating connectivity status
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fragmentManager.showStatusNotification(Utils.ConnectionStatus.connected);
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
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
        if(chatFragment != null)
        chatFragment.rebindMonkeyItem(message);
    }

    @Override
    void addOldMessages(ArrayList<MonkeyItem> messages, boolean hasReachedEnd) {
        if(chatFragment != null)
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
        if(inputListener == null)
            inputListener = createInputListener();
        if(vnPlayer == null)
            vnPlayer = new DefaultVoiceNotePlayer(this);
        //set all messages as read, use default avatar
        MonkeyChatFragment fragment =
                MonkeyChatFragment.Companion.newGroupInstance("0", conversation.getName(),
                "", false, System.currentTimeMillis(), conversation.getGroupMembers());
        fragmentManager.setChatFragment(fragment, inputListener, vnPlayer);
    }

    @Override
    public void requestConversations() {
        if(convFragment != null)
            convFragment.insertConversations(new FakeConversations().getAll(this), true);
    }

    @Override
    public void setChatFragment(@Nullable MonkeyChatFragment monkeyChatFragment) {
        chatFragment = monkeyChatFragment;
    }

    @Override
    public void setConversationsFragment(@Nullable MonkeyConversationsFragment monkeyConversationsFragment) {
        convFragment = monkeyConversationsFragment;
    }

    @Override
    public void retainMessages(@NotNull String conversationId, @NotNull List<? extends MonkeyItem> messages) {

    }

    @Override
    public void onLoadMoreConversations(int loreturnadedItems) {

    }

    @Override
    public void retainConversations(@NotNull List<? extends MonkeyConversation> conversations) {

    }

    @Nullable
    @Override
    public GroupChat getGroupChat(@NotNull String conversationId, @NotNull String membersIds) {
        return null;
    }

    @Override
    public void onStartChatFragment(@NotNull String conversationId) {

    }

    @Override
    public void onStopChatFragment(@NotNull String conversationId) {

    }

    @Override
    public void onConversationDeleted(@NotNull MonkeyConversation group) {

    }

}