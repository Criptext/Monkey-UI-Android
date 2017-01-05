package com.criptext.monkeykitui

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
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
    var noContentText : TextView? = null
    var outButton : Button? = null
    var isGroup : Boolean = false
    var loadingLayout : FrameLayout? = null

    private fun setInfo(){
        val groupInfo = (activity as InfoActivity).getInfo(arguments.getString("conversationId"))
        if(isGroup) {
            leftText?.text = leftText?.context?.resources?.getString(R.string.mk_text_participants)
            if(groupInfo != null)
                rightText?.text = groupInfo.size.toString() + " of 50"
            outButton?.visibility = View.VISIBLE
        }else{
            leftText?.text = leftText?.context?.resources?.getString(R.string.mk_text_common)
            rightText?.text = ""
            outButton?.visibility = View.GONE
        }
        if(groupInfo != null && groupInfo.size <= 0 && isGroup){
            loadingLayout?.visibility = View.VISIBLE
            noContentText?.visibility = View.GONE
        }else if (groupInfo != null && groupInfo.size <= 0 && !isGroup){
            loadingLayout?.visibility = View.GONE
            noContentText?.visibility = View.VISIBLE
        }else{
            loadingLayout?.visibility = View.GONE
            noContentText?.visibility = View.GONE
        }
        if(groupInfo!=null)
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
        setHasOptionsMenu(true)
        recyclerView = initRecyclerView(view)
        infoAdapter = MonkeyInfoAdapter(activity)
        infoAdapter!!.recyclerView = recyclerView
        recyclerView.adapter = infoAdapter
        rightText = view.findViewById(R.id.rightTextList) as TextView
        leftText = view.findViewById(R.id.leftTextList) as TextView
        outButton = view.findViewById(R.id.mk_info_out) as Button
        loadingLayout = view.findViewById(R.id.info_load) as FrameLayout
        noContentText = view.findViewById(R.id.noContentText) as TextView
        setInfo()

        if(!isGroup || !arguments.getBoolean("canAddMembers")){
            view.findViewById(R.id.add_participant).visibility = View.GONE
            view.findViewById(R.id.participant_gap).visibility = View.VISIBLE
        }else{
            view.findViewById(R.id.add_participant).setOnClickListener {
                (activity as InfoActivity).onAddParticipant();
            }
        }

        (outButton as? Button)?.setOnClickListener{
            var alert = AlertDialog.Builder(recyclerView.context)
            alert.setTitle(recyclerView.context.getString(R.string.mk_text_exit_group))
            alert.setPositiveButton(recyclerView.context.getString(R.string.mk_text_confirm)){
                dialog, whichButton -> (activity as InfoActivity).onExitGroup(arguments.getString("conversationId"));
            }
            alert.setNegativeButton(recyclerView.context.getString(R.string.mk_text_cancel)){
                dialog, whichButton ->
            }
            alert.show()
        }

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
        fun newInfoInstance(conversationId : String, isGroup : Boolean, canAddMembers : Boolean): MonkeyInfoFragment {
            val args = Bundle()
            args.putString("conversationId", conversationId)
            args.putBoolean("isGroup", isGroup)
            args.putBoolean("canAddMembers", canAddMembers)
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
        noContentText?.visibility = View.GONE
    }

    fun removeMember(monkeyId : String){
        infoAdapter?.removeMember(monkeyId);
    }

    override fun onCreateOptionsMenu(menu : Menu?, inflater: MenuInflater){
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

}
