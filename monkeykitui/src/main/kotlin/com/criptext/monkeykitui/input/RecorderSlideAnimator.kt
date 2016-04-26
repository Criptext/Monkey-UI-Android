package com.criptext.monkeykitui.input

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.criptext.monkeykitui.input.BaseInputView

/**
 * Created by gesuwall on 4/25/16.
 */

class RecorderSlideAnimator(redMic: View, timer: View, slideMessage: View, button: View)  {
    var redMic : View
    var timer : View
    var slideMsg: View
    var button : View

    var textInput : View? = null

    var redMicStartX: Float = 0f
    var timerStartX: Float = 0f
    var slideMsgStartX: Float = 0f
    var buttonStartX: Float = 0f
    var animStartX: Float = 0f

    var buttonScaleFactor = 2.5f

    var playingRevealAnim = false
    var playingConcealAnim = false
    var currentSet: AnimatorSet? = null

    var dragger : ViewDraggerFadeOut? = null

    init{
        this.redMic = redMic
        this.timer = timer
        this.slideMsg = slideMessage
        this.button = button
    }
    fun hideRecorder(cancelled: Boolean): Boolean {
        if(playingConcealAnim)
            return false
        else if(playingRevealAnim)
            currentSet?.cancel()

        val buttonXAnimator = ObjectAnimator.ofFloat(button, "scaleX", button.scaleX, 1f)
        val buttonYAnimator = ObjectAnimator.ofFloat(button, "scaleY", button.scaleY, 1f)
        val buttonSlideAnimator = ObjectAnimator.ofFloat(button, "x", button.x, animStartX -
                BaseInputView.dpToPx(80, button.context))

        val accelerator = AccelerateInterpolator()

        val revealMic = ObjectAnimator.ofFloat(redMic, "alpha", redMic.alpha, 0f)
        val moveMic = ObjectAnimator.ofFloat(redMic, "x", redMic.x, animStartX)
        moveMic.interpolator = accelerator

        val revealTimer = ObjectAnimator.ofFloat(timer, "alpha", timer.alpha, 0f)
        val moveTimer = ObjectAnimator.ofFloat(timer, "x", timer.x, animStartX)
        moveTimer.interpolator = accelerator

        val revealSlideMsg = ObjectAnimator.ofFloat(slideMsg, "alpha", slideMsg.alpha, 0f)
        val moveSlideMsg = ObjectAnimator.ofFloat(slideMsg, "x", slideMsg.x, animStartX)
        moveSlideMsg.interpolator = accelerator

        val set = AnimatorSet()
        set.playTogether(buttonXAnimator, buttonYAnimator, revealMic, moveMic, revealSlideMsg,
                moveSlideMsg, revealTimer, moveTimer, buttonSlideAnimator)
        set.duration = 300
        set.addListener(object : Animator.AnimatorListener{
            fun resetAnimation(){
                playingConcealAnim = false

                textInput?.visibility = View.VISIBLE

                redMic.x = redMicStartX
                timer.x = timerStartX
                slideMsg.x = slideMsgStartX
                button.x = buttonStartX

            }
            override fun onAnimationCancel(animation: Animator?) {
                resetAnimation()
            }

            override fun onAnimationEnd(animation: Animator?) {
                resetAnimation()
            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                playingConcealAnim = true
            }

        })
        set.start()
        return true

    }

    fun initStartValues(){
        redMicStartX = redMic.x
        timerStartX = timer.x
        slideMsgStartX = slideMsg.x
        buttonStartX = button.x
    }

    fun revealRecorder(): Boolean {
        if(playingRevealAnim) {
            currentSet?.cancel()
            return false
        } else if(playingConcealAnim)
            return false

        initStartValues()
        animStartX = button.x + BaseInputView.dpToPx(80, button.context)
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
        set.duration = 300
        set.addListener(object : Animator.AnimatorListener{
            override fun onAnimationCancel(animation: Animator?) {
                playingRevealAnim = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                playingRevealAnim = false
                dragger?.fadeView = slideMsg
                dragger?.textStartX = slideMsgStartX
            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                playingRevealAnim = true
                textInput?.visibility = View.INVISIBLE
            }

        })
        set.start()
        return true

    }

}
