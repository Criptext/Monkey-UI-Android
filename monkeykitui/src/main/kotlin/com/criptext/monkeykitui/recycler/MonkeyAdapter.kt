package com.criptext.monkeykitui.recycler

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.bubble.*
import com.criptext.monkeykitui.photoview.PhotoViewActivity
import com.criptext.monkeykitui.recycler.audio.AudioPlaybackHandler
import com.criptext.monkeykitui.recycler.holders.*
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
    val mContext : Context
    private val datalist : ArrayList<MonkeyItem>
/*    set(value) {
        if(!hasReachedEnd)
            datalist.add(EndItem())
        field = value
    } */
    var hasReachedEnd : Boolean = true
    set(value) {
        if(!value && field != value) {
            datalist.add(0, EndItem())
            notifyItemInserted(0)
        }
        field = value
    }

    private var selectedMessage : MonkeyItem?

    var audioListener : AudioListener?
    var audioHandler : AudioPlaybackHandler?
    var imageListener : ImageListener?
    var onLongClickListener : OnLongClickMonkeyListener?

    init{
        mContext = ctx
        datalist = list
        selectedMessage = null
        audioListener = null
        audioHandler = null
        imageListener = object : ImageListener {
            override fun onImageClicked(position: Int, item: MonkeyItem) {
                val intent = Intent(mContext, PhotoViewActivity::class.java)
                intent.putExtra(PhotoViewActivity.IMAGE_DATA_PATH, item.getFilePath())
                mContext.startActivity(intent)
            }

        }
        onLongClickListener = null

    }

    val chatActivity : ChatActivity
        get() = mContext as ChatActivity

    override fun getItemCount(): Int {
        return datalist.size
    }

    fun getViewTypes() : Int{
        return 7
    }

    override fun getItemViewType(position: Int): Int {
        val item = datalist[position]
        //incoming messages have viewtypes/2 higher type
        if(item.getMessageType() == MonkeyItem.MonkeyItemType.MoreMessages.ordinal)
            return item.getMessageType()

        return item.getMessageType() + (if(item.isIncomingMessage()) 6 else 0)
    }


    override fun onViewAttachedToWindow(holder: MonkeyHolder?) {
        super.onViewAttachedToWindow(holder)
        val endHolder = holder as? MonkeyEndHolder
        if(endHolder != null) {
            endHolder.setOnClickListener {  }
            chatActivity.onLoadMoreData(datalist.size)
        }
    }


    override fun onBindViewHolder(holder : MonkeyHolder, position : Int) {
        //set Dates
        val item = datalist[position]

        if(holder is MonkeyEndHolder) {
            holder.setOnClickListener({
                chatActivity.onLoadMoreData(datalist.size)
            })
            return
        }

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
                val playingAudio = audioHandler?.currentlyPlayingItem?.item

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
                if(!target.exists()){
                    chatActivity.onFileDownloadRequested(position, item)
                    audioHolder.updatePlayPauseButton(false)
                    audioHolder.setWaitingForDownload()
                } else if(playingAudio?.getMessageId().equals(item.getMessageId())){// Message is playing
                    audioHolder.setReadyForPlayback()
                    if(audioHandler?.playingAudio ?: false){
                        Log.d("MonkeyAdapter", "set pause button")
                        audioHolder.updatePlayPauseButton(true)
                        audioHolder.updateAudioProgress(audioHandler?.playbackProgress ?: 0,
                                audioHandler?.playbackProgressText ?: MonkeyAudioHolder.DEFAULT_AUDIO_DURATION)
                        audioHolder.setAudioActions(pauseAction)
                    } else {
                        audioHolder.updatePlayPauseButton(false)
                        audioHolder.setAudioActions(playAction)
                    }
                    audioHolder.setOnSeekBarChangeListener(object : CircularAudioView.OnCircularAudioViewChangeListener{
                        override fun onStartTrackingTouch(seekBar: CircularAudioView?) {
                            Log.d("Seekbar", "start tracking")
                            audioHandler?.updateProgressEnabled = false
                        }

                        override fun onStopTrackingTouch(seekBar: CircularAudioView?) {
                            Log.d("Seekbar", "stop tracking")
                            audioHandler?.updateProgressEnabled = true
                        }

                        override fun onProgressChanged(CircularAudioView: CircularAudioView?, progress: Int, fromUser: Boolean) {
                            if(fromUser && progress > -1 && progress < 100)
                                audioListener?.onProgressManuallyChanged(position, item, progress)
                        }
                    })
                } else {
                    audioHolder.setReadyForPlayback()
                    audioHolder.updatePlayPauseButton(false)
                    audioHolder.updateAudioProgress(0, MonkeyAudioHolder.DEFAULT_AUDIO_DURATION)
                    audioHolder.setAudioDurationText(item.getAudioDuration())
                    audioHolder.setAudioActions(playAction)
                }
            }
            MonkeyItem.MonkeyItemType.photo -> {
                val imageHolder = holder as MonkeyImageHolder
                val file = File(item.getFilePath())
                if(file.exists()){
                    imageHolder.setDownloadedImage(file, chatActivity as Context)
                    if(file.length() < item.getFileSize())
                        imageHolder.setRetryDownloadButton(position, item, chatActivity)
                }
                else{
                    imageHolder.setNotDownloadedImage(item, chatActivity as Context)
                    chatActivity.onFileDownloadRequested(position, item)
                }

                imageHolder.setOnClickListener(View.OnClickListener { imageListener?.onImageClicked(position, item) })
                imageHolder.setOnLongClickListener(View.OnLongClickListener {
                    onLongClickListener?.onLongClick(position, item)
                    true
                })

            }
        }

    }

    fun inflateView(incoming: Boolean, inLayout: Int, outLayout : Int) : View {
        if(incoming)
            return LayoutInflater.from(mContext).inflate(inLayout, null)
        return LayoutInflater.from(mContext).inflate(outLayout, null)
    }

    protected fun removeEndOfRecyclerView(){
        val lastItem = datalist[0]
        if(lastItem.getMessageType() == MonkeyItem.MonkeyItemType.MoreMessages.ordinal){
            datalist.remove(lastItem)
            notifyItemRemoved(0)
            hasReachedEnd = true
            Log.d("adapter", "remove end")
        }

    }

    fun addNewData(newData : ArrayList<MonkeyItem>){
        removeEndOfRecyclerView()
        datalist.addAll(0, newData)
        notifyItemRangeInserted(0, newData.size)
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
            MonkeyItem.MonkeyItemType.MoreMessages ->  {
                mView = LayoutInflater.from(mContext).inflate(R.layout.end_of_recycler_view, null)
                return MonkeyEndHolder(mView)
            }
        }
        return null
    }


    fun smoothlyAddNewData(newData: ArrayList<MonkeyItem>, recyclerView: RecyclerView, reachedEnd: Boolean){
            addNewData(newData);
            recyclerView.smoothScrollToPosition(newData.size - (if(reachedEnd) 1 else 0) );
            hasReachedEnd = reachedEnd
        }
    companion object {

    }

}
