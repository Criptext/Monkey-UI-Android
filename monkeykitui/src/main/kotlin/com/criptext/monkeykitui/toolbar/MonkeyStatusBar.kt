package com.criptext.monkeykitui.toolbar

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.util.Utils

/**
 * Created by daniel on 9/16/16.
 */

open class MonkeyStatusBar(var activity: AppCompatActivity){

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

    var open = false

    init {
        viewStatusCont = getDefaultViewForStatus()
        viewStatus = null
        handlerStatus = null
        runnableStatus = null
        pendingAction = null
        lastColor = android.R.color.white
    }

    fun getDefaultViewForStatus(): TextView {
        var textview = TextView(activity)
        textview.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        textview.setTextColor(Color.WHITE)
        textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        textview.gravity = Gravity.CENTER
        return textview
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

        if (open) return

        if(viewStatus == null)
            return

        if (viewStatus!!.tag == "iddle" && status== Utils.ConnectionStatus.connected){
            return
        }

        if (viewStatus!!.tag == "closing") {
            pendingAction = Runnable { showStatusNotification(status) }
            return
        }

        if(handlerStatus!=null)
            handlerStatus!!.removeCallbacks(runnableStatus)

        open = true

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
        if(!open) return

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
            open = false
        }
    }

}