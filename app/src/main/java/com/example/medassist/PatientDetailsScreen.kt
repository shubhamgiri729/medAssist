package com.example.medassist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medassist.model.Medicine
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDetailsScreen(
    userId: String,
    patientName: String,
    onBack: () -> Unit,
    onAddPrescription: () -> Unit
) {

    val db = FirebaseFirestore.getInstance()

    var medicines by remember { mutableStateOf(listOf<Medicine>()) }
    var reports by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    //FETCH MEDICINES
    LaunchedEffect(Unit) {
        db.collection("users")
            .document(userId)
            .collection("medicines")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {
                    medicines = snapshot.documents.map {
                        Medicine(
                            id = it.id,
                            name = it.getString("name") ?: "",
                            dosage = it.getString("dosage") ?: "",
                            hour = it.getLong("hour")?.toInt() ?: 0,
                            minute = it.getLong("minute")?.toInt() ?: 0,
                            patientId = userId,
                            isTaken = it.getBoolean("isTaken") ?: false,
                            status = it.getString("status") ?: "pending",
                            endDate = it.getString("endDate") ?: "" // ✅ FIXED (String date)
                        )
                    }
                }
            }
    }

    //FETCH REPORTS
    LaunchedEffect(Unit) {
        db.collection("users")
            .document(userId)
            .collection("reports")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {
                    reports = snapshot.documents.map { it.data ?: emptyMap() }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(patientName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPrescription) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(padding)) {

            item {
                Text(
                    "Prescriptions",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            //MEDICINES LIST
            items(medicines) { medicine ->

                val isExpired = medicine.endDate < today

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(" ${medicine.name}")
                        Text("Dosage: ${medicine.dosage}")

                        val time = String.format(
                            "%02d:%02d",
                            medicine.hour,
                            medicine.minute
                        )
                        Text("Time: $time")

                        Text("End Date: ${medicine.endDate}")

                        Spacer(modifier = Modifier.height(6.dp))

                        //STATUS
                        Text(
                            text = when (medicine.status) {
                                "taken" -> "Taken"
                                "not_taken" -> "Not Taken"
                                else -> "Pending"
                            }
                        )

                        //EXPIRY
                        if (isExpired) {
                            Text(
                                "Prescription expired",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        //DELETE ONLY (clean UI)
                        Button(
                            onClick = {
                                db.collection("users")
                                    .document(userId)
                                    .collection("medicines")
                                    .document(medicine.id)
                                    .delete()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete Prescription")
                        }
                    }
                }
            }

            //REPORT SECTION
            item {
                Text(
                    "Medicine Report",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

            items(reports) { report ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(" ${report["name"]}")
                        Text("Date: ${report["date"]}")

                        val status = report["status"]

                        Text(
                            text = when (status) {
                                "taken" -> "Taken"
                                "not_taken" -> "Not Taken"
                                else -> "Unknown"
                            }
                        )
                    }
                }
            }
        }
    }
}