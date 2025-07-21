package at.mcbabo.corex.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val isDarkMode: Boolean = false,
    val language: String = "en",
    val weightUnit: WeightUnit = WeightUnit.KG,
    val notificationsEnabled: Boolean = true,
    val autoBackup: Boolean = false,
    val reminderTime: String = "09:00",
    val firstLaunch: Boolean = true,
    val lastSyncTimestamp: Long = 0L,
    val selectedTheme: AppTheme = AppTheme.SYSTEM,
    // Workout defaults
    val defaultReps: Int = 10,
    val defaultSets: Int = 3,
)

@Serializable
enum class WeightUnit(val displayName: String, val symbol: String) {
    KG("Kilograms", "kg"),
    LBS("Pounds", "lbs")
}

@Serializable
enum class AppTheme(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System Default")
}