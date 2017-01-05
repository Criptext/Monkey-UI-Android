package com.criptext.monkeykitui.conversation

import android.content.Context
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.Callback
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.cav.EmojiHandler
import com.criptext.monkeykitui.conversation.dialog.ConversationOptionsDialog
import com.criptext.monkeykitui.conversation.dialog.OnConversationOptionClicked
import com.criptext.monkeykitui.conversation.holder.ConversationHolder
import com.criptext.monkeykitui.conversation.holder.ConversationListUI
import com.criptext.monkeykitui.conversation.holder.ConversationTransaction
import com.criptext.monkeykitui.recycler.SlowRecyclerLoader
import com.criptext.monkeykitui.util.InsertionSort
import com.criptext.monkeykitui.util.SnackbarUtils
import com.criptext.monkeykitui.util.Utils
import java.util.*

/**
 * Created by gesuwall on 8/11/16.
 */

open class MonkeyConversationsAdapter(val mContext: Context) : RecyclerView.Adapter<ConversationHolder>() {

    var conversations: ConversationsList

    val mSelectableItemBg: Int

    private val conversationsActivity: ConversationsActivity
    get() = mContext as ConversationsActivity

    var recyclerView : RecyclerView? = null

    val dataLoader : SlowRecyclerLoader

    var maxTextWidth: Int? = null

    var conversationToDelete: MonkeyConversation? = null
    set(value) {
        val oldValue = field
        if(oldValue != null && value!=null){
            conversationsActivity.onConversationDeleted(oldValue)
        }
        field = value
    }

    val conversationOptions: MutableList<OnConversationOptionClicked>
    val groupOptions: MutableList<OnConversationOptionClicked>
    var onConversationLongClicked: OnConversationLongClicked

    init {
        conversations = ConversationsList()
        //get that clickable background
        val mTypedValue = TypedValue();
        mContext.theme.resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mSelectableItemBg = mTypedValue.resourceId
        mContext as? ConversationsActivity ?:
                throw IllegalArgumentException(
                        "The context of this MonkeConversationsAdapter must implement ConversationsActivity!")
        dataLoader = SlowRecyclerLoader(null, mContext)

        conversationOptions = mutableListOf(
            object: OnConversationOptionClicked(mContext.getString(R.string.mk_delete_conversation)){
                override fun invoke(conv: MonkeyConversation) {
                    removeConversationFromRecycler(conv)
                }
            })

        groupOptions = mutableListOf(
            object: OnConversationOptionClicked(mContext.getString(R.string.mk_exit_group)) {
                override fun invoke(conv: MonkeyConversation) {
                    removeConversationFromRecycler(conv)
                }
            })

        onConversationLongClicked = object : OnConversationLongClicked {
            override fun invoke(p1: MonkeyConversation) {
                val options = if(p1.isGroup()) groupOptions else conversationOptions
                val dialog = ConversationOptionsDialog(options, p1)
                dialog.show(mContext)
            }

        }
    }

    override fun onViewAttachedToWindow(holder: ConversationHolder?) {
        super.onViewAttachedToWindow(holder)
        val endHolder = holder as? ConversationHolder.EndHolder
        if(endHolder != null) {
            //endHolder.setOnClickListener {  }
            dataLoader.delayNewBatch(conversations.size)
        }
    }


    override fun getItemCount() = conversations.size

    private fun getSentMessageCheckmark(status: MonkeyConversation.ConversationStatus): Int{
        return when(status){
            MonkeyConversation.ConversationStatus.deliveredMessage -> R.drawable.mk_checkmark_sent
            MonkeyConversation.ConversationStatus.sentMessageRead -> R.drawable.mk_checkmark_read
            MonkeyConversation.ConversationStatus.sendingMessage -> R.drawable.ic_clock
            else -> 0
        }
    }

    override fun onBindViewHolder(holder: ConversationHolder?, position: Int) {
        val conversation = conversations[position]
        if(holder != null && conversation.getStatus() ==
                MonkeyConversation.ConversationStatus.moreConversations.ordinal)
            (holder as ConversationHolder.EndHolder).adjustHeight(matchParentHeight = conversations.size == 1)
        else if (holder != null && conversation.getStatus() >
                MonkeyConversation.ConversationStatus.moreConversations.ordinal){
            holder.setName(conversation.getName())
            holder.setSecondaryText(conversation.getSecondaryText())
            holder.setDate(Utils.getFormattedDate(conversation.getDatetime(), mContext))
            holder.setTotalNewMessages(conversation.getTotalNewMessages())
            holder.setAvatar(conversation.getAvatarFilePath(), conversation.isGroup())

            holder.itemView.setOnClickListener {
                conversationsActivity.onConversationClicked(conversation)
            }

            holder.itemView.setOnLongClickListener({
                onConversationLongClicked.invoke(conversation)
                true
            })

            val holderType = getItemViewType(position)
            when(ConversationHolder.ViewTypes.values()[holderType]){
                ConversationHolder.ViewTypes.empty -> {
                    holder.setSecondaryText( mContext.getString(
                            if(conversation.isGroup()) R.string.mk_empty_group_text
                            else R.string.mk_empty_conversation_text))
                    holder.setSecondaryTextLeftDrawable(0)
                }
                ConversationHolder.ViewTypes.sentMessage,
                ConversationHolder.ViewTypes.sendingMessage ->{
                    holder.setSecondaryTextLeftDrawable(getSentMessageCheckmark(
                            MonkeyConversation.ConversationStatus.values()[conversation.getStatus()]))
                }

                ConversationHolder.ViewTypes.newMessages -> {
                    holder.setTotalNewMessages(conversation.getTotalNewMessages())
                }

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val conversation = conversations[position]
        return when (MonkeyConversation.ConversationStatus.values()[conversation.getStatus()]){
            MonkeyConversation.ConversationStatus.empty ->
                ConversationHolder.ViewTypes.empty.ordinal

            MonkeyConversation.ConversationStatus.receivedMessage ->
                if(conversation.getTotalNewMessages() > 0)
                    ConversationHolder.ViewTypes.newMessages.ordinal
                else
                    ConversationHolder.ViewTypes.receivedMessage.ordinal

            MonkeyConversation.ConversationStatus.sendingMessage ->
                ConversationHolder.ViewTypes.sendingMessage.ordinal

            MonkeyConversation.ConversationStatus.deliveredMessage,
            MonkeyConversation.ConversationStatus.sentMessageRead ->
                ConversationHolder.ViewTypes.sentMessage.ordinal

            MonkeyConversation.ConversationStatus.moreConversations ->
                ConversationHolder.ViewTypes.moreConversations.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ConversationHolder? {
        if(maxTextWidth == null)
            maxTextWidth = parent!!.width - mContext.resources.getDimension(R.dimen.mk_avatar_size).toInt() * 11 / 4

        val isLoadingView = viewType == ConversationHolder.ViewTypes.moreConversations.ordinal
        val mView: View
        if(isLoadingView){
            mView = LayoutInflater.from(mContext).inflate(R.layout.end_of_recycler_view, null)
            return ConversationHolder.EndHolder(mView)

        } else {
            mView = LayoutInflater.from(mContext).inflate(R.layout.item_mk_conversation, null)
            mView.setBackgroundResource(mSelectableItemBg)
            return ConversationHolder(mView, ConversationHolder.ViewTypes.values()[viewType], maxTextWidth!!)
        }
    }

    fun removeEndOfRecyclerView(){
        removeEndOfRecyclerView(false)
    }
    fun removeEndOfRecyclerView(silent: Boolean){
        if(conversations.isEmpty())
            return;

        val lastPosition = conversations.size - 1
        val lastItem = conversations[lastPosition]
        if(lastItem.getStatus() == MonkeyConversation.ConversationStatus.moreConversations.ordinal){
            conversations.removeConversationAt(lastPosition)
            if(!silent)
                notifyItemRemoved(lastPosition)
            conversations.hasReachedEnd = true
        }

    }

    /**
     * Removes a conversation from the recyclerview, animating the removal and displaying a snackbar
     * with an undo action. If the conversation is a group, a OnAttachStateChange listener is added
     * so that when the snackbar is removed from the view, the onConversationDeleted callback of
     * ConversationsActivity is called. the group is temporarily stored in the groupToExit attribute
     * so that in case that the listener is never called, we still have the reference to the group
     * that must be exited. MonkeyConversationsFragment should check the groupToExit variable on stop
     * to make sure that the user leaves it.
     * @param conversation the conversation to remove
     */
    private fun removeConversationFromRecycler(conversation: MonkeyConversation){
        val pos = conversations.getConversationPositionByTimestamp(conversation)
        if(pos > -1){
            conversations.removeConversationAt(pos)
            val recycler = recyclerView
            if(recycler != null){
                val name = conversation.getName()
                val msg = if(conversation.isGroup()) "${mContext.getString(R.string.mk_exit_group_msg)} \"$name\""
                        else "${mContext.getString(R.string.mk_delete_conversation_msg)} $name"
                SnackbarUtils.showUndoMessage(recycler = recycler, msg = EmojiHandler.decodeJava(EmojiHandler.decodeJava(msg)),
                    undoAction = {
                        conversationToDelete = null
                        conversations.addNewConversation(conversation)
                    },
                    callback = object  : Callback() {
                        override fun onDismissed(snackbar: Snackbar?, event: Int) {
                            if (event  != DISMISS_EVENT_ACTION) {
                                val deleted = conversationToDelete
                                if(deleted != null && deleted == conversation){
                                    conversationsActivity.onConversationDeleted(deleted)
                                    conversationToDelete = null
                                }
                            }
                        }
                })
                //need to wait until snackbar dismissed to leave
                conversationToDelete = conversation
            }
        }
    }
}