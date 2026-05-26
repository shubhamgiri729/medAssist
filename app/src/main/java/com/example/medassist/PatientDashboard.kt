package com.example.medassist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.medassist.model.Medicine
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDashboard(
    onProfileClick: () -> Unit
) {

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var medicines by remember { mutableStateOf(listOf<Medicine>()) }
    var userName by remember { mutableStateOf("User") } // ✅ NEW

    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    LaunchedEffect(Unit) {

        if (userId != null) {

            // ✅ FETCH USER NAME
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener {
                    userName = it.getString("name") ?: "User"
                }

            // ✅ FETCH MEDICINES
            db.collection("users")
                .document(userId)
                .collection("medicines")
                .addSnapshotListener { snapshot, _ ->

                    if (snapshot != null) {

                        medicines = snapshot.documents.mapNotNull { doc ->

                            val endDate = doc.getString("endDate") ?: ""
                            val isExpired = endDate < todayDate

                            if (isExpired) return@mapNotNull null // ❌ skip expired

                            Medicine(
                                id = doc.id,
                                name = doc.getString("name") ?: "",
                                dosage = doc.getString("dosage") ?: "",
                                hour = doc.getLong("hour")?.toInt() ?: 0,
                                minute = doc.getLong("minute")?.toInt() ?: 0,
                                patientId = userId,
                                isTaken = doc.getBoolean("isTaken") ?: false
                            )
                        }

                        // 🔔 SCHEDULE ALARMS
                        val manager = MedicineReminderManager(context)
                        medicines.forEach {
                            manager.scheduleCustomAlarm(
                                it.hour,
                                it.minute,
                                it.name,
                                it.id,
                                it.dosage
                            )
                        }
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patient Dashboard") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { padding ->

        Column(modifier = Modifier.padding(padding)) {

            // USER NAME DISPLAY
            Text(
                text = "Hello, $userName 👋",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )

            Text(
                "Your Medicines",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {

                items(medicines) { medicine ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {

                        Column(modifier = Modifier.padding(16.dp)) {

                            //MEDICINE DETAILS
                            Text(
                                text = " ${medicine.name}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text("Dosage: ${medicine.dosage}")

                            val time = String.format(
                                "%02d:%02d",
                                medicine.hour,
                                medicine.minute
                            )
                            Text("Time: $time")

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                //TAKEN
                                Button(
                                    onClick = {

                                        val report = hashMapOf(
                                            "medicineId" to medicine.id,
                                            "name" to medicine.name,
                                            "status" to "taken",
                                            "date" to todayDate,
                                            "timestamp" to System.currentTimeMillis()
                                        )

                                        db.collection("users")
                                            .document(userId!!)
                                            .collection("reports")
                                            .add(report)

                                        db.collection("users")
                                            .document(userId)
                                            .collection("medicines")
                                            .document(medicine.id)
                                            .update("isTaken", true)

                                        AlarmHelper.cancelAlarm(context, medicine.id)
                                    }
                                ) {
                                    Text("Taken")
                                }

                                // NOT TAKEN
                                Button(
                                    onClick = {

                                        val report = hashMapOf(
                                            "medicineId" to medicine.id,
                                            "name" to medicine.name,
                                            "status" to "not_taken",
                                            "date" to todayDate,
                                            "timestamp" to System.currentTimeMillis()
                                        )

                                        db.collection("users")
                                            .document(userId!!)
                                            .collection("reports")
                                            .add(report)

                                        db.collection("users")
                                            .document(userId)
                                            .collection("medicines")
                                            .document(medicine.id)
                                            .update("isTaken", false)

                                        //RESCHEDULE
                                        MedicineReminderManager(context)
                                            .scheduleCustomAlarm(
                                                medicine.hour,
                                                medicine.minute,
                                                medicine.name,
                                                medicine.id,
                                                medicine.dosage
                                            )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Not Taken")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}