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
import com.criptext.monkeykitui.util.MonkeyFragmentManager
import com.criptext.monkeykitui.util.Utils
import com.github.badoualy.morphytoolbar.MorphyToolbar
import de.hdodenhof.circleimageview.CircleImageView

/**
 * Created by daniel on 9/16/16.
 */

open class MonkeyToolbar(var activity: AppCompatActivity, var conversationsTitle: String, var expandColor: Int) : AppBarLayout.OnOffsetChangedListener{

    var imageViewAvatar: CircleImageView?
    var toolbar: Toolbar
    var monkeyId: String?
    var avatarURL: String?
    var customToolbar : HeaderView?

    init {

        monkeyId = null
        avatarURL = null
        toolbar = activity.findViewById(R.id.toolbar) as Toolbar
        val appToolbar = activity.findViewById(R.id.toolbar_layout) as AppBarLayout?

        val mInflater = LayoutInflater.from(activity)
        //val mCustomView = mInflater.inflate(R.layout.custom_toolbar, toolbar)

        customToolbar = activity.findViewById(R.id.custom_toolbar) as HeaderView
        imageViewAvatar = customToolbar?.findViewById(R.id.imageViewAvatar) as CircleImageView

        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        checkIfChatFragmentIsVisible()
        
        setupClickListener()
    }

    fun checkIfChatFragmentIsVisible(){
        if (activity.supportFragmentManager.backStackEntryCount > 0) {
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            val monkeyChatFragment = activity.supportFragmentManager.findFragmentByTag(MonkeyFragmentManager.CHAT_FRAGMENT_TAG) as MonkeyChatFragment?
            customToolbar?.title?.text = monkeyChatFragment?.getChatTitle()
            customToolbar?.imageView?.visibility = View.VISIBLE
            //(toolbar.findViewById(R.id.textViewTitle) as TextView).text = monkeyChatFragment?.getChatTitle()
            //(toolbar.findViewById(R.id.imageViewAvatar) as ImageView).visibility = View.VISIBLE
            Utils.setAvatarAsync(activity, imageViewAvatar as ImageView, monkeyChatFragment?.getAvatarURL(), !(monkeyChatFragment?.isGroupConversation() ?: false), null)
        } else {
            customToolbar?.title?.text = "Monkey Sample"
            customToolbar?.imageView?.visibility = View.GONE
            customToolbar?.subtitle?.visibility = View.GONE

            //(toolbar.findViewById(R.id.textViewTitle) as TextView).text = "Monkey Sample"
            //(toolbar.findViewById(R.id.imageViewAvatar) as ImageView).visibility = View.GONE
            //(toolbar.findViewById(R.id.textViewSubTitle) as TextView).visibility = View.GONE
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        }
    }

    fun setupClickListener(){
        customToolbar?.secondContainer?.setOnClickListener(View.OnClickListener {
            if (activity.supportFragmentManager.backStackEntryCount > 0) {
                Log.d("GGWP", "CLICK")
                val toolbar_layout = activity.findViewById(R.id.toolbar_layout) as AppBarLayout
                toolbar_layout.setExpanded(true)
                (activity as ToolbarDelegate).onClickToolbar(monkeyId?:"",
                        "", "", "")
            }
        })
    }

    fun configureForChat(chatTitle: String, avatarURL: String, isGroup: Boolean, monkeyID: String){
        //Utils.setAvatarAsync(activity, imageViewAvatar as ImageView, avatarURL, !isGroup, null)

        this.avatarURL = avatarURL
        this.monkeyId = monkeyID
    }

    fun setSubtitle(subtitle: String){
        customToolbar?.subtitle?.visibility = View.VISIBLE
        customToolbar?.subtitle?.text = subtitle

        //(toolbar.findViewById(R.id.textViewSubTitle) as TextView).text = subtitle
        //(toolbar.findViewById(R.id.textViewSubTitle) as TextView).visibility = View.VISIBLE
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}