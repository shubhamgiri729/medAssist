package com.example.medassist.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.medassist.model.Medicine

@Composable
fun MedicineCard(
    medicine: Medicine,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(medicine.name, style = MaterialTheme.typography.titleLarge)
                Text("Dosage: ${medicine.dosage}")
                val formattedTime = String.format("%02d:%02d", medicine.hour, medicine.minute)
                Text("Time: $formattedTime")
            }

            Checkbox(
                checked = medicine.isTaken,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
