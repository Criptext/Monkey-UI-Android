package com.criptext.uisample;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.view.WindowManager;

import com.criptext.monkeykitui.recycler.audio.AudioPlaybackHandler;

/**
 * Created by daniel on 5/12/16.
 */

public class SensorHandler implements SensorEventListener {

    private AudioPlaybackHandler audioHandler;
    private boolean isProximityOn=false;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private AudioManager mAudioManager;
    private Activity activity;
    private float prevBrightness;
    private WindowManager.LayoutParams layout;

    public SensorHandler(AudioPlaybackHandler audioHandler, Activity activity) {

        this.audioHandler = audioHandler;
        this.activity = activity;
        layout = activity.getWindow().getAttributes();
        mAudioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.values[0] < mSensor.getMaximumRange()) {

            if(audioHandler!=null && audioHandler.isPlayingAudio()) {
                audioHandler.pauseAudioHolderPlayer();
                audioHandler.releasePlayer();
                audioHandler.initPlayerWithFrontSpeaker();
                activity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
                isProximityOn=true;
                prevBrightness = layout.screenBrightness;
                layout.screenBrightness = 0;
                activity.getWindow().setAttributes(layout);
            }

        } else {

            if(audioHandler!=null && isProximityOn){
                audioHandler.pauseAudioHolderPlayer();
                audioHandler.releasePlayer();
                audioHandler.initPlayer();
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                isProximityOn=false;
                layout.screenBrightness = prevBrightness;
                activity.getWindow().setAttributes(layout);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onPause(){
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    public void onStop(){
        if(isProximityOn){
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
            isProximityOn=false;
            layout.screenBrightness = prevBrightness;
            activity.getWindow().setAttributes(layout);
        }
    }

    public void onDestroy(){
        mSensorManager.unregisterListener(this);
    }

}
