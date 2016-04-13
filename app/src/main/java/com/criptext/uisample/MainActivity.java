package com.criptext.uisample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.criptext.monkeykitui.recycler.ChatActivity;
import com.criptext.monkeykitui.recycler.MonkeyAdapter;
import com.criptext.monkeykitui.recycler.MonkeyItem;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ChatActivity {

    String[] messages = { "Hello", "'sup", "How are you doing", "Is everything OK?", "I'm at work", "I'm at school",
    "The weather is terrible", "I'm not feeling very well", "Today is my lucky day", "I hate when that happens",
    "I'm fine", "What are you doing this weekend?", "Sorry, I have plans", "I'm free", "Everything is going according to plan",
    "Here's my credit card number: 1111 2222 3333 4444"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<MonkeyItem> messages =generateRandomMessages();
        MonkeyAdapter adapter = new MonkeyAdapter(this, messages);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recycler.setAdapter(adapter);
    }

    private ArrayList<MonkeyItem> generateRandomMessages(){
        ArrayList<MonkeyItem> arrayList = new ArrayList<MonkeyItem>();
        long timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48;
        for(int i = 0; i < 100; i++){
            Random r = new Random();
            boolean incoming = r.nextBoolean();
            String message = messages[r.nextInt(messages.length)];
            MessageItem item = new MessageItem(incoming ? "1":"0", message, message, timestamp, incoming);
            timestamp += r.nextInt(1000 * 60 * 10);
            arrayList.add(item);
        }

        //ADD A SAMPLE IMAGE
        Random r = new Random();
        timestamp += r.nextInt(1000 * 60 * 10);
        arrayList.add(new MessagePhotoItem(this,"1","1",timestamp,true));

        return arrayList;
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
    public void onMessageLongClicked(int position, @NotNull MonkeyItem item) {

    }

    @Override
    public boolean isOnline() {
        return true;
    }

    @Override
    public void onFileDownloadRequested(int position, @NotNull MonkeyItem item) {

    }

    @NotNull
    @Override
    public String getFilePath(int position, @NotNull MonkeyItem item) {
        return "";
    }
}
