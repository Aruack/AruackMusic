package com.aruack.music.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.aruack.music.core.datastore.UserPreferences
import com.aruack.music.core.datastore.UserPreferencesRepository
import com.aruack.music.ui.navigation.MainAppNavigation
import com.aruack.music.ui.theme.AruackMusicTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferencesRepository: UserPreferencesRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val userPrefs by preferencesRepository.userPreferencesFlow.collectAsState(initial = UserPreferences())

            AruackMusicTheme(
                appTheme = userPrefs.theme,
                accentColor = userPrefs.accentColor
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainAppNavigation()
                }
            }
        }
    }
}
