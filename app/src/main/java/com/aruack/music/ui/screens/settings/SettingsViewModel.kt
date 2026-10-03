package com.aruack.music.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aruack.music.core.datastore.AccentColor
import com.aruack.music.core.datastore.AppTheme
import com.aruack.music.core.datastore.UserPreferences
import com.aruack.music.core.datastore.UserPreferencesRepository
import com.aruack.music.core.repository.MusicRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val musicRepository: MusicRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            preferencesRepository.setTheme(theme)
        }
    }

    fun setAccentColor(accentColor: AccentColor) {
        viewModelScope.launch {
            preferencesRepository.setAccentColor(accentColor)
        }
    }

    fun setOfflineModeOnly(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setOfflineModeOnly(enabled)
        }
    }

    fun setJamendoEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setJamendoEnabled(enabled)
        }
    }

    fun setArchiveEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setArchiveEnabled(enabled)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            musicRepository.clearHistory()
        }
    }
}
