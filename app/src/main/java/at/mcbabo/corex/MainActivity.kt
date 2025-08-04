package at.mcbabo.corex

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import at.mcbabo.corex.data.models.AppSettings
import at.mcbabo.corex.data.models.ThemeMode
import at.mcbabo.corex.data.repositories.SettingsRepository
import at.mcbabo.corex.navigation.CoreXNavGraph
import at.mcbabo.corex.ui.theme.AppTheme
import at.mcbabo.corex.util.DismissKeyboard
import at.mcbabo.corex.util.NotificationScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            lifecycleScope.launch {
                if (isGranted) {
                    Log.d("Permission", "Notification permission granted")

                    settingsRepository.updateSettings { settings ->
                        settings.copy(notificationsEnabled = true)
                    }
                    scheduleNotifications()
                } else {
                    Log.d("Permission", "Notification permission denied")

                    settingsRepository.updateSettings { settings ->
                        settings.copy(notificationsEnabled = false)
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        window.setBackgroundDrawableResource(android.R.color.transparent)

        lifecycleScope.launch {
            if (settingsRepository.isFirstLaunch()) {
                settingsRepository.markFirstLaunchComplete()
            }
        }

        enableEdgeToEdge()

        setContent {
            val settings by settingsRepository.settingsFlow.collectAsStateWithLifecycle(
                initialValue = AppSettings()
            )

            val isDarkTheme =
                when (settings.selectedTheme) {
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                }

            val dynamicColorsEnabled = settings.dynamicColors

            val navController = rememberNavController()

            AppTheme(
                darkTheme = isDarkTheme,
                dynamicColor = dynamicColorsEnabled
            ) {
                SystemBarsTheme()

                DismissKeyboard {
                    CoreXNavGraph(navController = navController)
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        when (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            PackageManager.PERMISSION_GRANTED -> {
                Log.d("Permission", "Notification permission already granted")
                scheduleNotifications()
            }

            else -> {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun scheduleNotifications() {
        lifecycleScope.launch {
            try {
                val settings = settingsRepository.settingsFlow.first()
                if (settings.notificationsEnabled) {
                    notificationScheduler.scheduleDailyWork(this@MainActivity)
                    Log.d("MainActivity", "Notifications scheduled successfully")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to schedule notifications", e)
            }
        }
    }
}

@Composable
fun SystemBarsTheme(backgroundColor: Color = MaterialTheme.colorScheme.background) {
    val activity = LocalActivity.current

    LaunchedEffect(backgroundColor) {
        activity?.window?.setBackgroundDrawable(
            backgroundColor.toArgb().toDrawable()
        )
    }
}
