package com.example.medassist.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medassist.AlarmHelper
import com.example.medassist.MedicineReminderManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val docId = intent.getStringExtra("docId") ?: return
        val action = intent.getStringExtra("action") ?: return
        val hour = intent.getIntExtra("hour", 0)
        val minute = intent.getIntExtra("minute", 0)
        val name = intent.getStringExtra("name") ?: ""

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val ref = db.collection("users")
            .document(userId)
            .collection("medicines")
            .document(docId)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val status = if (action == "taken") "taken" else "not_taken"

        // ✅ UPDATE MEDICINE
        ref.update(
            mapOf(
                "isTaken" to (action == "taken"),
                "status" to status
            )
        )

        // SAVE REPORT (IMPORTANT FOR DOCTOR)
        val report = hashMapOf(
            "name" to name,
            "status" to status,
            "date" to today
        )

        db.collection("users")
            .document(userId)
            .collection("reports")
            .add(report)

        if (action == "taken") {
            AlarmHelper.cancelAlarm(context, docId)
        } else {
            MedicineReminderManager(context)
                .scheduleCustomAlarm(hour, minute, name, docId)
        }
    }
}