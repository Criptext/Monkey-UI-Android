package com.criptext.monkeykitui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.criptext.monkeykitui.conversation.ConversationsActivity
import com.criptext.monkeykitui.conversation.MonkeyConversation
import com.criptext.monkeykitui.conversation.MonkeyConversationsAdapter
import java.util.*

/**
 * Created by gesuwall on 8/11/16.
 */

open class MonkeyConversationsFragment: Fragment(){

    open val conversationsLayout: Int
        get() = R.layout.recycler_layout
    lateinit var recyclerView: RecyclerView
    lateinit protected var conversationsAdapter: MonkeyConversationsAdapter


    /**
     * finds the RecyclerView in the view layout of the current fragment, ands sets an appropiate
     * LayoutManager.
     *
     * The default implementation sets a LinearLayoutManager with the stackFromEnd property set as true
     * @return the RecyclerView object of this fragment ready to set an adapter with data.
     */
    open protected fun initRecyclerView(view: View): RecyclerView {
        val recycler = view as RecyclerView
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        recycler.layoutManager = linearLayoutManager;
        return recycler
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(conversationsLayout, null)
        recyclerView = initRecyclerView(view)
        conversationsAdapter = MonkeyConversationsAdapter(activity)
        recyclerView!!.adapter = conversationsAdapter
        return view
    }

    override fun onStart() {
        super.onStart()
        (activity as ConversationsActivity).requestConversations()
    }
    fun insertConversations(conversations: ArrayList<MonkeyConversation>){
        conversationsAdapter.insertConversations(conversations)
    }
}
