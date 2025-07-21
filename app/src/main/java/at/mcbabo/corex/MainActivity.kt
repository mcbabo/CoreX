package at.mcbabo.corex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import at.mcbabo.corex.data.models.AppSettings
import at.mcbabo.corex.data.models.ThemeMode
import at.mcbabo.corex.data.repositories.SettingsRepository
import at.mcbabo.corex.navigation.CoreXNavGraph
import at.mcbabo.corex.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var navController: NavHostController

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setBackgroundDrawableResource(android.R.color.transparent)

        lifecycleScope.launch {
            if (settingsRepository.isFirstLaunch()) {
                // Show onboarding or setup
                settingsRepository.markFirstLaunchComplete()
            }
        }

        enableEdgeToEdge()
        setContent {
            val settings by settingsRepository.settingsFlow.collectAsStateWithLifecycle(
                initialValue = AppSettings()
            )

            // Determine dark theme based on user preference
            val isDarkTheme = when (settings.selectedTheme) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            AppTheme(darkTheme = isDarkTheme) {
                SystemBarsTheme()

                navController = rememberNavController()
                DismissKeyboard {
                    CoreXNavGraph(navController = navController)
                }
            }
        }
    }
}

@Composable
fun SystemBarsTheme(
    backgroundColor: Color = MaterialTheme.colorScheme.background
) {
    val activity = LocalActivity.current

    LaunchedEffect(backgroundColor) {
        activity?.window?.setBackgroundDrawable(
            backgroundColor.toArgb().toDrawable()
        )
    }
}
