package com.criptext.monkeykitui.input

import android.content.Context
import android.util.AttributeSet
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
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        view.layoutParams = params
        return SideButton(view, dpToPx(50))

    }

}
