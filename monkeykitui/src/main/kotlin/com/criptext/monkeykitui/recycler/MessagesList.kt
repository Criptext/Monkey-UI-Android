package com.criptext.monkeykitui.recycler

import android.util.Log
import com.criptext.monkeykitui.recycler.holders.MessageListUI
import com.criptext.monkeykitui.util.InsertionSort
import java.util.*

/**
 * Created by gesuwall on 12/28/16.
 */
class MessagesList(val conversationId: String) : AbstractList<MonkeyItem>() {
    private val list: ArrayList<MonkeyItem>
    private val set: HashSet<String>
    var messageListUI: MessageListUI?

    var hasReachedEnd : Boolean = true
        set(value) {
            if(!value && (list.isEmpty() || list[0] !is EndItem)) {
                list.add(0, EndItem())
                messageListUI?.notifyItemInserted(0)
            }
            field = value
        }

    init {
        list = ArrayList()
        set = HashSet()
        messageListUI = null
    }

    override val size: Int
        get() = list.size

    override fun get(index: Int) = if(!list.isEmpty() && list[0] is EndItem) list[index + 1] else list[index]

    fun getItemAt(index: Int) = list[index]

    fun removeMessageAt(index: Int) {
        val item = list.removeAt(index)
        set.remove(item.getMessageId())
    }

    fun existMessage(item: MonkeyItem): Boolean{
        if(set.contains(item.getMessageId()))
            return true
        else if(!item.isIncomingMessage()){
            val oldId = item.getOldMessageId()
            if(oldId != null && set.contains(oldId))
                return true
        }
        return false
    }

    /**
     * Iterate array and create a new array without duplicates
     */
    fun removeIfExist(messages: List<MonkeyItem>): ArrayList<MonkeyItem>{
        val withoutDuplicates: ArrayList<MonkeyItem> = ArrayList()
        messages.map {
            if(!existMessage(it)) {
                withoutDuplicates.add(it)
            }
        }
        return withoutDuplicates
    }

    /**
     * Adds a group of MonkeyItems to the beginning of the list. This should be only used for showing
     * older messages as the user scrolls up.
     * @param newData the list of MonkeyItems to add
     * @param reachedEnd boolean that indicates whether there are still more items available. If there are
     * then when the user scrolls to the beginning of the list, the adapter should attempt to load the
     * remaining items and show a view that tells the user that it is loading messages.
     */
    fun addOldMessages(newData : Collection<MonkeyItem>, reachedEnd: Boolean){
        messageListUI?.removeLoadingView()
        val filteredData = removeIfExist(ArrayList(newData))
        if(filteredData.size > 0) {
            list.addAll(0, filteredData)
            val lastNewIndex = filteredData.size - 1
            InsertionSort(list, MonkeyItem.defaultComparator, lastNewIndex).sortBackwards()
            messageListUI?.notifyItemRangeInserted(0, filteredData.size)
            messageListUI?.scrollWithOffset(filteredData.size)

            for(item: MonkeyItem in filteredData){
                set.add(item.getMessageId())
            }
        }

        hasReachedEnd = reachedEnd
    }
/**
     * Finds the adapter position by the MonkeyItem's timestamp.
     * @param targetId the timestamp of the MonkeyItem whose adapter position will be searched. This
     * timestamp must belong to an existing MonkeyItem in this adapter.
     * @return The adapter position of the MonkeyItem. If the item was not found returns
     * the negated expected position.
     */
    fun getItemPositionByTimestamp(item: MonkeyItem) = MonkeyItem.findItemPositionInList(item, list)

    /**
     * Finds the adapter position by the MonkeyItem's id.
     * @param targetId the id of the MonkeyItem whose adapter position will be searched. This
     * timestamp must belong to an existing MonkeyItem in this adapter.
     * @return The adapter position of the MonkeyItem. If the item was not found returns -1
     */
    fun getLastItemPositionById(targetId: String) = MonkeyItem.findLastPositionById(targetId, list)

    /**
     * finds a message using the order timestamp and the ID with a binary search algorithm.
     * @param searchItem a monkeyItem containing the requested item's ID and order timestamp.
     * @return the requested item. Null if the item was not found.
     */
    fun getItemByTimestamp(searchItem: MonkeyItem): MonkeyItem?{
        val position = getItemPositionByTimestamp(searchItem)
        if(position > -1)
            return list[position]

        return null
    }

    /**
     * finds a message using the order timestamp and the ID with a binary search algorithm, then
     * updates it using a MonkeyItemTransaction object.
     * @param searchItem a monkeyItem containing the requested item's ID and order timestamp.
     * @return the requested item. Null if the item was not found.
     */
    fun updateMessage(searchItem: MonkeyItem, transaction: MonkeyItemTransaction){
        val position = getItemPositionByTimestamp(searchItem)
        if(position > -1) {
            val old = list[position]
            val new = transaction.invoke(old)
            list.add(position, new)
            list.removeAt(position + 1)

            set.remove(old.getMessageId())
            set.add(new.getMessageId())

            if(new.getDeliveryStatus() != old.getDeliveryStatus())
                messageListUI?.notifyItemChanged(position)
            else
                messageListUI?.rebindMonkeyItem(new)
        }
    }

    fun updateMessage(messageId: String, messageTimestamp: Long, transaction: MonkeyItemTransaction){
        val searchItem = object: MonkeyItem {
            override fun getAudioDuration() = 0L
            override fun getDeliveryStatus() = MonkeyItem.DeliveryStatus.sending
            override fun getSenderId() = ""
            override fun getConversationId() = ""
            override fun getFileSize() = 0L
            override fun getFilePath() = ""
            override fun getMessageId() = messageId
            override fun getMessageText() = ""
            override fun getMessageTimestamp() = messageTimestamp
            override fun getMessageTimestampOrder() = messageTimestamp
            override fun getMessageType() = 0
            override fun getOldMessageId() = ""
            override fun getPlaceholderFilePath() = ""
            override fun isIncomingMessage() = true
        }
        updateMessage(searchItem, transaction)
    }

    fun removeItemById(id: String){
        val pos = getLastItemPositionById(id)
        if(pos > -1){
            removeMessageAt(pos)
            messageListUI?.notifyItemRemoved(pos)
        }
    }

        /**
     * Adds a new item to the RecyclerView with a smooth scrolling animation. The scrolling animation
     * is only used in any of these 2 conditions:
     * - The last messages are visible and the new message is added at the last position
     * - the message was sent by the user.
     * @param item MonkeyItem to add. It will be added at the end of the messagesList, so it should
     * have a higher timestamp than the other messages.
     * @recylerview The recyclerView object that displays the messages.
     */
    fun smoothlyAddNewItem(item : MonkeyItem){
        if(!existMessage(item)) {
            //make sure it goes to the right position!
            var newPos = InsertionSort(list, MonkeyItem.defaultComparator)
                    .insertAtCorrectPosition(item, insertAtEnd = true)
            set.add(item.getMessageId())

            val listUI = messageListUI
            if (listUI != null) {
                val last = listUI.findLastVisibleItemPosition()
                listUI.notifyItemInserted(newPos)
                //Only scroll if the latest messages are visible and new message goes right next
                //OR... message was sent by the user.
                val latestMessagesAreVisible = last >= list.size - 2
                val newMessageIsLatest = newPos == (list.size - 1)
                if ((newMessageIsLatest && latestMessagesAreVisible) || !item.isIncomingMessage())
                    listUI.scrollToPosition(list.size - 1)
            }
        }
    }

    fun getLastItem(): MonkeyItem? = list.lastOrNull()

    fun getFirstItem(): MonkeyItem?{
        if(list.firstOrNull() is EndItem) {
            return list.getOrNull(1)
        }
        else {
            return list.firstOrNull()
        }
    }

    fun insertMessages(messages: List<MonkeyItem>, hasReachedEnd: Boolean) {
        if (list.isNotEmpty())
            throw IllegalStateException("Can't insert messages, list is not empty")
        addOldMessages(messages, hasReachedEnd)
    }

    fun smoothlyAddNewItems(newData : Collection<MonkeyItem>){
        val filteredData = removeIfExist(ArrayList(newData))
        if(filteredData.size > 0) {
            val firstNewIndex = list.size
            list.addAll(filteredData)
            InsertionSort(list, MonkeyItem.defaultComparator, Math.max(1, firstNewIndex)).sort()
            for(item: MonkeyItem in filteredData){
                set.add(item.getMessageId())
            }

            val listUI = messageListUI
            if (listUI != null) {
                listUI.notifyItemRangeInserted(firstNewIndex, filteredData.size);
                val last = listUI.findLastVisibleItemPosition()
                //Only scroll if this is the latest message
                if (firstNewIndex == (list.size - 1) && last >= list.size - 2) {
                    listUI.scrollToPosition(list.size - 1);
                }
            }
        }
    }

    /**
     * removes all messages from the adapter and clears the RecyclerView
     */
    fun removeAllMessages(){
        val totalMessages = list.size
        list.clear()
        messageListUI?.notifyItemRangeRemoved(0, totalMessages)
    }



    /**
     * Looks for a monkey item with a specified Id, starting by the most recent ones.
     * @return the message with the requested Id. returns null if the message does not exist
     */
    fun findMonkeyItemById(id: String) = list.findLast { it.getMessageId() == id }

}
