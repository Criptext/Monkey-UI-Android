package com.criptext.monkeykitui.input.listeners

/**
 * Created by jigl on 4/15/16.
 */
abstract class AudioRecorder {

    var inputListener: InputListener? = null

    abstract fun startRecording()

    abstract fun stopRecording()

    abstract fun cancelRecording()

}
