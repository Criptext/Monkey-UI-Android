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
import com.criptext.monkeykitui.input.BaseInputView
import com.criptext.monkeykitui.input.MediaInputView
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.*
import com.criptext.monkeykitui.recycler.audio.AudioUIUpdater
import com.criptext.monkeykitui.recycler.audio.PlaybackService
import com.criptext.monkeykitui.recycler.holders.MessageListUI
import com.criptext.monkeykitui.recycler.holders.MonkeyChatHolder
import com.etiennelawlor.imagegallery.library.activities.FullScreenImageGalleryActivity
import com.etiennelawlor.imagegallery.library.adapters.FullScreenImageGalleryAdapter
import com.squareup.picasso.Picasso
import java.io.File

/**
 * Created by Gabriel on 8/10/16.
 */

open class MonkeyChatFragment(): Fragment(), FullScreenImageGalleryAdapter.FullScreenImageLoader, MessageListUI{


    private var isTransitioning = false
    private var runAfterTransition: Runnable? = null
    var shouldUpdateAudioView: Boolean = false
    var chatHolder: MonkeyChatHolder? = null

    companion object {
        val chatConversationId = "MonkeyChatFragment.conversationId"
        val chatmembersGroupIds = "MonkeyChatFragment.membersIds"
        val chatTitleName = "MonkeyChatFragment.titleName"
        val chatAvatarUrl = "MonkeyChatFragment.avatarUrl"
        val initalLastReadValue = "MonkeyChatFragment.lastread"
        val chatLayoutId = "MonkeyChatFragment.chatLayoutId"
    }

    var voiceNotePlayer: PlaybackService.VoiceNotePlayerBinder?
        set(value) {
            chatHolder?.voiceNotePlayer = value
        }
        get() = chatHolder?.voiceNotePlayer


    var inputListener: InputListener?
        set(value) {
            chatHolder?.inputListener = value
        }
        get() = chatHolder?.inputListener


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        if (inflater != null) {
            chatHolder = MonkeyChatHolder(inflater)
            val convId = getConversationId()
            val lastRead = getInitialLastRead()
            val list = (activity as ChatActivity).getInitialMessages(convId)
            val members = getGroupMembers()
            var groupData: GroupChat? = null
            if (members != null)
                groupData = (activity as ChatActivity).getGroupChat(convId, members)
            chatHolder!!.setInitialMessages(convId, lastRead, list, groupData)
            FullScreenImageGalleryActivity.setFullScreenImageLoader(this)
            return chatHolder!!.rootView
        }
        return null
    }

    override fun onStart() {
        super.onStart()
        val convId = getConversationId()
        (activity as ChatActivity).onStartChatFragment(this, convId)
        chatHolder?.onStart()
        voiceNotePlayer?.setUiUpdater(chatHolder!!.audioUIUpdater)
        voiceNotePlayer?.removeNotificationControl(convId)
        if(shouldUpdateAudioView)
            reloadAllMessages()
    }

    override fun onStop() {
        super.onStop()
        chatHolder?.onStop()
        val isRotating = activity.isChangingConfigurations
        voiceNotePlayer?.setIsInForeground(isRotating)
        voiceNotePlayer?.setUiUpdater(null)
        if(voiceNotePlayer?.currentlyPlayingItem != null)
            shouldUpdateAudioView = true
        (activity as ChatActivity).onStopChatFragment(getConversationId())

    }

    override fun onDestroy() {
        super.onDestroy()
        chatHolder?.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("ActivityResult", "MonkeyChatFragment")
        chatHolder?.onActivtyResult(requestCode, resultCode, data)
    }

    override fun rebindMonkeyItem(item: MonkeyItem){
        chatHolder?.rebindMonkeyItem(item)
    }

    fun reloadAllMessages(){
        if(!isTransitioning)
            chatHolder?.notifyDataSetChanged()
        else {
            runAfterTransition = Runnable {
                chatHolder?.notifyDataSetChanged()
            }
        }
    }

    fun getConversationId() = arguments.getString(chatConversationId)

    fun getGroupMembers() = arguments.getString(chatmembersGroupIds)

    fun getInitialLastRead() = arguments.getLong(initalLastReadValue)

    fun getChatTitle(): String{
        val args = arguments
        return args.getString(chatTitleName)
    }

    fun getAvatarURL(): String?{
        val args = arguments
        return args.getString(chatAvatarUrl)
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
                (activity as ChatActivity).deleteAllMessages(getConversationId())
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        chatHolder?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


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

    override fun notifyItemChanged(pos: Int) {
      chatHolder?.notifyItemChanged(pos)
    }

    override fun findLastVisibleItemPosition(): Int {
        return chatHolder?.findLastVisibleItemPosition() ?: 0
    }

    override fun notifyDataSetChanged() {
        chatHolder?.notifyDataSetChanged()
    }

    override fun notifyItemRangeInserted(pos: Int, count: Int) {
        chatHolder?.notifyItemRangeInserted(pos, count)
    }

    override fun notifyItemInserted(pos: Int) {
         chatHolder?.notifyItemInserted(pos)
    }

    override fun notifyItemRemoved(pos: Int)  {
         chatHolder?.notifyItemRemoved(pos)
    }

    override fun notifyItemRangeRemoved(pos: Int, count: Int) {
        chatHolder?.notifyItemRangeRemoved(pos, count)
    }

    override fun removeLoadingView() {
        chatHolder?.removeLoadingView()
    }

    override fun scrollToPosition(pos: Int) {
        chatHolder?.scrollToPosition(pos)
    }

    override fun scrollWithOffset(newItemsCount: Int) {
        chatHolder?.scrollWithOffset(newItemsCount)
    }
}
