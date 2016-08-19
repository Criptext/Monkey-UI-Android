package com.criptext.monkeykitui.util

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.criptext.monkeykitui.MonkeyChatFragment
import com.criptext.monkeykitui.MonkeyConversationsFragment
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer

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
     * container will be used by all the UI Kit's fragments
     */
    fun setContentLayout(savedInstanceState: Bundle?){
        //The content layout must have a FrameLayout as container of the fragments. using
        // different layouts like RelativeLayout may have weird results. It's best to use
        // our mk_fragment_container
        activity.setContentView(fragmentContainerLayout)
        if(savedInstanceState == null) //don't set conversations fragment if the activity is being recreated
            setConversationsFragment();
    }

    /**
     * Add a new Chat fragment to the activity with a slide animation.
     * @param inputListener object that listens to the user's inputs in the chat
     * @param voiceNotePlayer object that plays voice notes in the chat
     */
    fun setChatFragment(inputListener: InputListener, voiceNotePlayer: VoiceNotePlayer){
        val chatFragment = MonkeyChatFragment()
        setChatFragment(chatFragment, inputListener, voiceNotePlayer)
    }

    /**
     * Add a Chat fragment to the activity with a slide animation.
     * @param inputListener object that listens to the user's inputs in the chat
     * @param voiceNotePlayer object that plays voice notes in the chat
     */
    fun setChatFragment(chatFragment: MonkeyChatFragment, inputListener: InputListener, voiceNotePlayer: VoiceNotePlayer){
        chatFragment.inputListener = inputListener
        //instantiate an object to play voice notes and pass it to the fragment
        chatFragment.voiceNotePlayer = voiceNotePlayer
        val ft = activity.supportFragmentManager.beginTransaction();
        //animations must be set before adding or replacing fragments
        ft.setCustomAnimations(chatFragmentInAnimation,
                conversationsFragmentOutAnimation,
                conversationsFragmentInAnimation,
                chatFragmentOutAnimation)
        ft.replace(fragmentContainerId, chatFragment)
        ft.addToBackStack(null)
        ft.commit()
    }

}