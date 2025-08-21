package at.mcbabo.corex.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.navArgument
import androidx.navigation.navigation
import at.mcbabo.corex.ui.animatedComposable
import at.mcbabo.corex.ui.screens.CreateExerciseScreen
import at.mcbabo.corex.ui.screens.CreateWorkoutScreen
import at.mcbabo.corex.ui.screens.EditWorkoutScreen
import at.mcbabo.corex.ui.screens.ExercisesScreen
import at.mcbabo.corex.ui.screens.HomeScreen
import at.mcbabo.corex.ui.screens.SettingsScreen
import at.mcbabo.corex.ui.screens.WeightProgressionDetailScreen
import at.mcbabo.corex.ui.screens.WorkoutScreen
import at.mcbabo.corex.ui.screens.settings.GeneralSettingsScreen
import at.mcbabo.corex.ui.screens.settings.UnitsSettingsScreen
import at.mcbabo.corex.ui.screens.settings.appearance.AppearanceSettingsScreen
import at.mcbabo.corex.ui.screens.settings.appearance.ColorModeSettingsScreen
import at.mcbabo.corex.ui.screens.settings.appearance.LanguageSettingsScreen

@Composable
fun CoreXNavGraph(
    navController: NavHostController,
    startDestination: Screen = Screen.Home
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route
    ) {
        // Home screen
        animatedComposable(Screen.Home.route) {
            HomeScreen(navController)
        }

        // Exercise screens
        animatedComposable(Screen.Exercises.route) {
            ExercisesScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        animatedComposable(Screen.CreateExercise.route) {
            CreateExerciseScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Workout screens
        animatedComposable(Screen.CreateWorkout.route) {
            CreateWorkoutScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        animatedComposable(
            route = Screen.EditWorkout.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
            EditWorkoutScreen(
                navController = navController,
                workoutId = workoutId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        animatedComposable(
            route = Screen.WorkoutDetail.route,
            arguments = listOf(
                navArgument("workoutId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
            WorkoutScreen(
                navController = navController,
                workoutId = workoutId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        animatedComposable(
            route = Screen.WeightProgressionDetail.route,
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
            WeightProgressionDetailScreen(
                exerciseId = exerciseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Settings navigation graph
        settingsNavGraph(
            navController = navController,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

fun NavGraphBuilder.settingsNavGraph(navController: NavController, onNavigateBack: () -> Unit) {
    navigation(
        startDestination = Screen.Settings.route,
        route = Screen.SettingsGraph.route
    ) {
        animatedComposable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                onNavigateBack = onNavigateBack
            )
        }

        animatedComposable(Screen.GeneralSettings.route) {
            GeneralSettingsScreen(onNavigateBack = onNavigateBack)
        }

        animatedComposable(Screen.LanguageSettings.route) {
            LanguageSettingsScreen(onNavigateBack = onNavigateBack)
        }

        animatedComposable(Screen.UnitsSettings.route) {
            UnitsSettingsScreen(onNavigateBack = onNavigateBack)
        }

        // Nested appearance settings graph
        appearanceSettingsNavGraph(
            navController = navController,
            onNavigateBack = onNavigateBack
        )
    }
}

fun NavGraphBuilder.appearanceSettingsNavGraph(
    navController: NavController,
    onNavigateBack: () -> Unit
) {
    navigation(
        startDestination = Screen.AppearanceSettings.route,
        route = Screen.AppearanceSettingsGraph.route
    ) {
        animatedComposable(Screen.AppearanceSettings.route) {
            AppearanceSettingsScreen(
                navController = navController,
                onNavigateBack = onNavigateBack
            )
        }

        animatedComposable(Screen.ColorModeSettings.route) {
            ColorModeSettingsScreen(onNavigateBack = onNavigateBack)
        }
    }
}