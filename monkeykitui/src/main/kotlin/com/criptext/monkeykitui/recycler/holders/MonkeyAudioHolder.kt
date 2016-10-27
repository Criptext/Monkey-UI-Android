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
import com.criptext.monkeykitui.cav.AudioActions
import com.criptext.monkeykitui.cav.CircularAudioView
import com.criptext.monkeykitui.util.Utils


/**
 * Created by gesuwall on 4/12/16.
 */

open class MonkeyAudioHolder: MonkeyHolder, MonkeyFile {

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
        playButtonView!!.isClickable = false //this should only be clickable to download
        downloadProgressView?.visibility = View.GONE
    }

    override fun setWaitingForDownload(){
        playButtonView!!.visibility = View.INVISIBLE
        downloadProgressView?.visibility = View.VISIBLE
        playButtonView!!.isClickable = false
        playButtonView!!.setOnClickListener(null)
    }

    override fun setWaitingForUpload(){
        playButtonView!!.visibility = View.VISIBLE
        sendingProgressBar?.visibility = View.VISIBLE
        playButtonView!!.setImageDrawable(ContextCompat.getDrawable(playButtonView!!.context,
                R.drawable.audio_play_in))
        playButtonView!!.isClickable = false
        playButtonView!!.setOnClickListener(null)
    }

    private fun setErrorInTransfer(retryDrawable: Drawable, retryListener: View.OnClickListener){
        playButtonView!!.visibility = View.VISIBLE
        downloadProgressView?.visibility = View.INVISIBLE
        playButtonView!!.setImageDrawable(retryDrawable)
        playButtonView!!.isClickable = true
        playButtonView!!.setOnClickListener(retryListener)
        sendingProgressBar?.visibility = View.INVISIBLE
    }

    override fun setErrorInDownload(listener: View.OnClickListener){
        setErrorInTransfer(ContextCompat.getDrawable(playButtonView!!.context,
                R.drawable.ic_play_down), listener)
    }

    override fun setErrorInUpload(listener: View.OnClickListener){
        setErrorInTransfer(ContextCompat.getDrawable(playButtonView!!.context,
                R.drawable.ic_play_up), listener)
        checkmarkImageView?.visibility = View.INVISIBLE
    }
    open fun updatePlayPauseButton(isPlaying: Boolean){
        if(isPlaying)
            playButtonView!!.setImageLevel(1);
        else
            playButtonView!!.setImageLevel(0);
    }

    open fun updateAudioProgress(percentage: Int, audioTime: Long){
        //if(!(percentage == 0 && audioTime != 0.toLong())){
            circularAudioView!!.progress = if(percentage > 100) 100 else percentage
        //}
        setAudioDurationText(audioTime)
    }

    open fun setAudioDurationText(duration : Long){
        durationTextView!!.text = Utils.getAudioTimeFormattedText(duration)
    }

    open fun setOnSeekBarChangeListener(listener: CircularAudioView.OnCircularAudioViewChangeListener){
        circularAudioView!!.setOnSeekBarChangeListener(listener)
    }

}
