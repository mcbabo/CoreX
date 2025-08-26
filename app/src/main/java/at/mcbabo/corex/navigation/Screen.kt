package at.mcbabo.corex.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")

    // EXERCISES
    object Exercises : Screen("exercises")
    object CreateExercise : Screen("create_exercise")

    // WORKOUTS
    object CreateWorkout : Screen("create_workout")

    object EditWorkout : Screen("edit_workout/{workoutId}") {
        fun createRoute(workoutId: Long) = "edit_workout/$workoutId"
    }

    object WorkoutDetail : Screen("workout_detail/{workoutId}") {
        fun createRoute(workoutId: Long) = "workout_detail/$workoutId"
    }

    object WeightProgressionDetail : Screen("weight_progression_detail/{exerciseId}") {
        fun createRoute(exerciseId: Long) = "weight_progression_detail/$exerciseId"
    }

    // SETTINGS - Graph routes
    object SettingsGraph : Screen("settings_graph")
    object AppearanceSettingsGraph : Screen("appearance_settings_graph")

    // SETTINGS - Screen routes
    object Settings : Screen("settings")
    object GeneralSettings : Screen("general_settings")
    object AppearanceSettings : Screen("appearance_settings")
    object ColorModeSettings : Screen("color_mode_settings")
    object LanguageSettings : Screen("language_settings")
    object UnitsSettings : Screen("units_settings")
    object DevSettings : Screen("dev_settings")
}