package com.example.medassist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDashboard(
    onPatientClick: (String, String) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: DoctorViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val patients by viewModel.patients.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Dashboard") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            item {
                Text(
                    "My Patients",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (patients.isEmpty()) {
                item {
                    Text(
                        "No patients found.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            items(patients) { patient ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {

                    Column(modifier = Modifier.padding(16.dp)) {

                        // SHOW NAME
                        Text(
                            text = patient.name,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // SHOW PHONE
                        Text(
                            text = "Phone: ${patient.phone.ifEmpty { "N/A" }}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        //SHOW EMAIL
                        Text(
                            text = "Email: ${patient.email.ifEmpty { "N/A" }}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onPatientClick(patient.userId, patient.name)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Manage Patient")
                        }
                    }
                }
            }
        }
    }
}