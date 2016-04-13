package com.criptext.monkeykitui.recycler

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.bubble.*
import com.criptext.monkeykitui.recycler.holders.MonkeyAudioHolder
import com.criptext.monkeykitui.recycler.holders.MonkeyHolder
import com.criptext.monkeykitui.recycler.holders.MonkeyImageHolder
import com.criptext.monkeykitui.recycler.holders.MonkeyTextHolder
import com.criptext.monkeykitui.recycler.listeners.AudioListener
import com.criptext.monkeykitui.recycler.listeners.ImageListener
import com.criptext.monkeykitui.recycler.listeners.OnLongClickMonkeyListener
import com.criptext.monkeykitui.util.Utils
import com.innovative.circularaudioview.AudioActions
import com.innovative.circularaudioview.CircularAudioView
import java.io.File
import java.util.*

/**
 * Created by gesuwall on 4/4/16.
 */

class MonkeyAdapter(ctx: Context, list : ArrayList<MonkeyItem>) : RecyclerView.Adapter<MonkeyHolder>() {
    private val mContext : Context
    private val datalist : ArrayList<MonkeyItem>
    private var selectedMessage : MonkeyItem?

    var audioListener : AudioListener?
    var imageListener : ImageListener?
    var onLongClickListener : OnLongClickMonkeyListener?

    init{
        mContext = ctx
        datalist = list
        selectedMessage = null
        audioListener = null
        imageListener = null
        onLongClickListener = null

    }

    val chatActivity : ChatActivity
        get() = mContext as ChatActivity

    override fun getItemCount(): Int {
        return datalist.size
    }

    fun getViewTypes() : Int{
        return 10
    }

    override fun getItemViewType(position: Int): Int {
        val item = datalist[position]
        //incoming messages have viewtypes/2 higher type
        return item.getMessageType() + (if(item.isIncomingMessage()) 5 else 0)
    }





    override fun onBindViewHolder(holder : MonkeyHolder, position : Int) {
        //set Dates
        val item = datalist[position]
        holder.setMessageDate(item.getMessageTimestamp())
        //long click
        holder.setOnLongClickListener(View.OnLongClickListener {
            onLongClickListener?.onLongClick(position, item)
            Toast.makeText(mContext, "long clicked: " + position, Toast.LENGTH_SHORT).show()
            true
        })

        if (item.isIncomingMessage()) { //stuff for incoming messages
            if (chatActivity.isGroupChat()) {
                holder.setSenderName(chatActivity.getMenberName(item.getContactSessionId()),
                        chatActivity.getMemberColor(item.getContactSessionId()))
            }
        } else { //stuff for outgoing messages
           holder.updateReadStatus(item.getOutgoingMessageStatus())
           holder.updateSendingStatus(item.getOutgoingMessageStatus(), chatActivity.isOnline(), item.getMessageTimestamp())
        }

        //selected status
        val selected = selectedMessage
        holder.updateSelectedStatus(selected != null && selected.getMessageId() == item.getMessageId())

        //type specific stuff
        when(MonkeyItem.MonkeyItemType.values()[item.getMessageType()]){
            MonkeyItem.MonkeyItemType.text -> {
                val textHolder = holder as MonkeyTextHolder
                textHolder.messageTextView!!.text = item.getMessageText()
            }
            MonkeyItem.MonkeyItemType.audio -> {
                val audioHolder = holder as MonkeyAudioHolder
                val target = File(item.getFilePath())
                val playingAudio = chatActivity.getPlayingAudio()

                val playAction = object : AudioActions() {
                            override fun onActionClicked() {
                                super.onActionClicked()
                                audioListener?.onPlayButtonClicked(position, item)
                            }

                            override fun onActionLongClicked() {
                                super.onActionLongClicked()
                                onLongClickListener?.onLongClick(position, item)
                            }
                        }

                val pauseAction = object : AudioActions() {
                            override fun onActionClicked() {
                                super.onActionClicked()
                                audioListener?.onPauseButtonClicked(position, item)
                            }

                            override fun onActionLongClicked() {
                                super.onActionLongClicked()
                                onLongClickListener?.onLongClick(position, item)
                            }
                        }
                if(!item.isIncomingMessage() && !target.exists()){
                    chatActivity.onFileDownloadRequested(position, item)
                    audioHolder.updatePlayPauseButton(false)
                    audioHolder.setWaitingForDownload()
                } else if(playingAudio?.getMessageId() == item.getMessageId()){// Message is playing
                    audioHolder.setReadyForPlayback()
                    if(chatActivity.isAudioPlaybackPaused()){
                        audioHolder.updatePlayPauseButton(false)
                        audioHolder.setAudioActions(playAction)
                    } else {
                        audioHolder.updateAudioProgress(chatActivity.getPlayingAudioProgress(),
                                chatActivity.getPlayingAudioProgressText())
                        audioHolder.setAudioActions(pauseAction)
                    }
                } else {
                    audioHolder.setReadyForPlayback()
                    audioHolder.updatePlayPauseButton(false)
                    audioHolder.setAudioDurationText(item.getAudioDuration())
                    audioHolder.setAudioActions(playAction)
                }
            }
            MonkeyItem.MonkeyItemType.photo -> {
                val imageHolder = holder as MonkeyImageHolder
                val target = File(item.getFilePath())
                if(target.exists()){
                    imageHolder.setDownloadedImage(target)
                    if(target.length() < item.getFileSize())
                        imageHolder.setRetryDownloadButton(position, item, chatActivity)
                }
                else{
                    imageHolder.setNotDownloadedImage(item)
                    chatActivity.onFileDownloadRequested(position, item)
                }
            }
        }

    }

    fun inflateView(incoming: Boolean, inLayout: Int, outLayout : Int) : View {
        if(incoming)
            return LayoutInflater.from(mContext).inflate(inLayout, null)
        return LayoutInflater.from(mContext).inflate(outLayout, null)
    }

    override fun onCreateViewHolder(p0: ViewGroup?, viewtype: Int): MonkeyHolder? {
        var view : MonkeyView
        var mView : View
        var incoming = viewtype >= (getViewTypes()/2)
        val truetype = viewtype%MonkeyItem.MonkeyItemType.values().size
        when(MonkeyItem.MonkeyItemType.values()[truetype]){
            MonkeyItem.MonkeyItemType.text -> {
                mView = inflateView(incoming, R.layout.text_message_view_in, R.layout.text_message_view_out)
                return MonkeyTextHolder(mView)
            }
            MonkeyItem.MonkeyItemType.photo -> {
                mView = inflateView(incoming, R.layout.image_message_view_in, R.layout.image_message_view_out)
                return MonkeyImageHolder(mView)
            }
            MonkeyItem.MonkeyItemType.audio -> {
                mView = inflateView(incoming, R.layout.audio_message_view_in, R.layout.audio_message_view_out)
                return MonkeyAudioHolder(mView)
            }
            MonkeyItem.MonkeyItemType.file -> view = FileMessageView(mContext, incoming)
            MonkeyItem.MonkeyItemType.contact -> view = ContactMessageView(mContext, incoming)
        }
        return MonkeyHolder(view, truetype)
    }

}
