package com.criptext.monkeykitui.input.recorder

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.BaseInputView
import com.criptext.monkeykitui.input.recorder.VoiceNoteRecorder
import com.criptext.monkeykitui.util.MonkeyAnimatorListener

/**
 * Created by gesuwall on 4/25/16.
 */

class RecorderSlideAnimator(redMic: View, timer: View, slideMessage: View, button: View)  {
    var redMic : View
    var timer : View
    var slideMsg: View
    var button : View
    var buttonForeground: View? = null
    var buttonBackground: View ? = null


    var leftButton : View? = null
    var textInput : View? = null

    var redMicStartX: Float = 0f
    var timerStartX: Float = 0f
    var slideMsgStartX: Float = 0f
    var buttonStartX: Float = 0f
    var animStartX: Float = 0f

    var buttonScaleFactor = 80f

    var playingRevealAnim = false
    var playingConcealAnim = false
    var currentSet: AnimatorSet? = null

    var dragger : ViewDraggerFadeOut? = null
    var recordingAnimation : RecordingAnimation? = null
    var audioRecorder: VoiceNoteRecorder? = null

    init{
        this.redMic = redMic
        this.timer = timer
        this.slideMsg = slideMessage
        this.button = button
        this.buttonBackground = this.button.findViewById(R.id.button_mic_backgorund) as View
        this.buttonForeground = this.button.findViewById(R.id.button_mic_foreground) as View
    }
    fun hideRecorder(cancelled: Boolean): Boolean {
        if(playingConcealAnim)
            return false
        else if(playingRevealAnim) {
            currentSet?.cancel()
            return false
        }

        val buttonXAnimator = ObjectAnimator.ofFloat(buttonBackground, "scaleX", button.scaleX, 1f)
        val buttonYAnimator = ObjectAnimator.ofFloat(buttonBackground, "scaleY", button.scaleY, 1f)

        val buttonSlideAnimator = ObjectAnimator.ofFloat(button, "x", button.x, animStartX -
                button.context.resources.getDimension(R.dimen.cancel_audio_msg_length))

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
        set.addListener(object : MonkeyAnimatorListener() {


            override fun onAnimationCancel() {
                playingConcealAnim = false
                resetAnimation()
                audioRecorder?.cancelRecording()
            }

            override fun onAnimationEnd() {
                playingConcealAnim = false
                resetAnimation()
                if(cancelled)
                    audioRecorder?.cancelRecording()
                else
                    audioRecorder?.stopRecording()
            }

            override fun onAnimationStart() {
                playingConcealAnim = true
                recordingAnimation?.cancel()

            }

        })
        currentSet = set
        set.start()
        return true

    }

    fun resetAnimation(){
        textInput?.visibility = View.VISIBLE
        leftButton?.visibility = View.VISIBLE

        redMic.x = redMicStartX
        timer.x = timerStartX
        slideMsg.x = slideMsgStartX
        button.x = buttonStartX

        redMic.alpha = 0f
        timer.alpha = 0f
        slideMsg.alpha = 0f

        button.scaleY = 1f
        button.scaleX = 1f
        buttonBackground?.scaleX = 1f
        buttonBackground?.scaleY = 1f
        (buttonForeground as ImageView).setColorFilter(button.context.resources.getColor(R.color.mk_icon_unfocus_tint))
        (buttonForeground as ImageView).alpha = 0.7f

        textInput?.requestFocus()
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
        animStartX = button.x +
                button.context.resources.getDimension(R.dimen.cancel_audio_msg_length).toInt()

        (buttonForeground as ImageView).setColorFilter(button.context.resources.getColor(R.color.mk_icon_expanded_tint))

        val buttonXAnimator = ObjectAnimator.ofFloat(buttonBackground, "scaleX", 1f, buttonScaleFactor)
        val buttonYAnimator = ObjectAnimator.ofFloat(buttonBackground, "scaleY", 1f, buttonScaleFactor)

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
        set.addListener(object : MonkeyAnimatorListener() {
            override fun onAnimationCancel() {
                playingRevealAnim = false
                resetAnimation()
            }

            override fun onAnimationEnd() {
                playingRevealAnim = false
                dragger?.fadeView = slideMsg
                dragger?.textStartX = slideMsgStartX

                val startedRecording = audioRecorder?.startRecording() ?: false
                if(startedRecording)
                    recordingAnimation?.start()
                else
                    recordingAnimation?.error()
            }

            override fun onAnimationStart() {
                playingRevealAnim = true
                textInput?.visibility = View.INVISIBLE
                leftButton?.visibility = View.INVISIBLE
                (buttonForeground as ImageView).alpha = 1f
            }

        })
        currentSet = set
        set.start()
        return true

    }

}
