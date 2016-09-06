package com.criptext.monkeykitui.util

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
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
     * Components to show connectivity status to the user.
     */
    var viewStatusCont: View? = null
    set (value){
        viewStatus?.removeAllViews()
        if(value != null){
            viewStatus?.addView(value)
        }
        field = value
    }
    var viewStatus: FrameLayout?
    var handlerStatus: Handler?
    var runnableStatus: Runnable?
    var pendingAction: Runnable?
    var lastColor: Int?

    /**
     * Title of the conversations fragment.
     */
    var conversationsTitle: String = "UI Sample"

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
        viewStatusCont = getDefaultViewForStatus()
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
        activity.supportActionBar!!.title = conversationsTitle
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
        if(savedInstanceState == null) //don't set conversations fragment if the activity is being recreated
            setConversationsFragment();
        initStatusBar()
        addOnBackStackChangedListener()
    }

    fun getDefaultViewForStatus(): TextView{
        var textview = TextView(activity)
        textview.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        textview.setTextColor(Color.WHITE)
        textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        textview.gravity = Gravity.CENTER
        return textview
    }

    fun addOnBackStackChangedListener(){
        activity.supportFragmentManager.addOnBackStackChangedListener({
            if (activity.supportFragmentManager.backStackEntryCount > 0) {
                activity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            } else {
                activity.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                activity.supportActionBar!!.title = conversationsTitle
            }
        })
    }

    fun initStatusBar(){
        viewStatus = activity.findViewById(R.id.viewStatus) as FrameLayout?
        viewStatus!!.addView(viewStatusCont)
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

        if (viewStatus!!.tag == "iddle" && status==Utils.ConnectionStatus.connected){
            return
        }

        if (viewStatus!!.tag == "closing") {
            pendingAction = Runnable { showStatusNotification(status) }
            return
        }

        if(handlerStatus!=null)
            handlerStatus!!.removeCallbacks(runnableStatus)

        when(status){
            Utils.ConnectionStatus.connected->{
                if(viewStatusCont is TextView)
                    (viewStatusCont as TextView).text = activity.getString(R.string.mk_status_connected)
                changeColorAnimated(viewStatus!!, lastColor!!, activity.resources.getColor(R.color.mk_status_connected))
                handlerStatus!!.postDelayed(runnableStatus, 1000)
            }
            Utils.ConnectionStatus.disconnected -> {
                if(viewStatusCont is TextView)
                    (viewStatusCont as TextView).text = activity.getString(R.string.mk_status_disconnected)
                changeColorAnimated(viewStatus!!, lastColor!!, activity.resources.getColor(R.color.mk_status_disconnected))
            }
            Utils.ConnectionStatus.connecting -> {
                if(viewStatusCont is TextView)
                    (viewStatusCont as TextView).text = activity.getString(R.string.mk_status_connecting)
                changeColorAnimated(viewStatus!!, lastColor!!, activity.resources.getColor(R.color.mk_status_connecting))
            }
            Utils.ConnectionStatus.waiting_for_network -> {
                if(viewStatusCont is TextView)
                    (viewStatusCont as TextView).text = activity.getString(R.string.mk_status_no_network)
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
                    pendingAction = null
                }
                override fun onAnimationCancel(animation: Animator) {
                }
                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }
    }

    /**
     * Add a new Chat fragment to the activity with a slide animation replacing any existant
     * conversations fragment
     * @param inputListener object that listens to the user's inputs in the chat
     * @param voiceNotePlayer object that plays voice notes in the chat
     */
    fun setChatFragment(inputListener: InputListener, voiceNotePlayer: VoiceNotePlayer)
            : Collection<MonkeyConversation>{
        val chatFragment = MonkeyChatFragment()
        return setChatFragment(chatFragment, inputListener, voiceNotePlayer)
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
                fragmentContainerId) as MonkeyConversationsFragment //finding by id may be too slow?
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
        ft.replace(fragmentContainerId, chatFragment)
        ft.addToBackStack(null)
        ft.commit()

        return list
    }

}