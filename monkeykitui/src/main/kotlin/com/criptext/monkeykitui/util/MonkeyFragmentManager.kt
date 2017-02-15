package com.criptext.monkeykitui.util

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.criptext.monkeykitui.MonkeyChatFragment
import com.criptext.monkeykitui.MonkeyConversationsFragment
import com.criptext.monkeykitui.MonkeyInfoFragment
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.conversation.MonkeyConversation
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.audio.PlaybackService
import com.criptext.monkeykitui.toolbar.MonkeyStatusBar
import com.criptext.monkeykitui.toolbar.MonkeyToolbar
import java.util.*

/**
 * Created by gesuwall on 8/15/16.
 */

class MonkeyFragmentManager(val activity: AppCompatActivity, val conversationsTitle: String, val mkFragmentStack: Stack<FragmentTypes>){
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
     * resource id of the animation to use when the conversations fragment reenters the activity,
     * replacing the chat fragment
     */
    var conversationsFragmentInAnimation: Int

    /**
     * if true, every fragment will display the back button in the toolbar. default is false
     */
    var alwaysShowBackButton = false

    init{
        fragmentContainerLayout = R.layout.mk_fragment_activity
        fragmentContainerId = R.id.fragment_container
        chatFragmentInAnimation = R.anim.mk_fragment_slide_right_in
        conversationsFragmentOutAnimation = R.anim.mk_fragment_slide_left_out
        chatFragmentOutAnimation = R.anim.mk_fragment_slide_right_out
        conversationsFragmentInAnimation = R.anim.mk_fragment_slide_left_in
        monkeyStatusBar = MonkeyStatusBar(activity)
        monkeyToolbar = null

        activity.supportFragmentManager.addOnBackStackChangedListener {
            //mkFragmentStack always has one more item than backStackEntryCount
            //substracting one unit from mkFragmentStack count is REALLY IMPORTANT
            //only try to pop if backstack count decreased
            if (activity.supportFragmentManager.backStackEntryCount == 0  || activity.supportFragmentManager.backStackEntryCount < mkFragmentStack.count() - 1) {
                if (mkFragmentStack.count() > 1) //Check before running into StackEmptyException
                    mkFragmentStack.pop()
                val currentFragment = mkFragmentStack.peek()
                when (currentFragment) {
                    FragmentTypes.conversations ->
                        monkeyToolbar?.setConversationsToolbar(conversationsTitle, alwaysShowBackButton)
                }
            }
        }

    }

    fun restoreToolbar(activeConversation: MonkeyConversation?) {
        when (mkFragmentStack.peek()) {
            FragmentTypes.conversations -> monkeyToolbar?.setConversationsToolbar(
                    conversationsTitle, alwaysShowBackButton)
            FragmentTypes.chat, FragmentTypes.info -> {
                if (activeConversation != null)
                    monkeyToolbar?.setChatToolbar(chatTitle = activeConversation.getName(),
                        avatarURL = activeConversation.getAvatarFilePath(),
                        isGroup = activeConversation.isGroup())
                else throw IllegalArgumentException("Active conversation argument was null despite " +
                        "the fact that the stack has a ${mkFragmentStack.peek()} value")
            }
            null -> {Log.e("MonkeyFragmentManager", "Can't call restoreToolbar with an empty fragment stack")}
        }
    }

    /**
     * Add a new Conversations fragment to the activity.
     */
    fun setConversationsFragment() {
        mkFragmentStack.push(FragmentTypes.conversations)
        val convFragment = MonkeyConversationsFragment()
        val ft = activity.supportFragmentManager.beginTransaction()
        ft.add(fragmentContainerId, convFragment)
        ft.commit()
        monkeyToolbar?.setConversationsToolbar(conversationsTitle, alwaysShowBackButton)
    }
    /**
     * Set a layout with a FrameLayout as fragment container in the activity. this fragment
     * container will be used by all the UI Kit's fragments. If this is the first time that the
     * activity is being created.
     * @oaram savedInstanceState the bundle passed in the onCreate() callback
     */
    fun setContentLayout(savedInstanceState: Bundle?, setConversations : Boolean){
        //The content layout must have a FrameLayout as container of the fragments. using
        // different layouts like RelativeLayout may have weird results. It's best to use
        // our mk_fragment_container
        activity.setContentView(fragmentContainerLayout)
        monkeyToolbar = MonkeyToolbar(activity)
        if(savedInstanceState == null && setConversations) //don't set conversations fragment if the activity is being recreated
            setConversationsFragment();
        monkeyStatusBar?.initStatusBar()

    }

    fun setToolbarOnClickListener(listener: View.OnClickListener) {
        monkeyToolbar!!.setOnClickListener(listener)

    }

    /**
     * Add a Chat fragment to the activity with a slide animation replacing any existant
     * conversations fragment
     * @param inputListener object that listens to the user's inputs in the chat
     * @param voiceNotePlayer object that plays voice notes in the chat
     */
    fun setChatFragment(chatFragment: MonkeyChatFragment) {

        if (mkFragmentStack.peek() == FragmentTypes.conversations) {
            //for tests to work, toolbar and fragmentStack must be updated before
            // messing with the fragment backstack
            mkFragmentStack.push(FragmentTypes.chat)
            monkeyToolbar?.setChatToolbar(chatFragment.getChatTitle(), chatFragment.getAvatarURL(),
                    chatFragment.isGroupConversation() ?: false)
        }

        val ft = activity.supportFragmentManager.beginTransaction();
        //animations must be set before adding or replacing fragments
        ft.setCustomAnimations(chatFragmentInAnimation,
                conversationsFragmentOutAnimation,
                conversationsFragmentInAnimation,
                chatFragmentOutAnimation)
        ft.replace(fragmentContainerId, chatFragment, CHAT_FRAGMENT_TAG)
        ft.addToBackStack(null)
        ft.commit()
    }

    fun setInfoFragment(infoFragment: MonkeyInfoFragment){
        mkFragmentStack.push(FragmentTypes.info)
        val ft = activity.supportFragmentManager.beginTransaction();
        ft.setCustomAnimations(chatFragmentInAnimation,
                conversationsFragmentOutAnimation,
                conversationsFragmentInAnimation,
                chatFragmentOutAnimation)
        ft.replace(fragmentContainerId, infoFragment, INFO_FRAGMENT_TAG)
        ft.addToBackStack(null)
        ft.commit()
    }

    fun setChatFragmentFromInfo(chatFragment: MonkeyChatFragment, inputListener: InputListener,
                        voiceNotePlayer: PlaybackService.VoiceNotePlayerBinder){

        activity.supportFragmentManager.popBackStack();
        activity.supportFragmentManager.popBackStack();

        val ft = activity.supportFragmentManager.beginTransaction();
        ft.setCustomAnimations(chatFragmentInAnimation,
                conversationsFragmentOutAnimation,
                conversationsFragmentInAnimation,
                chatFragmentOutAnimation)
        chatFragment.inputListener = inputListener
        chatFragment.voiceNotePlayer = voiceNotePlayer
        ft.replace(fragmentContainerId, chatFragment, CHAT_FRAGMENT_TAG)
        ft.addToBackStack(null)
        ft.commit()

        activity.supportFragmentManager.executePendingTransactions();
        mkFragmentStack.push(FragmentTypes.chat)
        monkeyToolbar?.setChatToolbar(chatFragment.getChatTitle(), chatFragment.getAvatarURL(),
                chatFragment.isGroupConversation() ?: false)

    }

    fun showStatusNotification(status: Utils.ConnectionStatus) {
        monkeyStatusBar?.showStatusNotification(status)
    }

    fun setSubtitle(subtitle: String){
        monkeyToolbar?.setSubtitle(subtitle)
    }

    enum class FragmentTypes {
        conversations, chat, info
    }

    companion object {
        /**
         * TAG to recognize fragment from Fragment manager
         */
        val CHAT_FRAGMENT_TAG: String = "CHAT_TAG"
        val INFO_FRAGMENT_TAG: String = "INFO_TAG"
    }

    fun popStack(times : Int){
        val fragmentManager = activity.supportFragmentManager as android.support.v4.app.FragmentManager
        var timess = times as Int
        while(timess > 0){
            if(fragmentManager.backStackEntryCount >= timess){
                fragmentManager.popBackStack()
                timess--
            }else{
                timess = 0
            }
        }
    }

}