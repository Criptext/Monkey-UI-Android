package com.criptext.uisample;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.criptext.monkeykitui.input.MediaInputView;
import com.criptext.monkeykitui.input.listeners.InputListener;
import com.criptext.monkeykitui.input.listeners.OnAttachmentButtonClickListener;
import com.criptext.monkeykitui.input.listeners.OnSendButtonClickListener;
import com.criptext.monkeykitui.input.listeners.RecordingListener;
import com.criptext.monkeykitui.recycler.ChatActivity;
import com.criptext.monkeykitui.recycler.MonkeyAdapter;
import com.criptext.monkeykitui.recycler.MonkeyItem;
import com.criptext.monkeykitui.recycler.audio.AudioPlaybackHandler;
import com.soundcloud.android.crop.Crop;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ChatActivity {

    final static String[] messages = { "Hello", "'sup", "How are you doing", "Is everything OK?", "I'm at work", "I'm at school",
    "The weather is terrible", "I'm not feeling very well", "Today is my lucky day", "I hate when that happens",
    "I'm fine", "What are you doing this weekend?", "Sorry, I have plans", "I'm free", "Everything is going according to plan",
    "Here's my credit card number: 1111 2222 3333 4444"};
    final static int MAX_MESSAGES = 150;
    MonkeyAdapter adapter;
    RecyclerView recycler;
    AudioPlaybackHandler audioHandler;

    String mAudioFileName = null;
    MediaRecorder mRecorder = null;

    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final String TEMP_AUDIO_FILE_NAME = "temp_audio.3gp";

    private MediaInputView mediaInputView;

    final static class SlowMessageLoader extends AsyncTask<WeakReference<MainActivity>, Void, ArrayList<MonkeyItem>>{
        WeakReference<MainActivity> activityWeakReference;
        @Override
        protected void onPostExecute(ArrayList<MonkeyItem> newData) {
            MainActivity act = activityWeakReference.get();
            if(act != null && newData != null){
                act.adapter.smoothlyAddNewData(newData, act.recycler, act.adapter.getItemCount() + newData.size() > MAX_MESSAGES);
            }
        }


        @Override
        protected ArrayList<MonkeyItem> doInBackground(WeakReference<MainActivity>... params) {
            activityWeakReference = params[0];
            try {
                Thread.sleep(500);
            } catch(InterruptedException ex){

            }
            return generateRandomMessages(activityWeakReference.get());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAudioFile();
        createImageFile();
        ArrayList<MonkeyItem> messages =generateRandomMessages(this);
        adapter = new MonkeyAdapter(this, messages);
        adapter.setHasReachedEnd(false);

        //
        recycler = (RecyclerView) findViewById(R.id.recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(adapter);

        mediaInputView = (MediaInputView) findViewById(R.id.inputView);
        if(mediaInputView!=null) {
            mediaInputView.setRecordingListener(new RecordingListener() {
                @Override
                public void onStartRecording() {
                    Log.d("MainActivity", "start record");
                    startRecording();
                }

                @Override
                public void onStopRecording() {
                    Log.d("MainActivity", "stop record");
                    stopRecording();
                    sendAudioFile();
                }

                @Override
                public void onCancelRecording() {
                    cancelRecording();
                }
            });

            mediaInputView.setInputListener(new InputListener() {
                @Override
                public void onNewItem(@NotNull MonkeyItem item) {
                    MessageItem newItem = new MessageItem("0", item.getMessageId(),
                            item.getMessageText(), item.getMessageTimestamp(), item.isIncomingMessage(),
                            MonkeyItem.MonkeyItemType.values()[item.getMessageType()]);
                    adapter.smoothlyAddNewItem(newItem, recycler);
                }
            });

            /*
            ONLY IF DEVELOPER DECIDES TO USE HIS OWN OPTIONS FOR LEFT BUTTON
            *
            mediaInputView.setActionString(new String [] {"Take a Photo", "Choose Photo"});
            mediaInputView.setOnAttachmentButtonClickListener(new OnAttachmentButtonClickListener() {
                @Override
                public void onAttachmentButtonClickListener(int item) {
                    mPhotoFileName = (System.currentTimeMillis()/1000) + TEMP_PHOTO_FILE_NAME;
                    switch (item){
                        case 0:
                            takePicture();
                            break;
                        case 1:
                            Crop.pickImage(MainActivity.this);
                            break;
                    }
                }
            });
            */
        }

        audioHandler = new AudioPlaybackHandler(adapter, recycler);

    }

    @Override
    protected void onStop() {
        audioHandler.releasePlayer();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private static ArrayList<MonkeyItem> generateRandomMessages(Context ctx){
        if(ctx == null)
            return null;

        ArrayList<MonkeyItem> arrayList = new ArrayList<MonkeyItem>();
        long timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48;
        for(int i = 0; i < 26; i++){
            Random r = new Random();
            boolean incoming = r.nextBoolean();
            MessageItem item;

            if(i%6 == 1){
                //audio
                item = new MessageItem(incoming ? "1":"0", "" + timestamp,
                        ctx.getCacheDir() + "/barney.aac", timestamp, incoming,
                        MonkeyItem.MonkeyItemType.audio);
                item.setDuration("00:10");
            }
            else if(i%8 == 1){
                //photo
                item = new MessageItem(incoming ? "1":"0", "" + timestamp,
                        ctx.getCacheDir() + "/mrbean.jpg", timestamp, incoming,
                        MonkeyItem.MonkeyItemType.photo);
                item.setPlaceHolderFilePath(ctx.getCacheDir() + "/mrbean_blur.jpg");
            }
            else {
                //text
                String message = messages[r.nextInt(messages.length)];
                item = new MessageItem(incoming ? "1":"0", "" + timestamp, message, timestamp, incoming,
                        MonkeyItem.MonkeyItemType.text);
            }
            timestamp += r.nextInt(1000 * 60 * 10);
            arrayList.add(item);
        }

        return arrayList;
    }

    /**
     * Si no tengo archivos creo uno nuevo.
     */
    private void createAudioFile(){
        File file = new File(getCacheDir() + "/barney.aac");
        if(!file.exists()){
            try {
            InputStream ins = getResources().openRawResource(R.raw.barney);
            FileOutputStream outputStream = new FileOutputStream(file.getPath());

            byte buf[] = new byte[1024];
            int len;

                while ((len = ins.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Si no tengo archivos creo uno nuevo.
     */
    private void createImageFile(){
        File file = new File(getCacheDir() + "/mrbean.jpg");
        if(!file.exists()){
            try {
                InputStream ins = getResources().openRawResource(R.raw.mrbean);
                FileOutputStream outputStream = new FileOutputStream(file.getPath());

                byte buf[] = new byte[1024];
                int len;

                while ((len = ins.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        file = new File(getCacheDir() + "/mrbean_blur.jpg");
        if(!file.exists()){
            try {
                InputStream ins = getResources().openRawResource(R.raw.mrbean_blur);
                FileOutputStream outputStream = new FileOutputStream(file.getPath());

                byte buf[] = new byte[1024];
                int len;

                while ((len = ins.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /***AUDIO RECORD STUFFS****/

    private void startRecording(){

        try {
            mAudioFileName = getCacheDir().toString() + "/" + (System.currentTimeMillis()/1000) + TEMP_AUDIO_FILE_NAME;
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mAudioFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //TO MAKE AUDIO LOW QUALITY
            mRecorder.setAudioSamplingRate(22050);//8khz-92khz
            mRecorder.setAudioEncodingBitRate(22050);//8000
            mRecorder.prepare();
            mRecorder.start();
            mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                        mr.release();
                    }
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private void stopRecording() {
        try{
            if(mRecorder!=null){
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void cancelRecording() {
        stopRecording();
        File file = new File(mAudioFileName);
        if(file.exists())
            file.delete();
    }

    private void sendAudioFile(){

        File file = new File(mAudioFileName);
        if(file.exists()) {
            long timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48;
            MessageItem item = new MessageItem("0", "" + timestamp,
                    mAudioFileName, timestamp, false,
                    MonkeyItem.MonkeyItemType.audio);
            item.setDuration("00:10");
            adapter.getMessagesList().add(item);
            adapter.notifyDataSetChanged();
            recycler.scrollToPosition(adapter.getMessagesList().size()-1);
        }
    }

    /***IMAGE RECORD STUFFS****/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if(mediaInputView!=null && mediaInputView.getCameraHandler()!=null)
            mediaInputView.getCameraHandler().onActivityResult(requestCode,resultCode, data);

    }

    /***OVERRIDE METHODS****/

    @Override
    public int getMemberColor(@NotNull String sessionId) {
        return Color.WHITE;
    }

    @NotNull
    @Override
    public String getMenberName(@NotNull String sessionId) {
        return "Unknown";
    }

    @Override
    public boolean isGroupChat() {
        return false;
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public void onFileDownloadRequested(int position, @NotNull MonkeyItem item) {

    }

    @Override
    public void onLoadMoreData(int loadedItems) {
        SlowMessageLoader loader = new SlowMessageLoader();
        loader.execute(new WeakReference<>(this));
    }
}
