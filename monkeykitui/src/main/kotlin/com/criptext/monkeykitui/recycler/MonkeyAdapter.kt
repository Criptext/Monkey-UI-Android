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
import com.criptext.monkeykitui.cav.AudioActions
import com.criptext.monkeykitui.cav.CircularAudioView
import com.criptext.monkeykitui.util.InsertionSort
import java.io.File
import java.util.*

/**
 * Adapter class for displaying MonkeyItem messages on a RecyclerView.
 * Displays 3 kinds of messages, Text, Audio, and Photos.
 * Created by gesuwall on 4/4/16.
 */

open class MonkeyAdapter(val mContext: Context) : RecyclerView.Adapter<MonkeyHolder>() {
    var groupChat : GroupChat? = null
    private val messagesList: ArrayList<MonkeyItem>
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
    var voiceNotePlayer: VoiceNotePlayer?

    /**
     * Listener to do an action when user clicks a photo. Default action should be open a photo viewer
     */
    var imageListener : ImageListener?


    val dataLoader : SlowRecyclerLoader

    var loadingDelay: Long
        get() =  dataLoader.delayTime
        set(value) { dataLoader.delayTime = value }

    init{
        messagesList = ArrayList<MonkeyItem>()
        selectedMessage = null
        voiceNotePlayer = null
        imageListener = object : ImageListener {
            override fun onImageClicked(position: Int, item: MonkeyItem) {
                Log.d("PhotoHolder", "clicked $position with ${item.getDeliveryStatus()}")
                val intent = Intent(mContext, PhotoViewActivity::class.java)
                intent.putExtra(PhotoViewActivity.IMAGE_DATA_PATH, item.getFilePath())
                mContext.startActivity(intent)
            }

        }
        monkeyConfig = MonkeyConfig()
        dataLoader = SlowRecyclerLoader(false, mContext)

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
            //endHolder.setOnClickListener {  }
            dataLoader.delayNewBatch(messagesList.size)
        }
    }

    fun rebindMonkeyItem(monkeyItem: MonkeyItem, recyclerView: RecyclerView){
        val position = getItemPositionByTimestamp(monkeyItem)
        val monkeyHolder = recyclerView.findViewHolderForAdapterPosition(position) as MonkeyHolder?
        if(monkeyHolder != null)
            onBindViewHolder(monkeyHolder, position)
        else //sometimes recyclerview cant find the viewholder, and never rebind the holder. this may fix it...
            notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder : MonkeyHolder, position : Int) {

        val typeClassification = getItemViewType(position) / MonkeyItem.MonkeyItemType.values().size
        val item = messagesList[position]

        if(holder is MonkeyEndHolder) {
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
                MonkeyItem.MonkeyItemType.file -> {
                    bindMonkeyFileHolder(position, item, holder)
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
                MonkeyItem.MonkeyItemType.file -> {
                    bindMonkeyFileProcessingHolder(position, item, holder)
                }
            }
        }
    }

    fun isFollowupMessage(position: Int): Boolean{
        if(position > 0 && messagesList[position].getContactSessionId()
                    == messagesList[position - 1].getContactSessionId())
                return true
        return false
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
            if (group != null && !isFollowupMessage(position)) {
                holder.setSenderName(group.getMemberName(item.getContactSessionId()),
                        group.getMemberColor(item.getContactSessionId()))
            } else
                holder.hideSenderName()
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
     * Common binding for all files, including audio and phot. Callse the correct network method
     * to download or upload the file if necessary
     * @param item The monkeyItem to bind. It  must of type file, audio o photo
     * @param fileHolder The fileHolder to display the item's UI
     */
    open protected fun bindMonkeyFile(item: MonkeyItem, fileHolder: MonkeyFile){
        Log.d("PhotoHolder", "status ${item.getDeliveryStatus()}")
        if(item.isIncomingMessage()){
            when(item.getDeliveryStatus()){
                MonkeyItem.DeliveryStatus.error ->
                    fileHolder.setErrorInDownload(View.OnClickListener {
                        chatActivity.onFileDownloadRequested(item)
                    })
                MonkeyItem.DeliveryStatus.sending -> {
                    fileHolder.setWaitingForDownload()
                    chatActivity.onFileDownloadRequested(item)
                }
            }
        } else {
            val fileExists = File(item.getFilePath()).exists()
            when(item.getDeliveryStatus()){
                MonkeyItem.DeliveryStatus.error ->
                    fileHolder.setErrorInUpload(View.OnClickListener {
                        if(fileExists)
                            chatActivity.onFileUploadRequested(item)
                        else
                            chatActivity.onFileDownloadRequested(item)
                    })
                MonkeyItem.DeliveryStatus.sending -> {
                    fileHolder.setWaitingForUpload()
                    if(fileExists)
                        chatActivity.onFileUploadRequested(item)
                    else
                        chatActivity.onFileDownloadRequested(item)
                }
            }
        }
    }
    open protected fun bindMonkeyFileHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder) {
        val fileHolder = holder as MonkeyFileHolder
        val file = File(item.getFilePath())
        fileHolder.showFileData(file.name, getTotalSizeFile(item.getFileSize()))
        fileHolder.showFileIcon(file.extension)
    }

    open protected fun bindMonkeyFileProcessingHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder) {
        val fileHolder = holder as MonkeyFileHolder
        val file = File(item.getFilePath())
        if(file.exists()) {
            fileHolder.showFileData(file.name, getTotalSizeFile(item.getFileSize()))
        }
        bindMonkeyFile(item, fileHolder)

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
        if(!file.exists() || file.length() < item.getFileSize()) {
            Log.d("PhotoHolder", "bind ${file.length()} ${item.getFileSize()}")
            chatActivity.onFileDownloadRequested(item)
            imageHolder.setOnClickListener(null)
        }else {
            imageHolder.setDownloadedImage(file, chatActivity as Context)
            imageHolder.setOnClickListener(View.OnClickListener { imageListener?.onImageClicked(position, item) })
        }

    }

    open protected fun bindMonkeyPhotoProcessingHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder){

        val imageHolder = holder as MonkeyImageHolder
        val file = File(item.getFilePath())
        if(file.exists() && !item.isIncomingMessage())
            imageHolder.setDownloadedImage(file, mContext)

        bindMonkeyFile(item, imageHolder)
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
        val playingAudio = voiceNotePlayer?.currentlyPlayingItem?.item

        val playAction = object : AudioActions() {
                    override fun onActionClicked() {
                        super.onActionClicked()
                        voiceNotePlayer?.onPlayButtonClicked(item)
                    }

                    override fun onActionLongClicked() {
                        super.onActionLongClicked()
                    }
                }

        val pauseAction = object : AudioActions() {
                    override fun onActionClicked() {
                        super.onActionClicked()
                        voiceNotePlayer?.onPauseButtonClicked()
                    }

                    override fun onActionLongClicked() {
                        super.onActionLongClicked()
                    }
                }
        audioHolder.setAudioDurationText(item.getAudioDuration())
        if(!target.exists()){ //Message does not exist, needs to be downloaded
            chatActivity.onFileDownloadRequested(item)
            audioHolder.updatePlayPauseButton(false)
            audioHolder.setWaitingForDownload()
        } else if(playingAudio?.getMessageId().equals(item.getMessageId())){// Message is prepared in MediaPlayer
            audioHolder.setReadyForPlayback()
            audioHolder.updateAudioProgress(voiceNotePlayer?.playbackProgress ?: 0,
                        voiceNotePlayer?.playbackPosition?.toLong() ?: 0)
            if(voiceNotePlayer?.isPlayingAudio ?: false){ // Message is playing
                audioHolder.updatePlayPauseButton(true)
                audioHolder.setAudioActions(pauseAction)
            } else { // Message is paused
                audioHolder.updatePlayPauseButton(false)
                audioHolder.setAudioActions(playAction)
            }
            audioHolder.setOnSeekBarChangeListener(object : CircularAudioView.OnCircularAudioViewChangeListener{
                val isThisMonkeyItemPlaying : Boolean
                get() = item.getMessageTimestamp() == voiceNotePlayer?.currentlyPlayingItem?.item?.getMessageTimestamp()
                override fun onStartTrackingTouch(seekBar: CircularAudioView?) {
                    if(isThisMonkeyItemPlaying)
                        voiceNotePlayer?.updateProgressEnabled = false
                }

                override fun onStopTrackingTouch(seekBar: CircularAudioView?) {
                    if(isThisMonkeyItemPlaying)
                        voiceNotePlayer?.updateProgressEnabled = true
                }

                override fun onProgressChanged(CircularAudioView: CircularAudioView?, progress: Int, fromUser: Boolean) {
                    if(fromUser && isThisMonkeyItemPlaying && progress > -1 && progress < 100)
                        voiceNotePlayer?.onProgressManuallyChanged(item, progress)
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

        audioHolder.updateAudioProgress(0, 0)
        bindMonkeyFile(item, holder)
    }

    fun inflateView(incoming: Boolean, inLayout: Int, outLayout : Int) : View {
        if(incoming) {
            return LayoutInflater.from(mContext).inflate(inLayout, null)
        }
        else {
            return LayoutInflater.from(mContext).inflate(outLayout, null)
        }
    }

    protected fun removeEndOfRecyclerView(){
        removeEndOfRecyclerView(false)
    }

    /**
     *removes the more messages view
     */
    protected fun removeEndOfRecyclerView(silent: Boolean){
        if(messagesList.isEmpty())
            return;

        val lastItem = messagesList[0]
        if(lastItem.getMessageType() == MonkeyItem.MonkeyItemType.MoreMessages.ordinal){
            messagesList.remove(lastItem)
            if(!silent)
                notifyItemRemoved(0)
            hasReachedEnd = true
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
    fun addOldMessages(newData : Collection<MonkeyItem>, reachedEnd: Boolean, recyclerView: RecyclerView){
        removeEndOfRecyclerView()
        if(newData.size > 0) {
            messagesList.addAll(0, newData)
            val lastNewIndex = newData.size - 1
            InsertionSort(messagesList, Comparator { t1, t2 -> itemCmp(t1, t2) }, lastNewIndex).sortBackwards()
            notifyItemRangeInserted(0, newData.size)

            //Scroll only if position is not in the last position
            val manager = recyclerView.layoutManager as LinearLayoutManager
            if(messagesList.size - newData.size > 0 && !isLastItemDisplaying(manager)) {
                manager.scrollToPositionWithOffset(newData.size,
                        mContext.resources.getDimension(R.dimen.scroll_offset).toInt());
            }
        }

        hasReachedEnd = reachedEnd
    }

    /**
     * Check whether the last item in RecyclerView is being displayed or not
     * @param manager LinearLayoutManager
     * @return true if last position was Visible and false Otherwise
     */
    private fun isLastItemDisplaying(manager: LinearLayoutManager): Boolean {
        val visibleItemCount = manager.childCount
        val totalItemCount = manager.itemCount
        val pastVisiblesItems = manager.findFirstVisibleItemPosition()
        if (pastVisiblesItems > 0 && visibleItemCount > 0 && totalItemCount > 0) {
            return true
        }
        return false
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

    open fun createMonkeyFileHolder(received: Boolean, transferring: Boolean): MonkeyHolder {
        if(transferring) {
            return MonkeyFileHolder(inflateView(received,
                    R.layout.file_message_view_in_pending, R.layout.file_message_view_out_pending))
        }
        else {
            return MonkeyFileHolder(inflateView(received, R.layout.file_message_view_in,
                    R.layout.file_message_view_out))
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
        val isTransferring: Boolean
        val isIncoming: Boolean
        when(typeClassification){
            0 -> { isIncoming = false; isTransferring = false }
            1 -> { isIncoming = false; isTransferring = true }
            2 -> { isIncoming = true; isTransferring = false }
            3 -> { isIncoming = true; isTransferring = true }
            else -> throw IllegalArgumentException("Wrong typeClassification: $typeClassification. Should be 0-3")
        }

        val truetype = viewtype - MonkeyItem.MonkeyItemType.values().size * typeClassification
        when (MonkeyItem.MonkeyItemType.values()[truetype]) {
            MonkeyItem.MonkeyItemType.text -> return createMonkeyTextHolder(isIncoming, isTransferring)
            MonkeyItem.MonkeyItemType.photo -> return createMonkeyPhotoHolder(isIncoming, isTransferring)
            MonkeyItem.MonkeyItemType.audio -> return createMonkeyAudioHolder(isIncoming, isTransferring)
            MonkeyItem.MonkeyItemType.file -> return createMonkeyFileHolder(isIncoming, isTransferring)
            //MonkeyItem.MonkeyItemType.contact ->
            MonkeyItem.MonkeyItemType.MoreMessages -> return createMoreMessagesView()
        }
        return null
    }

    fun takeAllMessages() : Collection<MonkeyItem>{
        removeEndOfRecyclerView(true)
        return messagesList
    }


    fun getTotalSizeFile(totalBytes: Long) : String{
        if((totalBytes/1000) < 1000)
            return String.format("%.2f", (totalBytes/1000).toDouble())+" KB";
        else
            return String.format("%.2f", (totalBytes / 1000000).toDouble())+" MB";
    }

    internal fun itemCmp(t1: MonkeyItem, t2: MonkeyItem) =

            if(t1.getMessageTimestampOrder() < t2.getMessageTimestampOrder()) {
              -1
           }else if (t1.getMessageTimestampOrder() > t2.getMessageTimestampOrder()) {
               1
           } else t1.getMessageId().compareTo(t2.getMessageId())
    /**
     * Finds the adapter position by the MonkeyItem's timestamp.
     * @param targetId the timestamp of the MonkeyItem whose adapter position will be searched. This
     * timestamp must belong to an existing MonkeyItem in this adapter.
     * @return The adapter position of the MonkeyItem. If the item was not found returns
     * the negated expected position.
     */
    fun getItemPositionByTimestamp(item: MonkeyItem) = messagesList.binarySearch(item,
            Comparator { t1, t2 -> itemCmp(t1, t2) })


    /**
     * Looks for a monkey item with a specified Id, starting by the most recent ones.
     * @return the message with the requested Id. returns null if the message does not exist
     */
    fun findMonkeyItemById(id: String) = messagesList.findLast { it.getMessageId() == id }

    /**
     * Adds a new item to the RecyclerView with a smooth scrolling animation
     * @param item MonkeyItem to add. It will be added at the end of the messagesList, so it should
     * have a higher timestamp than the other messages.
     * @recylerview The recyclerView object that displays the messages.
     */
    fun smoothlyAddNewItem(item : MonkeyItem, recyclerView: RecyclerView){

        val manager = recyclerView.layoutManager as LinearLayoutManager
        val last = manager.findLastVisibleItemPosition()

        //make sure it goes to the right position!
        var newPos = InsertionSort(messagesList, Comparator { t1, t2 ->  itemCmp(t1, t2) })
                .insertAtCorrectPosition (item, insertAtEnd = true)
        notifyItemInserted(newPos)

        //Only scroll if this is the latest message
        if(newPos == (messagesList.size - 1) && last >= messagesList.size - 2) {
            recyclerView.scrollToPosition(messagesList.size - 1);
        }

    }

    fun getLastItem(): MonkeyItem? = messagesList.lastOrNull()

    fun getFirstItem(): MonkeyItem? = messagesList.firstOrNull()

    fun smoothlyAddNewItems(newData : Collection<MonkeyItem>, recyclerView: RecyclerView){
        if(newData.size > 0) {
            val manager = recyclerView.layoutManager as LinearLayoutManager
            val last = manager.findLastVisibleItemPosition()
            val firstNewIndex = messagesList.size
            messagesList.addAll(newData)
            InsertionSort(messagesList, Comparator { it1, it2 -> itemCmp(it1, it2) }, Math.max(1, firstNewIndex)).sort()
            notifyItemRangeInserted(firstNewIndex, newData.size);
            //Only scroll if this is the latest message
            if(firstNewIndex == (messagesList.size - 1) && last >= messagesList.size - 2) {
                recyclerView.scrollToPosition(messagesList.size - 1);
            }
        }
    }

    /**
     * removes all messages from the adapter and clears the RecyclerView
     */
    fun clear(){
        val totalMessages = messagesList.size
        messagesList.clear()
        notifyItemRangeRemoved(0, totalMessages)
    }

}
