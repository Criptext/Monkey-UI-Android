package com.criptext.monkeykitui.input

import android.os.Handler
import android.view.View
import android.widget.TextView

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
            counter += PERIOD
            updateUI()
        }
    }

    fun updateUI(){
        val time = currentTime
        watch.text = time

        recordingMic.alpha = Math.cos(counter.toDouble()/11.6).toFloat()
        handler.postDelayed(updateRunnable, PERIOD)
    }

    fun start(){
        counter = 0L
        watch.text = "00:00"
        recordingMic.alpha = 1f
        updateRunnable.run()
    }

    fun cancel(){
        handler.removeCallbacks(updateRunnable)
        watch.text = "00:00"
    }




}
