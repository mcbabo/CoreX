package at.mcbabo.corex.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import at.mcbabo.corex.data.models.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

// Extension property to create DataStore instance
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_settings"
)

@Singleton
class SettingsDataStore @Inject constructor(
    private val context: Context
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    companion object {
        // Option 1: Store as single JSON string (simpler)
        private val SETTINGS_JSON_KEY = stringPreferencesKey("settings_json")

        // Option 2: Store as individual keys (more granular)
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val WEIGHT_UNIT_KEY = stringPreferencesKey("weight_unit")
        private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications")
        private val AUTO_BACKUP_KEY = booleanPreferencesKey("auto_backup")
        private val REMINDER_TIME_KEY = stringPreferencesKey("reminder_time")
        private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
        private val LAST_SYNC_KEY = longPreferencesKey("last_sync")
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    // Method 1: JSON approach (recommended for complex settings)
    val settingsFlow: Flow<AppSettings> = context.settingsDataStore.data
        .catch { exception ->
            // Handle any errors gracefully
            emit(androidx.datastore.preferences.core.emptyPreferences())
        }
        .map { preferences ->
            val settingsJson = preferences[SETTINGS_JSON_KEY]
            if (settingsJson != null) {
                try {
                    json.decodeFromString<AppSettings>(settingsJson)
                } catch (e: Exception) {
                    AppSettings() // Return defaults if parsing fails
                }
            } else {
                AppSettings() // Return defaults if no settings exist
            }
        }

    suspend fun updateSettings(settings: AppSettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[SETTINGS_JSON_KEY] = json.encodeToString(settings)
        }
    }

    suspend fun updateSettings(update: (AppSettings) -> AppSettings) {
        context.settingsDataStore.edit { preferences ->
            val currentSettingsJson = preferences[SETTINGS_JSON_KEY]
            val currentSettings = if (currentSettingsJson != null) {
                try {
                    json.decodeFromString<AppSettings>(currentSettingsJson)
                } catch (e: Exception) {
                    AppSettings()
                }
            } else {
                AppSettings()
            }

            val updatedSettings = update(currentSettings)
            preferences[SETTINGS_JSON_KEY] = json.encodeToString(updatedSettings)
        }
    }

}