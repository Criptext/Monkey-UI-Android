package com.criptext.monkeykitui.recycler.audio

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.RemoteViews
import com.criptext.monkeykitui.R

/**
 * Created by gesuwall on 11/1/16.
 */

class PlaybackNotification (var imgResource: Int, var notificationTitle: String){

    companion object {
        val notificationID  = 3000

        fun removePlaybackNotification (ctx: Context) {
            val mNotifyManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            mNotifyManager.cancel(notificationID)
        }
    }

    fun start(ctx: Context, showPauseButton: Boolean) {
        val mNotifyManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(ctx);
        val remoteViews = RemoteViews(ctx.packageName, if(showPauseButton) R.layout.pause_notification else R.layout.playback_notification)
        remoteViews.setImageViewResource(R.id.notfication_image, imgResource)
        remoteViews.setTextViewText(R.id.notification_title, notificationTitle)
        val intent = Intent(ctx, PlaybackService::class.java)
        intent.putExtra(PlaybackService.togglePlayback, true)
        remoteViews.setOnClickPendingIntent(R.id.play_button, PendingIntent.getService(ctx, 0, intent, Service.BIND_AUTO_CREATE))
        mBuilder.setContent(remoteViews)
            .setSmallIcon(R.drawable.btn_mic)
        mNotifyManager.notify(notificationID, mBuilder.build());
    }

}