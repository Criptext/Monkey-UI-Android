package com.criptext.monkeykitui.recycler.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.bubble.AudioMessageView
import com.criptext.monkeykitui.bubble.MonkeyView
import com.innovative.circularaudioview.CircularAudioView


/**
 * Created by gesuwall on 4/12/16.
 */

class MonkeyAudioHolder: MonkeyHolder {

    var durationTextView : TextView? = null
    var circularAudioView : CircularAudioView? = null
    var playButtonView : ImageView? = null

    constructor(view : View) : super(view) {
        durationTextView = view.findViewById(R.id.text_shown) as TextView
        circularAudioView = view.findViewById(R.id.seek_bar) as CircularAudioView
        playButtonView = view.findViewById(R.id.play_button) as ImageView
    }

    constructor(view : MonkeyView, type : Int) : super(view, type) {
        val amv = view as AudioMessageView
        durationTextView = amv.durationTextView
        circularAudioView = amv.circularAudioView
        playButtonView = amv.playButtonView
    }

    fun setReadyForPlayback(){

    }
}
