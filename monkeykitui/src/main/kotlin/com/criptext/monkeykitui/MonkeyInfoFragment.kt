package com.criptext.monkeykitui

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.criptext.monkeykitui.conversation.ConversationsActivity
import com.criptext.monkeykitui.conversation.MonkeyConversationsAdapter
import com.criptext.monkeykitui.info.InfoActivity
import com.criptext.monkeykitui.info.MonkeyInfoAdapter
import com.criptext.monkeykitui.recycler.ChatActivity
import com.criptext.monkeykitui.recycler.MonkeyAdapter
import com.criptext.monkeykitui.recycler.MonkeyUser
import java.util.*

/**
 * Created by daniel on 8/11/16.
 */

open class MonkeyInfoFragment: Fragment(){

    open val infoLayout: Int
        get() = R.layout.fragment_info
    lateinit var recyclerView: RecyclerView
    protected var infoAdapter: MonkeyInfoAdapter? = null

    private fun setInfo(){
        val groupInfo = (activity as InfoActivity).getInfo()
        infoAdapter?.addMembers(groupInfo);
    }

    open protected fun initRecyclerView(view: View): RecyclerView {
        //RecyclerView must be inside a container http://stackoverflow.com/a/32695034/5207721
        val recycler = view.findViewById(R.id.recycler) as RecyclerView
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        recycler.layoutManager = linearLayoutManager;
        return recycler
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(infoLayout, null)
        recyclerView = initRecyclerView(view)
        infoAdapter = MonkeyInfoAdapter(activity)
        infoAdapter!!.recyclerView = recyclerView
        recyclerView.adapter = infoAdapter
        setInfo();
        return view
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onAttach(activity: Activity?) {
        val infoActivty = activity as? InfoActivity
        infoActivty?.setInfoFragment(this)
        super.onAttach(activity)
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun takeAllItems(): Collection<MonkeyUser> = infoAdapter?.getAllMonkeyUsers()
            ?: listOf()

    companion object {
        fun newInfoInstance(): MonkeyInfoFragment {
            val newInstance = MonkeyInfoFragment()
            return newInstance
        }
    }

    fun addUsers(arrayList: ArrayList<MonkeyUser>){
        infoAdapter?.addMembers(arrayList)
    }
}
