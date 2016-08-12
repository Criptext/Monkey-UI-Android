package com.criptext.monkeykitui.conversation

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.recycler.ChatActivity
import com.criptext.monkeykitui.util.Utils
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.*

/**
 * Created by gesuwall on 8/11/16.
 */

open class MonkeyConversationsAdapter(val mContext: Context) : RecyclerView.Adapter<MonkeyConversationsAdapter.ConversationHolder>() {

    private val conversationsList: ArrayList<MonkeyConversation>
    val mSelectableItemBg: Int

    private val conversationsActivity: ConversationsActivity
    get() = mContext as ConversationsActivity

    init {
        conversationsList = ArrayList<MonkeyConversation>()
        //get that clickable background
        val mTypedValue = TypedValue();
        mContext.theme.resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mSelectableItemBg = mTypedValue.resourceId
        mContext as? ConversationsActivity ?:
                throw IllegalArgumentException(
                        "The context of this MonkeConversationsAdapter must implement ConversationsActivity!")
    }

    override fun getItemCount() = conversationsList.size

    override fun onBindViewHolder(holder: ConversationHolder?, position: Int) {
        val conversation = conversationsList[position]
        if(holder != null){
            holder.setName(conversation.getName())
            holder.setSecondaryText(conversation.getSecondaryText())
            holder.setDate(Utils.getHoraVerdadera(conversation.getDatetime()))
            holder.setTotalNewMessages(conversation.getTotalNewMessages())
            holder.setAvatar(conversation.getAvatarFilePath(), conversation.isGroup())

            holder.itemView.setOnClickListener {
                conversationsActivity.onConversationClicked(conversation)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ConversationHolder? {
        val mView = LayoutInflater.from(mContext).inflate(R.layout.item_mk_conversation, null)
        mView.setBackgroundResource(mSelectableItemBg)
        return ConversationHolder(mView)
    }

    class ConversationHolder: RecyclerView.ViewHolder {
        val nameTextView: TextView
        val secondaryTextView: TextView
        val dateTextView: TextView
        val badge: TextView
        val avatarImageView: CircleImageView

        constructor(view : View) : super(view) {
            nameTextView = view.findViewById(R.id.conv_name) as TextView
            secondaryTextView = view.findViewById(R.id.conv_secondary_txt) as TextView
            dateTextView = view.findViewById(R.id.conv_date) as TextView
            badge = view.findViewById(R.id.conv_badge) as TextView
            avatarImageView = view.findViewById(R.id.conv_avatar) as CircleImageView
        }

        fun setName(name: String){
            nameTextView.text = name
        }

        fun setSecondaryText(text: String){
            secondaryTextView.text = text
        }

        fun setDate(dateString: String){
            dateTextView.text = dateString
        }

        fun setTotalNewMessages(totalNewMessages: Int){
            badge.text = totalNewMessages.toString()
        }

        fun setAvatar(filepath: String?, isGroup: Boolean){
            if(filepath != null)
                Picasso.with(avatarImageView.context)
                    .load(File(filepath))
                    .into(avatarImageView)
            else
                avatarImageView.setImageResource(if(isGroup) R.drawable.mk_default_group_avatar else
                    R.drawable.mk_default_user_img)
        }
    }

    fun insertConversations(conversations: ArrayList<MonkeyConversation>){
        conversationsList.clear()
        conversationsList.addAll(conversations)
        notifyDataSetChanged()
    }
}