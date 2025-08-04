package at.mcbabo.corex.data.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.mcbabo.corex.data.models.AppSettings
import at.mcbabo.corex.data.models.Language
import at.mcbabo.corex.data.models.ThemeMode
import at.mcbabo.corex.data.models.WeightUnit
import at.mcbabo.corex.data.repositories.SettingsRepository
import at.mcbabo.corex.util.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    application: Application,
    private val settingsRepository: SettingsRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {
    private val appContext = application.applicationContext

    // Expose settings as StateFlow for Compose
    val settings: StateFlow<AppSettings> =
        settingsRepository.settingsFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppSettings()
            )

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColors(enabled)
        }
    }

    // TODO()
    fun setWeightUnit(unit: WeightUnit) {
        viewModelScope.launch {
            settingsRepository.setWeightUnit(unit)
        }
    }

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)

            val localeList = LocaleListCompat.forLanguageTags(language.symbol)
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }

    // Workout defaults actions
    fun setDefaultReps(reps: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultReps(reps)
        }
    }

    fun setDefaultSets(sets: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultSets(sets)
        }
    }

    fun toggleNotifications() {
        viewModelScope.launch {
            val updated = settingsRepository.toggleNotifications()

            if (updated.notificationsEnabled) {
                notificationScheduler.scheduleDailyWork(appContext)
            } else {
                notificationScheduler.cancelWork(appContext)
            }
        }
    }

    fun setReminderTime(reminderTime: String, context: Context) {
        viewModelScope.launch {
            settingsRepository.setReminderTime(reminderTime)
            try {
                notificationScheduler.rescheduleWithNewTime(context)
                Log.d(
                    "SettingsViewModel",
                    "Successfully rescheduled notifications for $reminderTime"
                )
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Failed to reschedule notifications", e)
            }
        }
    }
}
