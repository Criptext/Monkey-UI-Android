package com.criptext.monkeykitui.util

import android.support.v7.app.AppCompatActivity
import android.view.View
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.toolbar.MonkeyStatusBar
import com.criptext.monkeykitui.toolbar.MonkeyToolbar
import java.util.*

/**
 * Created by gesuwall on 3/20/17.
 */

abstract class ToolbarFragmentManager<T>(val activity: AppCompatActivity, val mkFragmentStack: Stack<T>) {
    open var mainTitle: String = ""
    /**
     * class to handle title, subtitle and avatar in the toolbar
     */
    var monkeyToolbar: MonkeyToolbar? = null
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
     * class to show connectivity status to the user.
     */
    var monkeyStatusBar: MonkeyStatusBar

    /**
     * resource id of the animation to use when the conversations fragment reenters the activity,
     * replacing the chat fragment
     */
    var conversationsFragmentInAnimation: Int

    fun setToolbarOnClickListener(listener: View.OnClickListener) {
        monkeyToolbar!!.setOnClickListener(listener)
    }

    fun setSubtitle(subtitle: String){
        monkeyToolbar?.setSubtitle(subtitle)
    }

    init {
        fragmentContainerLayout = R.layout.mk_fragment_activity
        fragmentContainerId = R.id.fragment_container
        chatFragmentInAnimation = R.anim.mk_fragment_slide_right_in
        conversationsFragmentOutAnimation = R.anim.mk_fragment_slide_left_out
        chatFragmentOutAnimation = R.anim.mk_fragment_slide_right_out
        conversationsFragmentInAnimation = R.anim.mk_fragment_slide_left_in
        monkeyStatusBar = MonkeyStatusBar(activity)
    }

    fun showConnectionStatusNotification(status: Utils.ConnectionStatus) {
        monkeyStatusBar.showStatusNotification(status)
    }

    fun popStack(times : Int){
        val fragmentManager = activity.supportFragmentManager
        var timess = times
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
