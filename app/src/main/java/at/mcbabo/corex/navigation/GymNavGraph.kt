package at.mcbabo.corex.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import at.mcbabo.corex.ui.animatedComposable
import at.mcbabo.corex.ui.motion.EmphasizeEasing
import at.mcbabo.corex.ui.screens.CreateWorkoutScreen
import at.mcbabo.corex.ui.screens.ExercisesScreen
import at.mcbabo.corex.ui.screens.HomeScreen
import at.mcbabo.corex.ui.screens.WorkoutScreen

const val DURATION_ENTER = 400
const val DURATION_EXIT = 200
const val initialOffset = 0.10f

private fun <T> enterTween() = tween<T>(durationMillis = DURATION_ENTER, easing = EmphasizeEasing)

private fun <T> exitTween() = tween<T>(durationMillis = DURATION_ENTER, easing = EmphasizeEasing)

private val fadeSpring =
    spring<Float>(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
private val fadeTween = tween<Float>(durationMillis = DURATION_EXIT)

private val fadeSpec = fadeTween

@Composable
fun GymNavGraph(
    navController: NavHostController,
    startDestination: Screen = Screen.Home
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route
    ) {
        animatedComposable(
            route = Screen.Home.route
        ) { backStackEntry ->
            HomeScreen(navController)
        }
        animatedComposable(
            route = Screen.CreateWorkout.route
        ) { backStackEntry ->
            CreateWorkoutScreen(navController, {}, {})
        }
        animatedComposable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })

        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0
            WorkoutScreen(navController = navController, workoutId)
        }

        animatedComposable(
            route = Screen.Exercises.route
        ) { backStackEntry ->
            ExercisesScreen(navController)
        }
        /*
        animatedComposable(
            route = Screen.Exercise.route,
            arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0
            ExerciseScreen(navController, exerciseId)
        }

        animatedComposable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0
            WorkoutDetailScreen(navController, workoutId)
        }
        */
    }
}