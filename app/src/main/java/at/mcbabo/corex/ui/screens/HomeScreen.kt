package at.mcbabo.corex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.mcbabo.corex.data.viewmodels.WorkoutViewModel
import at.mcbabo.corex.navigation.Screen
import at.mcbabo.corex.ui.components.WorkoutListItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = hiltViewModel()
) {
    val workouts by workoutViewModel.getWorkoutSummaries().collectAsState(initial = emptyList())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("My Workouts") },
                actions = {
                    IconButton(onClick = { navController.navigate(route = Screen.Exercises.route) }) {
                        Icon(
                            imageVector = Icons.Outlined.Build,
                            contentDescription = "Localized description",
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(route = Screen.CreateWorkout.route) }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Loading indicator
                if (workouts.isEmpty()) {
                    Text("No workouts yet")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(workouts) { workout ->
                            WorkoutListItem(workout, Modifier.padding(horizontal = 16.dp), {
                                navController.navigate(
                                    Screen.WorkoutDetail.passWorkoutId(
                                        workout.id
                                    )
                                )
                            }, {})
                        }
                    }
                }
            }
        }
    )
}
