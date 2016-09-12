package com.criptext.monkeykitui.input.recorder

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import com.criptext.monkeykitui.recycler.MonkeyItem
import com.criptext.monkeykitui.util.OutputFile
import java.io.File

/**
 * VoiceNoteRecorder implementation using MediaRecorder. this class records voice notes
 * with low quality, encoding it with AAC and saving it as a 3gp file.
 * Created by gesuwall on 4/29/16.
 */

class DefaultVoiceNoteRecorder(ctx : Context, val maxRecordingSize: Long) : VoiceNoteRecorder() {

    private var mAudioFileName: String? = null
    private lateinit var mRecorder: MediaRecorder
    private val ctx : Context
    private var startTime : Long = 0

    companion object {
        val TEMP_AUDIO_FILE_NAME = "audio.m4a";
    }

    init {
        this.ctx = ctx
    }

    override fun startRecording(): Boolean{

        try {
            val outputFile = OutputFile.create(ctx, "MonkeyKit Files", TEMP_AUDIO_FILE_NAME)
            if(outputFile != null && outputFile.freeSpace > maxRecordingSize * 1.5){
                mAudioFileName = outputFile.absolutePath
                mRecorder = MediaRecorder()
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                mRecorder.setOutputFile(mAudioFileName)
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                //TO MAKE AUDIO LOW QUALITY
                mRecorder.setAudioSamplingRate(22050)//8khz-92khz
                mRecorder.setAudioEncodingBitRate(22050)//8000
                mRecorder.prepare()
                mRecorder.setOnErrorListener { mr, what, extra ->
                    if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                        mr.release()
                    } else if(what == MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN){
                        //Log.d("DefaultVoiceNoteRec", "delete file size: ${File(mAudioFileName).length()} || free space: ${outputFile.freeSpace}")
                        File(mAudioFileName).delete()
                        mAudioFileName = null
                    }
                }
                startTime = System.currentTimeMillis()
                mRecorder.start()
                return true
            } else {
                Log.e("DefaultVoiceNoteRec", "Can't write to file")
            }
        }
        catch(e: Exception){
            e.printStackTrace();
        }
        return false
    }

     fun releaseRecorder() {
        try{
            mRecorder.stop()
            mRecorder.release()
        }catch(e: Exception){
            e.printStackTrace()
        }
    }

    override fun stopRecording() {
        if(mAudioFileName != null)
            releaseRecorder()
        sendAudioFile()
    }

    override fun cancelRecording() {
        releaseRecorder()
        if(mAudioFileName != null) {
            val file = File(mAudioFileName)
            if (file.exists())
                file.delete()
        }
    }

    fun sendAudioFile(){
        if(mAudioFileName != null){
            val file = File(mAudioFileName);
            val timestamp = System.currentTimeMillis() //- 1000 * 60 * 60 * 48
            val duration = timestamp - startTime
            val newItem = object : MonkeyItem {

                override fun getMessageTimestampOrder(): Long {
                    return timestamp
                }

                override fun getOldMessageId(): String {
                    return "-" + timestamp
                }

                override fun getAudioDuration(): Long {
                    return duration
                }

                override fun getSenderId(): String {
                    return ""
                }

                override fun getFilePath(): String {
                    return file.absolutePath
                }

                override fun getFileSize(): Long {
                    return file.length()
                }

                override fun getMessageText(): String {
                    return ""
                }

                override fun getMessageId(): String {
                    return "" + timestamp
                }

                override fun getMessageTimestamp(): Long {
                    return timestamp/1000
                }

                override fun getMessageType(): Int {
                    return MonkeyItem.MonkeyItemType.audio.ordinal
                }

                override fun getDeliveryStatus(): MonkeyItem.DeliveryStatus {
                    return MonkeyItem.DeliveryStatus.read
                }

                override fun isIncomingMessage(): Boolean {
                    return false
                }

                override fun getPlaceholderFilePath(): String {
                    return ""
                }

            }
            inputListener?.onNewItem(newItem)
        } else
            inputListener?.onNewItemFileError(MonkeyItem.MonkeyItemType.audio.ordinal)
    }

}
