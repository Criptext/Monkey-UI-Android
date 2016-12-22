package com.criptext.uisample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.view.MenuItem;

import com.criptext.monkeykitui.MonkeyChatFragment;
import com.criptext.monkeykitui.MonkeyConversationsFragment;
import com.criptext.monkeykitui.conversation.ConversationsActivity;
import com.criptext.monkeykitui.conversation.ConversationsList;
import com.criptext.monkeykitui.conversation.MonkeyConversation;
import com.criptext.monkeykitui.input.listeners.InputListener;
import com.criptext.monkeykitui.recycler.GroupChat;
import com.criptext.monkeykitui.recycler.MonkeyItem;
import com.criptext.monkeykitui.recycler.audio.PlaybackService;
import com.criptext.monkeykitui.toolbar.ToolbarDelegate;
import com.criptext.monkeykitui.util.MonkeyFragmentManager;
import com.criptext.monkeykitui.util.Utils;
import com.criptext.uisample.conversation.FakeConversations;
import com.criptext.uisample.conversation.StateFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by gesuwall on 8/10/16.
 */
public class MainFragmentActivity extends BaseChatActivity implements ConversationsActivity, ToolbarDelegate {
    MonkeyChatFragment chatFragment;
    MonkeyConversationsFragment convFragment;
    InputListener inputListener;
    MonkeyFragmentManager fragmentManager;
    ConversationsList conversations;

    StateFragment stateFragment;

    static String STATE_KEY = "MonkeyKitUI.StateFragment";

    private boolean restoreState() {
        boolean restored = true;
        //Store activity state in headless fragment
        StateFragment _stateFragment = (StateFragment) getSupportFragmentManager().findFragmentByTag(STATE_KEY);
        if (_stateFragment == null) {
            _stateFragment = StateFragment.newStateFragment(this);
            getSupportFragmentManager().beginTransaction().add(_stateFragment, STATE_KEY).commit();
            restored = false;
        }
        stateFragment = _stateFragment;
        return restored;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean restored = restoreState();
        conversations = new ConversationsList(stateFragment.conversationsList);
        fragmentManager = new MonkeyFragmentManager(this, "UI Sample", stateFragment.mkFragmentStack);
        fragmentManager.setContentLayout(savedInstanceState);
        if (restored) //if this is a restored instance, we must restore the toolbar as well
        fragmentManager.restoreToolbar(stateFragment.activeConversation);

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
        //set all messages as read, use default avatar
        MonkeyChatFragment fragment =
                new MonkeyChatFragment.Builder("0", conversation.getName())
                    .setLastRead(System.currentTimeMillis())
                    .setReachedEnd(false)
                    .build();
        fragmentManager.setChatFragment(fragment);
        stateFragment.activeConversation = conversation;
    }

    @Override
    public void setVoiceNotePlayer(PlaybackService.VoiceNotePlayerBinder player) {
        super.setVoiceNotePlayer(player);
        if(chatFragment != null)
            chatFragment.setVoiceNotePlayer(player);
    }

    @Override
    public void setChatFragment(@Nullable MonkeyChatFragment monkeyChatFragment) {
        chatFragment = monkeyChatFragment;
        if (chatFragment != null) {
            chatFragment.setInputListener(createInputListener());
            //instantiate an object to play voice notes and pass it to the fragment
            chatFragment.setVoiceNotePlayer(vnPlayer);
        }
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
    public ConversationsList onRequestConversations() {
        return conversations;
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

    @Override
    public void onClickToolbar(@NotNull String monkeyID, @NotNull String name, @NotNull String lastSeen, @NotNull String avatarURL) {

    }

    @Override
    public void deleteChatFragment(@NotNull MonkeyChatFragment monkeyChatFragment) {

    }

    @Override
    public void deleteAllMessages(@NotNull String conversationId) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(chatFragment != null)
            chatFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}