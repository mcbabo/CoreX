package at.mcbabo.corex.navigation

sealed class Screen(val route: String) {
    object Home : Screen(route = "home_screen")
    object Exercises : Screen(route = "exercises_screen")
    object Settings : Screen(route = "settings_screen")

    object CreateWorkout : Screen(route = "create_workout_screen")
    object WorkoutDetail : Screen(route = "workout_detail_screen/{workoutId}") {
        fun passWorkoutId(workoutId: Long) = "workout_detail_screen/$workoutId"
    }
}