package com.criptext.monkeykitui

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.criptext.monkeykitui.conversation.ConversationsActivity
import com.criptext.monkeykitui.conversation.MonkeyConversation
import com.criptext.monkeykitui.conversation.MonkeyConversationsAdapter
import com.criptext.monkeykitui.conversation.dialog.ConversationOptionsDialog
import com.criptext.monkeykitui.conversation.dialog.OnConversationOptionClicked
import com.criptext.monkeykitui.conversation.holder.ConversationTransaction
import com.criptext.monkeykitui.dialog.DialogOption
import java.util.*

/**
 * Created by gesuwall on 8/11/16.
 */

open class MonkeyConversationsFragment: Fragment(){

    open val conversationsLayout: Int
        get() = R.layout.recycler_layout
    lateinit var recyclerView: RecyclerView
    protected var conversationsAdapter: MonkeyConversationsAdapter? = null


    /**
     * finds the RecyclerView in the view layout of the current fragment, ands sets an appropiate
     * LayoutManager.
     *
     * The default implementation sets a LinearLayoutManager with the stackFromEnd property set as true
     * @return the RecyclerView object of this fragment ready to set an adapter with data.
     */
    open protected fun initRecyclerView(view: View): RecyclerView {
        //RecyclerView must be inside a container http://stackoverflow.com/a/32695034/5207721
        val recycler = view.findViewById(R.id.recycler) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        recycler.layoutManager = linearLayoutManager;
        return recycler
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(conversationsLayout, null)
        recyclerView = initRecyclerView(view)
        conversationsAdapter = MonkeyConversationsAdapter(activity)
        conversationsAdapter!!.recyclerView = recyclerView
        recyclerView.adapter = conversationsAdapter
        return view
    }

    override fun onStart() {
        super.onStart()
        (activity as ConversationsActivity).requestConversations()
    }

    override fun onAttach(activity: Activity?) {
        val conversationsActivty = activity as? ConversationsActivity
        conversationsActivty?.setConversationsFragment(this)
        super.onAttach(activity)
    }

    override fun onDetach() {
        (activity as ConversationsActivity).setConversationsFragment(null)
        super.onDetach()
    }

    override fun onStop() {
        super.onStop()

        val pendingConversationToDelete = conversationsAdapter?.conversationToDelete
        if(pendingConversationToDelete != null)
            (activity as ConversationsActivity).onConversationDeleted(pendingConversationToDelete)

        val adapter = conversationsAdapter
        if(adapter != null)
            (activity as ConversationsActivity).retainConversations(adapter.takeAllConversations())

        conversationsAdapter?.conversationToDelete = null
    }

    override fun onDestroy() {
        super.onDestroy()
        val adapter = conversationsAdapter
        if(adapter != null)
            (activity as ConversationsActivity).retainConversations(adapter.takeAllConversations())

    }

    fun takeAllConversations(): Collection<MonkeyConversation> = conversationsAdapter?.takeAllConversations()
                            ?: listOf()
    /**
     * adds a list of conversations to this adapter. If there were already any conversations, they
     * will be removed.
     * @param conversations a list of conversations to add. After calling this function, the adapter
     * will contain ONLY the conversations in this list.
     * @param hasReachedEnd false if there are no remaining Conversations to load, else display a
     * loading view when the user scrolls to the end
     */
    fun insertConversations(conversations: Collection<MonkeyConversation>, hasReachedEnd: Boolean){
        conversationsAdapter?.insertConversations(conversations, hasReachedEnd)
    }


    /**
     * Uses binary search to find a conversation with a given identifier and timestamp.
     * @param id unique identifier of the conversation to search
     * @param timestamp long with the timestamp of the conversation to search
     * @return A conversation in the adapter that matches the id and timestamp. null if it does not exists
    fun findConversation(id: String, timestamp: Long){
        conversationsAdapter.getConversationPositionByTimestamp(object: MonkeyConversation {
            override fun getGroupMembers() = null
            override fun getConvId() = id
            override fun getDatetime() = timestamp
            override fun getAvatarFilePath() = null
            override fun getName() = ""
            override fun getSecondaryText() = ""
            override fun getStatus() = 0
            override fun getTotalNewMessages() = 0
            override fun isGroup() = false
        })
    }
     */

    /**
     * Looks for a conversation with a given id using linear search. Search start with the latest
     * conversations
     * @param id A string with an unique identifier of the conversation to search
     * @result the conversation with the matching identifier. null if it does not exist
     */
    fun findConversationById(id: String): MonkeyConversation? {
        return conversationsAdapter?.findConversationItemById(id)
    }

    /**
     * Updates the view of an existing conversation. Uses binary search to find the conversation's
     * position in the adapter.
     * @param conversationItem the conversation to update. it must not be updated yet
     * @param transaction a ConversationTransaction object that updates the conversation item
     */
    fun updateConversation(conversationItem: MonkeyConversation, transaction: ConversationTransaction){
        conversationsAdapter?.updateConversation(conversationItem, transaction)
    }

    fun updateConversation(conversationItem: MonkeyConversation){
        conversationsAdapter?.updateConversation(conversationItem)
    }

    fun updateConversations(set: Set<Map.Entry<MonkeyConversation, ConversationTransaction>>) {
        conversationsAdapter?.updateConversations(set)
    }

    /**
     * adds a collection of conversations to the bottom of the adapter's list. The changes are then
     * notified to the UI
     * @param conversations conversations to add
     * @param hasReachedEnd false if there are no remaining Conversations to load, else display a
     * loading view when the user scrolls to the end
     */
    fun addOldConversations(conversations: Collection<MonkeyConversation>, hasReachedEnd: Boolean){
        conversationsAdapter?.addOldConversations(conversations, hasReachedEnd)
    }
    /**
     * adds a conversation to the top of the recycler view.
     * @param newConversation conversation to add
     */
    fun addNewConversation(newConversation: MonkeyConversation, scrollToFirst: Boolean){
        conversationsAdapter?.addNewConversation(newConversation)
        if(scrollToFirst)
            recyclerView.smoothScrollToPosition(0)
    }

    fun getLastConversation(): MonkeyConversation? = conversationsAdapter?.getLastConversation()
}
