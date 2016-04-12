package com.criptext.monkeykitui.bubble

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.innovative.circularaudioview.CircularAudioView

/**
 * Created by gesuwall on 3/30/16.
 */

class AudioMessageView : MonkeyView {
    override val inLayoutId : Int
            get() = R.layout.audio_message_view_in
    override val outLayoutId : Int
            get() = R.layout.audio_message_view_out

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    constructor(context: Context, incoming: Boolean) : super(context, incoming) {
    }


    override fun setBubbleBackground(resId: Int) {
    }

    val durationTextView : TextView
        get() = findViewById(R.id.text_shown) as TextView

    val circularAudioView : CircularAudioView
        get() = findViewById(R.id.seek_bar) as CircularAudioView

    val playButtonView : ImageView
        get() = findViewById(R.id.play_button) as ImageView
}
