package com.criptext.monkeykitui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import com.criptext.monkeykitui.input.AudioInputView
import com.criptext.monkeykitui.input.BaseInputView
import com.criptext.monkeykitui.input.MediaInputView
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.*
import com.criptext.monkeykitui.recycler.audio.AudioUIUpdater
import com.criptext.monkeykitui.recycler.audio.PlaybackService
import com.criptext.monkeykitui.recycler.holders.MessageListUI
import com.etiennelawlor.imagegallery.library.activities.FullScreenImageGalleryActivity
import com.etiennelawlor.imagegallery.library.adapters.FullScreenImageGalleryAdapter
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by Gabriel on 8/10/16.
 */

open class MonkeyChatFragment(): Fragment(), FullScreenImageGalleryAdapter.FullScreenImageLoader, MessageListUI{

    val LOAD_FILE = 777
    lateinit var recyclerView: RecyclerView
    private lateinit var monkeyAdapter: MonkeyAdapter
    private lateinit var audioUIUpdater: AudioUIUpdater
    private var inputView: BaseInputView? = null
    var chatLayout: Int = R.layout.monkey_chat_layout
    private set

    var inputListener: InputListener? = null
    set(value) {
        if(view != null) {
            inputView?.inputListener = value
        }
        field = value
    }

    var voiceNotePlayer: PlaybackService.VoiceNotePlayerBinder? = null
        set(value) {
            if(view != null) { //check that fragment is ready.
                monkeyAdapter.voiceNotePlayer = value
                if(value != null) {
                    value.setIsInForeground(true)
                    value.setUiUpdater(audioUIUpdater)
                    val playingItem = value.currentlyPlayingItem
                    if (playingItem != null)
                        audioUIUpdater.rebindAudioHolder(playingItem)
                    //if we are coming back into the chat, remove notification
                    value.removeNotificationControl(monkeyAdapter.conversationId)
                }

            }
            field = value
        }

    private var isTransitioning = false
    private var runAfterTransition: Runnable? = null
    var shouldUpdateAudioView: Boolean = false

    companion object {
        val chatConversationId = "MonkeyChatFragment.conversationId"
        val chatmembersGroupIds = "MonkeyChatFragment.membersIds"
        val chatTitleName = "MonkeyChatFragment.titleName"
        val chatAvatarUrl = "MonkeyChatFragment.avatarUrl"
        val initalLastReadValue = "MonkeyChatFragment.lastread"
        val chatLayoutId = "MonkeyChatFragment.chatLayoutId"


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

        val args = arguments
        val conversationId = args.getString(chatConversationId)
        val lastRead = args.getLong(initalLastReadValue)
        monkeyAdapter = MonkeyAdapter(activity, conversationId, lastRead)
        monkeyAdapter.recyclerView = recycler
        recycler.adapter = monkeyAdapter
        return recycler
    }

    private fun setInitialMessages(){
        val args = arguments
        val conversationId = args.getString(chatConversationId)
        val initialMessages = (activity as ChatActivity).getInitialMessages(conversationId)
        if(initialMessages != null){
            initialMessages.messageListUI = this
            monkeyAdapter.messages = initialMessages
        } else
            (activity as ChatActivity).onLoadMoreMessages(conversationId, 0)
        val groupMembers = getGroupMembers()
        if(groupMembers != null){
            val groupChat = (activity as ChatActivity).getGroupChat(conversationId, groupMembers)
            monkeyAdapter.groupChat = groupChat
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(chatLayout, null)
        setHasOptionsMenu(true)
        recyclerView = initRecyclerView(view)

        inputView = view.findViewById(R.id.inputView) as BaseInputView
        (inputView as? MediaInputView)?.setDefaultRecorder()
        inputView?.inputListener = this.inputListener

        audioUIUpdater = AudioUIUpdater(recyclerView)
        monkeyAdapter.voiceNotePlayer = voiceNotePlayer
        voiceNotePlayer?.setUiUpdater(audioUIUpdater)

        FullScreenImageGalleryActivity.setFullScreenImageLoader(this)

        return view
    }

    override fun onStart() {
        super.onStart()
        voiceNotePlayer?.setUiUpdater(audioUIUpdater)
        voiceNotePlayer?.removeNotificationControl(monkeyAdapter.conversationId)
        if (monkeyAdapter.itemCount == 0)
            setInitialMessages()
        if (monkeyAdapter.messages.messageListUI == null)
            //Fragment was stopped, we must now attach the UI back to list
            monkeyAdapter.messages.messageListUI = this
        (activity as ChatActivity).onStartChatFragment(this, monkeyAdapter.conversationId)
        if(shouldUpdateAudioView)
            reloadAllMessages()
    }

    override fun onStop() {
        super.onStop()
        val isRotating = activity.isChangingConfigurations
        voiceNotePlayer?.setIsInForeground(isRotating)
        voiceNotePlayer?.setUiUpdater(null)
        inputListener?.onStopTyping()
        (activity as ChatActivity).onStopChatFragment(monkeyAdapter.conversationId)

        if(voiceNotePlayer?.currentlyPlayingItem != null)
            shouldUpdateAudioView = true
        monkeyAdapter.messages.messageListUI = null
    }

    override fun onDestroy() {
        super.onDestroy()
        inputListener?.onStopTyping()
        inputListener = object: InputListener {
            override fun onNewItemFileError(type: Int) { }

            override fun onStopTyping() { }

            override fun onNewItem(item: MonkeyItem) { }

            override fun onTyping(text: String) { }
        }
        (inputView as? MediaInputView)?.recorder = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode != Activity.RESULT_OK)
            return

        val mediaInputView = inputView as? MediaInputView

        if(requestCode == LOAD_FILE){
            mediaInputView?.attachmentButton?.onActivityResult(requestCode, resultCode, data)
            return
        }
        mediaInputView?.cameraHandler?.onActivityResult(requestCode, resultCode, data)
    }

    override fun rebindMonkeyItem(item: MonkeyItem){
        monkeyAdapter.rebindMonkeyItem(item, recyclerView)
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

    fun getConversationId() = arguments.getString(chatConversationId)

    fun getGroupMembers() = arguments.getString(chatmembersGroupIds)

    fun getChatTitle(): String{
        val args = arguments
        return args.getString(chatTitleName)
    }

    fun getAvatarURL(): String?{
        val args = arguments
        return args.getString(chatAvatarUrl)
    }

    /**
     * Updates the read status of all messages according to the new conversation lastRead value
     * @param lastRead a timestamp with the last time the the other party read the conversation's
     * messages. All messages with a sent timestamp lower than this will be displayed as read.
     */
    fun setLastRead(lastRead: Long) {
        if(lastRead > monkeyAdapter.lastRead) {
            monkeyAdapter.lastRead = lastRead
            reloadAllMessages()
        }
    }

    fun isGroupConversation(): Boolean?{
        val args = arguments
        val conversationId = args.getString(chatConversationId)
        return conversationId.contains("G:")
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

    override fun loadFullScreenImage(iv: ImageView?, imageUrl: String?, width: Int, bglinearLayout: LinearLayout?) {
        if (imageUrl?.length != 0) {
            Picasso.with(iv?.context).load(File(imageUrl)).resize(width, 0).into(iv)
        } else {
            iv?.setImageDrawable(null)
        }
    }

    override fun onCreateOptionsMenu(menu : Menu?, inflater: MenuInflater){
        inflater.inflate(R.menu.menu_chat, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_deleteall -> {
                (activity as ChatActivity).deleteAllMessages(monkeyAdapter.conversationId)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        inputView?.onRequestPermissionsResult(requestCode, grantResults)
        monkeyAdapter.onRequestPermissionsResult(requestCode, grantResults)
    }

    fun refreshDeliveryStatus(item: MonkeyItem) = monkeyAdapter.refreshDeliveryStatus(item, recyclerView)

    class Builder(private val conversationId: String, private val chatTitle: String) {
            private val fragment: MonkeyChatFragment
            private var avatarURL: String? = null
            private var lastRead: Long = 0L
            private var membersIds: String? = null
            private var layoutId: Int = R.layout.monkey_chat_layout

            init  {
                fragment = MonkeyChatFragment()
            }

            fun setAvatarURL(avatarURL: String): Builder {
                this.avatarURL = avatarURL
                return this
            }

            fun setLastRead(lastRead: Long): Builder {
                this.lastRead = lastRead
                return this
            }

            fun setMembersIds(membersIds: String): Builder {
                this.membersIds = membersIds
                return this
            }

            fun setLayoutId(layoutId: Int): Builder {
                this.layoutId = layoutId
                return this
            }

            fun build(): MonkeyChatFragment {
                val newBundle = Bundle()
                newBundle.putString(chatConversationId, conversationId)
                newBundle.putString(chatmembersGroupIds, membersIds)
                newBundle.putString(chatTitleName, chatTitle)
                newBundle.putString(chatAvatarUrl, avatarURL)
                newBundle.putLong(initalLastReadValue, lastRead)
                newBundle.putInt(chatLayoutId, layoutId)
                fragment.arguments = newBundle
                return fragment
            }
        }

    override fun notifyItemChanged(pos: Int) = monkeyAdapter.notifyItemChanged(pos)

    override fun findLastVisibleItemPosition(): Int {
        val manager = recyclerView.layoutManager as LinearLayoutManager
        return manager.findLastVisibleItemPosition()
    }

    override fun notifyDataSetChanged() = monkeyAdapter.notifyDataSetChanged()

    override fun notifyItemRangeInserted(pos: Int, count: Int) = monkeyAdapter.notifyItemRangeInserted(pos, count)

    override fun notifyItemInserted(pos: Int) = monkeyAdapter.notifyItemInserted(pos)

    override fun notifyItemRemoved(pos: Int) = monkeyAdapter.notifyItemRemoved(pos)

    override fun notifyItemRangeRemoved(pos: Int, count: Int) = monkeyAdapter.notifyItemRangeRemoved(pos, count)

    override fun removeLoadingView() = monkeyAdapter.removeEndOfRecyclerView()

    override fun scrollToPosition(pos: Int) = recyclerView.scrollToPosition(pos)

    override fun scrollWithOffset(newItemsCount: Int) = monkeyAdapter.scrollWithOffset(newItemsCount)
}
