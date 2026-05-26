package com.example.medassist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.medassist.utils.AlarmReceiver
import java.util.*

class MedicineReminderManager(private val context: Context) {

    fun scheduleCustomAlarm(
        hour: Int,
        minute: Int,
        name: String,
        id: String,
        dosage: String = ""
    ) {

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("name", name)
            putExtra("docId", id)
            putExtra("dosage", dosage)
            putExtra("hour", hour)
            putExtra("minute", minute)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // ✅ If time already passed → schedule next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            // ✅ BEST METHOD (works reliably on all devices)
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(
                    calendar.timeInMillis,
                    pendingIntent
                ),
                pendingIntent
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}