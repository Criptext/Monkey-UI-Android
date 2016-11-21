package com.criptext.monkeykitui.input.recorder

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View

/**
 * Created by gesuwall on 4/25/16.
 */

open class RecorderTouchListener(val activity: Activity) : View.OnTouchListener {
    var blocked : Boolean = false
    var lastHit : Long = 0L
    var startTime : Long = 0L

    var startX : Float = -1f

    var recordingAnimations : RecorderSlideAnimator? = null

    val maxLength = 80

    lateinit var dragger : ViewDraggerFadeOut

    fun vibrate(ctx: Context){
        if(ContextCompat.checkSelfPermission(ctx, Manifest.permission.VIBRATE) ==
                PackageManager.PERMISSION_GRANTED) {
            val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(50)
        }
    }

    fun hasPermissionsToRecord(ctx: Context)  = ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO) ==
                        PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (event.pointerCount > 1 || blocked)
            return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                if(hasPermissionsToRecord(v.context)){
                    startTime = System.nanoTime()
                    startX = event.rawX


                    dragger = ViewDraggerFadeOut(v)
                    recordingAnimations?.dragger = dragger
                    val started = recordingAnimations?.revealRecorder() ?: true
                    if (!started) {
                        startX = -1f
                        return true
                    }
                    vibrate(v.context)
                } else {
                    ActivityCompat.requestPermissions(activity, arrayOf(
                        Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            9001)
                    return false
                }

            }
            MotionEvent.ACTION_UP -> {
                if(startX == -1f)
                    return true

                recordingAnimations?.hideRecorder(false)


            }
            MotionEvent.ACTION_MOVE -> {
                if(startX == -1f)
                    return true

                val reachedEnd = dragger.drag((startX - event.rawX).toInt())
                if (reachedEnd) {
                    recordingAnimations?.hideRecorder(true)
                    startX = -1f
                }


            }
        }
        return true
    }
}