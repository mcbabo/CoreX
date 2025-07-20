package at.mcbabo.corex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.viewmodels.ExerciseViewModel
import at.mcbabo.corex.ui.components.ExerciseListItem
import at.mcbabo.corex.ui.components.FilterChips
import at.mcbabo.corex.ui.components.bottomsheets.ExerciseDetailBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    navController: NavController,
    exerciseViewModel: ExerciseViewModel = hiltViewModel()
) {
    val exercises by exerciseViewModel.exercises.collectAsState(initial = emptyList())

    val muscleGroups by exerciseViewModel.muscleGroups.collectAsState(initial = emptyList())

    var showFilters by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState()
    var selectedExercise by remember { mutableStateOf<ExerciseModel?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Exercises") },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                    TextButton(onClick = { /* Navigate to create exercise */ }) {
                        Text("Add Exercise")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showFilters) {
                    item {
                        FilterChips(
                            muscleGroups = muscleGroups,
                            selectedGroup = exerciseViewModel.selectedMuscleGroup.collectAsState().value,
                            onGroupSelected = { exerciseViewModel.filterByMuscleGroup(it) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                items(exercises) { exercise ->
                    ExerciseListItem(
                        exercise = exercise,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = {
                            selectedExercise = exercise
                            showBottomSheet = true
                        },
                        onLongPress = { /* Handle long press */ }
                    )
                }
            }
        }
    )

    if (showBottomSheet && selectedExercise != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedExercise = null
            },
            sheetState = bottomSheetState
        ) {
            ExerciseDetailBottomSheet(
                exercise = selectedExercise!!,
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
                    exerciseViewModel.deleteExercise(exercise)
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
}
