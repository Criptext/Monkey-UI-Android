package com.criptext.monkeykitui.util

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.criptext.monkeykitui.MonkeyChatFragment
import com.criptext.monkeykitui.MonkeyConversationsFragment
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.conversation.MonkeyConversation
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer
import com.criptext.monkeykitui.toolbar.MonkeyStatusBar
import com.criptext.monkeykitui.toolbar.MonkeyToolbar

/**
 * Created by gesuwall on 8/15/16.
 */

class MonkeyFragmentManager(val activity: AppCompatActivity){
    /**
     * resource id of the xml layout to use in the Activity that will display the chat fragments
     */
    var fragmentContainerLayout: Int
    /**
     * resource if of the ViewGroup that will contain the chat fragments
     */
    var fragmentContainerId: Int

    /**
     * resource id of the animation to use when the chat fragment enters the activity, replacing
     * the conversations fragment
     */
    var chatFragmentInAnimation: Int

    /**
     * resource id of the animation to use when the chat fragment leaves the activity, replaced
     * the returning conversations fragment
     */
    var chatFragmentOutAnimation: Int

    /**
     * resource id of the animation to use when the conversations fragment leaves the activity,
     * replaced by the chat fragment
     */
    var conversationsFragmentOutAnimation: Int

    /**
     * class to handle title, subtitle and avatar in the toolbar
     */
    var monkeyToolbar: MonkeyToolbar?

    /**
     * class to show connectivity status to the user.
     */
    var monkeyStatusBar: MonkeyStatusBar?

    /**
     * Title of the conversations fragment.
     */
    var conversationsTitle: String = "UI Sample"

    /**
     * Color for expanded status bar
     */
    var expandedToolbarColor: Int = 0
        get() {
        if(field == 0){
            val value = TypedValue()
            activity.theme.resolveAttribute(R.attr.colorPrimary, value, true)
            return value.data
        }
        return field
    }

    /**
     * resource id of the animation to use when the conversations fragment reenters the activity,
     * replacing the chat fragment
     */
    var conversationsFragmentInAnimation: Int
    init{
        fragmentContainerLayout = R.layout.mk_fragment_activity
        fragmentContainerId = R.id.fragment_container
        chatFragmentInAnimation = R.anim.mk_fragment_slide_right_in
        conversationsFragmentOutAnimation = R.anim.mk_fragment_slide_left_out
        chatFragmentOutAnimation = R.anim.mk_fragment_slide_right_out
        conversationsFragmentInAnimation = R.anim.mk_fragment_slide_left_in
        monkeyStatusBar = MonkeyStatusBar(activity)
        monkeyToolbar = null
    }

    /**
     * Add a new Conversations fragment to the activity.
     */
    private fun setConversationsFragment() {
        val convFragment = MonkeyConversationsFragment()
        val ft = activity.supportFragmentManager.beginTransaction()
        ft.add(fragmentContainerId, convFragment)
        ft.commit()
    }
    /**
     * Set a layout with a FrameLayout as fragment container in the activity. this fragment
     * container will be used by all the UI Kit's fragments. If this is the first time that the
     * activity is being created.
     * @oaram savedInstanceState the bundle passed in the onCreate() callback
     */
    fun setContentLayout(savedInstanceState: Bundle?){
        //The content layout must have a FrameLayout as container of the fragments. using
        // different layouts like RelativeLayout may have weird results. It's best to use
        // our mk_fragment_container
        activity.setContentView(fragmentContainerLayout)
        monkeyToolbar = MonkeyToolbar(activity, conversationsTitle, expandedToolbarColor)
        if(savedInstanceState == null) //don't set conversations fragment if the activity is being recreated
            setConversationsFragment();
        monkeyStatusBar?.initStatusBar()
        addOnBackStackChangedListener()
    }

    fun addOnBackStackChangedListener(){
        activity.supportFragmentManager.addOnBackStackChangedListener({
            monkeyToolbar?.checkIfChatFragmentIsVisible()
        })
    }

    /**
     * Add a Chat fragment to the activity with a slide animation replacing any existant
     * conversations fragment
     * @param inputListener object that listens to the user's inputs in the chat
     * @param voiceNotePlayer object that plays voice notes in the chat
     */
    fun setChatFragment(chatFragment: MonkeyChatFragment, inputListener: InputListener,
                        voiceNotePlayer: VoiceNotePlayer): Collection<MonkeyConversation>{

        val conversationsFragment = activity.supportFragmentManager.findFragmentById(
                fragmentContainerId) as? MonkeyConversationsFragment? //finding by id may be too slow?
        if(conversationsFragment != null) {
            val list = conversationsFragment.takeAllConversations()
            chatFragment.inputListener = inputListener
            //instantiate an object to play voice notes and pass it to the fragment
            chatFragment.voiceNotePlayer = voiceNotePlayer
            val ft = activity.supportFragmentManager.beginTransaction();
            //animations must be set before adding or replacing fragments
            ft.setCustomAnimations(chatFragmentInAnimation,
                    conversationsFragmentOutAnimation,
                    conversationsFragmentInAnimation,
                    chatFragmentOutAnimation)
            ft.replace(fragmentContainerId, chatFragment, CHAT_FRAGMENT_TAG)
            ft.addToBackStack(null)
            ft.commit()

            monkeyToolbar?.configureForChat(chatFragment.getChatTitle(), chatFragment.getAvatarURL(),
                    chatFragment.isGroupConversation() ?: false, chatFragment.getConversationId())

            return list
        }
        return listOf()
    }

    fun showStatusNotification(status: Utils.ConnectionStatus) {
        monkeyStatusBar?.showStatusNotification(status)
    }

    fun setSubtitle(subtitle: String){
        monkeyToolbar?.setSubtitle(subtitle)
    }

    companion object {
        /**
         * TAG to recognize fragment from Fragment manager
         */
        val CHAT_FRAGMENT_TAG: String = "CHAT_TAG"
    }

}