package com.example.medassist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.medassist.SessionManager
import com.example.medassist.NavGraph
import com.example.medassist.Routes
import com.example.medassist.ui.theme.MedAssistTheme

import android.Manifest
import android.os.Build
import androidx.core.app.ActivityCompat


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)

        setContent {
            MedAssistTheme {

                val navController = rememberNavController()


                val startDestination = if (sessionManager.isLoggedIn()) {
                    when (sessionManager.getUserRole()) {
                        "doctor" -> Routes.DOCTOR_DASHBOARD
                        else -> Routes.PATIENT_DASHBOARD
                    }
                } else {
                    Routes.LOGIN
                }

                if (Build.VERSION.SDK_INT >= 33) {
                    requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1
                    )
                }


                NavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    sessionManager = sessionManager
                )
            }
        }
    }
}