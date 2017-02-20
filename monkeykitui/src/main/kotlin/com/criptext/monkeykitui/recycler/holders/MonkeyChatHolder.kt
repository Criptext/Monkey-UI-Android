package com.criptext.monkeykitui.recycler.holders

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.BaseInputView
import com.criptext.monkeykitui.input.MediaInputView
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.*
import com.criptext.monkeykitui.recycler.audio.AudioUIUpdater
import com.criptext.monkeykitui.recycler.audio.PlaybackService

/**
 * Created by gesuwall on 2/20/17.
 */

class MonkeyChatHolder(inflater: LayoutInflater, layoutRes: Int): MessageListUI {

    companion object {
        val LOAD_FILE = 777
    }

    val rootView: View
    val recyclerView: RecyclerView
    lateinit var monkeyAdapter: MonkeyAdapter
    lateinit var audioUIUpdater: AudioUIUpdater
    private var inputView: BaseInputView
    var chatLayout: Int
    private set

    var inputListener: InputListener? = null
    set(value) {
        inputView.inputListener = value
        field = value
    }

    var voiceNotePlayer: PlaybackService.VoiceNotePlayerBinder?
        set(value) {
            monkeyAdapter.voiceNotePlayer = value
            if(value != null) {
                value.setIsInForeground(true)
                value.setUiUpdater(audioUIUpdater)
                val playingItem = value.currentlyPlayingItem
                if (playingItem != null)
                    audioUIUpdater.rebindAudioHolder(playingItem)
                //if we are coming back into the chat, remove notification
                val convId = monkeyAdapter.conversationId
                value.removeNotificationControl(convId)
            }
        }

        get() = monkeyAdapter.voiceNotePlayer

    constructor(inflater: LayoutInflater): this(inflater, R.layout.monkey_chat_layout) {

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
        val linearLayoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.stackFromEnd = true
        recycler.layoutManager = linearLayoutManager
        return recycler
    }

    fun setInitialMessages(conversationId: String, lastRead: Long,
                           initialMessages: MessagesList, groupChat: GroupChat?) {
        monkeyAdapter = MonkeyAdapter(rootView.context, conversationId, lastRead)
        monkeyAdapter.voiceNotePlayer = voiceNotePlayer
        initialMessages.messageListUI = this
        monkeyAdapter.messages = initialMessages
        monkeyAdapter.groupChat = groupChat
        recyclerView.adapter = monkeyAdapter
        monkeyAdapter.recyclerView = recyclerView
        audioUIUpdater = AudioUIUpdater(recyclerView)
    }

    init {
        this.chatLayout = layoutRes
        rootView = inflater.inflate(chatLayout, null)
        recyclerView = initRecyclerView(rootView)

        inputView = rootView.findViewById(R.id.inputView) as BaseInputView
        (inputView as? MediaInputView)?.setDefaultRecorder()

    }

    fun onStart() {
        if (monkeyAdapter.messages.messageListUI == null)
            //Fragment was stopped, we must now attach the UI back to list
            monkeyAdapter.messages.messageListUI = this
    }

    fun onStop() {
        inputListener?.onStopTyping()
        monkeyAdapter.messages.messageListUI = null
    }

    fun onDestroy() {
        (inputView as? MediaInputView)?.recorder = null
    }

    fun onActivtyResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("ActivityResult", "MonkeyChatHolder")
        if(resultCode != Activity.RESULT_OK)
            return

        val mediaInputView = inputView as? MediaInputView

        if(requestCode == LOAD_FILE){
            mediaInputView?.attachmentButton?.onActivityResult(requestCode, resultCode, data)
            return
        }
        mediaInputView?.cameraHandler?.onActivityResult(requestCode, resultCode, data)
    }

    override fun notifyItemChanged(pos: Int) {
        monkeyAdapter.notifyItemChanged(pos)
    }

    override fun rebindMonkeyItem(item: MonkeyItem) {
        monkeyAdapter.rebindMonkeyItem(item, recyclerView)
    }

    override fun findLastVisibleItemPosition(): Int {
        val manager = recyclerView.layoutManager as LinearLayoutManager
        return manager.findLastVisibleItemPosition()
    }

    override fun notifyDataSetChanged() {
        monkeyAdapter.notifyDataSetChanged()
    }

    override fun notifyItemRangeInserted(pos: Int, count: Int) {
        monkeyAdapter.notifyItemRangeInserted(pos, count)
    }

    override fun notifyItemInserted(pos: Int) {
        monkeyAdapter.notifyItemInserted(pos)
    }

    override fun notifyItemRemoved(pos: Int) {
        monkeyAdapter.notifyItemRemoved(pos)
    }

    override fun notifyItemRangeRemoved(pos: Int, count: Int) {
        monkeyAdapter.notifyItemRangeRemoved(pos, count)
    }

    override fun removeLoadingView() {
        monkeyAdapter.removeEndOfRecyclerView()
    }

    override fun scrollToPosition(pos: Int) {
        recyclerView.scrollToPosition(pos)
    }

    override fun scrollWithOffset(newItemsCount: Int) {
        monkeyAdapter.scrollWithOffset(newItemsCount)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        inputView.onRequestPermissionsResult(requestCode, grantResults)
        monkeyAdapter.onRequestPermissionsResult(requestCode, grantResults)
    }
}