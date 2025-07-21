package at.mcbabo.corex.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import androidx.navigation.navigation
import at.mcbabo.corex.ui.animatedComposable
import at.mcbabo.corex.ui.motion.EmphasizeEasing
import at.mcbabo.corex.ui.screens.CreateWorkoutScreen
import at.mcbabo.corex.ui.screens.EditWorkoutScreen
import at.mcbabo.corex.ui.screens.ExercisesScreen
import at.mcbabo.corex.ui.screens.HomeScreen
import at.mcbabo.corex.ui.screens.SettingsScreen
import at.mcbabo.corex.ui.screens.WorkoutScreen
import at.mcbabo.corex.ui.screens.settings.AppearanceSettings
import at.mcbabo.corex.ui.screens.settings.GeneralSettings
import at.mcbabo.corex.ui.screens.settings.UnitsSettings

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
fun CoreXNavGraph(
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
            CreateWorkoutScreen(navController, { navController.popBackStack() }, {})
        }
        animatedComposable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })

        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0
            WorkoutScreen(
                navController = navController,
                workoutId,
                { navController.popBackStack() }
            )
        }

        animatedComposable(
            route = Screen.EditWorkout.route,
            arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0
            EditWorkoutScreen(navController, workoutId, { navController.popBackStack() })
        }

        animatedComposable(
            route = Screen.Exercises.route
        ) { backStackEntry ->
            ExercisesScreen(navController, { navController.popBackStack() })
        }

        settings(
            navController = navController,
            onNavigateBack = { navController.popBackStack() },
            onNavigateTo = { route -> navController.navigate(route.route) }
        )
    }
}

fun NavGraphBuilder.settings(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onNavigateTo: (route: Screen) -> Unit
) {
    navigation(
        startDestination = Screen.Settings.route,
        route = Screen.SettingsGraph.route
    ) {
        animatedComposable(
            route = Screen.Settings.route
        ) { backStackEntry ->
            SettingsScreen(navController, onNavigateBack)
        }

        animatedComposable(
            route = Screen.GeneralSettings.route
        ) { backStackEntry ->
            GeneralSettings(onNavigateBack)
        }

        animatedComposable(
            route = Screen.AppearanceSettings.route
        ) { backStackEntry ->
            AppearanceSettings(onNavigateBack)
        }

        animatedComposable(
            route = Screen.UnitsSettings.route
        ) { backStackEntry ->
            UnitsSettings(onNavigateBack)
        }
    }
}