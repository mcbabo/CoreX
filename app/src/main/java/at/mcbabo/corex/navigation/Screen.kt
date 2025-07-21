package at.mcbabo.corex.navigation

sealed class Screen(val route: String) {
    object Home : Screen(route = "home_screen")
    object Exercises : Screen(route = "exercises_screen")

    object CreateWorkout : Screen(route = "create_workout_screen")
    object EditWorkout : Screen(route = "edit_workout_screen/{workoutId}") {
        fun passWorkoutId(workoutId: Long) = "edit_workout_screen/$workoutId"
    }

    object WorkoutDetail : Screen(route = "workout_detail_screen/{workoutId}") {
        fun passWorkoutId(workoutId: Long) = "workout_detail_screen/$workoutId"
    }

    // SETTINGS
    object SettingsGraph : Screen(route = "settings_graph")

    object Settings : Screen(route = "settings_screen")
    object GeneralSettings : Screen(route = "general_settings_screen")
    object AppearanceSettings : Screen(route = "appearance_settings_screen")
    object UnitsSettings : Screen(route = "units_settings_screen")
}