package com.example.medassist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {

    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    // ✅ SAFE OBSERVER
    LaunchedEffect(Unit) {
        viewModel.authState.collect { state ->

            if (state is AuthState.Success) {

                val userId = viewModel.getCurrentUserId()

                if (userId != null) {

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { document ->

                            val role = document.getString("role") ?: "patient"

                            sessionManager.saveUserSession(userId, role)

                            onLoginSuccess()
                        }
                } else {
                    onLoginSuccess()
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text("Login", style = MaterialTheme.typography.headlineMedium)

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        viewModel.login(email.trim(), password.trim())
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                TextButton(onClick = onSignupClick) {
                    Text("Don't have an account? Sign Up")
                }

                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                if (authState is AuthState.Error) {
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}