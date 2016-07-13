package com.criptext.monkeykitui.recycler

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.photoview.PhotoViewActivity
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer
import com.criptext.monkeykitui.recycler.holders.*
import com.criptext.monkeykitui.recycler.listeners.ImageListener
import com.innovative.circularaudioview.AudioActions
import com.innovative.circularaudioview.CircularAudioView
import java.io.File
import java.util.*

/**
 * Adapter class for displaying MonkeyItem messages on a RecyclerView.
 * Displays 3 kinds of messages, Text, Audio, and Photos.
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


    var monkeyConfig: MonkeyConfig
    /**
     * Handles all media playback of voice notes. MonkeyAdapter must notify the audioHandler whenever
     * the user wants to play/pause a voice note.
     */
    var audioHandler : VoiceNotePlayer?

    /**
     * Listener to do an action when user clicks a photo. Default action should be open a photo viewer
     */
    var imageListener : ImageListener?

    //MessageLoading
    var requestmessagesTimestamp = 0L
    val DEFAULT_MESSAGE_LOAD_TIME = 500
    var messageLoadTime =  DEFAULT_MESSAGE_LOAD_TIME

    val handler = Handler()

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
        monkeyConfig = MonkeyConfig()
        setHasStableIds(true)

    }

    val chatActivity : ChatActivity
        get() = mContext as ChatActivity

    override fun getItemCount(): Int {
        return messagesList.size
    }

    /**
     * Gets number of View Types that this adapter can display on a RecyclerView. This number must
     * be constant. The value returned by this function is important for getItemViewType method
     * @return the number of View Types that this adapter can display on a RecyclerView
     */
    fun getViewTypes() : Int{
        return (MonkeyItem.MonkeyItemType.values().size - 1) * 4 + 1;
    }

    override fun getItemViewType(position: Int): Int {
        val item = messagesList[position]
        if(item.getDeliveryStatus().isTransferring()
                &&(item.getDeliveryStatus() == MonkeyItem.DeliveryStatus.delivered || item.getDeliveryStatus() == MonkeyItem.DeliveryStatus.read))
            throw IllegalArgumentException();
        val typeConst = if(!item.isIncomingMessage() && !item.getDeliveryStatus().isTransferring())
               0 //outgoing and delivered
            else if(!item.isIncomingMessage() && item.getDeliveryStatus().isTransferring())
               1 //outgoing and transferring
            else if(item.isIncomingMessage() && !item.getDeliveryStatus().isTransferring())
               2 //incoming and delivered
            else 3 //incoming and transferring
        if(item.getMessageType() == MonkeyItem.MonkeyItemType.MoreMessages.ordinal)
            return item.getMessageType()

        return item.getMessageType() + typeConst * MonkeyItem.MonkeyItemType.values().size
    }


    override fun onViewAttachedToWindow(holder: MonkeyHolder?) {
        super.onViewAttachedToWindow(holder)
        val endHolder = holder as? MonkeyEndHolder
        if(endHolder != null) {
            endHolder.setOnClickListener {  }
            requestmessagesTimestamp = System.currentTimeMillis();
            chatActivity.onLoadMoreData(messagesList.size)
        }
    }


    override fun getItemId(position: Int): Long {
        return messagesList[position].getMessageTimestamp()
    }

    fun rebindMonkeyItem(monkeyItem: MonkeyItem, recyclerView: RecyclerView){
        val position = getItemPositionByTimestamp(monkeyItem)
        val monkeyHolder = recyclerView.findViewHolderForAdapterPosition(position) as MonkeyHolder?
        if(monkeyHolder != null)
            onBindViewHolder(monkeyHolder, position)
    }

    override fun onBindViewHolder(holder : MonkeyHolder, position : Int) {

        val typeClassification = getItemViewType(position) / MonkeyItem.MonkeyItemType.values().size
        val item = messagesList[position]

        if(holder is MonkeyEndHolder) {
            holder.setOnClickListener({
                chatActivity.onLoadMoreData(messagesList.size)
            })
            return
        }

        bindCommonMonkeyHolder(position, item, holder)

        //type specific stuff
        if(typeClassification == 0 || typeClassification == 2) {
            when (MonkeyItem.MonkeyItemType.values()[item.getMessageType()]) {
                MonkeyItem.MonkeyItemType.text -> {
                    bindMonkeyTextHolder(position, item, holder)
                }
                MonkeyItem.MonkeyItemType.audio -> {
                    bindMonkeyAudioHolder(position, item, holder)
                }
                MonkeyItem.MonkeyItemType.photo -> {
                    bindMonkeyPhotoHolder(position, item, holder)
                }
            }
        }
        else if(typeClassification == 1 || typeClassification == 3) {
            when (MonkeyItem.MonkeyItemType.values()[item.getMessageType()]) {
                MonkeyItem.MonkeyItemType.text -> {
                    bindMonkeyTextHolder(position, item, holder)
                }
                MonkeyItem.MonkeyItemType.audio -> {
                    bindMonkeyAudioProcessingHolder(position, item, holder)
                }
                MonkeyItem.MonkeyItemType.photo -> {
                    bindMonkeyPhotoProcessingHolder(position, item, holder)
                }
            }
        }
    }

    /**
     * Binds an existing MonkeyHolder with a MonkeyItem. This method is called before type specific
     * methods like bindMonkeyTextView or bindMonkeyAudio. Common attributes for all MonkeyItem types
     * are set. It is called for every MonkeyItem, regardless of type.
     * @param position The adapter position of the MonkeyItem
     * @param item a MonkeyItem o show in the RecyclerView using a MonkeyHolder
     * @param holder The MonkeyHolder that will hold the UI for this MonkeyItem
     */
    open protected fun bindCommonMonkeyHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder){
        //set message date
        holder.setMessageDate(item.getMessageTimestamp())

        if (item.isIncomingMessage()) { //stuff for incoming messages
            val group = groupChat
            if (group != null) {
                holder.setSenderName(group.getMemberName(item.getContactSessionId()),
                        group.getMemberColor(item.getContactSessionId()))
            }
        } else { //stuff for outgoing messages
           holder.updateReadStatus(item.getDeliveryStatus())
           holder.updateSendingStatus(item.getDeliveryStatus(), chatActivity.isOnline(), item.getMessageTimestamp())
        }

        //selected status
        val selected = selectedMessage
        holder.updateSelectedStatus(selected != null && selected.getMessageId() == item.getMessageId())
    }
    /**
     * Binds an existing MonkeyHolder with a MonkeyItem of type Text. This method is called on the
     * onBindViewHolder method when the MonkeyItem is of type Text.
     * @param position The adapter position of the MonkeyItem
     * @param item a MonkeyItem of type Text to show in the RecyclerView using a MonkeyHolder
     * @param holder The MonkeyHolder that will hold the UI for this MonkeyItem
     */
    open protected fun bindMonkeyTextHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder){
        val textHolder = holder as MonkeyTextHolder
        textHolder.setText(item.getMessageText())
        textHolder.setOnLongClickListener(View.OnLongClickListener {
            val act = chatActivity as Activity
            val clipboard =  act.getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("MonkeyKitCopy", item.getMessageText());
            clipboard.primaryClip = clip;
            Toast.makeText(act, "Message copied to Clipboard", Toast.LENGTH_SHORT).show()
            true
        })
    }
    /**
     * Binds an existing MonkeyHolder with a MonkeyItem of type Photo. This method is called on the
     * onBindViewHolder method when the MonkeyItem is of type photo.
     * @param position The adapter position of the MonkeyItem
     * @param item a MonkeyItem of type Photo to show in the RecyclerView using a MonkeyHolder
     * @param holder The MonkeyHolder that will hold the UI for this MonkeyItem
     */
    open protected fun bindMonkeyPhotoHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder){

        val imageHolder = holder as MonkeyImageHolder
        val file = File(item.getFilePath())
        imageHolder.setDownloadedImage(file, chatActivity as Context)
        imageHolder.setOnClickListener(View.OnClickListener { imageListener?.onImageClicked(position, item) })
        if(file.length() < item.getFileSize())
            imageHolder.setRetryDownloadButton(position, item, chatActivity)

    }

    open protected fun bindMonkeyPhotoProcessingHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder){

        val imageHolder = holder as MonkeyImageHolder
        val file = File(item.getFilePath())
        if(file.exists()){
            imageHolder.setDownloadedImage(file, chatActivity as Context)
        }

        if(item.getDeliveryStatus() == MonkeyItem.DeliveryStatus.sending){
            if(!file.exists()){
                imageHolder.setNotDownloadedImage(item, chatActivity as Context)
                chatActivity.onFileDownloadRequested(position, item)
            }
        }
        else if(item.getDeliveryStatus() == MonkeyItem.DeliveryStatus.error){
            if(item.isIncomingMessage()){
                imageHolder.setRetryDownloadButton(position, item, chatActivity)
            }
            else{
                imageHolder.setRetryUploadButton(position, item, chatActivity)
            }
        }

        imageHolder.setOnClickListener(View.OnClickListener { imageListener?.onImageClicked(position, item) })

    }

    /**
     * Binds an existing MonkeyHolder with a MonkeyItem of type Audio. This method is called on the
     * onBindViewHolder method when the MonkeyItem is of type audio.
     * @param position The adapter position of the MonkeyItem
     * @param item a MonkeyItem of type Audio to show in the RecyclerView using a MonkeyHolder
     * @param holder The MonkeyHolder that will hold the UI for this MonkeyItem
     */
    open protected fun bindMonkeyAudioHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder){
        val audioHolder = holder as MonkeyAudioHolder
        val target = File(item.getFilePath())
        val playingAudio = audioHandler?.currentlyPlayingItem?.item

        val playAction = object : AudioActions() {
                    override fun onActionClicked() {
                        super.onActionClicked()
                        audioHandler?.onPlayButtonClicked(item)
                    }

                    override fun onActionLongClicked() {
                        super.onActionLongClicked()
                    }
                }

        val pauseAction = object : AudioActions() {
                    override fun onActionClicked() {
                        super.onActionClicked()
                        audioHandler?.onPauseButtonClicked()
                    }

                    override fun onActionLongClicked() {
                        super.onActionLongClicked()
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
                        audioHandler?.playbackPosition?.toLong() ?: 0)
            if(audioHandler?.isPlayingAudio ?: false){ // Message is playing
                audioHolder.updatePlayPauseButton(true)
                audioHolder.setAudioActions(pauseAction)
            } else { // Message is paused
                audioHolder.updatePlayPauseButton(false)
                audioHolder.setAudioActions(playAction)
            }
            audioHolder.setOnSeekBarChangeListener(object : CircularAudioView.OnCircularAudioViewChangeListener{
                val isThisMonkeyItemPlaying : Boolean
                get() = item.getMessageTimestamp() == audioHandler?.currentlyPlayingItem?.item?.getMessageTimestamp()
                override fun onStartTrackingTouch(seekBar: CircularAudioView?) {
                    Log.d("Seekbar", "start tracking")
                    if(isThisMonkeyItemPlaying)
                        audioHandler?.updateProgressEnabled = false
                }

                override fun onStopTrackingTouch(seekBar: CircularAudioView?) {
                    Log.d("Seekbar", "stop tracking")
                    if(isThisMonkeyItemPlaying)
                        audioHandler?.updateProgressEnabled = true
                }

                override fun onProgressChanged(CircularAudioView: CircularAudioView?, progress: Int, fromUser: Boolean) {
                    if(fromUser && isThisMonkeyItemPlaying && progress > -1 && progress < 100)
                        audioHandler?.onProgressManuallyChanged(item, progress)
                }
            })
        } else { //Message is available for playback but not prepared in the MediaPlayer
            audioHolder.setReadyForPlayback()
            audioHolder.updatePlayPauseButton(false)
            audioHolder.updateAudioProgress(0, item.getAudioDuration())
            audioHolder.setAudioActions(playAction)
        }
    }

    open protected fun bindMonkeyAudioProcessingHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder){
        val audioHolder = holder as MonkeyAudioHolder
        val target = File(item.getFilePath())

        audioHolder.updateAudioProgress(0, 0)
        if(item.isIncomingMessage()){
            when(item.getDeliveryStatus()){
                MonkeyItem.DeliveryStatus.error ->
                    audioHolder.setErrorInDownload(View.OnClickListener {
                        chatActivity.onFileDownloadRequested(position, item)
                    })
                MonkeyItem.DeliveryStatus.sending -> {
                    audioHolder.setWaitingForDownload()
                    chatActivity.onFileDownloadRequested(position, item)
                }
            }
        } else {
            when(item.getDeliveryStatus()){
                MonkeyItem.DeliveryStatus.error ->
                    audioHolder.setErrorInUpload(View.OnClickListener {
                        chatActivity.onFileUploadRequested(position, item)
                    })
                MonkeyItem.DeliveryStatus.sending -> {
                    audioHolder.setWaitingForUpload()
                    chatActivity.onFileUploadRequested(position, item)
                }
            }
        }
    }

    fun inflateView(incoming: Boolean, inLayout: Int, outLayout : Int) : View {
        if(incoming) {
            return LayoutInflater.from(mContext).inflate(inLayout, null)
        }
        else {
            return LayoutInflater.from(mContext).inflate(outLayout, null)
        }
    }

    /**
     *removes the more messages view
     */
    protected fun removeEndOfRecyclerView(){
        if(messagesList.isEmpty())
            return;

        val lastItem = messagesList[0]
        if(lastItem.getMessageType() == MonkeyItem.MonkeyItemType.MoreMessages.ordinal){
            messagesList.remove(lastItem)
            notifyItemRemoved(0)
            hasReachedEnd = true
            //Log.d("adapter", "remove end")
        }

    }

    /**
     * Adds a group of MonkeyItems to the beginning of the list. This should be only used for showing
     * older messages as the user scrolls up.
     * @param newData the list of MonkeyItems to add
     * @param reachedEnd boolean that indicates whether there are still more items available. If there are
     * then when the user scrolls to the beginning of the list, the adapter should attempt to load the
     * remaining items and show a view that tells the user that it is loading messages.
     */
    fun addOldMessages(newData : ArrayList<MonkeyItem>, reachedEnd: Boolean){

        fun addOldMessagesToAdapter(){
            removeEndOfRecyclerView()
            messagesList.addAll(0, newData)
            notifyItemRangeInserted(0, newData.size)
            hasReachedEnd = reachedEnd
        }
        val elapsedTime = System.currentTimeMillis() - requestmessagesTimestamp
        if (elapsedTime > messageLoadTime)
            addOldMessagesToAdapter()
        else{
            handler.postDelayed(Runnable { addOldMessagesToAdapter() },
                    messageLoadTime - elapsedTime)
        }
    }

    /**
     * Creates a new MonkeyHolder for messages of type text.
     * @param received boolean that indicates whether the message was received or sent by the user
     * @return a new MonkeyHolder for messages of type text.
     */
    open fun createMonkeyTextHolder(received: Boolean, transferring: Boolean): MonkeyHolder{

        val holder : MonkeyTextHolder?
        if(transferring)
            holder = MonkeyTextHolder(inflateView(received, R.layout.text_message_view_in, R.layout.text_message_view_out))
        else
            holder = MonkeyTextHolder(inflateView(received, R.layout.text_message_view_in, R.layout.text_message_view_out))

        //customize background colors
        if(received){
            val incomingBubleColor = monkeyConfig.textBubbleIncomingColor
            if(incomingBubleColor != null)
                holder.setBackgroundColor(incomingBubleColor)
        } else {
            val outgoingBubleColor = monkeyConfig.textBubbleOutgoingColor
            if(outgoingBubleColor != null)
                holder.setBackgroundColor(outgoingBubleColor)
        }

        return holder
    }

    /**
     * Creates a new MonkeyHolder for messages of type photo.
     * @param received boolean that indicates whether the message was received or sent by the user
     * @return a new MonkeyHolder for messages of type photo.
     */
    open fun createMonkeyPhotoHolder(received: Boolean, transferring: Boolean): MonkeyHolder{

        if(transferring)
            return MonkeyImageHolder(inflateView(received, R.layout.image_message_view_in_pending, R.layout.image_message_view_out_pending))
        else
            return MonkeyImageHolder(inflateView(received, R.layout.image_message_view_in, R.layout.image_message_view_out))

    }

    /**
     * Creates a new MonkeyHolder for messages of type audio.
     * @param received boolean that indicates whether the message was received or sent by the user
     * @return a new MonkeyHolder for messages of type audio.
     */
    open fun createMonkeyAudioHolder(received: Boolean, transferring: Boolean): MonkeyHolder{
        if(transferring) {
            return MonkeyAudioHolder(inflateView(received,
                    R.layout.audio_message_view_in_pending, R.layout.audio_message_view_out_pending))
        }
        else {
            return MonkeyAudioHolder(inflateView(received, R.layout.audio_message_view_in,
                    R.layout.audio_message_view_out))
        }

    }
    /**
     * Creates a new MonkeyHolder to be displayed when the adapter is loading more messages
     * @return a new MonkeyHolder to be displayed when the adapter is loading more messages
     */
    open fun createMoreMessagesView(): MonkeyHolder{
        val mView = LayoutInflater.from(mContext).inflate(R.layout.end_of_recycler_view, null)
        return MonkeyEndHolder(mView)
    }


    override fun onCreateViewHolder(p0: ViewGroup?, viewtype: Int): MonkeyHolder? {
        val typeClassification = viewtype / MonkeyItem.MonkeyItemType.values().size
        if (typeClassification == 0)
            when (MonkeyItem.MonkeyItemType.values()[viewtype]) {
                MonkeyItem.MonkeyItemType.text -> return createMonkeyTextHolder(false, false)
                MonkeyItem.MonkeyItemType.photo -> return createMonkeyPhotoHolder(false, false)
                MonkeyItem.MonkeyItemType.audio -> return createMonkeyAudioHolder(false, false)
            //MonkeyItem.MonkeyItemType.file ->
            //MonkeyItem.MonkeyItemType.contact ->
                MonkeyItem.MonkeyItemType.MoreMessages -> return createMoreMessagesView()
            }
        else if(typeClassification == 2) {
            val truetype = viewtype - MonkeyItem.MonkeyItemType.values().size * typeClassification
            when (MonkeyItem.MonkeyItemType.values()[truetype]) {
                MonkeyItem.MonkeyItemType.text -> return createMonkeyTextHolder(true, false)
                MonkeyItem.MonkeyItemType.photo -> return createMonkeyPhotoHolder(true, false)
                MonkeyItem.MonkeyItemType.audio -> return createMonkeyAudioHolder(true, false)
            //MonkeyItem.MonkeyItemType.file ->
            //MonkeyItem.MonkeyItemType.contact ->
                MonkeyItem.MonkeyItemType.MoreMessages -> return createMoreMessagesView()
            }
        }
        else if(typeClassification == 1) {
            val truetype = viewtype - MonkeyItem.MonkeyItemType.values().size * typeClassification
            when (MonkeyItem.MonkeyItemType.values()[truetype]) {
                MonkeyItem.MonkeyItemType.text -> return createMonkeyTextHolder(false, true)
                MonkeyItem.MonkeyItemType.photo -> return createMonkeyPhotoHolder(false, true)
                MonkeyItem.MonkeyItemType.audio -> return createMonkeyAudioHolder(false, true)
            //MonkeyItem.MonkeyItemType.file ->
            //MonkeyItem.MonkeyItemType.contact ->
                MonkeyItem.MonkeyItemType.MoreMessages -> return createMoreMessagesView()
            }
        }
        else if(typeClassification == 3) {
            val truetype = viewtype - MonkeyItem.MonkeyItemType.values().size * typeClassification
            when (MonkeyItem.MonkeyItemType.values()[truetype]) {
                MonkeyItem.MonkeyItemType.text -> return createMonkeyTextHolder(true, true)
                MonkeyItem.MonkeyItemType.photo -> return createMonkeyPhotoHolder(true, true)
                MonkeyItem.MonkeyItemType.audio -> return createMonkeyAudioHolder(true, true)
            //MonkeyItem.MonkeyItemType.file ->
            //MonkeyItem.MonkeyItemType.contact ->
                MonkeyItem.MonkeyItemType.MoreMessages -> return createMoreMessagesView()
            }
        }
        return null
    }


    /**
     * Finds the adapter position by the MonkeyItem's timestamp.
     * @param targetId the timestamp of the MonkeyItem whose adapter position will be searched. This
     * timestamp must belong to an existing MonkeyItem in this adapter.
     * @return The adapter position of the MonkeyItem. If the item was not found returns -1.
     */
    fun getItemPositionByTimestamp(item: MonkeyItem) = messagesList.binarySearch(item,
            Comparator { t1, t2 ->
               if(t1.getMessageTimestamp() < t2.getMessageTimestamp()) {
                  -1
               }else if (t1.getMessageTimestamp() > t2.getMessageTimestamp()) {
                   1
               } else
               0
            })

    /**
     * Adds a new item to the RecyclerView with a smooth scrolling animation
     * @param item MonkeyItem to add. It will be added at the end of the messagesList, so it should
     * have a higher timestamp than the other messages.
     * @recylerview The recyclerView object that displays the messages.
     */
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
