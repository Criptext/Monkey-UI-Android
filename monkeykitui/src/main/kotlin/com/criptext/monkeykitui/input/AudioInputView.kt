package com.criptext.monkeykitui.input

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.input.children.SideButton

/**
 * Created by gesuwall on 4/25/16.
 */

class AudioInputView : BaseInputView {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun setRightButton(): SideButton? {
        val view = inflate(context, R.layout.right_audio_btn, null);
        val params = LayoutParams(LayoutParams.MATCH_PARENT, dpToPx(100, context))
        view.layoutParams = params
        val btn = view.findViewById(R.id.button_mic)
        val mic = view.findViewById(R.id.redMic)
        val timer = view.findViewById(R.id.textViewTimeRecording)
        val slide = view.findViewById(R.id.layoutSwipeCancel)

        mic.bringToFront()
        timer.bringToFront()
        val recordingAnim = RecordingAnimation(mic, timer as TextView)
        val anim = RecorderSlideAnimator(mic, timer, slide, btn)
        anim.recordingAnimation = recordingAnim
        anim.textInput = editText
        val touchListener = RecorderTouchListener()
        touchListener.recordingAnimations = anim
        btn.setOnTouchListener(touchListener)

        return SideButton(view, dpToPx(50, context))

    }

    override fun init() {
        setWhiteBackground()
        super.init()
    }
    fun setWhiteBackground(){
        val view = View(context)
        view.setBackgroundColor(Color.WHITE)
        val  params = LayoutParams(LayoutParams.MATCH_PARENT, BaseInputView.dpToPx(53, context))
        params.gravity = Gravity.BOTTOM
        view.layoutParams = params
        addView(view)
    }

}
