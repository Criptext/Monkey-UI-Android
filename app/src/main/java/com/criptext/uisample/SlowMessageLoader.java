package com.criptext.uisample;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.criptext.monkeykitui.recycler.MonkeyItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

/**
 * Creates batches of fake messages asynchronously to be inserted in MonkeyAdapter. Everytime a new
 * batch is created it has messages a day older than the last batch. Each batch has messages
 * ordered by timestamp ascending.
 *
 * When the loading is done, the messages are inserted to the MonkeyAdapter in MainActivity as if
 * they were older messages.
 * Created by gesuwall on 5/12/16.
 */
public class SlowMessageLoader  {
    final static String[] messages = { "Hello", "'sup", "How are you doing", "Is everything OK?", "I'm at work", "I'm at school",
    "The weather is terrible", "I'm not feeling very well", "Today is my lucky day", "I hate when that happens",
    "I'm fine", "What are you doing this weekend?", "Sorry, I have plans", "I'm free", "Everything is going according to plan",
    "Here's my credit card number: 1111 2222 3333 4444"};
    final static int MAX_MESSAGES = 150;
    final static int BATCH_SIZE = 26;
    WeakReference<BaseChatActivity> activityWeakReference;
    private int batchNumber;

    public SlowMessageLoader(BaseChatActivity act){
        batchNumber = 0;
        activityWeakReference = new WeakReference(act);
    }

    AsyncTask newAsyncTask() {
        return new AsyncTask<Object, Void, ArrayList<MonkeyItem>>(){

            protected void onPostExecute(ArrayList<MonkeyItem> newData) {
                BaseChatActivity act = activityWeakReference.get();
                if(act != null && newData != null){
                    act.addOldMessages(newData, batchNumber * BATCH_SIZE > MAX_MESSAGES);
                }
            }

            @Override
            protected ArrayList<MonkeyItem> doInBackground(Object... params) {
                try {
                    Thread.sleep(500);
                } catch(InterruptedException ex){

                }
                return generateRandomMessages();
            }
        };
    }


    ArrayList<MonkeyItem> generateRandomMessages(){
        Context ctx = activityWeakReference.get();
        if(ctx == null)
            return null;

        ArrayList<MonkeyItem> arrayList = new ArrayList<MonkeyItem>();
        long timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * (++batchNumber) * 24;
        for(int i = 0; i < BATCH_SIZE; i++){
            //Log.d("SlowMessageLoader", "creating new msg: " + timestamp);
            Random r = new Random();
            boolean incoming = r.nextBoolean();
            MessageItem item;

            if(i%6 == 1){
                //audio
                item = new MessageItem(incoming ? "1":"0", "" + timestamp, "-" + timestamp,
                        FakeFiles.defaultAudioFilepath(ctx), timestamp/1000, timestamp, incoming,
                        MonkeyItem.MonkeyItemType.audio);
                item.setDuration(1000 * 10);
            } else if(i%7 == 1){
                item = new MessageItem(incoming ? "1":"0", "" + timestamp, "-" + timestamp,
                        FakeFiles.defaultImageFilepath(ctx), timestamp/1000, timestamp, incoming,
                        MonkeyItem.MonkeyItemType.file);
            }
            else if(i%8 == 1){
                //photo
                item = new MessageItem(incoming ? "1":"0", "" + timestamp, "-" + timestamp,
                        FakeFiles.defaultImageFilepath(ctx), timestamp/1000, timestamp, incoming,
                        MonkeyItem.MonkeyItemType.photo);
                item.setPlaceHolderFilePath(FakeFiles.defaultImageBlurFilepath(ctx));
            }
            else {
                //text
                String message = messages[r.nextInt(messages.length)];
                item = new MessageItem(incoming ? "1":"0", "" + timestamp, "-" + timestamp,
                        message, timestamp/1000, timestamp, incoming, MonkeyItem.MonkeyItemType.text);
            }

            timestamp += r.nextInt(1000 * 60 * 10);
            arrayList.add(item);
        }

        //photo with errors
        MessageItem item = new MessageItem("0", "" + timestamp++,"-" + timestamp,
                FakeFiles.defaultImageFilepath(ctx), timestamp/1000, timestamp, false,
                MonkeyItem.MonkeyItemType.photo);
        item.setStatus(MonkeyItem.DeliveryStatus.error);
        arrayList.add(item);
        item = new MessageItem("1", "" + timestamp++,"-" + timestamp,
                FakeFiles.defaultImageBlurFilepath(ctx), timestamp/1000, timestamp, true,
                MonkeyItem.MonkeyItemType.photo);
        item.setStatus(MonkeyItem.DeliveryStatus.error);
        arrayList.add(item);

        //audio with errors
        item = new MessageItem("0", "" + timestamp++,"-" + timestamp,
                ctx.getCacheDir() + "/barneyfake.aac", timestamp/1000, timestamp, false,
                MonkeyItem.MonkeyItemType.audio);
        item.setStatus(MonkeyItem.DeliveryStatus.error);
        arrayList.add(item);
        item = new MessageItem("1", "" + timestamp++,"-" + timestamp,
                ctx.getCacheDir() + "/barneyfake.aac", timestamp/1000, timestamp, true,
                MonkeyItem.MonkeyItemType.audio);
        item.setStatus(MonkeyItem.DeliveryStatus.error);
        arrayList.add(item);
        item = new MessageItem("0", "" + timestamp++,"-" + timestamp,
                FakeFiles.defaultImageFilepath(ctx), timestamp/1000, timestamp, false,
                MonkeyItem.MonkeyItemType.file);
        item.setDeliveryStatus(MonkeyItem.DeliveryStatus.sending);
        arrayList.add(item);
        item = new MessageItem("1", "" + timestamp++,"-" + timestamp,
                FakeFiles.defaultImageFilepath(ctx), timestamp/1000, timestamp, true,
                MonkeyItem.MonkeyItemType.file);
        item.setDeliveryStatus(MonkeyItem.DeliveryStatus.sending);
        arrayList.add(item);

        return arrayList;
    }

    public void execute(){
        AsyncTask task = newAsyncTask();
        task.execute();
    }
}
