package com.example.recipio.services

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.recipio.MainActivity
import com.example.recipio.R
import java.util.Calendar

class NotificationService {

    companion object {
        const val CHANNEL_ID = "recipio_daily_reminder"
        const val NOTIFICATION_ID = 1001
        private const val REQUEST_CODE = 2001

        fun createNotificationChannel(context: Context) {
            // Créer le canal de notification (requis pour Android 8.0+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.notification_channel_name)
                val descriptionText = context.getString(R.string.notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                        as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun scheduleNotification(context: Context, enabled: Boolean) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, DailyReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (enabled) {
                // Programmer la notification quotidienne à 11h00
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, 11)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)

                    // Si l'heure est déjà passée aujourd'hui, programmer pour demain
                    if (timeInMillis <= System.currentTimeMillis()) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }

                // Répéter tous les jours
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            } else {
                // Annuler les notifications
                alarmManager.cancel(pendingIntent)
            }
        }
    }
}

class DailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Intent pour ouvrir l'application
        val contentIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construire la notification
        val notificationBuilder = NotificationCompat.Builder(context, NotificationService.CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_accueil) // Utiliser une icône existante
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(context, R.color.black)) // Utiliser une couleur existante

        // Afficher la notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.notify(NotificationService.NOTIFICATION_ID, notificationBuilder.build())
    }
}