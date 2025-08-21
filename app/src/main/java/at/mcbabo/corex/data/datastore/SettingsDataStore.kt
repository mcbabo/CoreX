package at.mcbabo.corex.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import at.mcbabo.corex.data.models.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

// Extension property to create DataStore instance
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_settings"
)

@Singleton
class SettingsDataStore
@Inject
constructor(private val context: Context) {
    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    companion object {
        private val SETTINGS_JSON_KEY = stringPreferencesKey("settings_json")
    }

    val settingsFlow: Flow<AppSettings> =
        context.settingsDataStore.data
            .catch { exception ->
                emit(emptyPreferences())
            }.map { preferences ->
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
            val currentSettings =
                if (currentSettingsJson != null) {
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
