package com.criptext.monkeykitui.conversation

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
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
import com.criptext.monkeykitui.conversation.holder.ConversationTransaction
import com.criptext.monkeykitui.recycler.SlowRecyclerLoader
import com.criptext.monkeykitui.util.InsertionSort
import com.criptext.monkeykitui.util.Utils
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.*

/**
 * Created by gesuwall on 8/11/16.
 */

open class MonkeyConversationsAdapter(val mContext: Context) : RecyclerView.Adapter<ConversationHolder>() {

    private val conversationsList: ArrayList<MonkeyConversation>
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

    protected fun removeEndOfRecyclerView(){
        removeEndOfRecyclerView(false)
    }
    protected fun removeEndOfRecyclerView(silent: Boolean){
        if(conversationsList.isEmpty())
            return;

        val lastPosition = conversationsList.size - 1
        val lastItem = conversationsList[lastPosition]
        if(lastItem.getStatus() == MonkeyConversation.ConversationStatus.moreConversations.ordinal){
            conversationsList.remove(lastItem)
            if(!silent)
                notifyItemRemoved(lastPosition)
            hasReachedEnd = true
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
        removeEndOfRecyclerView()
        conversationsList.addAll(conversations)
        Collections.sort(conversationsList, Comparator { t1, t2 -> itemCmp(t1, t2) })
        notifyDataSetChanged()
        this.hasReachedEnd = hasReachedEnd
    }

    fun addNewConversation(newConversation: MonkeyConversation): Int{
        return addNewConversation(newConversation, silent = false)
    }

    /**
     * adds a conversation to the top of the adapter's list. The changes are then notified to the UI
     * @param newConversation conversation to add
     * @return the position at which the conversation was inserted. It should be zero, unless there is
     * a more recent conversation
     */
    private fun addNewConversation(newConversation: MonkeyConversation, silent: Boolean): Int{
        val actualPosition = InsertionSort(conversationsList, Comparator { t1, t2 ->  itemCmp(t1, t2) })
                .insertAtCorrectPosition(newConversation, insertAtEnd = false)
        if(!silent)
            notifyItemInserted(actualPosition)

        return actualPosition
    }

    private fun swapConversationPosition(movedConversation: MonkeyConversation, oldPosition: Int, recyclerView: RecyclerView){
        val newPosition = addNewConversation(movedConversation, silent = true)
        notifyItemMoved(oldPosition, newPosition)
        if(oldPosition == newPosition)
            notifyItemChanged(newPosition)
        else
            recyclerView.scrollToPosition(newPosition) //bug in android https://code.google.com/p/android/issues/detail?id=99047
    }

    /**
     * adds a collection of conversations to the bottom of the adapter's list. The changes are then
     * notified to the UI
     * @param oldConversations conversations to add
     * @param hasReachedEnd false if there are no remaining Conversations to load, else display a
     * loading view when the user scrolls to the end
     */
    fun addOldConversations(oldConversations: Collection<MonkeyConversation>, hasReachedEnd: Boolean,
                            recyclerView: RecyclerView){
        removeEndOfRecyclerView()
        if(oldConversations.size > 0) {
            val manager = recyclerView.layoutManager as LinearLayoutManager
            val firstNewIndex = conversationsList.size
            conversationsList.addAll(oldConversations)
            InsertionSort(conversationsList, Comparator { it1, it2 -> itemCmp(it1, it2) }, Math.max(1, firstNewIndex)).sort()
            notifyItemRangeInserted(firstNewIndex, oldConversations.size);
        }
        this.hasReachedEnd = hasReachedEnd
    }

    /**
     * updates a conversation in the recyclerView. calls notifyItemChanged with the updated conversation's
     * position.
     * @param updatedConversation the updated conversation. this object replaces the existing conversation
     * in the adapter.
     */
    fun updateConversation(conversation: MonkeyConversation, transaction: ConversationTransaction, recyclerView: RecyclerView){
        val position = getConversationPositionByTimestamp(conversation)
        if(position > -1){
            conversationsList.removeAt(position)
            transaction.updateConversation(conversation)
            swapConversationPosition(conversation, position, recyclerView)
        } else throw IllegalArgumentException("Conversation with ID: ${conversation.getId()} and " +
                "timestamp: ${conversation.getDatetime()} not found in adapter.")
    }

    /**
     * remove a conversation from the recyclerView, calls notifyItemRemoved with the removed conversation's
     * position
     * @param conversation the conversation to remove.
     */
    fun removeConversation(conversation: MonkeyConversation){
        val position = getConversationPositionByTimestamp(conversation)
        if(position > -1) {
            conversationsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getLastConversation(): MonkeyConversation? {
        if(conversationsList.size > 1) {
            val last = conversationsList[conversationsList.size - 1]
            if (last != null && last.getStatus() == MonkeyConversation.ConversationStatus.moreConversations.ordinal)
                return conversationsList[conversationsList.size - 2]
            else
                return last
        } else if(conversationsList.size == 1
                && conversationsList.last().getStatus() != MonkeyConversation.ConversationStatus.moreConversations.ordinal)
            return conversationsList.last()

        return null
    }

    fun takeAllConversations(): Collection<MonkeyConversation>{
        removeEndOfRecyclerView(true)
        return conversationsList
    }
    /**
     * Finds the adapter position by the MonkeyConversation's timestamp.
     * @param targetId the timestamp of the MonkeyConversation whose adapter position will be searched. This
     * timestamp must belong to an existing MonkeyConversation in this adapter.
     * @return The adapter position of the MonkeyItem. If the item was not found returns
     * the negated expected position.
     */
    fun getConversationPositionByTimestamp(item: MonkeyConversation) = conversationsList.binarySearch(item,
            Comparator { t1, t2 -> itemCmp(t1, t2) })


    protected fun itemCmp(t1: MonkeyConversation, t2: MonkeyConversation) =
        if(t1.getDatetime() > t2.getDatetime()) {
                -1
            }else if (t1.getDatetime() < t2.getDatetime()) {
                1
            } else t1.getId().compareTo(t2.getId())

    /**
     * Looks for a monkey conversation with a specified Id, starting by the most recent ones.
     * @return the message with the requested Id. returns null if the conversation does not exist
     */
    fun findConversationItemById(id: String) = conversationsList.find { it.getId() == id }

}