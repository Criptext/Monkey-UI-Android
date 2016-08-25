package com.criptext.monkeykitui.util

import android.accounts.AccountManager
import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.criptext.monkeykitui.MonkeyChatFragment
import com.criptext.monkeykitui.MonkeyConversationsFragment
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.recycler.audio.VoiceNotePlayer
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

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
     * View to show connectivity status to the user.
     */
    var viewStatus: FrameLayout?
    var handlerStatus: Handler?
    var runnableStatus: Runnable?
    var pendingAction: Runnable?
    var lastColor: Int?

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
        viewStatus = null
        handlerStatus = null
        runnableStatus = null
        pendingAction = null
        lastColor = android.R.color.white
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
        initStatusBar()
    }

    fun initStatusBar(){
        viewStatus = activity.findViewById(R.id.viewStatus) as FrameLayout?
        viewStatus!!.tag = "iddle"
        handlerStatus = Handler()
        runnableStatus = Runnable { closeStatusNotification() }
    }

    fun changeColorAnimated(targetView: View, colorFrom: Int, colorTo: Int){
        ObjectAnimator.ofObject(targetView, "backgroundColor", ArgbEvaluator(), colorFrom, colorTo).setDuration(500).start()
        lastColor = colorTo
    }

    fun showStatusNotification(status: Utils.ConnectionStatus) {

        if(viewStatus == null)
            return

        if (viewStatus!!.tag == "closing") {
            pendingAction = Runnable { showStatusNotification(status) }
            return
        }

        if(handlerStatus!=null)
            handlerStatus!!.removeCallbacks(runnableStatus)

        when(status){
            Utils.ConnectionStatus.connected->{
                (viewStatus!!.findViewById(R.id.textViewStatus) as TextView).text = activity.getString(R.string.mk_status_connected)
                changeColorAnimated(viewStatus!!, lastColor!!, activity.resources.getColor(R.color.mk_status_connected))
                handlerStatus!!.postDelayed(runnableStatus, 3000)
            }
            Utils.ConnectionStatus.disconnected -> {
                (viewStatus!!.findViewById(R.id.textViewStatus) as TextView).text = activity.getString(R.string.mk_status_disconnected)
                changeColorAnimated(viewStatus!!, lastColor!!, activity.resources.getColor(R.color.mk_status_disconnected))
            }
            Utils.ConnectionStatus.connecting -> {
                (viewStatus!!.findViewById(R.id.textViewStatus) as TextView).text = activity.getString(R.string.mk_status_connecting)
                changeColorAnimated(viewStatus!!, lastColor!!, activity.resources.getColor(R.color.mk_status_connecting))
            }
            Utils.ConnectionStatus.waiting_for_network -> {
                (viewStatus!!.findViewById(R.id.textViewStatus) as TextView).text = activity.getString(R.string.mk_status_no_network)
                changeColorAnimated(viewStatus!!, lastColor!!, activity.resources.getColor(R.color.mk_status_no_network))
            }
        }

        if (viewStatus!!.tag != "opening" && viewStatus!!.tag != "closing" && viewStatus!!.tag != "opened") {
            viewStatus!!.tag = "opening"
            viewStatus!!.animate().translationYBy(activity.resources.getDimension(R.dimen.status_height)).alpha(1.0f).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }
                override fun onAnimationEnd(animation: Animator) {
                    viewStatus!!.tag = "opened"
                }
                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }
    }

    fun closeStatusNotification() {
        if (viewStatus != null && viewStatus!!.tag != "closing") {
            viewStatus!!.tag = "closing"
            viewStatus!!.animate().translationYBy((-activity.resources.getDimension(R.dimen.status_height)).toFloat()).alpha(0f).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }
                override fun onAnimationEnd(animation: Animator) {
                    viewStatus!!.tag = "iddle"
                    handlerStatus!!.post(pendingAction)
                }
                override fun onAnimationCancel(animation: Animator) {
                }
                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }
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