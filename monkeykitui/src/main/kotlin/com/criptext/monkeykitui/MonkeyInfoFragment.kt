package com.criptext.monkeykitui

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.criptext.monkeykitui.conversation.ConversationsActivity
import com.criptext.monkeykitui.conversation.MonkeyConversationsAdapter
import com.criptext.monkeykitui.recycler.MonkeyInfo
import com.criptext.monkeykitui.info.InfoActivity
import com.criptext.monkeykitui.info.MonkeyInfoAdapter
import com.criptext.monkeykitui.recycler.ChatActivity
import com.criptext.monkeykitui.recycler.MonkeyAdapter
import java.util.*

/**
 * Created by daniel on 8/11/16.
 */

open class MonkeyInfoFragment : Fragment(){

    open val infoLayout: Int
        get() = R.layout.fragment_info
    lateinit var recyclerView: RecyclerView
    protected var infoAdapter: MonkeyInfoAdapter? = null
    var rightText : TextView? = null
    var leftText : TextView? = null
    var outButton : Button? = null
    var isGroup : Boolean = false
    var loadingLayout : FrameLayout? = null

    private fun setInfo(){
        val groupInfo = (activity as InfoActivity).getInfo()
        if(isGroup) {
            leftText?.text = "Participants"
            rightText?.text = groupInfo.size.toString() + " of 50"
            outButton?.visibility = View.VISIBLE
        }else{
            leftText?.text = "Conversations in Common"
            rightText?.text = ""
            outButton?.visibility = View.GONE
        }
        if(groupInfo.size <= 0){
            loadingLayout?.visibility = View.VISIBLE
        }else{
            loadingLayout?.visibility = View.GONE
        }
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
        isGroup = arguments.getBoolean("isGroup")

        val view = inflater!!.inflate(infoLayout, null)
        recyclerView = initRecyclerView(view)
        infoAdapter = MonkeyInfoAdapter(activity)
        infoAdapter!!.recyclerView = recyclerView
        recyclerView.adapter = infoAdapter
        rightText = view.findViewById(R.id.rightTextList) as TextView
        leftText = view.findViewById(R.id.leftTextList) as TextView
        outButton = view.findViewById(R.id.mk_info_out) as Button
        loadingLayout = view.findViewById(R.id.info_load) as FrameLayout
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
        val infoActivty = activity as? InfoActivity
        infoActivty?.setInfoFragment(null)
        super.onDetach()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun takeAllItems(): Collection<MonkeyInfo> = infoAdapter?.getAllMonkeyUsers()
            ?: listOf()

    companion object {
        fun newInfoInstance(isGroup : Boolean): MonkeyInfoFragment {
            val args = Bundle()
            args.putBoolean("isGroup", isGroup)
            val newInstance = MonkeyInfoFragment()
            newInstance.arguments = args
            return newInstance
        }
    }

    fun addUsers(arrayList: ArrayList<MonkeyInfo>){
        infoAdapter?.addMembers(arrayList)
    }

    fun setInfo(arraylist : ArrayList<MonkeyInfo>){
        infoAdapter?.setInfo(arraylist)
        rightText?.text = arraylist.size.toString() + " of 50"
        loadingLayout?.visibility = View.GONE
    }
}
