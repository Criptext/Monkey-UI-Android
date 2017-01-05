package com.criptext.monkeykitui.toolbar

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.MonkeyChatFragment
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.cav.EmojiHandler
import com.criptext.monkeykitui.util.MonkeyFragmentManager
import com.criptext.monkeykitui.util.Utils
import com.github.badoualy.morphytoolbar.MorphyToolbar
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by daniel on 9/16/16.
 */

open class MonkeyToolbar(activity: AppCompatActivity) {

    val toolbar: Toolbar
    val customToolbar : HeaderView
    val actionBar : ActionBar
    val appBarLayout : AppBarLayout

    init {
        toolbar = activity.findViewById(R.id.toolbar) as Toolbar
        customToolbar = activity.findViewById(R.id.custom_toolbar) as HeaderView

        activity.setSupportActionBar(toolbar)
        actionBar = activity.supportActionBar!!
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false)
        appBarLayout = activity.findViewById(R.id.toolbar_layout) as AppBarLayout
    }

    fun setConversationsToolbar(title: String, showBackButton: Boolean) {
        setToolbar(title, null, showBackButton, null, null)
    }

    fun setChatToolbar(chatTitle: String, avatarURL: String?, isGroup: Boolean) {
        if (avatarURL != null)
            setToolbar(chatTitle, null, true, avatarURL, null);
        else {
            val imgRes = if (isGroup) R.drawable.mk_default_group_avatar
                        else R.drawable.mk_default_user_img

            setToolbar(chatTitle, null, true, null, imgRes);
        }

    }

    fun setOnClickListener(listener: View.OnClickListener){
        customToolbar.secondContainer.setOnClickListener(listener)
    }

    fun setSubtitle(subtitle: String){
        customToolbar.subtitle.visibility = View.VISIBLE
        customToolbar.subtitle.text = EmojiHandler.decodeJava(EmojiHandler.decodeJava(subtitle))
    }

    fun setToolbar(title : String, subtitle : String?, backable : Boolean, avatarURL : String?, imgRSC : Int?){
        customToolbar.title.text = EmojiHandler.decodeJava(EmojiHandler.decodeJava(title))
        customToolbar.subtitle.visibility = View.GONE
        customToolbar.imageView.visibility = View.GONE
        if (avatarURL != null) {
            customToolbar.imageView.visibility = View.VISIBLE
            Utils.setAvatarAsync(toolbar.context, customToolbar.imageView, avatarURL,
                    false, null)
        }else if (imgRSC != null){
            customToolbar.imageView.visibility = View.VISIBLE
            customToolbar.imageView.setImageResource(imgRSC)
        }

        if (subtitle != null) {
            customToolbar.subtitle.visibility = View.VISIBLE
            customToolbar.subtitle.text = EmojiHandler.decodeJava(EmojiHandler.decodeJava(subtitle))
        }


        actionBar.setDisplayHomeAsUpEnabled(backable)
    }
}