package com.example.medassist

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    role: String,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val db = FirebaseFirestore.getInstance()
    val userId = authViewModel.getCurrentUserId()

    var name by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }
    var phone by remember { mutableStateOf("Loading...") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        name = document.getString("name") ?: "N/A"
                        email = document.getString("email") ?: "N/A"
                        phone = document.get("phone")?.toString() ?: document.get("number")?.toString() ?: "N/A"
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Icon
            Surface(
                modifier = Modifier.size(100.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Name
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Role Badge
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = role.uppercase(),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Details Section
            ProfileDetailItem(icon = Icons.Default.Email, label = "Email", value = email)
            ProfileDetailItem(icon = Icons.Default.Phone, label = "Phone", value = phone)

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ProfileDetailItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}