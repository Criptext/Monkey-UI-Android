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
import com.criptext.monkeykitui.input.BaseInputView
import com.criptext.monkeykitui.input.MediaInputView
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.ChatActivity
import com.criptext.monkeykitui.recycler.MonkeyAdapter
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.criptext.monkeykitui.recycler.audio.AudioUIUpdater
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer
import java.util.*

/**
 * Created by Gabriel on 8/10/16.
 */

open class MonkeyChatFragment(): Fragment() {
    open val chatLayout: Int
        get() = R.layout.monkey_chat_layout

    lateinit var recyclerView: RecyclerView
    private lateinit var monkeyAdapter: MonkeyAdapter
    private lateinit var audioUIUpdater: AudioUIUpdater
    private lateinit var inputView: BaseInputView
    lateinit var conversationId: String
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


    companion object {
        val chatHasReachedEnd = "MonkeyChatFragment.hasReachedEnd"
        val chatConversationId = "MonkeyChatFragment.conversationId"

        fun newInstance(conversationId: String, hasReachedEnd: Boolean): MonkeyChatFragment{
            val newInstance = MonkeyChatFragment()
            val newBundle = Bundle()
            newBundle.putString(chatConversationId, conversationId)
            newBundle.putBoolean(chatHasReachedEnd, hasReachedEnd)
            newInstance.arguments = newBundle
            return newInstance
        }
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
        conversationId = args.getString(chatConversationId)
        val reachedEnd = args.getBoolean(chatHasReachedEnd)
        val initialMessages = (activity as ChatActivity).getInitialMessages(conversationId)
        if(initialMessages != null){
            monkeyAdapter.addOldMessages(initialMessages, reachedEnd)
        } else if(reachedEnd) monkeyAdapter.hasReachedEnd = true
        else
            (activity as ChatActivity).onLoadMoreData(0)

    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(chatLayout, null)

        recyclerView = initRecyclerView(view)
        monkeyAdapter = MonkeyAdapter(activity)

        inputView = view.findViewById(R.id.inputView) as BaseInputView
        inputView.inputListener = this.inputListener

        setInitialMessages()
        recyclerView.adapter = monkeyAdapter
        audioUIUpdater = AudioUIUpdater(recyclerView)
        monkeyAdapter.voiceNotePlayer = voiceNotePlayer
        voiceNotePlayer?.uiUpdater = audioUIUpdater

        return view
    }

    override fun onStart() {
        super.onStart()
        voiceNotePlayer?.initPlayer()
    }

    override fun onStop() {
        super.onStop()
        voiceNotePlayer?.releasePlayer()
    }

    override fun onAttach(activity: Activity?) {
        val chatActivty = activity as? ChatActivity
        chatActivty?.setChatFragment(this)
        super.onAttach(activity)
    }

    override fun onDetach() {
        (activity as ChatActivity).setChatFragment(null)
        super.onDetach()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as ChatActivity).retainMessages(this.conversationId, monkeyAdapter.takeAllMessages())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode != Activity.RESULT_OK)
            return

        val mediaInputView = inputView as? MediaInputView
        mediaInputView?.cameraHandler?.onActivityResult(requestCode, resultCode, data)
    }

    fun findMonkeyItemById(id: String): MonkeyItem? = monkeyAdapter.findMonkeyItemById(id)

    fun rebindMonkeyItem(message: MonkeyItem){
        monkeyAdapter.rebindMonkeyItem(message, recyclerView)
    }

    fun addOldMessages(messages: ArrayList<MonkeyItem>, hasReachedEnd:  Boolean){
        monkeyAdapter.addOldMessages(messages, hasReachedEnd)
    }

    fun smoothlyAddNewItem(message: MonkeyItem){
        monkeyAdapter.smoothlyAddNewItem(message, recyclerView)
    }

    fun smoothlyAddNewItems(messages: ArrayList<MonkeyItem>){
        monkeyAdapter.smoothlyAddNewItems(messages, recyclerView)
    }

    fun clearMessages(){
        monkeyAdapter.clear()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if(enter) AnimationUtils.loadAnimation(activity, R.anim.mk_fragment_slide_right_in)
            else AnimationUtils.loadAnimation(activity, R.anim.mk_fragment_slide_right_out)
    }

    fun getLastMessage(): MonkeyItem?{
        if(monkeyAdapter.messagesList.size == 0)
            return null
        return monkeyAdapter.messagesList[monkeyAdapter.messagesList.size - 1]
    }
}
