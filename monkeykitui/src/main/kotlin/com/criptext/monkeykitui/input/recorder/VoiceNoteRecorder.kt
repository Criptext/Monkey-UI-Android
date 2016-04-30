package com.criptext.monkeykitui.input.recorder

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.File

/**
 * Created by gesuwall on 4/29/16.
 */

class VoiceNoteRecorder(ctx : Context) {
    private lateinit var mAudioFileName: String
    private val mRecorder: MediaRecorder
    private val ctx : Context

    companion object {
        val TEMP_AUDIO_FILE_NAME = "temp_audio.3gp";
    }

    init {
        mRecorder = MediaRecorder()
        this.ctx = ctx
    }

    fun startRecording(){

        try {
            mAudioFileName = ctx.cacheDir.toString() + "/" + (System.currentTimeMillis()/1000) + TEMP_AUDIO_FILE_NAME;
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mAudioFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            //TO MAKE AUDIO LOW QUALITY
            mRecorder.setAudioSamplingRate(22050);//8khz-92khz
            mRecorder.setAudioEncodingBitRate(22050);//8000
            mRecorder.prepare();
            mRecorder.start();
            mRecorder.setOnErrorListener { mr, what, extra ->
                if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
                    mr.release();
                }
            };
        }
        catch(e: Exception){
            e.printStackTrace();
        }

    }

     fun stopRecording() {
        try{
            mRecorder.stop()
            mRecorder.release()
        }catch(e: Exception){
            e.printStackTrace()
        }
    }

    fun cancelRecording() {
        stopRecording()
        val file = File(mAudioFileName)
        if(file.exists())
            file.delete()
    }

    fun sendAudioFile(){
        val file = File(mAudioFileName);
        if(file.exists()) {
            val timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 48
            MessageItem item = new MessageItem("0", "" + timestamp,
                    mAudioFileName, timestamp, false,
                    MonkeyItem.MonkeyItemType.audio);
            item.setDuration("00:10");
            adapter.getMessagesList().add(item);
            adapter.notifyDataSetChanged();
            recycler.scrollToPosition(adapter.getMessagesList().size()-1);
        }
    }



}
