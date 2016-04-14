package com.criptext.monkeykitui.recycler.holders

import android.content.DialogInterface
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.criptext.monkeykitui.bubble.AudioMessageView
import com.criptext.monkeykitui.bubble.MonkeyView
import com.innovative.circularaudioview.AudioActions
import com.innovative.circularaudioview.CircularAudioView


/**
 * Created by gesuwall on 4/12/16.
 */

class MonkeyAudioHolder: MonkeyHolder {

    var durationTextView : TextView? = null
    var circularAudioView : CircularAudioView? = null
    var playButtonView : ImageView? = null
    var downloadProgressView: ProgressBar? = null

    companion object {
        val DEFAULT_AUDIO_DURATION = "00:00"
    }

    constructor(view : View) : super(view) {
        durationTextView = view.findViewById(R.id.text_shown) as TextView
        circularAudioView = view.findViewById(R.id.seek_bar) as CircularAudioView
        playButtonView = view.findViewById(R.id.play_button) as ImageView
        downloadProgressView = view.findViewById(R.id.progress_audio) as ProgressBar?
    }

    constructor(view : MonkeyView, type : Int) : super(view, type) {
        val amv = view as AudioMessageView
        durationTextView = amv.durationTextView
        circularAudioView = amv.circularAudioView
        playButtonView = amv.playButtonView
    }

    fun setAudioActions(actions: AudioActions){
        circularAudioView!!.setAudioActions(actions)
    }

    fun setReadyForPlayback(){
        circularAudioView!!.visibility = View.VISIBLE
        downloadProgressView?.visibility = View.GONE
    }

    fun setWaitingForDownload(){
        circularAudioView!!.visibility = View.INVISIBLE
        downloadProgressView?.visibility = View.VISIBLE
    }

    fun updatePlayPauseButton(isPlaying: Boolean){
        if(isPlaying)
            playButtonView!!.setImageLevel(1);
        else
            playButtonView!!.setImageLevel(0);
    }

    fun updateAudioProgress(progress: Int, textProgress: String){
        circularAudioView!!.progress = if(progress > 100) 100 else progress
        durationTextView!!.text = textProgress

    }

    fun setAudioDurationText(textDuration : String){
        durationTextView!!.text = textDuration
    }

    fun setOnSeekBarChangeListener(listener: CircularAudioView.OnCircularAudioViewChangeListener){
        circularAudioView!!.setOnSeekBarChangeListener(listener)
    }

}
