package com.criptext.monkeykitui.input

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.SideButton
import com.criptext.monkeykitui.input.recorder.VoiceNoteRecorder
import com.criptext.monkeykitui.input.listeners.InputListener
import com.criptext.monkeykitui.input.recorder.*

/**
 * Created by gesuwall on 4/25/16.
 */

open class AudioInputView : TextInputView {
    private lateinit var slideAnimator : RecorderSlideAnimator
    private lateinit var txtBtn: ImageView
    private lateinit var recBtn: ImageView

    var recorder: VoiceNoteRecorder? = null
    set (value){
        slideAnimator.audioRecorder = value
        field = value
    }

    override var inputListener : InputListener? = null
    set (value){
        recorder?.inputListener = value
        field = value
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setSendTextDrawable(customDrawable: Drawable){
        txtBtn.setImageDrawable(customDrawable)
    }

    fun setSendAudioDrawable(customDrawable: Drawable){
        recBtn.setImageDrawable(customDrawable)
    }
    override fun setRightButton(a : TypedArray): SideButton? {
        val view = inflate(context, R.layout.right_audio_btn, null);
        val params = LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(100, context))
        view.layoutParams = params
        txtBtn = view.findViewById(R.id.button_send) as ImageView
        recBtn = view.findViewById(R.id.button_mic) as ImageView
        val mic = view.findViewById(R.id.redMic)
        val timer = view.findViewById(R.id.textViewTimeRecording)
        val slide = view.findViewById(R.id.layoutSwipeCancel)

        val customTextDrawable = a.getDrawable(R.styleable.InputView_sendTextDrawable)
        val customAudioDrawable = a.getDrawable(R.styleable.InputView_sendAudioDrawable)
        if (customTextDrawable != null)
            setSendTextDrawable(customTextDrawable)
        if (customAudioDrawable != null)
            setSendAudioDrawable(customAudioDrawable)

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

        setDefaultRecorder()
        return SideButton(view, dpToPx(50, context))

    }

    private fun setDefaultRecorder(){
        val recorder = DefaultVoiceNoteRecorder(context)
        this.recorder = recorder
    }


}
