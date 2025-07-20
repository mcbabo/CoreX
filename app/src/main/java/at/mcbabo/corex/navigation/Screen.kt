package at.mcbabo.corex.navigation

sealed class Screen(val route: String) {
    object Home : Screen(route = "home_screen")
    object Exercises : Screen(route = "exercises_screen")
    object Exercise : Screen(route = "exercise_screen/{exerciseId}") {
        fun passExerciseId(exerciseId: Long) = "exercise_screen/$exerciseId"
    }

    object CreateWorkout : Screen(route = "create_workout_screen")
    object WorkoutDetail : Screen(route = "workout_detail_screen/{workoutId}") {
        fun passWorkoutId(workoutId: Long) = "workout_detail_screen/$workoutId"
    }
}