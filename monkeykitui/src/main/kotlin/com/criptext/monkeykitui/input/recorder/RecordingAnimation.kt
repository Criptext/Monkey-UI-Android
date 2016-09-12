package com.criptext.monkeykitui.input.recorder

import android.graphics.Typeface
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 4/26/16.
 */

class RecordingAnimation(recordingMic: View, watch: TextView){

    companion object {
        val PERIOD = 67L
    }

    val recordingMic : View
    val watch : TextView
    val handler : Handler
    val updateRunnable : Runnable
    var cancel = false

    var counter : Long
    val currentTime : String
    get() {
        val totalSeconds = counter/1000L
        val minutes = totalSeconds/60L
        val seconds = totalSeconds%60L

        val minutesText = if(minutes < 10) "0" + minutes else minutes.toString()
        val secondsText = if(seconds < 10) "0" + seconds else seconds.toString()

        return "$minutesText:$secondsText"
    }

    init{
        this.recordingMic = recordingMic
        this.watch = watch
        handler = Handler()
        counter = 0L

        updateRunnable = Runnable {
            if (!cancel) {
                counter += PERIOD
                updateUI()
            }
        }
    }

    fun updateUI(){
        val time = currentTime
        watch.text = time
        val periodFactor = 0.17
        recordingMic.alpha = Math.cos(counter.toDouble()/180L * (2 * 3.14) * periodFactor).toFloat()/2 + 0.5f
        handler.postDelayed(updateRunnable, PERIOD)
    }

    fun start(){
        val leftImage = recordingMic as? ImageView
        leftImage?.setImageResource(R.drawable.btn_mic_red)

        counter = 0L
        watch.text = "00:00"
        recordingMic.alpha = 1f
        cancel = false
        updateRunnable.run()
    }

    fun cancel(){
        cancel = true
        handler.removeCallbacks(updateRunnable)
        watch.text = "00:00"
    }

    fun error(){
        val leftImage = recordingMic as? ImageView
        leftImage?.setImageResource(R.drawable.ic_error_red_32dp)
    }




}
