package com.criptext.monkeykitui.conversation

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.conversation.holder.ConversationHolder
import com.criptext.monkeykitui.recycler.SlowRecyclerLoader
import com.criptext.monkeykitui.util.Utils
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.*

/**
 * Created by gesuwall on 8/11/16.
 */

open class MonkeyConversationsAdapter(val mContext: Context) : RecyclerView.Adapter<ConversationHolder>() {

    val conversationsList: ArrayList<MonkeyConversation>
    val mSelectableItemBg: Int

    private val conversationsActivity: ConversationsActivity
    get() = mContext as ConversationsActivity

    var hasReachedEnd : Boolean = true
        set(value) {
            if(!value && field != value) {
                conversationsList.add(MonkeyConversation.endItem())
                notifyItemInserted(conversationsList.size - 1)
                //Log.d("MonkeyConversationsAdapter", "End item added")
            }
            field = value
        }


    val dataLoader : SlowRecyclerLoader

    var loadingDelay: Long
        get() =  dataLoader.delayTime
        set(value) { dataLoader.delayTime = value }

    init {
        conversationsList = ArrayList<MonkeyConversation>()
        //get that clickable background
        val mTypedValue = TypedValue();
        mContext.theme.resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mSelectableItemBg = mTypedValue.resourceId
        mContext as? ConversationsActivity ?:
                throw IllegalArgumentException(
                        "The context of this MonkeConversationsAdapter must implement ConversationsActivity!")
        dataLoader = SlowRecyclerLoader(true, mContext)
    }

    override fun onViewAttachedToWindow(holder: ConversationHolder?) {
        super.onViewAttachedToWindow(holder)
        val endHolder = holder as? ConversationHolder.EndHolder
        if(endHolder != null) {
            //endHolder.setOnClickListener {  }
            dataLoader.delayNewBatch(conversationsList.size)
        }
    }

    override fun getItemCount() = conversationsList.size

    private fun getSentMessageCheckmark(status: MonkeyConversation.ConversationStatus): Int{
        return when(status){
            MonkeyConversation.ConversationStatus.deliveredMessage -> R.drawable.mk_checkmark_sent
            MonkeyConversation.ConversationStatus.sentMessageRead -> R.drawable.mk_checkmark_read
            else -> 0
        }
    }
    override fun onBindViewHolder(holder: ConversationHolder?, position: Int) {
        val conversation = conversationsList[position]
        if(holder != null && conversation.getStatus() >
                MonkeyConversation.ConversationStatus.moreConversations.ordinal){
            holder.setName(conversation.getName())
            holder.setSecondaryText(conversation.getSecondaryText())
            holder.setDate(Utils.getHoraVerdadera(conversation.getDatetime()))
            holder.setTotalNewMessages(conversation.getTotalNewMessages())
            holder.setAvatar(conversation.getAvatarFilePath(), conversation.isGroup())

            holder.itemView.setOnClickListener {
                conversationsActivity.onConversationClicked(conversation)
            }

            val holderType = getItemViewType(position)
            when(ConversationHolder.ViewTypes.values()[holderType]){
                ConversationHolder.ViewTypes.empty -> {
                    holder.setSecondaryText( mContext.getString(
                            if(conversation.isGroup()) R.string.mk_empty_group_text
                            else R.string.mk_empty_conversation_text))
                    holder.setSecondaryTextLeftDrawable(0)
                }
                ConversationHolder.ViewTypes.sentMessage ->{
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
        val conversation = conversationsList[position]
        return when (MonkeyConversation.ConversationStatus.values()[conversation.getStatus()]){
            MonkeyConversation.ConversationStatus.empty ->
                ConversationHolder.ViewTypes.empty.ordinal

            MonkeyConversation.ConversationStatus.receivedMessage ->
                if(conversation.getTotalNewMessages() > 0)
                    ConversationHolder.ViewTypes.newMessages.ordinal
                else
                    ConversationHolder.ViewTypes.receivedMessage.ordinal

            MonkeyConversation.ConversationStatus.sendingMessage,
            MonkeyConversation.ConversationStatus.deliveredMessage,
            MonkeyConversation.ConversationStatus.sentMessageRead ->
                ConversationHolder.ViewTypes.sentMessage.ordinal

            MonkeyConversation.ConversationStatus.moreConversations ->
                ConversationHolder.ViewTypes.moreConversations.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ConversationHolder? {
        val isLoadingView = viewType == ConversationHolder.ViewTypes.moreConversations.ordinal
        val mView: View
        if(isLoadingView){
            mView = LayoutInflater.from(mContext).inflate(R.layout.end_of_recycler_view, null)
            return ConversationHolder.EndHolder(mView)

        } else {
            mView = LayoutInflater.from(mContext).inflate(R.layout.item_mk_conversation, null)
            mView.setBackgroundResource(mSelectableItemBg)
            return ConversationHolder(mView, ConversationHolder.ViewTypes.values()[viewType])
        }
    }

    /**
     * adds a list of conversations to this adapter. If there were already any conversations, they
     * will be removed.
     * @param conversations a list of conversations to add. After calling this function, the adapter
     * will contain ONLY the conversations in this list.
     * @param hasReachedEnd false if there are no remaining Conversations to load, else display a
     * loading view when the user scrolls to the end
     */
    fun insertConversations(conversations: Collection<MonkeyConversation>, hasReachedEnd: Boolean){
        conversationsList.clear()
        conversationsList.addAll(conversations)
        notifyDataSetChanged()
        this.hasReachedEnd = hasReachedEnd
    }

    /**
     * adds a conversation to the top of the adapter's list. The changes are then notified to the UI
     * @param newConversation conversation to add
     */
    fun addNewConversation(newConversation: MonkeyConversation){
        conversationsList.add(0, newConversation)
        notifyItemInserted(0)
    }

    /**
     * adds a collection of conversations to the bottom of the adapter's list. The changes are then
     * notified to the UI
     * @param oldConversations conversations to add
     * @param hasReachedEnd false if there are no remaining Conversations to load, else display a
     * loading view when the user scrolls to the end
     */
    fun addOldConversations(oldConversations: Collection<MonkeyConversation>, hasReachedEnd: Boolean){
        val startPoint = conversationsList.size
        conversationsList.addAll(oldConversations)
        notifyItemRangeInserted(startPoint, oldConversations.size)
        this.hasReachedEnd = hasReachedEnd
    }

    


}