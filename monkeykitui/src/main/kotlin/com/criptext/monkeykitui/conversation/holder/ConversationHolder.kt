package com.criptext.monkeykitui.conversation.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.cav.EmojiHandler
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

    constructor(view : View, textMaxWidth: Int) : super(Utils.getViewWithRecyclerLayoutParams(view)) {
        nameTextView = view.findViewById(R.id.conv_name) as TextView?
        secondaryTextView = view.findViewById(R.id.conv_secondary_txt) as TextView?
        dateTextView = view.findViewById(R.id.conv_date) as TextView?
        badge = view.findViewById(R.id.conv_badge) as TextView?
        avatarImageView = view.findViewById(R.id.conv_avatar) as CircleImageView?

        nameTextView?.maxWidth = textMaxWidth
        secondaryTextView?.maxWidth = textMaxWidth


    }

    constructor(view: View, type: ViewTypes, textMaxWidth: Int): this(view, textMaxWidth){
        if(type == ViewTypes.newMessages) {
            badge!!.visibility = View.VISIBLE
            dateTextView!!.setTextColor(view.context.resources.getColor(R.color.mk_yellow_highlight))
        }
    }

    open fun setName(name: String){
        nameTextView!!.text = (EmojiHandler.decodeJava(EmojiHandler.decodeJava(name)))
    }

    open fun setSecondaryText(text: String){
        secondaryTextView!!.text = (EmojiHandler.decodeJava(EmojiHandler.decodeJava(text)))
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
            if (filepath != null && filepath.length > 0)
                Utils.setAvatarAsync(imageView.context, imageView, filepath, !isGroup, null)
            else
                imageView.setImageResource(if (isGroup) R.drawable.mk_default_group_avatar else
                    R.drawable.mk_default_user_img)
        }
    }

    enum class ViewTypes {
        moreConversations, empty, receivedMessage, sentMessage, newMessages, sendingMessage
    }

    class EndHolder(view: View) : ConversationHolder(view, 0){

        fun adjustHeight(matchParentHeight: Boolean) {
            Utils.adjustHeight(itemView, matchParentHeight)
        }
    };
}


