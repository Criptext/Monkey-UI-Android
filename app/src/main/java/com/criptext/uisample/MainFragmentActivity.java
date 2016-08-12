package com.criptext.uisample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.criptext.monkeykitui.MonkeyChatFragment;
import com.criptext.monkeykitui.MonkeyConversationsFragment;
import com.criptext.monkeykitui.conversation.ConversationsActivity;
import com.criptext.monkeykitui.conversation.MonkeyConversation;
import com.criptext.monkeykitui.recycler.MonkeyItem;
import com.criptext.monkeykitui.recycler.audio.DefaultVoiceNotePlayer;
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer;
import com.criptext.uisample.conversation.FakeConversations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        //The content layout must have a FrameLayout as container of the fragments. using
        // different layouts like RelativeLayout may have weird results.
        setContentView(R.layout.activity_main_fragment);
        initConversationsFragment();
    }

    private void initConversationsFragment(){
        MonkeyConversationsFragment convFragment = new MonkeyConversationsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, convFragment);
        ft.commit();
    }
    private void initChatFragment(){
        MonkeyChatFragment chatFragment = new MonkeyChatFragment();
        //set an input listener to the chat fragment so that the user can compose and send messages
        chatFragment.setInputListener(createInputListener());
        //instantiate an object to play voice notes and pass it to the fragment
        vnPlayer = new DefaultVoiceNotePlayer(this);
        chatFragment.setVoiceNotePlayer(vnPlayer);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //animations must be set before adding or replacing fragments
        ft.setCustomAnimations(R.anim.mk_fragment_slide_right_in,
                R.anim.mk_fragment_slide_left_out,
                R.anim.mk_fragment_slide_left_in,
                R.anim.mk_fragment_slide_right_out);
        ft.replace(R.id.fragment_container, chatFragment);
        ft.addToBackStack(null);
        ft.commit();
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
        initChatFragment();
    }

    @Override
    public void requestConversations() {
        if(convFragment != null)
            convFragment.insertConversations(new FakeConversations().getAll(this));
    }

    @Override
    public void setChatFragment(@Nullable MonkeyChatFragment monkeyChatFragment) {
        chatFragment = monkeyChatFragment;
    }

    @Override
    public void setConversationsFragment(@Nullable MonkeyConversationsFragment monkeyConversationsFragment) {
        convFragment = monkeyConversationsFragment;
    }
}
