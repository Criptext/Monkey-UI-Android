package com.criptext.monkeykitui.input

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.criptext.monkeykitui.input.BaseInputView

/**
 * Created by gesuwall on 4/25/16.
 */

class RecorderSlideAnimator(redMic: View, timer: View, slideMessage: View, button: View) : RecorderAnimations {
    var redMic : View
    var timer : View
    var slideMsg: View
    var button : View

    var textInput : View? = null

    var redMicStartX: Float = 0f
    var timerStartX: Float = 0f
    var slideMsgStartX: Float = 0f
    var buttonStartX: Float = 0f

    var buttonScaleFactor = 2.5f
    init{
        this.redMic = redMic
        this.timer = timer
        this.slideMsg = slideMessage
        this.button = button
    }
    override fun hideRecorder(cancelled: Boolean) {

    }

    fun initStartValues(){
        redMicStartX = redMic.x
        timerStartX = timer.x
        slideMsgStartX = slideMsg.x
        buttonStartX = button.x
    }

    override fun revealRecorder() {
        initStartValues()
        val animStartX = button.x + BaseInputView.dpToPx(80, button.context)
        val buttonXAnimator = ObjectAnimator.ofFloat(button, "scaleX", 1f, buttonScaleFactor)
        val buttonYAnimator = ObjectAnimator.ofFloat(button, "scaleY", 1f, buttonScaleFactor)

        val decelerator = DecelerateInterpolator()

        val revealMic = ObjectAnimator.ofFloat(redMic, "alpha", 0f, 1f)
        val moveMic = ObjectAnimator.ofFloat(redMic, "x", animStartX, redMicStartX)
        moveMic.interpolator = decelerator

        val revealTimer = ObjectAnimator.ofFloat(timer, "alpha", 0f, 1f)
        val moveTimer = ObjectAnimator.ofFloat(timer, "x", animStartX, timerStartX)
        moveTimer.interpolator = decelerator

        val revealSlideMsg = ObjectAnimator.ofFloat(slideMsg, "alpha", 0f, 1f)
        val moveSlideMsg = ObjectAnimator.ofFloat(slideMsg, "x", animStartX, slideMsgStartX)
        moveSlideMsg.interpolator = decelerator

        val set = AnimatorSet()
        set.playTogether(buttonXAnimator, buttonYAnimator, revealMic, moveMic, revealSlideMsg,
                moveSlideMsg, revealTimer, moveTimer)
        set.duration = 500
        set.addListener(object : Animator.AnimatorListener{
            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                textInput?.visibility = View.INVISIBLE
            }

        })
        set.start()

    }

}
