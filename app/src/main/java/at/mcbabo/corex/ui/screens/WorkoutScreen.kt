package at.mcbabo.corex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.mcbabo.corex.data.entities.Workout
import at.mcbabo.corex.data.entities.WorkoutExercise
import at.mcbabo.corex.data.models.WorkoutModel
import at.mcbabo.corex.data.viewmodels.WorkoutViewModel
import at.mcbabo.corex.ui.components.SwipeableWorkoutExerciseCard
import at.mcbabo.corex.ui.components.bottomsheets.WorkoutExerciseDetailBottomSheet
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WorkoutScreen(
    navController: NavController,
    workoutId: Long,
    onNavigateBack: () -> Unit,
    workoutViewModel: WorkoutViewModel = hiltViewModel()
) {
    val workout by workoutViewModel.getWorkoutDetails(workoutId).collectAsState(null)
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState()
    var selectedExercise by remember { mutableStateOf<WorkoutExercise?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    val openAlertDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(workout?.workout?.name ?: "")
                        workout?.workout?.weekday?.let { weekday ->
                            Text(
                                text = DayOfWeek.of(weekday)
                                    .getDisplayName(TextStyle.FULL, Locale.getDefault()),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Add exercise button
                    IconButton(onClick = { showAddExerciseDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Exercise"
                        )
                    }

                    // More options menu
                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Workout") },
                                onClick = {
                                    showMenu = false
                                    // Navigate to edit screen
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Workout") },
                                onClick = {
                                    showMenu = false
                                    openAlertDialog.value = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            workout?.let { workoutData ->
                // Workout summary header
                item {
                    WorkoutSummaryCard(workout = workoutData)
                }

                // Section header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Exercises",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${workoutData.exercises.size} exercises",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (workoutData.exercises.isEmpty()) {
                    item {
                        EmptyExercisesState(
                            onAddExercise = { showAddExerciseDialog = true }
                        )
                    }
                } else {
                    items(
                        items = workoutData.exercises,
                        key = { it.workoutExercise.id }
                    ) { exercise ->
                        SwipeableWorkoutExerciseCard(
                            exercise = exercise,
                            onClick = {
                                selectedExercise = exercise
                                showBottomSheet = true
                            },
                            onMarkCompleted = { isCompleted ->
                                workoutViewModel.markExerciseCompleted(
                                    exercise.workoutExercise.id,
                                    isCompleted
                                )
                            },
                            onRecordWeight = { weight, notes ->
                                workoutViewModel.recordWeight(
                                    exercise.workoutExercise.id,
                                    weight,
                                    notes
                                )
                            },
                            onRemove = {
                                workoutViewModel.removeExerciseFromWorkout(
                                    exercise.workoutExercise.id
                                )
                            }
                        )
                    }
                }

                // Bottom spacing for FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            } ?: run {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }
                }
            }
        }
    }

    if (showBottomSheet && selectedExercise != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedExercise = null
            },
            sheetState = bottomSheetState
        ) {
            WorkoutExerciseDetailBottomSheet(
                workoutExercise = selectedExercise!!,
                onDismiss = {
                    showBottomSheet = false
                    selectedExercise = null
                },
                onEdit = { exercise ->
                    // Navigate to edit exercise screen
                    showBottomSheet = false
                    selectedExercise = null
                },
                onDelete = { exercise ->
                    showBottomSheet = false
                    selectedExercise = null
                },
                onAddToWorkout = { exercise ->
                    // Navigate to workout selection or add to current workout
                    showBottomSheet = false
                    selectedExercise = null
                },
            )
        }
    }

    if (openAlertDialog.value) {
        DeleteWorkoutDialog(
            onDismissRequest = {
                openAlertDialog.value = false
            },
            onConfirmation = {
                workoutViewModel.deleteWorkout(
                    WorkoutModel(
                        workout?.workout?.id ?: 0,
                        workout?.workout?.name ?: "",
                        workout?.workout?.weekday ?: 0,
                        true
                    )
                )
                openAlertDialog.value = false
                navController.popBackStack()
            },
            dialogTitle = "Delete Workout",
            dialogText = "If you delete this workout, all associated exercises will also be removed. Are you sure you want to proceed?",
            icon = Icons.Default.Info
        )
    }

}

@Composable
fun WorkoutSummaryCard(
    workout: Workout
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column {
            Text(
                text = "Workout Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress indicator
            val completedExercises = workout.exercises.count { it.workoutExercise.isCompleted }
            val totalExercises = workout.exercises.size
            val progress =
                if (totalExercises > 0) completedExercises.toFloat() / totalExercises else 0f

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$completedExercises/$totalExercises completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyExercisesState(
    onAddExercise: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No exercises yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Add some exercises to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onAddExercise) {
                Text("Add Exercise")
            }
        }
    }
}


@Composable
fun DeleteWorkoutDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = dialogTitle)
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}