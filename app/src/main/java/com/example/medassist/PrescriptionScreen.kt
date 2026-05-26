package com.example.medassist

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrescriptionScreen(
    userId: String,
    patientName: String,
    onBack: () -> Unit
) {

    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }

    // NEW: proper time input
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }

    // NEW: duration in days
    var days by remember { mutableStateOf("") }

    val db = FirebaseFirestore.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prescribe for $patientName") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = medicineName,
                onValueChange = { medicineName = it },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage") },
                modifier = Modifier.fillMaxWidth()
            )

            // TIME INPUT (HOUR + MINUTE)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                OutlinedTextField(
                    value = hour,
                    onValueChange = { hour = it },
                    label = { Text("Hour (0-23)") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = minute,
                    onValueChange = { minute = it },
                    label = { Text("Minute (0-59)") },
                    modifier = Modifier.weight(1f)
                )
            }

            // DURATION INPUT
            OutlinedTextField(
                value = days,
                onValueChange = { days = it },
                label = { Text("Duration (days)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {

                    try {
                        val h = hour.toInt()
                        val m = minute.toInt()
                        val d = days.toInt()

                        val currentTime = System.currentTimeMillis()
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val endDate = sdf.format(Date(currentTime + (d * 24 * 60 * 60 * 1000L)))

                        val medicine = hashMapOf(
                            "name" to medicineName,
                            "dosage" to dosage,
                            "hour" to h,
                            "minute" to m,
                            "isTaken" to false,
                            "status" to "pending",
                            "startDate" to currentTime,
                            "endDate" to endDate
                        )

                        db.collection("users")
                            .document(userId)
                            .collection("medicines")
                            .add(medicine)

                        onBack()

                    } catch (e: Exception) {
                        println("Invalid input")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Prescription")
            }
        }
    }
}