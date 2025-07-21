package at.mcbabo.corex.data.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.mcbabo.corex.data.models.AppSettings
import at.mcbabo.corex.data.models.ThemeMode
import at.mcbabo.corex.data.models.WeightUnit
import at.mcbabo.corex.data.repositories.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // Expose settings as StateFlow for Compose
    val settings: StateFlow<AppSettings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    // Events for one-time actions (like showing snackbars)
    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    // Actions
    fun toggleDarkMode() {
        viewModelScope.launch {
            settingsRepository.toggleDarkMode()
            _events.emit(SettingsEvent.ShowMessage("Theme updated"))
        }
    }

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
            _events.emit(SettingsEvent.ShowMessage("Theme set to ${theme.displayName}"))
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(language)
            _events.emit(SettingsEvent.ShowMessage("Language updated"))
        }
    }

    fun setWeightUnit(unit: WeightUnit) {
        viewModelScope.launch {
            settingsRepository.setWeightUnit(unit)
            _events.emit(SettingsEvent.ShowMessage("Weight unit set to ${unit.displayName}"))
        }
    }

    fun toggleNotifications() {
        viewModelScope.launch {
            settingsRepository.toggleNotifications()
            val enabled = settingsRepository.getCurrentSettings().notificationsEnabled
            val message = if (enabled) "Notifications enabled" else "Notifications disabled"
            _events.emit(SettingsEvent.ShowMessage(message))
        }
    }

    fun toggleAutoBackup() {
        viewModelScope.launch {
            settingsRepository.toggleAutoBackup()
            val enabled = settingsRepository.getCurrentSettings().autoBackup
            val message = if (enabled) "Auto backup enabled" else "Auto backup disabled"
            _events.emit(SettingsEvent.ShowMessage(message))
        }
    }

    fun setReminderTime(time: String) {
        viewModelScope.launch {
            settingsRepository.setReminderTime(time)
            _events.emit(SettingsEvent.ShowMessage("Reminder time updated"))
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            settingsRepository.resetToDefaults()
            _events.emit(SettingsEvent.ShowMessage("Settings reset to defaults"))
        }
    }

    // Workout defaults actions
    fun setDefaultReps(reps: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultReps(reps)
            _events.emit(SettingsEvent.ShowMessage("Default reps set to $reps"))
        }
    }

    fun setDefaultSets(sets: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultSets(sets)
            _events.emit(SettingsEvent.ShowMessage("Default sets set to $sets"))
        }
    }


    fun markFirstLaunchComplete() {
        viewModelScope.launch {
            settingsRepository.markFirstLaunchComplete()
        }
    }
}

sealed class SettingsEvent {
    data class ShowMessage(val message: String) : SettingsEvent()
    object NavigateBack : SettingsEvent()
}