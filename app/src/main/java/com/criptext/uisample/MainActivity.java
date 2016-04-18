package com.criptext.uisample;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.criptext.monkeykitui.input.ButtonsListeners;
import com.criptext.monkeykitui.input.InputView;
import com.criptext.monkeykitui.input.RecordingListeners;
import com.criptext.monkeykitui.recycler.ChatActivity;
import com.criptext.monkeykitui.recycler.MonkeyAdapter;
import com.criptext.monkeykitui.recycler.MonkeyItem;
import com.criptext.monkeykitui.recycler.listeners.AudioListener;
import com.soundcloud.android.crop.Crop;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ChatActivity {

    String[] messages = { "Hello", "'sup", "How are you doing", "Is everything OK?", "I'm at work", "I'm at school",
    "The weather is terrible", "I'm not feeling very well", "Today is my lucky day", "I hate when that happens",
    "I'm fine", "What are you doing this weekend?", "Sorry, I have plans", "I'm free", "Everything is going according to plan",
    "Here's my credit card number: 1111 2222 3333 4444"};

    MonkeyAdapter adapter;
    RecyclerView recycler;
    InputView inputView;
    ArrayList<MonkeyItem> monkeyMessages;

    MediaPlayer player;
    MonkeyItem playingItem = null;

    String mAudioFileName = null;
    String mPhotoFileName = null;
    File mPhotoFile = null;
    MediaRecorder mRecorder = null;

    public static final Uri CONTENT_URI = Uri.parse("content://com.criptext.uisample/");
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final String TEMP_AUDIO_FILE_NAME = "temp_audio.3gp";
    public enum RequestType {openGallery, takePicture, editPhoto, cropPhoto}
    private int orientationImage;

    int playingItemPosition = -1;
    boolean playingAudio = false;
    Runnable playerRunnable = new Runnable() {
        @Override
        public void run() {
            if(playingAudio){
                adapter.notifyItemChanged(playingItemPosition);
                recycler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createAudioFile();
        createImageFile();

        inputView = (InputView)findViewById(R.id.inputView);
        recycler = (RecyclerView) findViewById(R.id.recycler);

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mPhotoFile = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        } else {
            mPhotoFile = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }

        monkeyMessages = generateRandomMessages();
        adapter = new MonkeyAdapter(this, monkeyMessages);

        player = new MediaPlayer();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                player.start();
                playingAudio = true;
                playerRunnable.run();
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                notifyPlaybackStopped();

            }
        });

        adapter.setAudioListener(new AudioListener() {
            @Override
            public void onPlayButtonClicked(int position, @NotNull MonkeyItem item) {
                if (playingItem != null && item.getMessageId().equals(playingItem.getMessageId())) {
                    player.start();
                    adapter.notifyItemChanged(position);
                    return;
                } else {
                    player.reset();
                    playingItem = item;
                    adapter.notifyItemChanged(playingItemPosition);
                    playingItemPosition = position;
                    try {
                        player.setDataSource(MainActivity.this, Uri.fromFile(new File(item.getFilePath())));
                        player.prepareAsync();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void onPauseButtonClicked(int position, @NotNull MonkeyItem item) {
                player.pause();
                playingAudio = false;
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onProgressManuallyChanged(int position, @NotNull MonkeyItem item, int newProgress) {
                player.seekTo(newProgress * player.getDuration() / 100);
            }
        });

        recycler.setItemAnimator(new RecyclerView.ItemAnimator() {
            @Override
            public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @android.support.annotation.Nullable ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @android.support.annotation.Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
                return false;
            }

            @Override
            public void runPendingAnimations() {
            }

            @Override
            public void endAnimation(RecyclerView.ViewHolder item) {
            }

            @Override
            public void endAnimations() {
            }

            @Override
            public boolean isRunning() {
                return false;
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(adapter);

        inputView.setOnRecordListener(new RecordingListeners(){
            @Override
            public void onStartRecording() {
                super.onStartRecording();
                startRecording();
            }

            @Override
            public void onStopRecording() {
                super.onStopRecording();
                stopRecording();
                sendAudioFile();
            }

            @Override
            public void onCancelRecording() {
                super.onCancelRecording();
                cancelRecording();
            }
        });

        inputView.setOnButtonsClickedListener(new ButtonsListeners(){

            @Override
            public void onAttachmentButtonClicked() {
                super.onAttachmentButtonClicked();
                selectImage();
            }

            @Override
            public void onSendButtonClicked(String text) {
                super.onSendButtonClicked(text);
                long timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48;
                MessageItem item = new MessageItem("0", "" + timestamp, text, timestamp, false,
                        MonkeyItem.MonkeyItemType.text);
                monkeyMessages.add(item);
                adapter.notifyDataSetChanged();
                recycler.scrollToPosition(monkeyMessages.size()-1);
            }
        });
    }

    @Override
    protected void onStop() {
        try{
            if(playingAudio) {
                player.release();
                recycler.removeCallbacks(playerRunnable);
                notifyPlaybackStopped();
            }
        }catch (IllegalStateException ex){
            ex.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void notifyPlaybackStopped(){
        int oldPosition = playingItemPosition;
        playingAudio = false;
        playingItem = null;
        playingItemPosition= -1;
        adapter.notifyItemChanged(oldPosition);
    }

    private ArrayList<MonkeyItem> generateRandomMessages(){
        ArrayList<MonkeyItem> arrayList = new ArrayList<MonkeyItem>();
        long timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48;
        for(int i = 0; i < 100; i++){
            Random r = new Random();
            boolean incoming = r.nextBoolean();
            MessageItem item;

            if(i%6 == 1){
                //audio
                item = new MessageItem(incoming ? "1":"0", "" + timestamp,
                        getCacheDir() + "/barney.aac", timestamp, incoming,
                        MonkeyItem.MonkeyItemType.audio);
                item.setDuration("00:10");
            }
            else if(i%8 == 1){
                //photo
                item = new MessageItem(incoming ? "1":"0", "" + timestamp,
                        getCacheDir() + "/mrbean.jpg", timestamp, incoming,
                        MonkeyItem.MonkeyItemType.photo);
                item.setPlaceHolderFilePath(getCacheDir() + "/mrbean_blur.jpg");
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
            monkeyMessages.add(item);
            adapter.notifyDataSetChanged();
            recycler.scrollToPosition(monkeyMessages.size()-1);
        }
    }

    /***IMAGE RECORD STUFFS****/

    private void selectImage(){

        mPhotoFileName = (System.currentTimeMillis()/1000) + TEMP_PHOTO_FILE_NAME;

        final String [] items			= new String [] {"Take a Photo", "Choose Photo"};
        ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {
                    takePicture();
                } else if(item == 1){
                    Crop.pickImage(MainActivity.this);
                }
                dialog.dismiss();
            }
        } ).show();

    }

    public void takePicture() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri mImageCaptureUri;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(getTempFile());
            }
            else {
				/*
				 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
				 */
                mImageCaptureUri = CONTENT_URI;
            }
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, RequestType.takePicture.ordinal());
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public File getTempFile(){

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return new File(Environment.getExternalStorageDirectory(), mPhotoFileName);
        }
        else {
            return new File(getFilesDir(), mPhotoFileName);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == Crop.REQUEST_PICK )
            requestCode = RequestType.openGallery.ordinal();
        if (requestCode == Crop.REQUEST_CROP)
            requestCode = RequestType.cropPhoto.ordinal();

        Uri destination = Uri.fromFile(getTempFile());

        switch (RequestType.values()[requestCode]) {
            case openGallery: {
                try {
                    ExifInterface ei = new ExifInterface(getTempFile().getAbsolutePath());
                    orientationImage = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                } catch (IOException e) {
                    Log.e("error", "Exif error");
                }
                Crop.of(data.getData(), destination).start(this);
                break;
            }
            case takePicture: {
                try {
                    ExifInterface ei = new ExifInterface(getTempFile().getAbsolutePath());
                    orientationImage = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                } catch (IOException e) {
                    Log.e("error", "Exif error");
                }
                Crop.of(destination, destination).start(this);
                break;
            }
            case cropPhoto: {

                int rotation = 0;
                if (orientationImage == 0){
                    try {
                        ExifInterface ei = new ExifInterface(getTempFile().getAbsolutePath());
                        orientationImage = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    } catch (IOException e) {
                        Log.e("error", "Exif error");
                    }
                }
                if(orientationImage != 0){
                    switch (orientationImage){
                        case 3:{ // ORIENTATION_ROTATE_180
                            rotation = 180;
                        }
                        break;
                        case 6:{ // ORIENTATION_ROTATE_90
                            rotation = 90;
                        }
                        break;
                        case 8:{ // ORIENTATION_ROTATE_270
                            rotation = 270;
                        }
                        break;
                    }
                }
                if(rotation != 0){
                    Bitmap bmp = BitmapFactory.decodeFile(getTempFile().getAbsolutePath());
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotation);
                    Bitmap rotatedImg = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                    bmp.recycle();

                    try {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        rotatedImg.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] bitmapdata = bos.toByteArray();
                        FileOutputStream fos = new FileOutputStream(getTempFile());
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

                long timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48;
                MessageItem item = new MessageItem("0", "" + timestamp,
                        getTempFile().getAbsolutePath(), timestamp, false,
                        MonkeyItem.MonkeyItemType.photo);
                monkeyMessages.add(item);
                adapter.notifyDataSetChanged();
                recycler.scrollToPosition(monkeyMessages.size()-1);

                break;
            }
        }
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

    @Nullable
    @Override
    public MonkeyItem getPlayingAudio() {
        return playingItem;
    }

    @Override
    public int getPlayingAudioProgress() {
        return 100 * player.getCurrentPosition() / player.getDuration();
    }

    @NotNull
    @Override
    public String getPlayingAudioProgressText() {
        int progress = player.getCurrentPosition() / 1000;
        String res = "00:";
        if(progress < 10)
            res += "0";
        return res + progress;
    }

    @Override
    public boolean isAudioPlaybackPaused() {
        return !playingAudio;
    }

    @Override
    public void onFileDownloadRequested(int position, @NotNull MonkeyItem item) {

    }

}
