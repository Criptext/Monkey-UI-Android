package com.criptext.monkeykitui.util

import android.animation.Animator

/**
 * Created by gesuwall on 4/27/16.
 */

abstract class MonkeyAnimatorListener : Animator.AnimatorListener {
    var cancelled: Boolean = false
    private set
    override fun onAnimationCancel(animation: Animator?) {
        cancelled = true
        onAnimationCancel()
    }
    abstract fun onAnimationCancel()

    override fun onAnimationEnd(animation: Animator?) {
        if(!cancelled)
            onAnimationEnd()
    }
    abstract fun onAnimationEnd()

    override fun onAnimationRepeat(animation: Animator?) {

    }

    override fun onAnimationStart(animation: Animator?) {
        onAnimationStart()
    }
    abstract fun onAnimationStart()

}