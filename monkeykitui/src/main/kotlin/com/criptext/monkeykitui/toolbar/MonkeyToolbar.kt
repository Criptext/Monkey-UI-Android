package com.criptext.monkeykitui.toolbar

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.MonkeyChatFragment
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.util.MonkeyFragmentManager
import com.criptext.monkeykitui.util.Utils
import com.github.badoualy.morphytoolbar.MorphyToolbar
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by daniel on 9/16/16.
 */

open class MonkeyToolbar(var activity: AppCompatActivity, var conversationsTitle: String){

    var imageViewAvatar: CircleImageView?
    var toolbar: Toolbar
    var morphyToolbar: MorphyToolbar?
    var monkeyId: String?
    var avatarURL: String?

    init {

        val mInflater = LayoutInflater.from(activity)
        val mCustomView = mInflater.inflate(R.layout.custom_toolbar, null)

        monkeyId = null
        avatarURL = null
        toolbar = activity.findViewById(R.id.toolbar) as Toolbar

        imageViewAvatar = mCustomView.findViewById(R.id.imageViewAvatar) as CircleImageView

        checkIfChatFragmentIsVisible()

        morphyToolbar = MorphyToolbar.builder(activity, toolbar)
                .withToolbarAsSupportActionBar()
                .withContentMarginStart(0)
                .withTitle(conversationsTitle)
                .withSubtitle("")
                .withPicture((imageViewAvatar?.drawable as BitmapDrawable).bitmap)
                .withHidePictureWhenCollapsed(false).build()
        morphyToolbar?.hidePicture()
        morphyToolbar?.hideSubtitle()

        setupClickListener()
    }

    fun checkIfChatFragmentIsVisible(){
        if (activity.supportFragmentManager.backStackEntryCount > 0) {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            val monkeyChatFragment = activity.supportFragmentManager.findFragmentByTag(MonkeyFragmentManager.CHAT_FRAGMENT_TAG) as MonkeyChatFragment?
            morphyToolbar?.setTitle(monkeyChatFragment?.getChatTitle())

            Utils.setAvatarAsync(activity, imageViewAvatar as ImageView, monkeyChatFragment?.getAvatarURL(), !(monkeyChatFragment?.isGroupConversation() ?: false), null)

            morphyToolbar?.showPicture()
            morphyToolbar?.setDefaultMargin()
            morphyToolbar?.collapse()

        } else {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            morphyToolbar?.setTitle(conversationsTitle)

            morphyToolbar?.hidePicture()
            morphyToolbar?.hideSubtitle()
            morphyToolbar?.setNoLeftMargin()
            morphyToolbar?.collapse()
        }
    }

    fun setupClickListener(){
        morphyToolbar?.setOnClickListener(View.OnClickListener {
            if (activity.supportFragmentManager.backStackEntryCount > 0) {
                if (morphyToolbar?.isCollapsed ?:false) {
                    morphyToolbar?.expand(Color.BLUE, Color.BLUE)
                    (activity as ToolbarDelegate).onClickToolbar(monkeyId?:"",
                            morphyToolbar?.title?:"", morphyToolbar?.subTitle?:"", avatarURL?:"")
                } else {
                    morphyToolbar?.collapse()
                }
            }
        })
    }

    fun configureForChat(chatTitle: String, avatarURL: String, isGroup: Boolean, monkeyID: String){
        Utils.setAvatarAsync(activity, morphyToolbar?.imageView as ImageView, avatarURL, !isGroup, null)
        morphyToolbar?.setTitle(chatTitle)
        morphyToolbar?.showPicture()
        morphyToolbar?.setDefaultMargin()
        this.avatarURL = avatarURL
        this.monkeyId = monkeyID
    }

    fun setSubtitle(subtitle: String){
        morphyToolbar?.setSubtitle(subtitle)
        morphyToolbar?.showSubtitle()
    }

}