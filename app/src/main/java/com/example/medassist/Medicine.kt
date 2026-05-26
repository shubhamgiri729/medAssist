package com.example.medassist.model

data class Medicine(
    val id: String = "",
    val name: String = "",
    val dosage: String = "",
    val hour: Int = 0,
    val minute: Int = 0,
    val patientId: String = "",
    val isTaken: Boolean = false,
    val status: String = "pending",
    val endDate: String = ""
)