package br.edu.ufabc.reciclabc

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.edu.ufabc.reciclabc.model.NotificationGroup
import java.util.*

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        context?.let {
            val garbageType = intent.getStringExtra("garbageType")
            val addressId = intent.getIntExtra("addressId", -1)

            val builder = NotificationCompat.Builder(it, NOTIFICATIONCHANNELID)
                .setSmallIcon(R.drawable.ic_notification_icon)
                .setContentTitle(it.getString(R.string.broadcast_receiver_notification_title))
                .setContentText(it.getString(R.string.broadcast_receiver_notification_content, garbageType.toString()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager = NotificationManagerCompat.from(it)

            notificationManager.notify(addressId, builder.build())
        }
    }

    fun setAlarm(context: Context,
                 notificationGroup: NotificationGroup,
                 weekday: Int,
                 pendingIntent: PendingIntent) {

        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val scheduleDateMillis = calculateNextTriggerDateInMillis(weekday, notificationGroup.hours, notificationGroup.minutes)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            scheduleDateMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent
        )
    }

    fun cancelAlarm(context: Context, pendingIntent: PendingIntent) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    companion object {
        const val NOTIFICATIONCHANNELID = "notificacao_reciclabc"

        fun createNotificationChannel(context: Context) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.notification_channel_name)
                val descriptionText = context.getString(R.string.notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(NOTIFICATIONCHANNELID, name, importance).apply {
                    description = descriptionText
                }

                // Register the channel with the system
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun calculateNextTriggerDateInMillis(weekday: Int, hour: Int, minutes: Int): Long {
        val scheduledDate = Calendar.getInstance(TimeZone.getTimeZone("GMT-3")) //Today, GMT-3 (Brasilia)
        if (scheduledDate.get(Calendar.DAY_OF_WEEK) != weekday) {
            scheduledDate.add(
                Calendar.DAY_OF_MONTH,
                (weekday - scheduledDate.get(Calendar.DAY_OF_WEEK)) % 7
            )

        } else {
            val minOfDay =
                scheduledDate.get(Calendar.HOUR_OF_DAY) * 60 + scheduledDate.get(Calendar.MINUTE)
            if ( minOfDay >= (hour * 60 + minutes) ) scheduledDate.add(
                Calendar.DAY_OF_MONTH,
                7
            ) //Next week
        }
        scheduledDate.set(Calendar.HOUR_OF_DAY, hour)
        scheduledDate.set(Calendar.MINUTE, minutes)
        scheduledDate.timeZone.rawOffset

        return scheduledDate.timeInMillis
    }

    fun createPendingIntent(context: Context,
                            intent: Intent,
                            alarmId: Int,
                            garbageType: String,
                            addressId: Int): PendingIntent {
        return PendingIntent.getBroadcast(context,
            alarmId,
            intent.putExtra("garbageType",garbageType).putExtra("addressId", addressId),
            PendingIntent.FLAG_IMMUTABLE)
    }
}
