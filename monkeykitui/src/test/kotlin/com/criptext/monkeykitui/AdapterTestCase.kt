package com.criptext.monkeykitui

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.criptext.monkeykitui.recycler.ChatActivity
import com.criptext.monkeykitui.recycler.GroupChat
import com.criptext.monkeykitui.recycler.MonkeyAdapter
import com.criptext.monkeykitui.recycler.MonkeyItem
import org.junit.Before
import org.robolectric.Robolectric
import java.util.*

/**
 * Created by gesuwall on 8/26/16.
 */

open class AdapterTestCase {
lateinit var adapter: MonkeyAdapter
    var activity: Activity? = null
    var recycler: RecyclerView? = null

    val contactSessionId : String = "fakecontactsession"
    val mySessionId = "mysession"

    val FAKE_AUDIO = "myAudio.m4a"
    val FAKE_PHOTO = "myPhoto.jpg"
    val FAKE_FILE = "myFile.zip"
    @Before
    fun initAdapter(){
        if(activity == null){
            val newActivity = Robolectric.setupActivity(MonkeyActivity::class.java)
            adapter = MonkeyAdapter(newActivity, "mirror")
            recycler = RecyclerView(newActivity);
            recycler!!.layoutManager = LinearLayoutManager(newActivity)
            activity = newActivity
        }
    }
    class MonkeyActivity: Activity(), ChatActivity {
        override fun onMessageRemoved(item: MonkeyItem, unsent: Boolean) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onMessageRemoved(item: MonkeyItem, unsent: Boolean) {
        }

        override fun onLoadMoreMessages(conversationId: String) {
        }

        override fun getInitialMessages(conversationId: String): List<MonkeyItem> {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun retainMessages(conversationId: String, messages: List<MonkeyItem>) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getGroupChat(conversationId: String, membersIds: String): GroupChat {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onStartChatFragment(conversationId: String) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onStopChatFragment(conversationId: String) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setChatFragment(chatFragment: MonkeyChatFragment?) {
            throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun isOnline() = true

        override fun onFileDownloadRequested(item: MonkeyItem) {
        }

        override fun onFileUploadRequested(item: MonkeyItem) {
        }

    }

}