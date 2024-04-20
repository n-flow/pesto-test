package com.parth.pestotest.core.broadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.parth.pestotest.R

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "task_reminder_channel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskName = intent.getStringExtra("task_name")
        taskName?.let { showNotification(context, it) }
    }

    private fun showNotification(context: Context, taskName: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Task Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.pesto_logo)
            .setContentTitle("Task Reminder")
            .setContentText("Task \"$taskName\" is due!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(0, builder.build())
    }
}