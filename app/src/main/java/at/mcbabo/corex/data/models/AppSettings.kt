package at.mcbabo.corex.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val selectedTheme: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColors: Boolean = false,

    val language: Language = Language.SYSTEM,

    val weightUnit: WeightUnit = WeightUnit.KG,

    val notificationsEnabled: Boolean = true,
    val reminderTime: String = "09:00",

    val firstLaunch: Boolean = true,

    val lastSyncTimestamp: Long = 0L,

    // Workout defaults
    val defaultReps: Int = 10,
    val defaultSets: Int = 3,
)

@Serializable
enum class Language(val displayName: String, val symbol: String?) {
    SYSTEM("System", null),
    EN("English", "en"),
    DE("German", "de")
}

@Serializable
enum class WeightUnit(val displayName: String, val symbol: String) {
    KG("Kilograms", "kg"),
    LBS("Pounds", "lbs") // kg * 2.21
}

@Serializable
enum class ThemeMode(val displayName: String, val symbol: Boolean?) {
    LIGHT("Light", false),
    DARK("Dark", true),
    SYSTEM("System Default", null)
}