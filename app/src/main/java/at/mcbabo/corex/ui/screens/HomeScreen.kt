package at.mcbabo.corex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.mcbabo.corex.R
import at.mcbabo.corex.data.viewmodels.WorkoutViewModel
import at.mcbabo.corex.navigation.Screen
import at.mcbabo.corex.ui.components.WorkoutListItem
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = hiltViewModel()
) {
    val workouts by workoutViewModel.getWorkoutSummaries().collectAsState(initial = emptyList())


    val today = LocalDate.now().dayOfWeek.value
    val todayWorkouts = workouts.filter { it.weekday == today }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_workouts)) },
                actions = {
                    IconButton(onClick = { navController.navigate(route = Screen.Exercises.route) }) {
                        Icon(
                            imageVector = Icons.Outlined.Category,
                            contentDescription = "Localized description",
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(route = Screen.SettingsGraph.route) }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(route = Screen.CreateWorkout.route) }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Loading indicator
            if (workouts.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("No workouts yet")
                }
            } else {

                if (todayWorkouts.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.todays_workouts),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(todayWorkouts) { workout ->
                            WorkoutListItem(
                                workout,
                                Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                {
                                    navController.navigate(
                                        Screen.WorkoutDetail.passWorkoutId(
                                            workout.id
                                        )
                                    )
                                },
                                {})
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.workouts),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "${workouts.size} ${stringResource(R.string.workouts)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(workouts) { workout ->
                        WorkoutListItem(
                            workout,
                            Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            {
                                navController.navigate(
                                    Screen.WorkoutDetail.passWorkoutId(
                                        workout.id
                                    )
                                )
                            },
                            {})
                    }
                }
            }
        }
    }
}
