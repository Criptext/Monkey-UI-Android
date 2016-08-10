package com.criptext.uisample;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.criptext.monkeykitui.input.MediaInputView;
import com.criptext.monkeykitui.input.listeners.InputListener;
import com.criptext.monkeykitui.recycler.ChatActivity;
import com.criptext.monkeykitui.recycler.MonkeyAdapter;
import com.criptext.monkeykitui.recycler.MonkeyConfig;
import com.criptext.monkeykitui.recycler.MonkeyItem;
import com.criptext.monkeykitui.recycler.audio.AudioUIUpdater;
import com.criptext.monkeykitui.recycler.audio.DefaultVoiceNotePlayer;
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
        adapter = new MonkeyAdapter(this);
        adapter.addOldMessages(messages, false);
        //configureMonkeyAdapter();
        adapter.setHasReachedEnd(false);

        recycler = (RecyclerView) findViewById(R.id.recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(adapter);
        AudioUIUpdater uiUpdater = new AudioUIUpdater(recycler);
        voiceNotePlayer = new DefaultVoiceNotePlayer(this, uiUpdater);
        adapter.setVoiceNotePlayer(voiceNotePlayer);
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
        if(voiceNotePlayer!=null && voiceNotePlayer.isPlayingAudio()) {
            voiceNotePlayer.onPauseButtonClicked();
        }
        if(voiceNotePlayer!=null) {
            voiceNotePlayer.releasePlayer();
        }
        sensorHandler.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorHandler.onDestroy();
    }

    public void initInputView(){
        mediaInputView = (MediaInputView) findViewById(R.id.inputView);
        if(mediaInputView!=null) {
            mediaInputView.setInputListener(new InputListener() {
                @Override
                public void onNewItem(@NotNull MonkeyItem item) {
                    MessageItem newItem = new MessageItem("0", item.getMessageId(), item.getOldMessageId(),
                            item.getMessageText(), item.getMessageTimestamp(), item.getMessageTimestampOrder(),
                            item.isIncomingMessage(), MonkeyItem.MonkeyItemType.values()[item.getMessageType()]);

                    newItem.setStatus(MonkeyItem.DeliveryStatus.read);
                    switch (MonkeyItem.MonkeyItemType.values()[item.getMessageType()]) {
                        case audio: //init audio MessageItem
                            newItem.setDuration(item.getAudioDuration());
                            newItem.setMessageContent(item.getFilePath());
                            break;
                        case photo:
                            newItem.setMessageContent(item.getFilePath());
                            break;
                    }

                    adapter.smoothlyAddNewItem(newItem, recycler); // Add to recyclerView
                }
            });
            /*
            ONLY IF DEVELOPER DECIDES TO USE HIS OWN OPTIONS FOR LEFT BUTTON
            mediaInputView.getAttachmentHandler().addNewAttachmentButton(new AttachmentButton() {
                @NonNull
                @Override
                public String getTitle() {
                    return "Send Contact";
                }

                @Override
                public void clickButton() {
                    System.out.println("DO SOMETHING!!");
                }
            });
            */
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if(mediaInputView!=null && mediaInputView.getCameraHandler()!=null)
            mediaInputView.getCameraHandler().onActivityResult(requestCode,resultCode, data);

    }


    @Override
    void rebindMonkeyItem(MonkeyItem message) {
        adapter.rebindMonkeyItem(message, recycler);
    }

    @Override
    void addOldMessages(ArrayList<MonkeyItem> messages, boolean hasReachedEnd) {
        adapter.addOldMessages(messages, hasReachedEnd);
    }
}
