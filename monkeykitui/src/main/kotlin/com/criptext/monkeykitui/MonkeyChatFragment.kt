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
import com.criptext.monkeykitui.input.BaseInputView
import com.criptext.monkeykitui.input.MediaInputView
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.MonkeyAdapter
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.criptext.monkeykitui.recycler.audio.AudioUIUpdater
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer
import java.util.*

/**
 * Created by Gabriel on 8/10/16.
 */

open class MonkeyChatFragment: Fragment() {
    open val chatLayout: Int
        get() = R.layout.monkey_chat_layout

    lateinit var recyclerView: RecyclerView
    private lateinit var monkeyAdapter: MonkeyAdapter
    private lateinit var audioUIUpdater: AudioUIUpdater
    private lateinit var inputView: BaseInputView

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(chatLayout, null)

        recyclerView = initRecyclerView(view)
        monkeyAdapter = MonkeyAdapter(activity)
        recyclerView.adapter = monkeyAdapter
        audioUIUpdater = AudioUIUpdater(recyclerView)

        monkeyAdapter.voiceNotePlayer = voiceNotePlayer
        voiceNotePlayer?.uiUpdater = audioUIUpdater

        inputView = view.findViewById(R.id.inputView) as BaseInputView
        inputView.inputListener = this.inputListener

        monkeyAdapter.chatActivity.onLoadMoreData(0)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode != Activity.RESULT_OK)
            return

        val mediaInputView = inputView as? MediaInputView
        mediaInputView?.cameraHandler?.onActivityResult(requestCode, resultCode, data)
    }

    fun rebindMonkeyItem(message: MonkeyItem){
        monkeyAdapter.rebindMonkeyItem(message, recyclerView)
    }

    fun addOldMessages(messages: ArrayList<MonkeyItem>, hasReachedEnd:  Boolean){
        monkeyAdapter.addOldMessages(messages, hasReachedEnd)
    }

    fun smoothlyAddNewItem(message: MonkeyItem){
        monkeyAdapter.smoothlyAddNewItem(message, recyclerView)
    }

}
