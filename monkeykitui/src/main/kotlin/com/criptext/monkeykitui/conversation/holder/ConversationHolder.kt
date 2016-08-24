package com.criptext.monkeykitui.conversation.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.util.Utils
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

/**
 * Created by gesuwall on 8/24/16.
 */

open class ConversationHolder: RecyclerView.ViewHolder {

    val nameTextView: TextView?
    val secondaryTextView: TextView?
    val dateTextView: TextView?
    val badge: TextView?
    val avatarImageView: CircleImageView?

    constructor(view : View) : super(Utils.getViewWithRecyclerLayoutParams(view)) {
        nameTextView = view.findViewById(R.id.conv_name) as TextView?
        secondaryTextView = view.findViewById(R.id.conv_secondary_txt) as TextView?
        dateTextView = view.findViewById(R.id.conv_date) as TextView?
        badge = view.findViewById(R.id.conv_badge) as TextView?
        avatarImageView = view.findViewById(R.id.conv_avatar) as CircleImageView?
    }

    constructor(view: View, type: ViewTypes): this(view){
        if(type == ViewTypes.newMessages) {
            badge!!.visibility = View.VISIBLE
            dateTextView!!.setTextColor(view.context.resources.getColor(R.color.mk_yellow_highlight))
        }
    }

    open fun setName(name: String){
        nameTextView!!.text = name
    }

    open fun setSecondaryText(text: String){
        secondaryTextView!!.text = text
    }

    open fun setDate(dateString: String){
        dateTextView!!.text = dateString
    }

    open fun setSecondaryTextLeftDrawable(drawable: Int){
        secondaryTextView!!.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0)
    }

    open fun setTotalNewMessages(totalNewMessages: Int){
        badge!!.text = totalNewMessages.toString()
    }

    open fun setAvatar(filepath: String?, isGroup: Boolean){
        val imageView = avatarImageView
        if(imageView != null) {
            if (filepath != null)
                Picasso.with(imageView.context)
                        .load(File(filepath))
                        .into(imageView)
            else
                imageView.setImageResource(if (isGroup) R.drawable.mk_default_group_avatar else
                    R.drawable.mk_default_user_img)
        }
    }

    enum class ViewTypes {
        moreConversations, empty, receivedMessage, sentMessage, newMessages;
    }

    class EndHolder(view: View) : ConversationHolder(view);
}


