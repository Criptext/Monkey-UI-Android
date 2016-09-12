package com.criptext.uisample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.criptext.monkeykitui.input.listeners.InputListener;
import com.criptext.monkeykitui.recycler.ChatActivity;
import com.criptext.monkeykitui.recycler.MonkeyItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by gesuwall on 8/10/16.
 */
public abstract class BaseChatActivity extends AppCompatActivity implements ChatActivity {

    private Handler handler = new Handler();
    SlowMessageLoader loader; //loads fake messages
    FakeFiles fakeFiles; //manages fake photos and voice notes.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new SlowMessageLoader(this);
        fakeFiles = new FakeFiles(this);
        fakeFiles.createAudioFile();
        fakeFiles.createImageFile();
    }
    @Override
    public boolean isOnline() {
        return true;
    }

    public InputListener createInputListener(){
        //Everytime a new item is composed with the input view, create a new MessageItem instance of it
        //and add it to the recyclerView.
        return new InputListener() {
            @Override
            public void onNewItemFileError(@NotNull int filetype) {
                Toast.makeText(BaseChatActivity.this, "Error writing file of type " +
                        MonkeyItem.MonkeyItemType.values()[filetype], Toast.LENGTH_LONG).show();
            }

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

                    smoothlyAddNewItem(newItem); // Add to recyclerView
                }
            };
    }

    private void mockFileNetworkRequests(MonkeyItem item) {
        final MessageItem message = (MessageItem) item;
        Runnable errorCallback = new Runnable() {
            @Override
            public void run() {
                //message could not be downloaded/uploaded, set status as error and update the UI
                if (message.getDeliveryStatus() == MonkeyItem.DeliveryStatus.sending) {
                    message.setDeliveryStatus(MonkeyItem.DeliveryStatus.error);
                    rebindMonkeyItem(message);
                }
            }
        };
        //We are not actually making any network requests so if the message is not in 'sending' status
        //mark it as 'sending' and rebind. after 3 seconds mark it as error.
        if (message.getDeliveryStatus() != MonkeyItem.DeliveryStatus.sending) {
            message.setDeliveryStatus(MonkeyItem.DeliveryStatus.sending);
            rebindMonkeyItem(message);
        } else
            handler.postDelayed(errorCallback, 3000);
    }

    /**
     * updates the UI of a MonkeyItem that is displayed in the RecyclerView
     * @param message The message whose ViewHolder we want to update.
     */
    abstract void rebindMonkeyItem(MonkeyItem message);

    /**
     * Adds a list of messages to the beginning of the RecyclerView.
     * @param messages list of messages to add
     * @param hasReachedEnd true if the are no more old messages that can be added to this conversation
     */
    abstract void addOldMessages(ArrayList<MonkeyItem> messages, boolean hasReachedEnd);

    /**
     * Adds a MonkeyItem to the RecyclerView at the last position with a smooth animation.
     * @param message the monkeyItem to add
     */
    abstract void smoothlyAddNewItem(MonkeyItem message);

    @Override
    public void onFileDownloadRequested(@NotNull MonkeyItem item) {
        mockFileNetworkRequests(item);
    }

    @Override
    public void onFileUploadRequested(@NotNull MonkeyItem item) {
        mockFileNetworkRequests(item);
    }

    @Override
    public void onLoadMoreData(String conversationId) {
        loader.execute();
    }

    @Nullable
    @Override
    public List<MonkeyItem> getInitialMessages(@NotNull String conversationId) {
        return null;
    }
}
