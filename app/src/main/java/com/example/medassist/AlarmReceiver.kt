package com.example.medassist.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.medassist.MedicineReminderManager
import kotlin.random.Random

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val name = intent.getStringExtra("name") ?: "Medicine"
        val hour = intent.getIntExtra("hour", 0)
        val minute = intent.getIntExtra("minute", 0)
        val docId = intent.getStringExtra("docId") ?: return   // ✅ FIXED
        val dosage = intent.getStringExtra("dosage") ?: ""

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "med"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Medicine Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        // ✅ TAKEN
        val takenIntent = Intent(context, ActionReceiver::class.java).apply {
            putExtra("docId", docId)
            putExtra("action", "taken")
            putExtra("hour", hour)
            putExtra("minute", minute)
            putExtra("name", name)
        }

        val takenPendingIntent = PendingIntent.getBroadcast(
            context,
            docId.hashCode(),
            takenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // ❌ NOT TAKEN
        val notTakenIntent = Intent(context, ActionReceiver::class.java).apply {
            putExtra("docId", docId)
            putExtra("action", "not_taken")
            putExtra("hour", hour)
            putExtra("minute", minute)
            putExtra("name", name)
        }

        val notTakenPendingIntent = PendingIntent.getBroadcast(
            context,
            docId.hashCode() + 1,
            notTakenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val timeText = String.format("%02d:%02d", hour, minute)

        val contentText = if (dosage.isNotEmpty()) {
            "$name ($dosage) at $timeText"
        } else {
            "$name at $timeText"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("💊 Medicine Reminder")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0, "✅ Taken", takenPendingIntent)
            .addAction(0, "❌ Not Taken", notTakenPendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(Random.nextInt(), notification)
        Log.d("ALARM_DEBUG", "Alarm Triggered")
        Toast.makeText(context, "Alarm Triggered", Toast.LENGTH_LONG).show()

        // 🔁 RESCHEDULE FOR NEXT DAY (VERY IMPORTANT)
        MedicineReminderManager(context)
            .scheduleCustomAlarm(hour, minute, name, docId, dosage)
    }
}
