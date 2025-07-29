package at.mcbabo.corex.data.viewmodels


import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.mcbabo.corex.NotificationScheduler
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
    private val settingsRepository: SettingsRepository,
    private val notificationScheduler: NotificationScheduler
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

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
            _events.emit(SettingsEvent.ShowMessage("Theme set to ${theme.displayName}"))
        }
    }

    fun setWeightUnit(unit: WeightUnit) {
        viewModelScope.launch {
            settingsRepository.setWeightUnit(unit)
            _events.emit(SettingsEvent.ShowMessage("Weight unit set to ${unit.displayName}"))
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
            _events.emit(SettingsEvent.ShowMessage("Reminder set to $reminderTime"))
        }
    }

}

sealed class SettingsEvent {
    data class ShowMessage(val message: String) : SettingsEvent()
    object NavigateBack : SettingsEvent()
}