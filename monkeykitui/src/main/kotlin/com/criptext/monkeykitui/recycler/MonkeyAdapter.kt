package com.criptext.monkeykitui.recycler

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.bubble.*
import com.criptext.monkeykitui.recycler.holders.MonkeyAudioHolder
import com.criptext.monkeykitui.recycler.holders.MonkeyHolder
import com.criptext.monkeykitui.recycler.holders.MonkeyTextHolder
import com.criptext.monkeykitui.util.Utils
import com.innovative.circularaudioview.CircularAudioView
import java.io.File
import java.util.*

/**
 * Created by gesuwall on 4/4/16.
 */

class MonkeyAdapter(ctx: Context, list : ArrayList<MonkeyItem>) : RecyclerView.Adapter<MonkeyHolder>() {
    private val mContext : Context
    private val datalist : ArrayList<MonkeyItem>
    private var selectedMessage : MonkeyItem?

    init{
        mContext = ctx
        datalist = list
        selectedMessage = null

    }

    val chatActivity : ChatActivity
        get() = mContext as ChatActivity

    override fun getItemCount(): Int {
        return datalist.size
    }

    fun getViewTypes() : Int{
        return 10
    }

    override fun getItemViewType(position: Int): Int {
        val item = datalist[position]
        //incoming messages have viewtypes/2 higher type
        return item.getMessageType() + (if(item.isIncomingMessage()) 5 else 0)
    }





    override fun onBindViewHolder(holder : MonkeyHolder, position : Int) {
        //set Dates
        val item = datalist[position]
        holder.setMessageDate(item.getMessageTimestamp())
        //long click
        holder.setOnLongClickListener(View.OnLongClickListener {
            chatActivity.onMessageLongClicked(position, item)
            Toast.makeText(mContext, "long clicked: " + position, Toast.LENGTH_SHORT).show()
            true
        })

        if (item.isIncomingMessage()) { //stuff for incoming messages
            if (chatActivity.isGroupChat()) {
                holder.setSenderName(chatActivity.getMenberName(item.getContactSessionId()),
                        chatActivity.getMemberColor(item.getContactSessionId()))
            }
        } else { //stuff for outgoing messages
           holder.updateReadStatus(item.getOutgoingMessageStatus())
           holder.updateSendingStatus(item.getOutgoingMessageStatus(), chatActivity.isOnline(), item.getMessageTimestamp())
        }

        //selected status
        val selected = selectedMessage
        holder.updateSelectedStatus(selected != null && selected.getMessageId() == item.getMessageId())

        //type specific stuff
        when(MonkeyItem.MonkeyItemType.values()[item.getMessageType()]){
            MonkeyItem.MonkeyItemType.text -> {
                val textHolder = holder as MonkeyTextHolder
                textHolder.messageTextView!!.text = item.getMessageText()
            }
            MonkeyItem.MonkeyItemType.audio -> {
                val audioHolder = holder as MonkeyAudioHolder
                val target = File(chatActivity.getFilePath(position, item))
                if(target.exists()){
                    audioHolder.setReadyForPlayback()
                }

            }
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup?, viewtype: Int): MonkeyHolder? {
        var view : MonkeyView
        var mView : View
        var incoming = viewtype >= (getViewTypes()/2)
        val truetype = viewtype%MonkeyItem.MonkeyItemType.values().size
        when(MonkeyItem.MonkeyItemType.values()[truetype]){
            MonkeyItem.MonkeyItemType.text -> {
            if (incoming) {
                mView = LayoutInflater.from(mContext).inflate(R.layout.text_message_view_in, null)
            } else {
                mView = LayoutInflater.from(mContext).inflate(R.layout.text_message_view_out, null)
            }
                return MonkeyTextHolder(mView)
            }
            MonkeyItem.MonkeyItemType.audio -> view = AudioMessageView(mContext, incoming)
            MonkeyItem.MonkeyItemType.photo -> view = ImageMessageView(mContext, incoming)
            MonkeyItem.MonkeyItemType.file -> view = FileMessageView(mContext, incoming)
            MonkeyItem.MonkeyItemType.contact -> view = ContactMessageView(mContext, incoming)
            }
        return MonkeyHolder(view, truetype)

        }



}
