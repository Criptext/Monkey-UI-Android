package com.criptext.monkeykitui.conversation

import android.util.Log
import com.criptext.monkeykitui.conversation.holder.ConversationListUI
import com.criptext.monkeykitui.conversation.holder.ConversationTransaction
import com.criptext.monkeykitui.util.InsertionSort
import java.util.*

/**
 * List that holds the conversations of a chat application. It is always ordered using the timestamps
 * of the conversations, every insert adds the item at the correct position. Attempts to insert
 * duplicates are ignored.
 * Created by Gabriel on 11/28/16.
 */

class ConversationsList() : AbstractList<MonkeyConversation>() {
    private val list: ArrayList<MonkeyConversation>
    private val set: HashSet<String>
    var listUI: ConversationListUI?

    var hasReachedEnd : Boolean = true
        set(value) {
            if(!value && field != value) {
                list.add(MonkeyConversation.endItem())
                listUI?.notifyConversationInserted(list.size - 1)
            } else if(value && field != value) {
                listUI?.removeLoadingView()
            }
            field = value
        }

    init {
        list = ArrayList()
        set = HashSet()
        listUI = null
    }

    constructor(conversations: Collection<MonkeyConversation>): this() {
        list.addAll(conversations)
    }

    override fun get(index: Int) = list[index]

    override fun iterator() = list.iterator()

    override val size: Int
        get() = list.size


    private fun assertConversationIsNotEndItem(newConversation: MonkeyConversation){
        if(newConversation.getStatus() == MonkeyConversation.ConversationStatus.moreConversations.ordinal) {
            val invalidStatus = newConversation.getStatus()
            throw IllegalArgumentException("New conversations can never have status = $invalidStatus\n" +
                    "It is currently used by Conversation status = ${MonkeyConversation.ConversationStatus.values()[invalidStatus]}\n" +
                    "Please check the docs for valid status values. conversation name: ${newConversation.getName()}. id: ${newConversation.getConvId()}")
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
        list.clear()
        set.clear()
        listUI?.removeLoadingView()
        //sanity check
        for(conv in conversations) {
            assertConversationIsNotEndItem(conv)
            set.add(conv.getConvId())
        }

        list.addAll(conversations)
        Collections.sort(list, MonkeyConversation.defaultComparator)
        listUI?.refresh()
        this.hasReachedEnd = hasReachedEnd
    }

    /**
     * Adds a conversation to the top of the adapter's list.
     * @return the position at which the conversation was inserted. It should be zero, unless there is
     * a more recent conversation. If the conversation is already present in the adapter, it returns
     * -1.
     */
    fun addNewConversation(newConversation: MonkeyConversation): Int{
        if(set.contains(newConversation.getConvId()))
            return -1;
        else
            return addNewConversation(newConversation, silent = false)
    }

    /**
     * adds a conversation to the top of the list. You should make sure that the conversation
     * isn't already loaded in the adapter before calling this method.
     * @param newConversation conversation to add
     * @param silent UI is updated right after adding the conversation only if this parameter is false.
     * @return the position at which the conversation was inserted. It should be zero, unless there is
     * a more recent conversation
     */
    private fun addNewConversation(newConversation: MonkeyConversation, silent: Boolean): Int{
        assertConversationIsNotEndItem(newConversation)
        val actualPosition = InsertionSort(list, MonkeyConversation.defaultComparator)
                .insertAtCorrectPosition(newConversation, insertAtEnd = false)
        if(!silent)
            listUI?.notifyConversationInserted(actualPosition)

        set.add(newConversation.getConvId())
        return actualPosition
    }

    private fun swapConversationPosition(movedConversation: MonkeyConversation, oldPosition: Int){
        val newPosition = addNewConversation(movedConversation, silent = true)
        listUI?.notifyConversationMoved(oldPosition, newPosition)
        if(oldPosition == newPosition)
            listUI?.notifyConversationChanged(newPosition)
        else
            listUI?.scrollToPosition(newPosition) //bug in android https://code.google.com/p/android/issues/detail?id=99047
    }

    fun removeConversationAt(position: Int): MonkeyConversation?  =
        if (position >= 0 && position < size) {
            val item = list[position]
            list.removeAt(position)
            set.remove(item.getConvId())
            listUI?.notifyConversationRemoved(position)
            item
        } else null


    /**
     * adds a collection of conversations to the bottom of the adapter's list. The changes are then
     * notified to the UI
     * @param oldConversations conversations to add
     * @param hasReachedEnd false if there are no remaining Conversations to load, else display a
     * loading view when the user scrolls to the end
     */
    fun addOldConversations(oldConversations: Collection<MonkeyConversation>, hasReachedEnd: Boolean){
        listUI?.removeLoadingView()
        val filteredConversations = oldConversations.filterNot { it -> set.contains(it.getConvId()) }
        if(oldConversations.size > 0) {

            //sanity check
            for(conv in filteredConversations) {
                assertConversationIsNotEndItem(conv)
                set.add(conv.getConvId())
            }

            val firstNewIndex = list.size
            list.addAll(filteredConversations)
            InsertionSort(list, MonkeyConversation.defaultComparator, Math.max(1, firstNewIndex)).sort()
            listUI?.notifyConversationRangeInserted(firstNewIndex, filteredConversations.size);
        }
        this.hasReachedEnd = hasReachedEnd
    }

    /**
     * updates a conversation in the list, using a transaction.
     * @param target conversation to find and update. This object only needs to have valid id and timestamp.
     * @param transaction the transaction object that will update the conversation once it is found
     * in the adapter
     */
    fun updateConversation(target: MonkeyConversation, transaction: ConversationTransaction){
        val position = getConversationPositionByTimestamp(target)
        if(position > -1){
            val conversation = list.removeAt(position)
            transaction.updateConversation(conversation)
            swapConversationPosition(conversation, position)
        } else Log.e("ConversationsAdapter", "Conversation with ID: ${target.getConvId()} and " +
                "timestamp: ${target.getDatetime()} not found in adapter.")
    }

    /**
     * Calls notifyItemChanged with the updated conversation's
     * position.
     * @param  conversation to find and update.
     */
    fun updateConversation(conversation: MonkeyConversation){
        val position = getConversationPositionByTimestamp(conversation)
        if(position > -1) {
            listUI?.notifyConversationChanged(position)
        } else Log.e("ConversationsAdapter", "Conversation with ID: ${conversation.getConvId()} and " +
                "timestamp: ${conversation.getDatetime()} not found in adapter. Can't notify item changed.")
    }

    fun updateConversations(updateSet: Set<Map.Entry<String, ConversationTransaction>>) {
        val iterator = updateSet.iterator()
        while(iterator.hasNext()) {
            val entry = iterator.next()
            val transaction = entry.value
            if(set.contains(entry.key)) {
                val conversationPos = list.indexOfFirst { conv -> conv.getConvId() == entry.key }
                if (conversationPos == -1)
                    Log.e("ConversationsAdapter", "Update failed. Conversation with ID: ${entry.key}" +
                            "was expected to be in adapter.")
                else {
                    val conversation = list.removeAt(conversationPos)
                    transaction.updateConversation(conversation)
                    addNewConversation(conversation, true)
                }
            }
        }
        listUI?.refresh()
    }

    fun getLastConversation(): MonkeyConversation? {
        val listSize = size
        if(listSize > 1) {
            val last = list[listSize - 1]
            if (last.getStatus() == MonkeyConversation.ConversationStatus.moreConversations.ordinal)
                return list[listSize - 2]
            else
                return last
        } else if(listSize == 1) {
            val last = list[0]
            if(last.getStatus() != MonkeyConversation.ConversationStatus.moreConversations.ordinal)
                return last
        }

        return null
    }

    /**
     * Finds the list position by the MonkeyConversation's timestamp.
     * @param targetId the timestamp of the MonkeyConversation whose adapter position will be searched. This
     * timestamp must belong to an existing MonkeyConversation in this adapter.
     * @return The adapter position of the MonkeyItem. If the item was not found returns
     * the negated expected position.
     */
    fun getConversationPositionByTimestamp(item: MonkeyConversation) = list.binarySearch(item,
            MonkeyConversation.defaultComparator)

    /**
     * Looks for a monkey conversation with a specified Id, starting by the most recent ones.
     * @return the message with the requested Id. returns null if the conversation does not exist
     */
    fun findConversationById(id: String) = list.find { it.getConvId() == id }
}