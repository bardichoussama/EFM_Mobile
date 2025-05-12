package com.example.emfmobile.utils
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.emfmobile.R

private const val CHANNEL_ID = "inscrition_channel"

fun showNotification(context: Context, message: String) {
    createNotificationChannel(context)

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground) // Remplace par ton propre icône
        .setContentTitle("Todo Inscription")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    with(NotificationManagerCompat.from(context)) {
        notify(System.currentTimeMillis().toInt(), notification)
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Inscription Notifications"
        val descriptionText = "Notifications pour les Inscription"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}