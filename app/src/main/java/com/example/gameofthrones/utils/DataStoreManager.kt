package com.example.gameofthrones.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DataStoreManager(private val context: Context) {

    companion object {
        private const val PREFERENCES_NAME = "app_settings"

        private val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        private val FONT_SIZE_KEY = intPreferencesKey("font_size")

        private val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)
    }

    suspend fun getDarkTheme(): Boolean {
        return getData(DARK_THEME_KEY, false)
    }

    fun setDarkTheme(isDarkTheme: Boolean) {
        saveData(DARK_THEME_KEY, isDarkTheme)
    }

    suspend fun getFontSize(): Int {
        return getData(FONT_SIZE_KEY, 16)
    }

    fun setFontSize(fontSize: Int) {
        saveData(FONT_SIZE_KEY, fontSize)
    }

    private suspend fun <T> getData(key: Preferences.Key<T>, defaultValue: T): T {
        val flow: Flow<T> = context.dataStore.data
            .map { preferences -> preferences[key] ?: defaultValue }
        return flow.first()
    }

    private fun <T> saveData(key: Preferences.Key<T>, value: T) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }
}
