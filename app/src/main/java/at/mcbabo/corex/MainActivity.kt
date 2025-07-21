package at.mcbabo.corex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
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
            AppTheme(darkTheme = isSystemInDarkTheme()) {
                navController = rememberNavController()
                DismissKeyboard {
                    CoreXNavGraph(navController = navController)
                }
            }
        }
    }
}
