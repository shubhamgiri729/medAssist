package com.example.medassist

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Auth State
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // LOGIN
    fun login(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Please fill all fields")
            return
        }

        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authState.value = AuthState.Success
            }
            .addOnFailureListener {
                _authState.value =
                    AuthState.Error(it.message ?: "Login Failed")
            }
    }

    // SIGNUP
    fun signup(
        name: String,
        phone: String,
        email: String,
        password: String,
        role: String
    ) {

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Please fill all fields")
            return
        }

        _authState.value = AuthState.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val uid = auth.currentUser?.uid

                if (uid == null) {
                    _authState.value = AuthState.Error("User ID not found")
                    return@addOnSuccessListener
                }

                val userMap = hashMapOf(
                    "name" to name,
                    "phone" to phone,
                    "email" to email,
                    "role" to role,
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("users")
                    .document(uid)
                    .set(userMap)
                    .addOnSuccessListener {
                        _authState.value = AuthState.Success
                    }
                    .addOnFailureListener {
                        _authState.value =
                            AuthState.Error(it.message ?: "Firestore Error")
                    }
            }
            .addOnFailureListener {
                _authState.value =
                    AuthState.Error(it.message ?: "Signup Failed")
            }
    }

    // FETCH ROLE (VERY IMPORTANT)
    fun fetchUserRole(uid: String, onResult: (String) -> Unit) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val role = it.getString("role") ?: "patient"
                onResult(role)
            }
            .addOnFailureListener {
                onResult("patient") // fallback
            }
    }

    // Get userId
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // Logout
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }
}