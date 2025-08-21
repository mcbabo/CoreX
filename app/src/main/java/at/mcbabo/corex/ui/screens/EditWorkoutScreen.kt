package at.mcbabo.corex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.mcbabo.corex.R
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.models.WorkoutModel
import at.mcbabo.corex.data.models.toWeekdayInts
import at.mcbabo.corex.data.viewmodels.ExerciseViewModel
import at.mcbabo.corex.data.viewmodels.WorkoutViewModel
import at.mcbabo.corex.ui.components.BackButton
import at.mcbabo.corex.ui.components.ExerciseListItem
import at.mcbabo.corex.ui.components.FilterChips
import at.mcbabo.corex.ui.components.SelectedExerciseItem
import at.mcbabo.corex.ui.components.WeekdaySelector
import java.time.DayOfWeek

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditWorkoutScreen(
    navController: NavController,
    workoutId: Long,
    onNavigateBack: () -> Unit,
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
    val workout by workoutViewModel.getWorkoutDetails(workoutId).collectAsState(null)
    val allExercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())
    val muscleGroups by exerciseViewModel.muscleGroups.collectAsState()

    var workoutName by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<String?>(null) }
    var selectedExercises by remember { mutableStateOf<List<ExerciseModel>>(emptyList()) }
    val isLoading = allExercises.isEmpty() && muscleGroups.isEmpty()

    var selectedWeekdays by remember(workout) {
        mutableStateOf(
            workout?.weekdays?.map { weekdayModel ->
                DayOfWeek.of(weekdayModel.weekday % 7)
            }?.toSet() ?: emptySet()
        )
    }

    val availableExercises by remember {
        derivedStateOf {
            if (allExercises.isEmpty()) return@derivedStateOf emptyList()

            val selectedIds = selectedExercises.map { it.id }.toSet()

            allExercises
                .asSequence()
                .filter { it.id !in selectedIds }
                .filter { exercise ->
                    selectedMuscleGroup?.let { exercise.muscleGroup == it } != false
                }.toList()
        }
    }

    LaunchedEffect(workout) {
        workout?.let { workoutData ->
            if (workoutName.isEmpty()) {
                workoutName = workoutData.workout.name
            }

            if (selectedExercises.isEmpty()) {
                selectedExercises = workoutData.exercises.map { it.exercise }
            }
        }
    }

    fun updateWorkout() {
        workout?.let { currentWorkout ->
            workoutViewModel.updateWorkout(
                WorkoutModel(
                    id = currentWorkout.workout.id,
                    name = workoutName
                ),
                selectedWeekdays.toWeekdayInts()
            )

            val originalExerciseIds = currentWorkout.exercises.map { it.exercise.id }.toSet()
            val selectedExerciseIds = selectedExercises.map { it.id }.toSet()

            val exercisesToRemove =
                currentWorkout.exercises.filter {
                    it.exercise.id !in selectedExerciseIds
                }

            val exercisesToAdd =
                selectedExercises.filter {
                    it.id !in originalExerciseIds
                }

            exercisesToRemove.forEach { workoutExercise ->
                workoutViewModel.removeExerciseFromWorkout(workoutExercise.workoutExercise.id)
            }

            exercisesToAdd.forEach { exercise ->
                workoutViewModel.addExerciseToWorkout(workout?.workout?.id ?: 0, exercise.id)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_workout)) },
                navigationIcon = { BackButton(onNavigateBack) },
                actions = {
                    TextButton(
                        onClick = {
                            updateWorkout()
                            navController.popBackStack()
                        }
                    ) {
                        Text(stringResource(R.string.update))
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                workout?.let { workout ->
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.workout_details),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = workoutName,
                                onValueChange = { workoutName = it },
                                label = { Text(stringResource(R.string.workout_name)) },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                singleLine = true
                            )
                        }

                        WeekdaySelector(selectedDays = selectedWeekdays) { selectedWeekdays = it }
                    }
                } ?: run {
                    Text(text = stringResource(R.string.loading_workout_details))
                }

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.selected_exercises),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${selectedExercises.size} ${stringResource(R.string.exercises)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedExercises) { exercise ->
                            SelectedExerciseItem(exercise = exercise) {
                                selectedExercises = selectedExercises.filter { it.id != exercise.id }
                            }
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.add_exercise),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        FilterChips(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            muscleGroups = muscleGroups,
                            selectedGroup = selectedMuscleGroup,
                            onGroupSelected = { selectedMuscleGroup = it }
                        )
                    }

                    if (availableExercises.isEmpty()) {
                        item {
                            Text(
                                text =
                                    if (selectedMuscleGroup != null) {
                                        stringResource(R.string.no_filter_match)
                                    } else {
                                        stringResource(R.string.all_exercises_added)
                                    },
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(availableExercises) { exercise ->
                            ExerciseListItem(
                                exercise,
                                Modifier.padding(horizontal = 16.dp),
                                onClick = {
                                    selectedExercises = selectedExercises + exercise
                                }
                            ) {}
                        }
                    }
                }
            }
        }
    }
}
