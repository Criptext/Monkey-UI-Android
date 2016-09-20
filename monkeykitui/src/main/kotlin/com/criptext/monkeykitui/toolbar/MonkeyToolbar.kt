package com.criptext.monkeykitui.toolbar

import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.MonkeyChatFragment
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.util.MonkeyFragmentManager
import com.criptext.monkeykitui.util.Utils
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by daniel on 9/16/16.
 */

open class MonkeyToolbar(var activity: AppCompatActivity, var conversationsTitle: String){

    var imageViewAvatar: CircleImageView
    var textViewTitle: TextView
    var textViewSubtitle: TextView

    init {

        val mInflater = LayoutInflater.from(activity)
        val mCustomView = mInflater.inflate(R.layout.custom_toolbar, null)

        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity.supportActionBar?.customView = mCustomView
        activity.supportActionBar?.setDisplayShowCustomEnabled(true)

        imageViewAvatar = mCustomView.findViewById(R.id.imageViewAvatar) as CircleImageView
        textViewTitle = mCustomView.findViewById(R.id.textViewTitle) as TextView
        textViewSubtitle = mCustomView.findViewById(R.id.textViewSubTitle) as TextView

        checkIfChatFragmentIsVisible()
    }

    fun checkIfChatFragmentIsVisible(){
        if (activity.supportFragmentManager.backStackEntryCount > 0) {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            val monkeyChatFragment = activity.supportFragmentManager.findFragmentByTag(MonkeyFragmentManager.CHAT_FRAGMENT_TAG) as MonkeyChatFragment?
            textViewTitle?.text = monkeyChatFragment?.getChatTitle()

            Utils.setAvatarAsync(activity, imageViewAvatar as ImageView, monkeyChatFragment?.getAvatarURL(), !(monkeyChatFragment?.isGroupConversation() ?: false), null)
            imageViewAvatar?.visibility = View.VISIBLE
        } else {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            textViewTitle.text = conversationsTitle
            imageViewAvatar.visibility = View.GONE
            textViewSubtitle.visibility = View.GONE
        }
    }

    fun configureForChat(chatTitle: String, avatarURL: String, isGroup: Boolean){
        Utils.setAvatarAsync(activity, imageViewAvatar as ImageView, avatarURL, !isGroup, null)
        imageViewAvatar?.visibility = View.VISIBLE
        textViewTitle?.text = chatTitle
    }

    fun setSubtitle(subtitle: String){
        textViewSubtitle.text = subtitle
        textViewSubtitle.visibility = View.VISIBLE
    }
}