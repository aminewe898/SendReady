package com.example.sendready

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.sendready.core.designsystem.theme.SendReadyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.sendready.core.domain.repository.SettingsRepository
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by settingsRepository.isDarkMode.collectAsState(initial = isSystemInDarkTheme())
            SendReadyTheme(darkTheme = isDarkMode) {
                SendReadyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
