package com.example.medassist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("patient") }

    val authState by viewModel.authState.collectAsState()

    // SAFE OBSERVER
    LaunchedEffect(Unit) {
        viewModel.authState.collect { state ->
            if (state is AuthState.Success) {
                kotlinx.coroutines.delay(300)
                onSignupSuccess()
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

                Text("Create Account", style = MaterialTheme.typography.headlineMedium)

                // NAME
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // PHONE
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

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

                Text("Select Role")

                Row(horizontalArrangement = Arrangement.SpaceEvenly) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRole == "patient",
                            onClick = { selectedRole = "patient" }
                        )
                        Text("Patient")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedRole == "doctor",
                            onClick = { selectedRole = "doctor" }
                        )
                        Text("Doctor")
                    }
                }

                Button(
                    onClick = {
                        viewModel.signup(
                            name = name,
                            phone = phone,
                            email = email,
                            password = password,
                            role = selectedRole
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Up")
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