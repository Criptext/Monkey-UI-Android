package com.criptext.monkeykitui.input.recorder

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText

/**
 * Created by gesuwall on 4/27/16.
 */

class RecorderTextWatcher(textBtn: View, recordBtn: View) : TextWatcher {
    val textBtn : View
    val recordBtn: View

    var typing = false

    init{
        this.textBtn = textBtn
        this.recordBtn = recordBtn
    }
    override fun afterTextChanged(s: Editable) {
        if (!typing && s.length > 0) {
            typing = true
            changeButtons(textBtn, recordBtn)
        } else if (typing && s.length == 0) {
            typing = false;
            changeButtons(recordBtn, textBtn)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    private fun createBtnAnimator(view: View, grow: Boolean): Animator {
        val start = if(grow) 0.01f else 1f
        val end = if(grow) 1f else 0.01f
        val animX = ObjectAnimator.ofFloat(view, "scaleX", start, end)
        val animY = ObjectAnimator.ofFloat(view, "scaleY", start, end)
        val set = AnimatorSet()
        set.playTogether(animX, animY)
        set.addListener(object: Animator.AnimatorListener {
            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if(!grow)
                    view.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animator?) {
                throw UnsupportedOperationException()
            }

            override fun onAnimationStart(animation: Animator?) {
                if(grow)
                    view.visibility = View.VISIBLE
            }

        })
        set.duration = 150
        return set
    }

    private fun changeButtons(viewIn: View, viewOut: View){
        val animator1 = createBtnAnimator(viewOut, false)
        val animator2 = createBtnAnimator(viewIn, true)

        val set = AnimatorSet()
        set.playSequentially(animator1, animator2)
        set.start()

    }

}
