package com.example.medassist

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.medassist.model.Patient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DoctorViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _patients = MutableStateFlow<List<Patient>>(emptyList())
    val patients: StateFlow<List<Patient>> = _patients

    init {
        fetchPatients()
    }

    private fun fetchPatients() {
        db.collection("users")
            .whereEqualTo("role", "patient")
            .addSnapshotListener { result, error ->

                if (error != null) {
                    Log.e("DoctorViewModel", "Error fetching patients", error)
                    _patients.value = emptyList()
                    return@addSnapshotListener
                }

                val list = result?.documents?.map {
                    val phone = it.get("phone")?.toString() ?: it.get("number")?.toString() ?: ""
                    Log.d("DoctorViewModel", "Patient: ${it.id}, Name: ${it.get("name")}, Phone: $phone")
                    
                    Patient(
                        userId = it.id,
                        name = it.getString("name") ?: "Unknown",
                        email = it.getString("email") ?: "",
                        phone = phone,
                        role = it.getString("role") ?: ""
                    )
                } ?: emptyList()

                _patients.value = list
            }
    }
}