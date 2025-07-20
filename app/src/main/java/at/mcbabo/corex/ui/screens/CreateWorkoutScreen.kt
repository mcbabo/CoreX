package at.mcbabo.corex.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.viewmodels.ExerciseViewModel
import at.mcbabo.corex.data.viewmodels.WorkoutViewModel
import at.mcbabo.corex.ui.components.ExerciseAvatar
import at.mcbabo.corex.ui.components.ExerciseListItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onWorkoutCreated: (Long) -> Unit,
    workoutViewModel: WorkoutViewModel = hiltViewModel(),
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
    // Workout basic info
    var workoutName by remember { mutableStateOf("") }
    var selectedWeekday by remember { mutableStateOf("Monday") }
    var isCreating by remember { mutableStateOf(false) }

    // Exercise selection
    val allExercises by exerciseViewModel.exercises.collectAsState(initial = emptyList())
    var selectedExercises by remember { mutableStateOf<List<ExerciseModel>>(emptyList()) }

    // Search and filter
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleGroup by remember { mutableStateOf<String?>(null) }
    val muscleGroups by exerciseViewModel.muscleGroups.collectAsState(initial = emptyList())

    // Filter available exercises
    val availableExercises = allExercises.filter { exercise ->
        val isNotSelected = !selectedExercises.any { it.id == exercise.id }
        val matchesSearch = if (searchQuery.isBlank()) {
            true
        } else {
            exercise.name.contains(searchQuery, ignoreCase = true) ||
                    exercise.muscleGroup.contains(searchQuery, ignoreCase = true)
        }
        val matchesMuscleGroup = selectedMuscleGroup?.let {
            exercise.muscleGroup == it
        } != false

        isNotSelected && matchesSearch && matchesMuscleGroup
    }

    val weekdays =
        listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Create Workout") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                                        selectedWeekday,
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
                            Text("Create")
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
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
                            label = { Text("Weekday") },
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
                            weekdays.forEach { weekday ->
                                DropdownMenuItem(
                                    text = { Text(weekday) },
                                    onClick = {
                                        selectedWeekday = weekday
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
                            text = "Selected Exercises",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${selectedExercises.size} exercises",
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
                            text = "Add Exercises",
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
                                value = selectedMuscleGroup ?: "All Muscle Groups",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Filter by Muscle Group") },
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
                                    text = { Text("All Muscle Groups") },
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

                    // Available Exercises List
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (availableExercises.isEmpty()) {
                            item {
                                Text(
                                    text = if (searchQuery.isNotBlank() || selectedMuscleGroup != null) {
                                        "No exercises match your filters"
                                    } else {
                                        "All exercises have been added"
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
                                    {})
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SelectedExerciseItem(
    exercise: ExerciseModel,
    onRemove: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(90.dp)
            .clickable(
                onClick = onRemove,
            ),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Box(
            modifier = Modifier
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ExerciseAvatar(exercise.name)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    exercise.name,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
    }
}
