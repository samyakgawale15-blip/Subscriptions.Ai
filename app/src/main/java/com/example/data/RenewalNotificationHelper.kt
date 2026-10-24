package com.example.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.model.RenewalAlert

object RenewalNotificationHelper {

  private const val TAG = "RenewalNotificationHelper"
  const val CHANNEL_ID = "supabase_renewal_alerts"
  const val CHANNEL_NAME = "Supabase Renewal Alerts"

  fun ensureNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Supabase Cloud Messaging alerts for approaching subscription renewals"
        enableLights(true)
        enableVibration(true)
      }
      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      manager?.createNotificationChannel(channel)
    }
  }

  fun postRenewalNotification(context: Context, alert: RenewalAlert) {
    try {
      ensureNotificationChannel(context)

      val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("alert_id", alert.id)
        putExtra("subscription_id", alert.subscriptionId)
      }

      val pendingIntent = PendingIntent.getActivity(
        context,
        alert.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )

      val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setContentTitle(alert.alertTitle)
        .setContentText(alert.alertBody)
        .setStyle(NotificationCompat.BigTextStyle().bigText(alert.alertBody))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .build()

      val notificationManager = NotificationManagerCompat.from(context)
      try {
        notificationManager.notify(alert.id.hashCode(), notification)
      } catch (se: SecurityException) {
        Log.w(TAG, "Notification permission not granted yet: ${se.message}")
      }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to post renewal notification: ${e.message}", e)
    }
  }
}
