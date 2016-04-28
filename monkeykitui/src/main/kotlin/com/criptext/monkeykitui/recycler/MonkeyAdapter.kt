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
import com.criptext.monkeykitui.recycler.GroupChat
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.bubble.*
import com.criptext.monkeykitui.photoview.PhotoViewActivity
import com.criptext.monkeykitui.recycler.audio.AudioPlaybackHandler
import com.criptext.monkeykitui.recycler.holders.*
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

open class MonkeyAdapter(ctx: Context, list : ArrayList<MonkeyItem>) : RecyclerView.Adapter<MonkeyHolder>() {
    val mContext : Context
    val messagesList: ArrayList<MonkeyItem>

    var groupChat : GroupChat? = null
    var hasReachedEnd : Boolean = true
    set(value) {
        if(!value && field != value) {
            messagesList.add(0, EndItem())
            notifyItemInserted(0)
            //Log.d("MonkeyAdapter", "End item added")
        }
        field = value
    }

    private var selectedMessage : MonkeyItem?

    var audioHandler : AudioPlaybackHandler?
    var imageListener : ImageListener?
    var onLongClickListener : OnLongClickMonkeyListener?

    init{
        mContext = ctx
        messagesList = list
        selectedMessage = null
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
        return messagesList.size
    }

    fun getViewTypes() : Int{
        return 7
    }

    override fun getItemViewType(position: Int): Int {
        val item = messagesList[position]
        //incoming messages have viewtypes/2 higher type
        //Log.d("MonkeyAdapter", "position: $position/${messagesList.size - 1} type: ${item.getMessageType()}" )
        if(item.getMessageType() == MonkeyItem.MonkeyItemType.MoreMessages.ordinal)
            return item.getMessageType()

        return item.getMessageType() + (if(item.isIncomingMessage()) 6 else 0)
    }


    override fun onViewAttachedToWindow(holder: MonkeyHolder?) {
        super.onViewAttachedToWindow(holder)
        val endHolder = holder as? MonkeyEndHolder
        if(endHolder != null) {
            endHolder.setOnClickListener {  }
            chatActivity.onLoadMoreData(messagesList.size)
        }
    }


    override fun onBindViewHolder(holder : MonkeyHolder, position : Int) {

        val item = messagesList[position]

        if(holder is MonkeyEndHolder) {
            holder.setOnClickListener({
                chatActivity.onLoadMoreData(messagesList.size)
            })
            return
        }

        bindMonkeyBasicView(position, item, holder)

        //type specific stuff
        when(MonkeyItem.MonkeyItemType.values()[item.getMessageType()]){
            MonkeyItem.MonkeyItemType.text -> {
                bindMonkeyTextView(position, item, holder)
            }
            MonkeyItem.MonkeyItemType.audio -> {
                bindMonkeyAudioView(position, item, holder)
            }
            MonkeyItem.MonkeyItemType.photo -> {
                bindMonkeyPhotoView(position, item, holder)
            }
        }

    }

    open protected fun bindMonkeyBasicView(position: Int, item: MonkeyItem, holder: MonkeyHolder){
        //set message date
        holder.setMessageDate(item.getMessageTimestamp())
        //long click
        holder.setOnLongClickListener(View.OnLongClickListener {
            onLongClickListener?.onLongClick(position, item)
            Toast.makeText(mContext, "long clicked: " + position, Toast.LENGTH_SHORT).show()
            true
        })

        if (item.isIncomingMessage()) { //stuff for incoming messages
            val group = groupChat
            if (group != null) {
                holder.setSenderName(group.getMemberName(item.getContactSessionId()),
                        group.getMemberColor(item.getContactSessionId()))
            }
        } else { //stuff for outgoing messages
           holder.updateReadStatus(item.getOutgoingMessageStatus())
           holder.updateSendingStatus(item.getOutgoingMessageStatus(), chatActivity.isOnline(), item.getMessageTimestamp())
        }

        //selected status
        val selected = selectedMessage
        holder.updateSelectedStatus(selected != null && selected.getMessageId() == item.getMessageId())
    }

    open protected fun bindMonkeyTextView(position: Int, item: MonkeyItem, holder: MonkeyHolder){
        val textHolder = holder as MonkeyTextHolder
        textHolder.messageTextView!!.text = item.getMessageText()
    }

    open protected fun bindMonkeyPhotoView(position: Int, item: MonkeyItem, holder: MonkeyHolder){
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

    open protected fun bindMonkeyAudioView(position: Int, item: MonkeyItem, holder: MonkeyHolder){
        val audioHolder = holder as MonkeyAudioHolder
        val target = File(item.getFilePath())
        val playingAudio = audioHandler?.currentlyPlayingItem?.item

        val playAction = object : AudioActions() {
                    override fun onActionClicked() {
                        super.onActionClicked()
                        audioHandler?.onPlayButtonClicked(position, item)
                    }

                    override fun onActionLongClicked() {
                        super.onActionLongClicked()
                        onLongClickListener?.onLongClick(position, item)
                    }
                }

        val pauseAction = object : AudioActions() {
                    override fun onActionClicked() {
                        super.onActionClicked()
                        audioHandler?.onPauseButtonClicked(position, item)
                    }

                    override fun onActionLongClicked() {
                        super.onActionLongClicked()
                        onLongClickListener?.onLongClick(position, item)
                    }
                }
        audioHolder.setAudioDurationText(item.getAudioDuration())
        if(!target.exists()){ //Message does not exist, needs to be downloaded
            chatActivity.onFileDownloadRequested(position, item)
            audioHolder.updatePlayPauseButton(false)
            audioHolder.setWaitingForDownload()
        } else if(playingAudio?.getMessageId().equals(item.getMessageId())){// Message is prepared in MediaPlayer
            audioHolder.setReadyForPlayback()
            audioHolder.updateAudioProgress(audioHandler?.playbackProgress ?: 0,
                        audioHandler?.playbackProgressText ?: MonkeyAudioHolder.DEFAULT_AUDIO_DURATION)
            if(audioHandler?.playingAudio ?: false){ // Message is playing
                audioHolder.updatePlayPauseButton(true)
                audioHolder.setAudioActions(pauseAction)
            } else { // Message is paused
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
                    if(fromUser && progress > -1 && progress < 100 && playingAudio?.getMessageId().equals(item.getMessageId()))
                        audioHandler?.onProgressManuallyChanged(position, item, progress)
                }
            })
        } else { //Message is available for playback but not prepared in the MediaPlayer
            audioHolder.setReadyForPlayback()
            audioHolder.updatePlayPauseButton(false)
            audioHolder.updateAudioProgress(0, MonkeyAudioHolder.DEFAULT_AUDIO_DURATION)
            audioHolder.setAudioActions(playAction)
        }
    }

    fun inflateView(incoming: Boolean, inLayout: Int, outLayout : Int) : View {
        if(incoming)
            return LayoutInflater.from(mContext).inflate(inLayout, null)
        return LayoutInflater.from(mContext).inflate(outLayout, null)
    }

    protected fun removeEndOfRecyclerView(){
        val lastItem = messagesList[0]
        if(lastItem.getMessageType() == MonkeyItem.MonkeyItemType.MoreMessages.ordinal){
            messagesList.remove(lastItem)
            notifyItemRemoved(0)
            hasReachedEnd = true
            //Log.d("adapter", "remove end")
        }

    }

    fun addNewData(newData : ArrayList<MonkeyItem>){
        removeEndOfRecyclerView()
        messagesList.addAll(0, newData)
        notifyItemRangeInserted(0, newData.size)

        val playingItem = audioHandler?.currentlyPlayingItem
        if(playingItem != null)
            playingItem.position += newData.size
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

    fun smoothlyAddNewItem(item : MonkeyItem, recyclerView: RecyclerView){

        val manager = recyclerView.layoutManager as LinearLayoutManager
        val last = manager.findLastVisibleItemPosition()
        messagesList.add(item);
        notifyItemInserted(messagesList.size);
        if(last >= messagesList.size - 2) {
            recyclerView.scrollToPosition(messagesList.size - 1);
        }
    }

}
