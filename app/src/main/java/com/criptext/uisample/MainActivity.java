package com.criptext.uisample;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Adapter;

import com.criptext.monkeykitui.recycler.ChatActivity;
import com.criptext.monkeykitui.recycler.MonkeyAdapter;
import com.criptext.monkeykitui.recycler.MonkeyItem;
import com.criptext.monkeykitui.recycler.listeners.AudioListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ChatActivity {
    String[] messages = { "Hello", "'sup", "How are you doing", "Is everything OK?", "I'm at work", "I'm at school",
    "The weather is terrible", "I'm not feeling very well", "Today is my lucky day", "I hate when that happens",
    "I'm fine", "What are you doing this weekend?", "Sorry, I have plans", "I'm free", "Everything is going according to plan",
    "Here's my credit card number: 1111 2222 3333 4444"};
    MonkeyAdapter adapter;
    RecyclerView recycler;

    Handler handler;
    MediaPlayer player;
    MonkeyItem playingItem = null;
    int playingItemPosition = -1;
    boolean playingAudio = false;
    Runnable playerRunnable = new Runnable() {
        @Override
        public void run() {
            if(playingAudio){
                adapter.updateAudioSeekbar(recycler, playingItemPosition);
                handler.postDelayed(this, 67);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createAudioFile();
        ArrayList<MonkeyItem> messages =generateRandomMessages();
        adapter = new MonkeyAdapter(this, messages);

        handler = new Handler();
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
                Log.d("MainActivity", "Playback Complete");

            }
        });
        adapter.setAudioListener(new AudioListener() {
            @Override
            public void onPlayButtonClicked(int position, @NotNull MonkeyItem item) {
                Log.d("MainActivity", "Play");
                if (playingItem != null && item.getMessageId().equals(playingItem.getMessageId())) {
                    player.start();
                    playingAudio = true;
                    playerRunnable.run();
                    adapter.notifyDataSetChanged();
                    return;
                } else {
                    player.reset();
                    playingItem = item;
                    playingAudio = true;
                    adapter.notifyDataSetChanged();
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
                Log.d("MainActivity", "Pause");
                handler.removeCallbacks(playerRunnable);
                player.pause();
                playingAudio = false;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onProgressManuallyChanged(int position, @NotNull MonkeyItem item, int newProgress) {
                player.seekTo(newProgress * player.getDuration() / 100);
            }
        });
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler.setAdapter(adapter);

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
        adapter.notifyDataSetChanged();
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
            } else {
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
    public void onFileDownloadRequested(int position, @NotNull MonkeyItem item) {

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
}
