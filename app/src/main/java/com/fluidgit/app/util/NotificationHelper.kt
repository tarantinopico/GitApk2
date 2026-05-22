package com.fluidgit.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.fluidgit.app.R

object NotificationHelper {
    private const val CHANNEL_ID = "fluidgit_operations"

    fun showProgressNotification(context: Context, id: Int, title: String, text: String, progress: Int, max: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Git Operations", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_sync) // Safe fallback icon
            .setContentTitle(title)
            .setContentText(text)
            .setProgress(max, progress, progress == -1)
            .setOngoing(true)
        
        manager.notify(id, builder.build())
    }

    fun dismissNotification(context: Context, id: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(id)
    }
}
