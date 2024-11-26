package com.example.gameofthrones.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "settings_prefs"
        private const val KEY_EMAIL = "key_email"
        private const val KEY_NOTIFICATIONS = "key_notifications"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getEmail(): String {
        return sharedPreferences.getString(KEY_EMAIL, "") ?: ""
    }

    fun setEmail(email: String) {
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun getNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true)
    }

    fun setNotificationsEnabled(isEnabled: Boolean) {
        editor.putBoolean(KEY_NOTIFICATIONS, isEnabled)
        editor.apply()
    }
}
