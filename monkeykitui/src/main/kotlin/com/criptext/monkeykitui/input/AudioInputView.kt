package com.criptext.monkeykitui.input

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.SideButton
import com.criptext.monkeykitui.input.listeners.OnSendButtonClickListener
import com.criptext.monkeykitui.input.listeners.RecordingListener
import com.criptext.monkeykitui.input.recorder.RecorderSlideAnimator
import com.criptext.monkeykitui.input.recorder.RecorderTextWatcher
import com.criptext.monkeykitui.input.recorder.RecorderTouchListener
import com.criptext.monkeykitui.input.recorder.RecordingAnimation

/**
 * Created by gesuwall on 4/25/16.
 */

open class AudioInputView : TextInputView {
    private lateinit var slideAnimator : RecorderSlideAnimator

    var recordingListener : RecordingListener? = null
    set (value){
        slideAnimator.recordingListener = value
    }
    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun setRightButton(a : TypedArray?): SideButton? {
        val view = inflate(context, R.layout.right_audio_btn, null);
        val params = LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(100, context))
        view.layoutParams = params
        val txtBtn = view.findViewById(R.id.button_send)
        val recBtn = view.findViewById(R.id.button_mic)
        val mic = view.findViewById(R.id.redMic)
        val timer = view.findViewById(R.id.textViewTimeRecording)
        val slide = view.findViewById(R.id.layoutSwipeCancel)

        if (a?.getDrawable(R.styleable.InputView_sendButton) != null)
            (txtBtn as ImageView).setImageDrawable(a?.getDrawable(R.styleable.InputView_sendButton))
        if (a?.getDrawable(R.styleable.InputView_micButton) != null)
            (recBtn as ImageView).setImageDrawable(a?.getDrawable(R.styleable.InputView_micButton))

        mic.bringToFront()
        timer.bringToFront()

        initSendTextButton(txtBtn) //enable txtBtn to send text messages

        editText.addTextChangedListener(RecorderTextWatcher(txtBtn, recBtn)) //toggle between audio and text buttons
        val recordingAnim = RecordingAnimation(mic, timer as TextView)       //controls animation that plays during recording

        slideAnimator = RecorderSlideAnimator(mic, timer, slide, recBtn)     //controls animation shows/hides recorder
        slideAnimator.recordingAnimation = recordingAnim
        slideAnimator.textInput = editText
        slideAnimator.leftButton = leftButtonView

        val touchListener = RecorderTouchListener()                          //starts animations depending on touch gestures
        touchListener.recordingAnimations = slideAnimator
        recBtn.setOnTouchListener(touchListener)

        return SideButton(view, dpToPx(50, context))

    }

    override fun init(a : TypedArray?) {
        setWhiteBackground()
        super.init(a)
    }

    fun setWhiteBackground(){
        val view = View(context)
        view.setBackgroundColor(Color.WHITE)
        val  params = LayoutParams(LayoutParams.MATCH_PARENT, context.resources.getDimension(R.dimen.audio_right_btn_height).toInt())
        params.gravity = Gravity.BOTTOM
        view.layoutParams = params
        addView(view)
    }

}
