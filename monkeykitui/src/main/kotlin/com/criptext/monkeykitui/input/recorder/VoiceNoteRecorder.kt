package com.criptext.monkeykitui.input.recorder

import com.criptext.monkeykitui.input.listeners.InputListener

/**
 * Abstract class for all classes that will record voice notes for Input View. By extending this
 * class you can implement audio recording the way your app needs.
 * Created by jigl on 4/15/16.
 */
abstract class VoiceNoteRecorder {

    var inputListener: InputListener? = null

    /**
     * This method is called when the user wants to start recording a new voice note.
     */
    abstract fun startRecording()

    /**
     * This method is called when the user stops the voice note recording. The implementation should
     * save the recording in an audio file and execute the callback of inputListener.
     */
    abstract fun stopRecording()

    /**
     * This method is called when the user cancels the voice note recording. The recording should be
     * discarded.
     */

    abstract fun cancelRecording()

}
