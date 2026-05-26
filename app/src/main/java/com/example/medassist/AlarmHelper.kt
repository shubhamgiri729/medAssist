package com.example.medassist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.medassist.utils.AlarmReceiver

object AlarmHelper {

    fun cancelAlarm(context: Context, id: String) {

        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun setThreeTimeReminder(context: Context, medicineName: String) {
        val manager = MedicineReminderManager(context)
        // Morning: 9:00 AM
        manager.scheduleCustomAlarm(9, 0, medicineName, "${medicineName}_morning")
        // Afternoon: 2:00 PM
        manager.scheduleCustomAlarm(14, 0, medicineName, "${medicineName}_afternoon")
        // Evening: 9:00 PM
        manager.scheduleCustomAlarm(21, 0, medicineName, "${medicineName}_evening")
    }
}