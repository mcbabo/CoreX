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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import at.mcbabo.corex.data.viewmodels.ExerciseViewModel
import at.mcbabo.corex.data.viewmodels.WorkoutViewModel
import at.mcbabo.corex.ui.components.ExerciseListItem
import at.mcbabo.corex.ui.components.SelectedExerciseItem
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
    // Workout basic info
    val weekdays = remember {
        DayOfWeek.entries.associateWith {
            it.getDisplayName(TextStyle.FULL, Locale.getDefault())
        }
    }

    var selectedDayOfWeek by remember { mutableStateOf<DayOfWeek?>(LocalDate.now().dayOfWeek) }
    val selectedWeekday = selectedDayOfWeek?.let { weekdays[it] } ?: ""

    var workoutName by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }

    val allExercises by exerciseViewModel.allExercises.collectAsState(initial = emptyList())
    val muscleGroups by exerciseViewModel.muscleGroups.collectAsState()

    // Local state for the screen's filtering (separate from ViewModel's filtering)
    var selectedMuscleGroup by remember { mutableStateOf<String?>(null) }
    var selectedExercises by remember { mutableStateOf<List<ExerciseModel>>(emptyList()) }

    // Optimized filtering with derivedStateOf
    val availableExercises by remember {
        derivedStateOf {
            if (allExercises.isEmpty()) return@derivedStateOf emptyList()

            val selectedIds = selectedExercises.map { it.id }.toSet()

            allExercises.asSequence()
                .filter { it.id !in selectedIds }
                .filter { exercise ->
                    selectedMuscleGroup?.let { exercise.muscleGroup == it } != false
                }
                .toList()
        }
    }

    val isLoading = allExercises.isEmpty() && muscleGroups.isEmpty()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_workout)) },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (workoutName.isNotBlank()) {
                                isCreating = true

                                coroutineScope.launch {
                                    workoutViewModel.createWorkoutWithExercises(
                                        workoutName,
                                        selectedDayOfWeek?.value ?: LocalDate.now().dayOfWeek.value,
                                        selectedExercises
                                    ).onSuccess {
                                        navController.popBackStack()
                                    }.onFailure {
                                        // Show error
                                    }
                                    isCreating = false
                                }
                            }
                        },
                        enabled = workoutName.isNotBlank() && !isCreating
                    ) {
                        if (isCreating) {
                            LoadingIndicator()
                        } else {
                            Text(stringResource(R.string.create))
                        }
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Workout Info Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Workout Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    OutlinedTextField(
                        value = workoutName,
                        onValueChange = { workoutName = it },
                        label = { Text("Workout Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )

                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedWeekday,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.weekday)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            weekdays.forEach { (dayOfWeek, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        selectedDayOfWeek = dayOfWeek
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
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
                            SelectedExerciseItem(
                                exercise = exercise,
                                onRemove = {
                                    selectedExercises =
                                        selectedExercises.filter { it.id != exercise.id }
                                }
                            )
                        }
                    }
                }

                // Exercise Selection Section
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.add_exercise),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                        )

                        // Muscle Group Filter
                        var muscleGroupExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = muscleGroupExpanded,
                            onExpandedChange = { muscleGroupExpanded = !muscleGroupExpanded },
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            OutlinedTextField(
                                value = selectedMuscleGroup
                                    ?: stringResource(R.string.all_muscle_groups),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.filter_by_muscle_group)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(
                                        ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                        true
                                    ),
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = muscleGroupExpanded)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = muscleGroupExpanded,
                                onDismissRequest = { muscleGroupExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.all_muscle_groups)) },
                                    onClick = {
                                        selectedMuscleGroup = null
                                        muscleGroupExpanded = false
                                    }
                                )
                                muscleGroups.forEach { muscleGroup ->
                                    DropdownMenuItem(
                                        text = { Text(muscleGroup) },
                                        onClick = {
                                            selectedMuscleGroup = muscleGroup
                                            muscleGroupExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (availableExercises.isEmpty()) {
                            item {
                                Text(
                                    text = if (selectedMuscleGroup != null) {
                                        stringResource(R.string.no_filter_match)
                                    } else {
                                        stringResource(R.string.all_exercises_added)
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        } else {
                            items(availableExercises) { exercise ->
                                ExerciseListItem(
                                    exercise,
                                    Modifier.padding(horizontal = 16.dp),
                                    onClick = {
                                        selectedExercises = selectedExercises + exercise
                                    },
                                    {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
