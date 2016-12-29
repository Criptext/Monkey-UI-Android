package com.criptext.uisample;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.criptext.monkeykitui.MonkeyChatFragment;
import com.criptext.monkeykitui.input.MediaInputView;
import com.criptext.monkeykitui.recycler.GroupChat;
import com.criptext.monkeykitui.recycler.MonkeyAdapter;
import com.criptext.monkeykitui.recycler.MonkeyConfig;
import com.criptext.monkeykitui.recycler.MonkeyItem;
import com.criptext.monkeykitui.recycler.audio.AudioUIUpdater;
import com.criptext.monkeykitui.recycler.audio.DefaultVoiceNotePlayer;
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseChatActivity{

    /* Adapter that shows our chat messages in the recyclerView */
    private MonkeyAdapter adapter;
    private RecyclerView recycler;

    /* Object that plays voice notes in our recyclerView from the chat */
    private VoiceNotePlayer voiceNotePlayer;
    /* This view is used to compose new messages: audio, photo and text */
    private MediaInputView mediaInputView;
    /* handles sensor events to to change voice note playback */
    private SensorHandler sensorHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loader = new SlowMessageLoader(this);
        ArrayList<MonkeyItem> messages = loader.generateRandomMessages();
        adapter = new MonkeyAdapter(this, "mirror");
        recycler = (RecyclerView) findViewById(R.id.recycler);
        //adapter.addOldMessages(messages, false, recycler);
        //configureMonkeyAdapter();
        //adapter.setHasReachedEnd(false);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(adapter);
        AudioUIUpdater uiUpdater = new AudioUIUpdater(recycler);
        //adapter.setVoiceNotePlayer(voiceNotePlayer);
        initInputView();

        sensorHandler = new SensorHandler(voiceNotePlayer, this);
    }

    /**
     * Call this on the onCreate method if you want to customize the look of the bubbles.
     */
    private void customizeMonkeyAdapter(){
        MonkeyConfig config = new MonkeyConfig();
        config.setTextBubbleIncomingColor(Color.GREEN);
        config.setTextBubbleOutgoingColor(Color.BLUE);
        adapter.setMonkeyConfig(config);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorHandler.onPause();
    }

    @Override
    protected void onStart(){
        super.onStart();
        voiceNotePlayer.initPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        voiceNotePlayer.releasePlayer();
        sensorHandler.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorHandler.onDestroy();
    }

    public void initInputView(){
        mediaInputView = (MediaInputView) findViewById(R.id.inputView);
        mediaInputView.setInputListener(createInputListener());
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if(mediaInputView!=null && mediaInputView.getCameraHandler()!=null)
            mediaInputView.getCameraHandler().onActivityResult(requestCode, resultCode, data);

    }


    @Override
    void rebindMonkeyItem(MonkeyItem message) {
        adapter.rebindMonkeyItem(message, recycler);
    }

    @Override
    void addOldMessages(ArrayList<MonkeyItem> messages, boolean hasReachedEnd) {
        //adapter.addOldMessages(messages, hasReachedEnd, recycler);
    }

    @Override
    void smoothlyAddNewItem(MonkeyItem message) {
//        adapter.smoothlyAddNewItem(message, recycler);
    }

    @Nullable
    @Override
    public GroupChat getGroupChat(@NotNull String conversationId, @NotNull String membersIds) {
        return null;
    }

    @Override
    public void onStopChatFragment(@NotNull String conversationId) {

    }

    @Override
    public void deleteAllMessages(@NotNull String conversationId) {

    }

    @Override
    public void onStartChatFragment(@NotNull MonkeyChatFragment fragment, @NotNull String conversationId) {

    }
}
