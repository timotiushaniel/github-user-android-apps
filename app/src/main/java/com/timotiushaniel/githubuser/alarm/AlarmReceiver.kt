package com.timotiushaniel.githubuser.alarm

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.timotiushaniel.githubuser.R
import com.timotiushaniel.githubuser.helper.Constant
import com.timotiushaniel.githubuser.helper.Constant.REMINDER_CHANNEL_ID
import com.timotiushaniel.githubuser.helper.Constant.REMINDER_CHANNEL_NAME
import com.timotiushaniel.githubuser.view.MainActivity
import com.timotiushaniel.githubuser.view.NotificationActivity
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ALARM_TYPE = "daily reminder"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra(Constant.EXTRA_MESSAGE)
        if (message != null) {
            showAlarmNotification(context, message)
        }
    }

    fun setDailyReminder(context: NotificationActivity, type: String, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constant.EXTRA_MESSAGE, message)
        intent.putExtra(Constant.EXTRA_TYPE, type)

        val timeArray = Constant.TIME_SCHEDULE.split(":").toTypedArray()
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            Constant.NOTIFICATION_ID, intent, 0
        )
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context, "Daily reminder has been turned on", Toast.LENGTH_SHORT).show()
    }

    private fun showAlarmNotification(context: Context, message: String) {

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0,
            intent, PendingIntent.FLAG_ONE_SHOT
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val builder = NotificationCompat.Builder(
            context, REMINDER_CHANNEL_ID
        ).apply {
            setContentTitle(Constant.ALARM_TITLE)
            setContentText(message)
            setAutoCancel(true)
            setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            setSmallIcon(R.drawable.ic_alarm)
            setDefaults(Notification.DEFAULT_SOUND)
        }

        builder.setContentIntent(pendingIntent)
        val notification = builder.build()
        notificationManager.notify(Constant.NOTIFICATION_ID, notification)
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = Constant.NOTIFICATION_ID
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Daily reminder has been turned off", Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManagerCompat: NotificationManager) {
        val channel = NotificationChannel(
            REMINDER_CHANNEL_ID,
            REMINDER_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            vibrationPattern = longArrayOf(0, 1000, 1000, 500, 1000)
            enableVibration(true)
        }
        notificationManagerCompat.createNotificationChannel(channel)
    }
}