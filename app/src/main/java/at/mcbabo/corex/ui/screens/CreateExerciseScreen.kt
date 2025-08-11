package at.mcbabo.corex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import at.mcbabo.corex.R
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.data.viewmodels.ExerciseViewModel
import at.mcbabo.corex.ui.components.FilterChips
import at.mcbabo.corex.ui.components.PreferencesHintCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExerciseScreen(onNavigateBack: () -> Unit, exerciseViewModel: ExerciseViewModel = hiltViewModel()) {
    val muscleGroups by exerciseViewModel.muscleGroups.collectAsState(initial = emptyList())
    var selectedMuscleGroup by remember { mutableStateOf<String?>(null) }

    var isBodyWeight by remember { mutableStateOf(false) }
    var exerciseName by remember { mutableStateOf("") }
    var exerciseDescription by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_exercise)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (exerciseDescription.isNotBlank() && !selectedMuscleGroup.isNullOrEmpty()) {
                            exerciseViewModel.createExercise(
                                ExerciseModel(
                                    name = exerciseName,
                                    description = exerciseDescription,
                                    muscleGroup = selectedMuscleGroup.toString(),
                                    isCustom = true, // Every created exercise is custom except built-in ones
                                    isBodyWeight = false
                                )
                            )
                            onNavigateBack()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Please fill in all fields before creating an exercise."
                                )
                            }
                        }
                    }) {
                        Text(stringResource(R.string.create))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.exercise_details),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = exerciseName,
                    onValueChange = { exerciseName = it },
                    label = { Text(stringResource(R.string.exercise_name)) },
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    singleLine = true
                )

                FilterChips(
                    muscleGroups = muscleGroups,
                    selectedGroup = selectedMuscleGroup,
                    onGroupSelected = { selectedMuscleGroup = it.toString() },
                    showAllItem = false,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                HorizontalDivider()

                OutlinedTextField(
                    value = exerciseDescription,
                    onValueChange = { newText ->
                        if (newText.length <= 400) {
                            exerciseDescription = newText
                        }
                    },
                    label = { Text(stringResource(R.string.description)) },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                    singleLine = false,
                    maxLines = 3
                )

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .defaultMinSize(minHeight = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.bodyweight))
                    Switch(checked = isBodyWeight, onCheckedChange = { isBodyWeight = it })
                }

                HorizontalDivider()
            }

            PreferencesHintCard(
                title = "Info",
                description = stringResource(R.string.create_exercise_info),
                icon = Icons.Outlined.Info,
                maxLines = 4
            )
        }
    }
}
