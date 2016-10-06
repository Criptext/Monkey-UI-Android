package com.criptext.monkeykitui.info

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.conversation.ConversationsActivity
import com.criptext.monkeykitui.conversation.MonkeyConversation
import com.criptext.monkeykitui.conversation.dialog.ConversationOptionsDialog
import com.criptext.monkeykitui.conversation.dialog.OnConversationOptionClicked
import com.criptext.monkeykitui.conversation.holder.ConversationHolder
import com.criptext.monkeykitui.conversation.holder.ConversationTransaction
import com.criptext.monkeykitui.info.holder.InfoHolder
import com.criptext.monkeykitui.recycler.MonkeyUser
import com.criptext.monkeykitui.recycler.SlowRecyclerLoader
import com.criptext.monkeykitui.util.InsertionSort
import com.criptext.monkeykitui.util.SnackbarUtils
import com.criptext.monkeykitui.util.Utils
import java.util.*

/**
 * Created by hirobreak on 04/10/16.
 */

open class MonkeyInfoAdapter(val mContext: Context) : RecyclerView.Adapter<InfoHolder>() {

    private val usersList: ArrayList<MonkeyUser>
    val mSelectableItemBg: Int

    private val conversationsActivity: ConversationsActivity
        get() = mContext as ConversationsActivity

    val dataLoader : SlowRecyclerLoader

    var maxTextWidth: Int? = null

    var recyclerView: RecyclerView? = null

    init {
        usersList = ArrayList<MonkeyUser>()
        //get that clickable background
        val mTypedValue = TypedValue();
        mContext.theme.resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mSelectableItemBg = mTypedValue.resourceId
        mContext as? ConversationsActivity ?:
                throw IllegalArgumentException(
                        "The context of this MonkeyInfoAdapter must implement InfoActivity!")
        dataLoader = SlowRecyclerLoader(null, mContext)

    }

    override fun onViewAttachedToWindow(holder: InfoHolder?) {
        super.onViewAttachedToWindow(holder)
        val endHolder = holder as? InfoHolder.EndHolder
        if(endHolder != null) {
            //endHolder.setOnClickListener {  }
            dataLoader.delayNewBatch(usersList.size)
        }
    }


    override fun getItemCount() = usersList.size

    private fun getSentMessageCheckmark(status: MonkeyConversation.ConversationStatus): Int{
        return when(status){
            MonkeyConversation.ConversationStatus.deliveredMessage -> R.drawable.mk_checkmark_sent
            MonkeyConversation.ConversationStatus.sentMessageRead -> R.drawable.mk_checkmark_read
            else -> 0
        }
    }
    override fun onBindViewHolder(holder: InfoHolder?, position: Int) {
        val user = usersList[position]
        if(holder != null){
            holder.setName(user.getName())
            holder.setSecondaryText(user.getConnectionStatus())
            holder.setTag(user.getRol())
            holder.setAvatar(user.getAvatarUrl(), false)
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

    fun getAllMonkeyUsers(): ArrayList<MonkeyUser> {
        return usersList
    }

    fun addMembers(arraylist : ArrayList<MonkeyUser>){
        usersList.addAll(arraylist);
    }

}