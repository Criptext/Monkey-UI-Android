package com.criptext.monkeykitui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import com.criptext.monkeykitui.conversation.ConversationsActivity
import com.criptext.monkeykitui.conversation.ConversationsList
import com.criptext.monkeykitui.conversation.MonkeyConversation
import com.criptext.monkeykitui.conversation.MonkeyConversationsAdapter
import com.criptext.monkeykitui.conversation.dialog.ConversationOptionsDialog
import com.criptext.monkeykitui.conversation.dialog.OnConversationOptionClicked
import com.criptext.monkeykitui.conversation.holder.ConversationListUI
import com.criptext.monkeykitui.conversation.holder.ConversationTransaction
import com.criptext.monkeykitui.dialog.DialogOption
import java.util.*

/**
 * Created by gesuwall on 8/11/16.
 */

open class MonkeyConversationsFragment: Fragment(), ConversationListUI {

    open val conversationsLayout: Int
        get() = R.layout.recycler_layout
    var recyclerView: RecyclerView? = null
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
        setHasOptionsMenu(false)
        recyclerView = initRecyclerView(view)
        conversationsAdapter = MonkeyConversationsAdapter(activity)
        conversationsAdapter!!.recyclerView = recyclerView
        recyclerView!!.adapter = conversationsAdapter
        return view
    }

    override fun onStart() {
        super.onStart()
        val conversationsActivty = context as? ConversationsActivity
        if(conversationsActivty != null) {
            conversationsActivty.setConversationsFragment(this)
            conversationsAdapter?.conversations = conversationsActivty.onRequestConversations()
        }
    }

    override fun onStop() {
        super.onStop()
        val conversationsActivty = context as? ConversationsActivity
        if(conversationsActivty != null) {
            val pendingConversationToDelete = conversationsAdapter?.conversationToDelete
            if (pendingConversationToDelete != null)
                conversationsActivty.onConversationDeleted(pendingConversationToDelete)

            val adapter = conversationsAdapter

            conversationsActivty.setConversationsFragment(null)
        }
        conversationsAdapter?.conversationToDelete = null
    }

    override fun onDestroy() {
        super.onDestroy()
        val adapter = conversationsAdapter
    }

    fun insertConversations(list: ConversationsList) {
        list.listUI = this
        conversationsAdapter?.conversations = list
        conversationsAdapter?.notifyItemRangeInserted(0, list.size)
    }

    override fun notifyConversationChanged(position: Int) {
        conversationsAdapter?.notifyItemChanged(position)
    }

    override fun notifyConversationInserted(position: Int) {
        conversationsAdapter?.notifyItemInserted(position)
    }

    override fun notifyConversationMoved(oldPosition: Int, newPosition: Int) {
        conversationsAdapter?.notifyItemMoved(oldPosition, newPosition)
    }

    override fun notifyConversationRangeInserted(start: Int, end: Int) {
        conversationsAdapter?.notifyItemRangeInserted(start, end)
    }

    override fun refresh() {
        conversationsAdapter?.notifyDataSetChanged()
    }

    override fun notifyConversationRemoved(position: Int) {
        conversationsAdapter?.notifyItemRemoved(position)
    }

    override fun removeLoadingView() {
        conversationsAdapter?.removeEndOfRecyclerView()
    }

    override fun scrollToPosition(position: Int) {
        recyclerView?.scrollToPosition(position)
    }

}
