package com.criptext.uisample;

import android.content.Intent;
import android.os.Bundle;

import com.criptext.monkeykitui.MonkeyChatFragment;
import com.criptext.monkeykitui.recycler.MonkeyItem;
import com.criptext.monkeykitui.recycler.audio.DefaultVoiceNotePlayer;
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer;

import java.util.ArrayList;

/**
 * Created by gesuwall on 8/10/16.
 */
public class MainFragmentActivity extends BaseChatActivity {
    MonkeyChatFragment chatFragment;
    VoiceNotePlayer vnPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        chatFragment = (MonkeyChatFragment) getSupportFragmentManager().findFragmentById(R.id.chat_fragment);
        chatFragment.setInputListener(createInputListener());
        vnPlayer = new DefaultVoiceNotePlayer(this);
        chatFragment.setVoiceNotePlayer(vnPlayer);
    }

    @Override
    protected void onStart() {
        super.onStart();
        vnPlayer.initPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vnPlayer.releasePlayer();
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
        if(chatFragment != null)
            chatFragment.onActivityResult(requestCode, resultCode, data);
    }
}
