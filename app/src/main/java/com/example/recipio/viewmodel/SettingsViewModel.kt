package com.example.recipio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipio.theme.ThemeManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val themeManager = ThemeManager(application.applicationContext)

    // Flux pour observer les changements de thème et de notifications
    val isDarkMode: Flow<Boolean> = themeManager.isDarkModeFlow
    val areNotificationsEnabled: Flow<Boolean> = themeManager.areNotificationsEnabledFlow

    // Fonction pour changer le mode sombre
    suspend fun setDarkMode(enabled: Boolean) {
        themeManager.setDarkMode(enabled)
    }

    // Fonction pour activer/désactiver les notifications
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        themeManager.setNotificationsEnabled(enabled)
    }

    // Initialiser les notifications lors du lancement de l'application
    init {
        viewModelScope.launch {
            themeManager.areNotificationsEnabledFlow.collect { enabled ->
                // Programmer les notifications en fonction de l'état sauvegardé
                com.example.recipio.services.NotificationService.scheduleNotification(
                    getApplication<Application>().applicationContext,
                    enabled
                )
            }
        }
    }
}