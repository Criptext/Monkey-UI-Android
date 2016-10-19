package com.criptext.monkeykitui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import com.criptext.monkeykitui.input.BaseInputView
import com.criptext.monkeykitui.input.MediaInputView
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.*
import com.criptext.monkeykitui.recycler.audio.AudioUIUpdater
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer
import com.etiennelawlor.imagegallery.library.activities.FullScreenImageGalleryActivity
import com.etiennelawlor.imagegallery.library.adapters.FullScreenImageGalleryAdapter
import com.squareup.picasso.Picasso
import java.io.File
import java.util.*

/**
 * Created by Gabriel on 8/10/16.
 */

open class MonkeyChatFragment(): Fragment(), FullScreenImageGalleryAdapter.FullScreenImageLoader{

    open val chatLayout: Int
        get() = R.layout.monkey_chat_layout

    lateinit var recyclerView: RecyclerView
    private lateinit var monkeyAdapter: MonkeyAdapter
    private lateinit var audioUIUpdater: AudioUIUpdater
    private lateinit var inputView: BaseInputView
    private set

    var inputListener: InputListener? = null
    set(value) {
        if(view != null) {
            inputView.inputListener = value
        }
        field = value
    }

    var voiceNotePlayer: VoiceNotePlayer? = null
        set(value) {
            if(view != null) {
                monkeyAdapter.voiceNotePlayer = value
                value?.uiUpdater = audioUIUpdater
            }
            field = value
        }

    private var isTransitioning = false
    private var runAfterTransition: Runnable? = null

    companion object {
        val chatHasReachedEnd = "MonkeyChatFragment.hasReachedEnd"
        val chatConversationId = "MonkeyChatFragment.conversationId"
        val chatmembersGroupIds = "MonkeyChatFragment.membersIds"
        val chatTitleName = "MonkeyChatFragment.titleName"
        val chatAvatarUrl = "MonkeyChatFragment.avatarUrl"
        val initalLastReadValue = "MonkeyChatFragment.lastread"

        fun newGroupInstance(conversationId: String, chatTitle: String, avatarURL: String,
                             hasReachedEnd: Boolean, lastRead: Long, membersIds: String?): MonkeyChatFragment {
            val newInstance = MonkeyChatFragment()
            val newBundle = Bundle()
            newBundle.putString(chatConversationId, conversationId)
            newBundle.putString(chatmembersGroupIds, membersIds)
            newBundle.putString(chatTitleName, chatTitle)
            newBundle.putString(chatAvatarUrl, avatarURL)
            newBundle.putBoolean(chatHasReachedEnd, hasReachedEnd)
            newBundle.putLong(initalLastReadValue, lastRead)
            newInstance.arguments = newBundle
            return newInstance
        }

        fun newGroupInstance(conversationId: String, chatTitle: String,
                             avatarURL: String, hasReachedEnd: Boolean, membersIds: String) =
                newGroupInstance(conversationId, chatTitle, avatarURL, hasReachedEnd, 0L, membersIds)

        fun newInstance(conversationId: String, chatTitle: String, avatarURL: String,
                        hasReachedEnd: Boolean, lastRead: Long) =
                newGroupInstance(conversationId, chatTitle, avatarURL, hasReachedEnd, lastRead, null)

        fun newInstance(conversationId: String, chatTitle: String, avatarURL: String,
                        hasReachedEnd: Boolean) =
                newGroupInstance(conversationId, chatTitle, avatarURL, hasReachedEnd, 0L, null)
    }


    /**
     * finds the RecyclerView in the view layout of the current fragment, ands sets an appropiate
     * LayoutManager.
     *
     * The default implementation sets a LinearLayoutManager with the stackFromEnd property set as true
     * @return the RecyclerView object of this fragment ready to set an adapter with data.
     */
    open fun initRecyclerView(view: View): RecyclerView {
        val recycler = view.findViewById(R.id.recycler) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.stackFromEnd = true;
        recycler.layoutManager = linearLayoutManager;
        return recycler
    }

    private fun setInitialMessages(){
        val args = arguments
        val conversationId = args.getString(chatConversationId)
        val reachedEnd = args.getBoolean(chatHasReachedEnd)
        val lastRead = args.getLong(initalLastReadValue)
        monkeyAdapter = MonkeyAdapter(activity, conversationId, lastRead)
        val initialMessages = (activity as ChatActivity).getInitialMessages(conversationId)
        if(initialMessages != null){
            monkeyAdapter.addOldMessages(initialMessages, reachedEnd, recyclerView)
        } else if(reachedEnd) monkeyAdapter.hasReachedEnd = true
        else
            (activity as ChatActivity).onLoadMoreMessages(conversationId, 0)
        val groupMembers = getGroupMembers()
        if(groupMembers != null){
            val groupChat = (activity as ChatActivity).getGroupChat(conversationId, groupMembers)
            monkeyAdapter.groupChat = groupChat
        }
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(chatLayout, null)

        recyclerView = initRecyclerView(view)

        inputView = view.findViewById(R.id.inputView) as BaseInputView
        (inputView as? MediaInputView)?.setDefaultRecorder()
        inputView.inputListener = this.inputListener

        setInitialMessages()
        monkeyAdapter.recyclerView = recyclerView
        recyclerView.adapter = monkeyAdapter
        audioUIUpdater = AudioUIUpdater(recyclerView)
        monkeyAdapter.voiceNotePlayer = voiceNotePlayer
        voiceNotePlayer?.uiUpdater = audioUIUpdater

        //(activity as AppCompatActivity).supportActionBar?.title = getChatTitle()
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        FullScreenImageGalleryActivity.setFullScreenImageLoader(this)

        return view
    }

    override fun onStart() {
        super.onStart()
        (activity as ChatActivity).onStartChatFragment(monkeyAdapter.conversationId)
        voiceNotePlayer?.initPlayer()
    }

    override fun onStop() {
        super.onStop()
        (activity as ChatActivity).onStopChatFragment(monkeyAdapter.conversationId)
        voiceNotePlayer?.releasePlayer()
    }

    override fun onAttach(activity: Activity?) {
        val chatActivty = activity as? ChatActivity
        chatActivty?.setChatFragment(this)
        super.onAttach(activity)
    }

    override fun onDetach() {
        (activity as ChatActivity).deleteChatFragment(this)
        super.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        (inputView as? MediaInputView)?.recorder = null
        (activity as ChatActivity).retainMessages(monkeyAdapter.conversationId, monkeyAdapter.takeAllMessages())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode != Activity.RESULT_OK)
            return

        val mediaInputView = inputView as? MediaInputView
        mediaInputView?.cameraHandler?.onActivityResult(requestCode, resultCode, data)
    }

    fun findMonkeyItemById(id: String): MonkeyItem? = monkeyAdapter.findMonkeyItemById(id)

    fun updateMessageDeliveryStatus(message: MonkeyItem){
        monkeyAdapter.updateMessageDeliveryStatus(message, recyclerView)
    }

    fun rebindMonkeyItem(message: MonkeyItem){
        monkeyAdapter.rebindMonkeyItem(message, recyclerView)
    }

    fun addOldMessages(messages: ArrayList<MonkeyItem>, hasReachedEnd:  Boolean){
        monkeyAdapter.addOldMessages(messages, hasReachedEnd, recyclerView)
    }

    fun smoothlyAddNewItem(message: MonkeyItem){
        monkeyAdapter.smoothlyAddNewItem(message, recyclerView)
    }

    fun smoothlyAddNewItems(messages: List<MonkeyItem>){
        monkeyAdapter.smoothlyAddNewItems(messages, recyclerView)
    }

    fun insertMessages(messages: List<MonkeyItem>) {
        monkeyAdapter.insertMessages(messages)
    }

    fun clearMessages(){
        monkeyAdapter.clear()
    }

    fun reloadAllMessages(){
        if(!isTransitioning)
            monkeyAdapter.notifyDataSetChanged()
        else {
            runAfterTransition = Runnable {
                monkeyAdapter.notifyDataSetChanged()
            }
        }
    }

    fun takeAllMessages(): List<MonkeyItem> {
        return monkeyAdapter.takeAllMessages()
    }

    fun getConversationId(): String{
        try {
            return monkeyAdapter.conversationId
        }
        catch (ex: UninitializedPropertyAccessException){
            ex.printStackTrace()
            return ""
        }
    }

    fun getGroupMembers() = arguments.getString(chatmembersGroupIds)

    fun getChatTitle(): String{
        val args = arguments
        return args.getString(chatTitleName)
    }

    fun getAvatarURL(): String{
        val args = arguments
        return args.getString(chatAvatarUrl)
    }

    /**
     * Updates the read status of all messages according to the new conversation lastRead value
     * @param lastRead a timestamp with the last time the the other party read the conversation's
     * messages. All messages with a sent timestamp lower than this will be displayed as read.
     */
    fun setLastRead(lastRead: Long) {
        monkeyAdapter.lastRead = lastRead
        reloadAllMessages();
    }

    fun isGroupConversation(): Boolean?{
        val args = arguments
        val conversationId = args.getString(chatConversationId)
        return conversationId.contains("G:")
    }

    fun updateMessage(messageId: String, messageTimestamp: Long, transaction: MonkeyItemTransaction){
        val searchItem = object: MonkeyItem {
            override fun getAudioDuration() = 0L
            override fun getDeliveryStatus() = MonkeyItem.DeliveryStatus.sending
            override fun getSenderId() = ""
            override fun getFileSize() = 0L
            override fun getFilePath() = ""
            override fun getMessageId() = messageId
            override fun getMessageText() = ""
            override fun getMessageTimestamp() = messageTimestamp
            override fun getMessageTimestampOrder() = messageTimestamp
            override fun getMessageType() = 0
            override fun getOldMessageId() = ""
            override fun getPlaceholderFilePath() = ""
            override fun isIncomingMessage() = true
        }
        monkeyAdapter.updateMessage(searchItem, transaction, recyclerView)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animation = if(enter) AnimationUtils.loadAnimation(activity, R.anim.mk_fragment_slide_right_in)
            else AnimationUtils.loadAnimation(activity, R.anim.mk_fragment_slide_right_out)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(p0: Animation?) {
                isTransitioning = false
                runAfterTransition?.run()
                runAfterTransition = null
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationStart(p0: Animation?) {
                isTransitioning = true
            }

        })
        return animation
    }

    fun getLastMessage(): MonkeyItem? = monkeyAdapter.getLastItem()

    fun getFirstMessage(): MonkeyItem? = monkeyAdapter.getFirstItem()

    fun removeMonkeyItem(id: String) = monkeyAdapter.removeItemById(id)

    override fun loadFullScreenImage(iv: ImageView?, imageUrl: String?, width: Int, bglinearLayout: LinearLayout?) {
        if (imageUrl?.length != 0) {
            Picasso.with(iv?.context).load(File(imageUrl)).resize(width, 0).into(iv)
        } else {
            iv?.setImageDrawable(null)
        }
    }
}
