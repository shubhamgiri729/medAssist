package com.example.medassist

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    sessionManager: SessionManager
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // LOGIN
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    val role = sessionManager.getUserRole()

                    if (role == "doctor") {
                        navController.navigate(Routes.DOCTOR_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Routes.PATIENT_DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                onSignupClick = {
                    navController.navigate(Routes.SIGNUP)
                }
            )
        }

        // SIGNUP
        composable(Routes.SIGNUP) {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SIGNUP) { inclusive = true }
                    }
                }
            )
        }

        // DOCTOR DASHBOARD
        composable(Routes.DOCTOR_DASHBOARD) {
            DoctorDashboard(
                onPatientClick = { userId, name ->
                    val encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Routes.PATIENT_DETAILS}/$userId/$encodedName")
                },
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        // PATIENT DASHBOARD
        composable(Routes.PATIENT_DASHBOARD) {
            PatientDashboard(
                onProfileClick = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        // PATIENT DETAILS
        composable("${Routes.PATIENT_DETAILS}/{userId}/{patientName}") { backStackEntry ->

            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val encodedName = backStackEntry.arguments?.getString("patientName") ?: ""
            // DECODE THE NAME
            val patientName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())

            PatientDetailsScreen(
                userId = userId,
                patientName = patientName,

                onBack = {
                    navController.popBackStack()
                },

                onAddPrescription = {
                    val reEncodedName = URLEncoder.encode(patientName, StandardCharsets.UTF_8.toString())
                    navController.navigate("${Routes.PRESCRIPTION}/$userId/$reEncodedName")
                }
            )
        }

        // PRESCRIPTION SCREEN
        composable("${Routes.PRESCRIPTION}/{userId}/{patientName}") { backStackEntry ->

            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val encodedName = backStackEntry.arguments?.getString("patientName") ?: ""
            // DECODE THE NAME
            val patientName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())

            PrescriptionScreen(
                userId = userId,
                patientName = patientName,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // PROFILE SCREEN
        composable(Routes.PROFILE) {
            ProfileScreen(
                role = sessionManager.getUserRole() ?: "patient",
                onBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    sessionManager.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}
