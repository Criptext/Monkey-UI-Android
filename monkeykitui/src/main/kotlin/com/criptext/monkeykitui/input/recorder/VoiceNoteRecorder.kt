package com.criptext.monkeykitui.input.recorder

import com.criptext.monkeykitui.input.listeners.InputListener

/**
 * Created by jigl on 4/15/16.
 */
abstract class VoiceNoteRecorder {

    var inputListener: InputListener? = null

    abstract fun startRecording()

    abstract fun stopRecording()

    abstract fun cancelRecording()

}
