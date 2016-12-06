package com.criptext.monkeykitui.recycler.audio

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.os.PowerManager
import android.util.Log


/**
 * Class that uses a Proximity Sensor to turn off the screen when the user has the device close to
 * face. If audio is playing, changes the audio stream to voice call mode.
 * Created by Gabriel on 5/13/16.
 */
class SensorHandler(private val voiceNotePlayer: VoiceNotePlayer?, private val ctx: Context) : SensorEventListener {
    var isProximityOn = false
    private set
    private val mSensorManager: SensorManager
    private val mSensor: Sensor
    private val mAudioManager: AudioManager
    private val mProximityWakeLock: PowerManager.WakeLock? by lazy {
        val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
        var field = 0x00000020
        try {
            if(android.os.Build.VERSION.SDK_INT >= 21)
                field = PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK
            else
                // Yeah, this is hidden field in older versions.
                field = PowerManager::class.java.javaClass.getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null)

        } catch (ignored: Throwable) { /* Undocumented field in this version */ }

        pm.newWakeLock(field, "ScreenOffLock")
    }

    init {
        mAudioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mSensorManager = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST)

    }

    override fun onSensorChanged(event: SensorEvent) {

        if (event.values[0] < mSensor.maximumRange) {

            if (voiceNotePlayer != null && voiceNotePlayer.isPlayingAudio) {
                voiceNotePlayer.onPauseButtonClicked()
                voiceNotePlayer.releasePlayer()
                voiceNotePlayer.initPlayerWithFrontSpeaker()
                //ctx.volumeControlStream = AudioManager.STREAM_VOICE_CALL
                mAudioManager.mode = AudioManager.STREAM_VOICE_CALL
                isProximityOn = true
                if(!(mProximityWakeLock?.isHeld ?: false)) mProximityWakeLock?.acquire()
            }

        } else {

            if (voiceNotePlayer != null && isProximityOn) {
                voiceNotePlayer.onPauseButtonClicked()
                voiceNotePlayer.releasePlayer()
                voiceNotePlayer.initPlayer()
                mAudioManager.mode = AudioManager.MODE_NORMAL
                isProximityOn = false
                if((mProximityWakeLock?.isHeld ?: false)) mProximityWakeLock?.release()
                if(voiceNotePlayer.currentlyPlayingItem != null)
                    voiceNotePlayer.uiUpdater?.rebindAudioHolder(voiceNotePlayer.currentlyPlayingItem!!.item)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    fun onDestroy() {
        mSensorManager.unregisterListener(this)
    }
}
