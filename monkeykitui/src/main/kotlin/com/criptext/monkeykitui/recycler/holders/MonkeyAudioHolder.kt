package com.criptext.monkeykitui.recycler.holders

import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.criptext.monkeykitui.R
import com.innovative.circularaudioview.AudioActions
import com.innovative.circularaudioview.CircularAudioView


/**
 * Created by gesuwall on 4/12/16.
 */

open class MonkeyAudioHolder: MonkeyHolder {

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
        circularAudioView!!.setAudioActions(object : AudioActions(){ })
    }

    open fun setAudioActions(actions: AudioActions){
        circularAudioView!!.setAudioActions(actions)
    }

    open fun setReadyForPlayback(){
        playButtonView!!.visibility = View.VISIBLE
        downloadProgressView?.visibility = View.GONE
    }

    open fun setWaitingForDownload(){
        playButtonView!!.visibility = View.INVISIBLE
        downloadProgressView?.visibility = View.VISIBLE
        playButtonView!!.setOnClickListener(null)
    }

    open fun setWaitingForUpload(){
        playButtonView!!.visibility = View.VISIBLE
        sendingProgressBar?.visibility = View.VISIBLE
        playButtonView!!.setImageDrawable(ContextCompat.getDrawable(playButtonView!!.context,
                R.drawable.audio_play_in))
        playButtonView!!.setOnClickListener(null)
    }

    private fun setErrorInTransfer(retryDrawable: Drawable, retryListener: View.OnClickListener){
        playButtonView!!.visibility = View.VISIBLE
        downloadProgressView?.visibility = View.INVISIBLE
        playButtonView!!.setImageDrawable(retryDrawable)
        playButtonView!!.setOnClickListener(retryListener)
        sendingProgressBar?.visibility = View.INVISIBLE
    }

    open fun setErrorInDownload(clickListener: View.OnClickListener){
        setErrorInTransfer(ContextCompat.getDrawable(playButtonView!!.context,
                R.drawable.ic_play_down), clickListener)
    }

    open fun setErrorInUpload(clickListener: View.OnClickListener){
        setErrorInTransfer(ContextCompat.getDrawable(playButtonView!!.context,
                R.drawable.ic_play_up), clickListener)
        checkmarkImageView?.visibility = View.INVISIBLE
    }
    open fun updatePlayPauseButton(isPlaying: Boolean){
        if(isPlaying)
            playButtonView!!.setImageLevel(1);
        else
            playButtonView!!.setImageLevel(0);
    }

    open fun updateAudioProgress(percentage: Int, audioTime: Long){
        circularAudioView!!.progress = if(percentage > 100) 100 else percentage
        setAudioDurationText(audioTime)
    }

    open fun getAudioTimeFormattedText(time: Long) : String
    {
        val totalSeconds = time / 1000;
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        var strMinutes = if(minutes < 10)  "0" + minutes else "" + minutes
        var strSeconds = if(seconds < 10)  "0" + seconds else "" + seconds

        return "$strMinutes:$strSeconds"
    }

    open fun setAudioDurationText(duration : Long){
        durationTextView!!.text = getAudioTimeFormattedText(duration)
    }

    open fun setOnSeekBarChangeListener(listener: CircularAudioView.OnCircularAudioViewChangeListener){
        circularAudioView!!.setOnSeekBarChangeListener(listener)
    }

}
