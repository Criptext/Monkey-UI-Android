package com.criptext.monkeykitui.info

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.conversation.ConversationsActivity
import com.criptext.monkeykitui.conversation.MonkeyConversation
import com.criptext.monkeykitui.conversation.dialog.ConversationOptionsDialog
import com.criptext.monkeykitui.conversation.dialog.OnConversationOptionClicked
import com.criptext.monkeykitui.conversation.dialog.OnInfoOptionClicked
import com.criptext.monkeykitui.conversation.holder.ConversationHolder
import com.criptext.monkeykitui.conversation.holder.ConversationTransaction
import com.criptext.monkeykitui.info.dialog.InfoOptionsDialog
import com.criptext.monkeykitui.info.OnInfoItemLongClicked
import com.criptext.monkeykitui.info.holder.InfoHolder
import com.criptext.monkeykitui.recycler.MonkeyInfo
import com.criptext.monkeykitui.recycler.SlowRecyclerLoader
import com.criptext.monkeykitui.util.InsertionSort
import com.criptext.monkeykitui.util.SnackbarUtils
import com.criptext.monkeykitui.util.Utils
import java.util.*

/**
 * Created by hirobreak on 04/10/16.
 */

open class MonkeyInfoAdapter(val mContext: Context) : RecyclerView.Adapter<InfoHolder>() {

    private val itemsList: ArrayList<MonkeyInfo>
    val mSelectableItemBg: Int

    private val infoActivity: InfoActivity
        get() = mContext as InfoActivity

    val dataLoader : SlowRecyclerLoader

    var maxTextWidth: Int? = null

    var recyclerView: RecyclerView? = null

    var onInfoItemLongClicked : OnInfoItemLongClicked
    val userOptions : MutableList<OnInfoOptionClicked>

    init {
        itemsList = ArrayList<MonkeyInfo>()
        //get that clickable background
        val mTypedValue = TypedValue();
        mContext.theme.resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mSelectableItemBg = mTypedValue.resourceId
        mContext as? ConversationsActivity ?:
                throw IllegalArgumentException(
                        "The context of this MonkeyInfoAdapter must implement InfoActivity!")
        dataLoader = SlowRecyclerLoader(null, mContext)

        userOptions = mutableListOf(
            object : OnInfoOptionClicked("Remove Member"){
                override fun invoke(info : MonkeyInfo){
                    infoActivity.removeMember(info.getInfoId())
                }
            }
        )

        onInfoItemLongClicked = object : OnInfoItemLongClicked {
            override fun invoke(info : MonkeyInfo){
                var options = userOptions
                val dialog = InfoOptionsDialog(options, info)
                dialog.show(mContext)
            }
        }

    }

    override fun onViewAttachedToWindow(holder: InfoHolder?) {
        super.onViewAttachedToWindow(holder)
        val endHolder = holder as? InfoHolder.EndHolder
        if(endHolder != null) {
            //endHolder.setOnClickListener {  }
            dataLoader.delayNewBatch(itemsList.size)
        }
    }


    override fun getItemCount() = itemsList.size

    private fun getSentMessageCheckmark(status: MonkeyConversation.ConversationStatus): Int{
        return when(status){
            MonkeyConversation.ConversationStatus.deliveredMessage -> R.drawable.mk_checkmark_sent
            MonkeyConversation.ConversationStatus.sentMessageRead -> R.drawable.mk_checkmark_read
            else -> 0
        }
    }
    override fun onBindViewHolder(holder: InfoHolder?, position: Int) {
        val infoItem = itemsList[position]
        if(holder != null){
            holder.setName(infoItem.getTitle())
            holder.setSecondaryText(infoItem.getSubtitle())
            holder.setTag(infoItem.getRightTitle())
            holder.setAvatar(infoItem.getAvatarUrl(), true)
            holder.itemView.setOnClickListener{
                infoActivity.onInfoItemClick(infoItem)
            }

            if(!infoItem.getInfoId().contains("G:")) {
                holder.itemView.setOnLongClickListener({
                    onInfoItemLongClicked.invoke(infoItem)
                    true
                })
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return 0;
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): InfoHolder? {
        if(maxTextWidth == null)
            maxTextWidth = parent!!.width - mContext.resources.getDimension(R.dimen.mk_avatar_size).toInt() * 11 / 4
        val mView: View

        mView = LayoutInflater.from(mContext).inflate(R.layout.item_mk_info, null)
        mView.setBackgroundResource(mSelectableItemBg)
        return InfoHolder(mView, maxTextWidth!!)

    }

    fun getAllMonkeyUsers(): ArrayList<MonkeyInfo> {
        return itemsList
    }

    fun addMembers(arraylist : ArrayList<MonkeyInfo>){
        itemsList.addAll(arraylist);
    }

    fun setInfo(arraylist : ArrayList<MonkeyInfo>){
        itemsList.clear()
        itemsList.addAll(arraylist);
        notifyDataSetChanged();
    }

    fun removeMember(monkeyId : String){
        val li = itemsList.listIterator()
        var position = -1;
        while(li.hasNext()){
            if(li.next().getInfoId().equals(monkeyId)){
                position = li.nextIndex() - 1
                break
            }
        }

        if(position > -1){
            itemsList.removeAt(position);
            notifyItemRemoved(position)
        }

    }
}


