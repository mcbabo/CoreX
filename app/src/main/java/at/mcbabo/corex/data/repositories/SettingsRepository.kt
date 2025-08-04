package at.mcbabo.corex.data.repositories

import at.mcbabo.corex.data.datastore.SettingsDataStore
import at.mcbabo.corex.data.models.AppSettings
import at.mcbabo.corex.data.models.Language
import at.mcbabo.corex.data.models.ThemeMode
import at.mcbabo.corex.data.models.WeightUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository
@Inject
constructor(private val settingsDataStore: SettingsDataStore) {
    // Expose settings as Flow for reactive UI
    val settingsFlow: Flow<AppSettings> = settingsDataStore.settingsFlow

    // Get current settings (suspend function)
    suspend fun getCurrentSettings(): AppSettings = settingsFlow.first()

    // Update entire settings object
    suspend fun updateSettings(settings: AppSettings) {
        settingsDataStore.updateSettings(settings)
    }

    // Update settings using a lambda
    suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        settingsDataStore.updateSettings(update)
    }

    suspend fun setLanguage(language: Language) {
        updateSettings { it.copy(language = language) }
    }

    suspend fun setWeightUnit(unit: WeightUnit) {
        updateSettings { it.copy(weightUnit = unit) }
    }

    suspend fun setTheme(theme: ThemeMode) {
        updateSettings { it.copy(selectedTheme = theme) }
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        updateSettings { it.copy(dynamicColors = enabled) }
    }

    suspend fun toggleNotifications(): AppSettings {
        var updatedSettings: AppSettings? = null
        updateSettings {
            it.copy(notificationsEnabled = !it.notificationsEnabled).also { updated ->
                updatedSettings = updated
            }
        }
        return updatedSettings ?: throw IllegalStateException("Failed to update settings")
    }

    suspend fun setReminderTime(time: String) {
        updateSettings { it.copy(reminderTime = time) }
    }

    suspend fun markFirstLaunchComplete() {
        updateSettings { it.copy(firstLaunch = false) }
    }

    // Workout defaults methods
    suspend fun setDefaultReps(reps: Int) {
        updateSettings { it.copy(defaultReps = reps) }
    }

    suspend fun setDefaultSets(sets: Int) {
        updateSettings { it.copy(defaultSets = sets) }
    }

    // Reset to defaults
    suspend fun resetToDefaults() {
        updateSettings(AppSettings())
    }

    // Check specific conditions
    suspend fun isFirstLaunch(): Boolean = getCurrentSettings().firstLaunch
}
