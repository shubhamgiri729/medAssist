package com.example.medassist

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
    }

    // Save session
    fun saveUserSession(userId: String, role: String) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_ROLE, role)
            apply()
        }
    }

    // Check login
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Get userId
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    // Get role
    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    // Logout
    fun logout() {
        prefs.edit().clear().apply()
    }
}