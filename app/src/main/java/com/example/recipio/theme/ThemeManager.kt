package com.example.recipio.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.recipio.services.NotificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemeManager(private val context: Context) {

    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val ARE_NOTIFICATIONS_ENABLED = booleanPreferencesKey("are_notifications_enabled")
    }

    // Obtenir l'état actuel du mode sombre
    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_DARK_MODE] ?: false
        }

    // Obtenir l'état actuel des notifications
    val areNotificationsEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ARE_NOTIFICATIONS_ENABLED] ?: true // Par défaut activé
        }

    // Mettre à jour le mode sombre
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = enabled
        }
    }

    // Mettre à jour les notifications
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ARE_NOTIFICATIONS_ENABLED] = enabled
        }

        // Programmer ou annuler les notifications
        NotificationService.scheduleNotification(context, enabled)
    }
}