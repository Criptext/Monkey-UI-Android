package com.criptext.monkeykitui.recycler

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer
import com.criptext.monkeykitui.recycler.holders.*
import com.criptext.monkeykitui.recycler.listeners.ImageListener
import com.criptext.monkeykitui.cav.AudioActions
import com.criptext.monkeykitui.cav.CircularAudioView
import com.criptext.monkeykitui.dialog.AbstractDialog
import com.criptext.monkeykitui.recycler.audio.PlaybackService
import com.criptext.monkeykitui.recycler.listeners.OnMessageOptionClicked
import com.criptext.monkeykitui.util.InsertionSort
import com.criptext.monkeykitui.util.SnackbarUtils
import com.etiennelawlor.imagegallery.library.activities.FullScreenImageGalleryActivity
import java.io.File
import java.net.URLConnection
import java.util.*

/**
 * Adapter class for displaying MonkeyItem messages on a RecyclerView.
 * Displays 3 kinds of messages, Text, Audio, and Photos.
 * Created by Gabriel on 4/4/16.
 */

open class MonkeyAdapter(val mContext: Context, val conversationId: String) : RecyclerView.Adapter<MonkeyHolder>() {
    var groupChat : GroupChat? = null
    var messages: MessagesList

    private var selectedMessage : MonkeyItem?

    var monkeyConfig: MonkeyConfig
    /**
     * Handles all media playback of voice notes. MonkeyAdapter must notify the audioHandler whenever
     * the user wants to play/pause a voice note.
     */
    var voiceNotePlayer: PlaybackService.VoiceNotePlayerBinder?

    /**
     * Listener to do an action when user clicks a photo. Default action should be open a photo viewer
     */
    var imageListener : ImageListener?


    val dataLoader : SlowRecyclerLoader

    val receivedMessageOptions: HashMap<Int, MutableList<OnMessageOptionClicked>>
    val sentMessageOptions: HashMap<Int, MutableList<OnMessageOptionClicked>>

    var loadingDelay: Long
        get() =  dataLoader.delayTime
        set(value) { dataLoader.delayTime = value }

    var itemToDelete: MonkeyItem? = null
    var itemThatNeedsPermissions: MonkeyItem? = null
    var recyclerView: RecyclerView? = null

    /**
     * timestamp in miliseconds of the last time the other party read the conversation's messages.
     * This value is used to display the read status of every message.
     */
    var lastRead = 0L
    private fun isRead(item: MonkeyItem) = item.getMessageTimestampOrder() <= lastRead

    init{
        messages = MessagesList("")
        selectedMessage = null
        voiceNotePlayer = null
        var messageOptions = MessageOptions(ctx = mContext,
                delete = { it: MonkeyItem -> removeItemFromRecycler(it, false)},
                unsend = { it: MonkeyItem ->
                    if(!it.getDeliveryStatus().isTransferring()) {
                        removeItemFromRecycler(it, true)
                    }
                } )
        receivedMessageOptions = messageOptions.initReceivedMessageOptions()
        sentMessageOptions = messageOptions.initSentMessageOptions()
        imageListener = object : ImageListener {
            override fun onImageClicked(position: Int, item: MonkeyItem) {
                val intent = Intent(mContext, FullScreenImageGalleryActivity::class.java)
                val images = mutableListOf(item.getFilePath())
                val bundle = Bundle()
                bundle.putStringArrayList(FullScreenImageGalleryActivity.KEY_IMAGES, ArrayList(images))
                bundle.putInt(FullScreenImageGalleryActivity.KEY_POSITION, 0)
                intent.putExtras(bundle)
                mContext.startActivity(intent)
            }

        }
        monkeyConfig = MonkeyConfig()
        dataLoader = SlowRecyclerLoader(conversationId, mContext)
    }

    constructor(mContext: Context, conversationId: String, lastRead: Long): this(mContext, conversationId){
        this.lastRead = lastRead
    }

    val chatActivity : ChatActivity
        get() = mContext as ChatActivity

    override fun getItemCount(): Int {
        return messages.actualSize
    }


    fun hasPermissionsToDownloadFiles() =
            ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    fun hasPermissionsToAccessFiles() =
            ContextCompat.checkSelfPermission(mContext,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    /**
     * Gets number of View Types that this adapter can display on a RecyclerView. This number must
     * be constant. The value returned by this function is important for getItemViewType method
     * @return the number of View Types that this adapter can display on a RecyclerView
     */
    fun getViewTypes() : Int{
        return (MonkeyItem.MonkeyItemType.values().size - 1) * 4 + 1;
    }

    override fun getItemViewType(position: Int): Int {
        val item = messages.getItemAt(position)
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
            dataLoader.delayNewBatch(messages.size)
        }
    }

    open fun isFileMessage(item: MonkeyItem) =
            item.getMessageType() == MonkeyItem.MonkeyItemType.file.ordinal ||
            item.getMessageType() == MonkeyItem.MonkeyItemType.photo.ordinal ||
            item.getMessageType() == MonkeyItem.MonkeyItemType.audio.ordinal


    fun refreshDeliveryStatus(monkeyItem: MonkeyItem, recyclerView: RecyclerView){
        recyclerView.itemAnimator.isRunning({
            val position = messages.getItemPositionByTimestamp(monkeyItem)
            if ((monkeyItem.getDeliveryStatus() == MonkeyItem.DeliveryStatus.delivered ||
                    monkeyItem.getDeliveryStatus() == MonkeyItem.DeliveryStatus.error) && isFileMessage(monkeyItem)) {
            //File messages need change MonkeyHolder
            notifyItemChanged(position)
        } else {
                //All non files just need tu update the checkmark
                val monkeyHolder = recyclerView.findViewHolderForAdapterPosition(position) as MonkeyHolder?
                monkeyHolder?.updateReadStatus(monkeyItem.getDeliveryStatus(), isRead(monkeyItem))
            }
        })
    }

    fun rebindMonkeyItem(monkeyItem: MonkeyItem, recyclerView: RecyclerView){
        recyclerView.post {
            val position = messages.getItemPositionByTimestamp(monkeyItem)
            val monkeyHolder = recyclerView.findViewHolderForAdapterPosition(position) as MonkeyHolder?
            if(monkeyHolder != null)
                onBindViewHolder(monkeyHolder, position)
            else {//sometimes recyclerview cant find the viewholder, and never rebind the holder. this may fix it...
                val position = messages.getItemPositionByTimestamp(monkeyItem)
                notifyItemChanged(position)
            }
        }
    }

    override fun onBindViewHolder(holder : MonkeyHolder, position : Int) {

        val typeClassification = getItemViewType(position) / MonkeyItem.MonkeyItemType.values().size
        val item = messages.getItemAt(position)

        if(holder is MonkeyEndHolder) {
            holder.adjustHeight(matchParentHeight = messages.actualSize == 1)
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

    protected fun getMessageLongClickOptions(item: MonkeyItem): MutableList<OnMessageOptionClicked>?{
        val monkeyType = item.getMessageType()
        val optionsMap = if(item.isIncomingMessage()) receivedMessageOptions else sentMessageOptions
        return optionsMap[monkeyType]
    }

    protected fun bindMessageLongClickListener(item: MonkeyItem, targetView: View){
        val options = getMessageLongClickOptions(item)
        if(options?.isNotEmpty() ?: false)
            targetView.setOnLongClickListener {
                MessageOptionsDialog(options!!, item).show(mContext)
                true
            }
        else targetView.setOnLongClickListener(null)
    }

    fun isFollowupMessage(position: Int): Boolean{
        if(position > 0 && messages.getItemAt(position).getSenderId()
                    == messages.getItemAt(position - 1).getSenderId())
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
        holder.setMessageDate(item.getMessageTimestamp()*1000)
        //set date separator
        val prevItem = if (position > 0) messages.getItemAt(position - 1) else null
        holder.setSeparatorText(position, item, prevItem)

        if (item.isIncomingMessage()) { //stuff for incoming messages
            val group = groupChat
            if (group != null && !isFollowupMessage(position)) {
                holder.setSenderName(group.getMemberName(item.getSenderId()),
                        group.getMemberColor(item.getSenderId()))
            } else
                holder.hideSenderName()
        } else { //stuff for outgoing messages
           holder.updateReadStatus(item.getDeliveryStatus(), isRead(item))
           holder.updateSendingStatus(item.getDeliveryStatus(), chatActivity.isOnline(), item.getMessageTimestamp())
        }

        val bubbleLayout = holder.bubbleLayout
        if(bubbleLayout != null)
        bindMessageLongClickListener(item, bubbleLayout)
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
        /*
        textHolder.setOnLongClickListener(View.OnLongClickListener {
            val act = chatActivity as Activity
            val clipboard =  act.getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("MonkeyKitCopy", item.getMessageText());
            clipboard.primaryClip = clip;
            Toast.makeText(act, "Message copied to Clipboard", Toast.LENGTH_SHORT).show()
            true
        })
        */
    }

    fun requestPermissionToDownload(item: MonkeyItem) {
        itemThatNeedsPermissions = item
        ActivityCompat.requestPermissions(mContext as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_SD)
    }

    fun requestPermissionToReadSDCard() {
        ActivityCompat.requestPermissions(mContext as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_READ_SD)
    }

    /**
     * Common binding for all files, including audio and phot. Callse the correct network method
     * to download or upload the file if necessary
     * @param item The monkeyItem to bind. It  must of type file, audio o photo
     * @param fileHolder The fileHolder to display the item's UI
     */
    open protected fun bindMonkeyFile(item: MonkeyItem, fileHolder: MonkeyFile){
        if(item.isIncomingMessage()){
            when(item.getDeliveryStatus()){
                MonkeyItem.DeliveryStatus.error ->
                    fileHolder.setErrorInDownload(View.OnClickListener {
                        if(hasPermissionsToDownloadFiles())
                            chatActivity.onFileDownloadRequested(item)
                        else
                            requestPermissionToDownload(item)

                    }, item.getFileSize())
                MonkeyItem.DeliveryStatus.sending -> {
                    fileHolder.setWaitingForDownload()
                    chatActivity.onFileDownloadRequested(item)
                }
            }
        } else {
            val fileExists = File(item.getFilePath()).exists()
            when(item.getDeliveryStatus()){
                MonkeyItem.DeliveryStatus.error ->
                    if(fileExists)
                        fileHolder.setErrorInUpload(View.OnClickListener {
                            if(hasPermissionsToAccessFiles())
                                chatActivity.onFileUploadRequested(item)
                            else
                                requestPermissionToReadSDCard()
                        }, item.getFileSize())
                    else
                        fileHolder.setErrorInDownload(View.OnClickListener {
                            if(hasPermissionsToDownloadFiles())
                                chatActivity.onFileDownloadRequested(item)
                            else
                                requestPermissionToDownload(item)
                            }, item.getFileSize())

                MonkeyItem.DeliveryStatus.sending -> {
                    if(fileExists) {
                        fileHolder.setWaitingForUpload()
                        chatActivity.onFileUploadRequested(item)
                    }
                    else {
                        fileHolder.setWaitingForDownload()
                        chatActivity.onFileDownloadRequested(item)
                    }
                }
            }
        }
    }
    open protected fun bindMonkeyFileHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder) {
        val fileHolder = holder as MonkeyFileHolder
        val file = File(item.getFilePath())
        if(!file.exists() || file.length() < item.getFileSize()) {
            chatActivity.onFileDownloadRequested(item)
            fileHolder.showFileData(item.getMessageText(), "Descargando...")
            fileHolder.showFileIcon(item.getMessageText().substring(item.getMessageText().lastIndexOf(".")+1))
            fileHolder.setOnClickListener(null)
        }else {
            fileHolder.showFileData(item.getMessageText(), getTotalSizeFile(item.getFileSize()))
            fileHolder.showFileIcon(item.getMessageText().substring(item.getMessageText().lastIndexOf(".")+1))
            fileHolder.setOnClickListener(View.OnClickListener {
                val openFile = Intent(Intent.ACTION_VIEW)
                openFile.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                openFile.setDataAndType(Uri.fromFile(file), URLConnection.guessContentTypeFromName(item.getMessageText()))
                mContext.startActivity(openFile)
            })

        }
    }

    open protected fun bindMonkeyFileProcessingHolder(position: Int, item: MonkeyItem, holder: MonkeyHolder) {
        val fileHolder = holder as MonkeyFileHolder
        val file = File(item.getFilePath())
        fileHolder.showFileData(item.getMessageText(), getTotalSizeFile(item.getFileSize()))
        if(file.exists()) {
            fileHolder.showFileIcon(item.getMessageText().substring(item.getMessageText().lastIndexOf(".")+1))
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
        if (hasPermissionsToAccessFiles()) {
            if(!file.exists() || file.length() < item.getFileSize()) {//file doesn't exist, ask server
                chatActivity.onFileDownloadRequested(item)
                imageHolder.setOnClickListener(null)
            } else {// set file from SD card
                imageHolder.setDownloadedImage(file, chatActivity as Context)
                imageHolder.setOnClickListener(View.OnClickListener { imageListener?.onImageClicked(position, item) })
                val imageView = imageHolder.photoImageView
                if(imageView != null)
                    bindMessageLongClickListener(item, imageView)
            }
        } else {//Ask for permissions on click
            imageHolder.setEmptyImage()
            imageHolder.setOnClickListener(View.OnClickListener { requestPermissionToReadSDCard() })
            val imageView = imageHolder.photoImageView
            if(imageView != null)
                bindMessageLongClickListener(item, imageView)
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
        val playingAudio = voiceNotePlayer?.currentlyPlayingItem
        val longClickOptions = getMessageLongClickOptions(item)
        val playAction = object : AudioActions() {
                    override fun onActionClicked() {
                        super.onActionClicked()
                        voiceNotePlayer?.playVoiceNote(item)
                    }

                    override fun onActionLongClicked() {
                        super.onActionLongClicked()
                        if(longClickOptions?.isNotEmpty() ?: false)
                            MessageOptionsDialog(longClickOptions!!, item).show(mContext)
                    }
                }

        val pauseAction = object : AudioActions() {
                    override fun onActionClicked() {
                        super.onActionClicked()
                        voiceNotePlayer?.pauseVoiceNote()
                    }

                    override fun onActionLongClicked() {
                        super.onActionLongClicked()
                        if(longClickOptions?.isNotEmpty() ?: false)
                            MessageOptionsDialog(longClickOptions!!, item).show(mContext)
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
                get() = item.getMessageTimestamp() == voiceNotePlayer?.currentlyPlayingItem?.getMessageTimestamp()
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
                        voiceNotePlayer?.setPlaybackProgress(item, progress)
                }
            })
        } else { //Message is available for playback but not prepared in the MediaPlayer
            if(!item.isIncomingMessage())
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

    fun removeEndOfRecyclerView(){
        removeEndOfRecyclerView(false)
    }

    /**
     *removes the more messages view
     */
    protected fun removeEndOfRecyclerView(silent: Boolean){
        if(messages.actualSize == 0)
            return;

        val lastItem = messages.getItemAt(0)
        if(lastItem.getMessageType() == MonkeyItem.MonkeyItemType.MoreMessages.ordinal){
            messages.removeMessageAt(0)
            if(!silent)
                notifyItemRemoved(0)
        }
    }

    fun scrollWithOffset(newItemsCount: Int) {
    //Scroll only if position is not in the last position
        val manager = recyclerView!!.layoutManager as LinearLayoutManager
        if(messages.actualSize - newItemsCount > 0 && !isLastItemDisplaying(manager)) {
            notifyItemChanged(newItemsCount)
            manager.scrollToPositionWithOffset(newItemsCount,
                    mContext.resources.getDimension(R.dimen.scroll_offset).toInt())
        }
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

    fun takeAllMessages() : List<MonkeyItem>{

        return messages.filterNot {
            it-> it is EndItem
        }
        //removeEndOfRecyclerView(true)
        //return messagesList
    }


    fun getTotalSizeFile(totalBytes: Long) : String{
        if((totalBytes/1000) < 1000)
            return String.format("%.2f", (totalBytes/1000).toDouble())+" KB";
        else
            return String.format("%.2f", (totalBytes / 1000000).toDouble())+" MB";
    }



    fun removeItemFromRecycler(item: MonkeyItem, unsent: Boolean){
        val pos = messages.getItemPositionByTimestamp(item)
        if(pos > -1){
            messages.removeMessageAt(pos)
            notifyItemRemoved(pos)

            val recycler = recyclerView
            if(recycler != null){
                val msg = "Message deleted"
                SnackbarUtils.showUndoMessage(recycler = recycler, msg = msg,
                        undoAction = {
                            itemToDelete = null
                            messages.smoothlyAddNewItem(item)
                        },
                        callback = object : Snackbar.Callback() {
                            override fun onDismissed(snackbar: Snackbar?, event: Int) {
                                if (event != DISMISS_EVENT_ACTION) {
                                    val deleted = itemToDelete
                                    if (deleted != null && deleted == item) {
                                        chatActivity.onMessageRemoved(deleted, unsent)
                                        itemToDelete = null
                                    }
                                }
                            }
                })
                //need to wait until snackbar dismissed to leave
                itemToDelete = item
            }
        }
    }


    fun onRequestPermissionsResult(requestCode: Int, result: IntArray) {
        if (requestCode == REQUEST_WRITE_SD && itemThatNeedsPermissions != null &&
                result[0] == PackageManager.PERMISSION_GRANTED) {
            notifyDataSetChanged()
            chatActivity.onFileDownloadRequested(itemThatNeedsPermissions!!)
        } else if (requestCode == REQUEST_READ_SD && result[0] == PackageManager.PERMISSION_GRANTED)
            notifyDataSetChanged()
        itemThatNeedsPermissions = null

    }

    class MessageOptionsDialog(options: MutableList<OnMessageOptionClicked>,
                                    val item: MonkeyItem) : AbstractDialog<OnMessageOptionClicked>(options) {
        override fun executeCallback(selectedOption: OnMessageOptionClicked) {
            selectedOption.invoke(item)
        }
    }

    companion object {
        val REQUEST_WRITE_SD = 9003
        val REQUEST_READ_SD = 9004
    }

}
