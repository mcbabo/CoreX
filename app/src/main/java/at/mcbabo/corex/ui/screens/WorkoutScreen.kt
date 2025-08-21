package at.mcbabo.corex.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.mcbabo.corex.R
import at.mcbabo.corex.data.entities.Workout
import at.mcbabo.corex.data.entities.WorkoutExercise
import at.mcbabo.corex.data.models.WorkoutModel
import at.mcbabo.corex.data.viewmodels.WorkoutViewModel
import at.mcbabo.corex.navigation.Screen
import at.mcbabo.corex.ui.components.SwipeableWorkoutExerciseCard
import at.mcbabo.corex.ui.components.WorkoutSummary
import at.mcbabo.corex.ui.components.bottomsheets.WorkoutExerciseDetailBottomSheet
import at.mcbabo.corex.ui.components.dialogs.DeleteWorkoutDialog
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Angle
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Spread
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WorkoutScreen(
    navController: NavController,
    workoutId: Long,
    onNavigateBack: () -> Unit,
    workoutViewModel: WorkoutViewModel = hiltViewModel()
) {
    val workout by workoutViewModel.getWorkoutDetails(workoutId).collectAsState(null)

    val coroutineScope = rememberCoroutineScope()
    var isAnimating by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState()
    var selectedExercise by remember { mutableStateOf<WorkoutExercise?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var arrangeMode by remember { mutableStateOf(false) }

    val openAlertDialog = remember { mutableStateOf(false) }

    val hapticFeedback = LocalHapticFeedback.current
    var orderedExercises by remember { mutableStateOf(emptyList<WorkoutExercise>()) }
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        orderedExercises = orderedExercises.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }

        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
    }

    LaunchedEffect(workout) {
        workout?.let {
            orderedExercises = it.exercises.toList().sortedBy { exercise -> exercise.workoutExercise.orderIndex }
        }
    }

    BackHandler(enabled = arrangeMode) {
        arrangeMode = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(workout?.workout?.name ?: "")
                        workout?.weekdays?.let { weekdays ->
                            Text(
                                text = weekdays.joinToString(", ") {
                                    DayOfWeek.of(it.weekday).getDisplayName(TextStyle.FULL, Locale.getDefault())
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isAnimating = true
                            workoutViewModel.resetWorkoutProgress(
                                workout?.workout?.id ?: 0
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocalFireDepartment,
                            contentDescription = "Clear all Completed Exercises"
                        )
                    }

                    var showMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.edit)) },
                                onClick = {
                                    showMenu = false
                                    navController.navigate(
                                        route = Screen.EditWorkout.createRoute(
                                            workoutId
                                        )
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.arrange_exercises)) },
                                onClick = {
                                    showMenu = false
                                    arrangeMode = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.delete_workout)) },
                                onClick = {
                                    showMenu = false
                                    openAlertDialog.value = true
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = arrangeMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        arrangeMode = false
                        workoutViewModel.updateExerciseOrder(
                            orderedExercises.mapIndexed { idx, exercise ->
                                exercise.workoutExercise.id to idx
                            }
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done Arranging Exercises"
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            workout?.let { workoutData ->
                WorkoutSummaryCard(workout = workoutData)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.exercises),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${workoutData.exercises.size} ${
                            stringResource(R.string.exercises)
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (workoutData.exercises.isEmpty()) {
                    EmptyExercisesState(
                        onAddExercise = {
                            navController.navigate(
                                route = Screen.EditWorkout.createRoute(
                                    workoutId
                                )
                            )
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = lazyListState,
                    ) {
                        items(orderedExercises, key = { it.workoutExercise.id }) { exercise ->
                            ReorderableItem(
                                state = reorderableLazyListState,
                                key = exercise.workoutExercise.id,
                                modifier = Modifier.fillMaxWidth()
                            ) { isDragging ->
                                val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)
                                Surface(shadowElevation = elevation) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        AnimatedVisibility(
                                            visible = arrangeMode,
                                            enter = fadeIn() + expandHorizontally(),
                                            exit = fadeOut() + shrinkHorizontally()
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.DragHandle,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .draggableHandle(
                                                        onDragStarted = {
                                                            hapticFeedback.performHapticFeedback(
                                                                HapticFeedbackType.GestureThresholdActivate
                                                            )
                                                        },
                                                        onDragStopped = {
                                                            hapticFeedback.performHapticFeedback(
                                                                HapticFeedbackType.GestureEnd
                                                            )
                                                        }
                                                    )
                                                    .padding(start = 12.dp)
                                                    .size(24.dp)
                                                    .alpha(if (exercise.workoutExercise.isCompleted) 0.6f else 1f),
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        SwipeableWorkoutExerciseCard(
                                            exercise = exercise,
                                            arrangeMode = arrangeMode,
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
                                                    exercise.exercise.id,
                                                    weight,
                                                    notes
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
                Spacer(modifier = Modifier.height(80.dp))
            } ?: run {
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

    if (isAnimating) {
        ConfettiKit(
            modifier = Modifier.fillMaxSize(),
            parties = listOf(
                Party(
                    speed = 10f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    angle = Angle.RIGHT - 45,
                    spread = Spread.SMALL + 30,
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.toArgb(),
                        MaterialTheme.colorScheme.secondary.toArgb(),
                        MaterialTheme.colorScheme.tertiary.toArgb(),
                        MaterialTheme.colorScheme.primaryContainer.toArgb()
                    ),
                    emitter = Emitter(duration = 5.seconds).perSecond(50),
                    position = Position.Relative(0.0, 0.5)
                ),
                Party(
                    speed = 10f,
                    maxSpeed = 30f,
                    damping = 0.9f,
                    angle = Angle.LEFT + 45,
                    spread = Spread.SMALL + 30,
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.toArgb(),
                        MaterialTheme.colorScheme.secondary.toArgb(),
                        MaterialTheme.colorScheme.tertiary.toArgb(),
                        MaterialTheme.colorScheme.primaryContainer.toArgb()
                    ),
                    emitter = Emitter(duration = 5.seconds).perSecond(50),
                    position = Position.Relative(1.0, 0.5)
                )
            ),
            onParticleSystemEnded = { _, _ -> isAnimating = false }
        )
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
            ) {
                coroutineScope.launch {
                    showBottomSheet = false
                    bottomSheetState.hide()
                }
                navController.navigate(
                    Screen.WeightProgressionDetail.createRoute(
                        selectedExercise?.exercise?.id ?: 0
                    )
                )
            }
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
                        workout?.workout?.id ?: 0, workout?.workout?.name ?: "", true
                    )
                )
                openAlertDialog.value = false
                navController.popBackStack()
            },
            dialogTitle = stringResource(R.string.delete_workout),
            dialogText = stringResource(R.string.delete_workout_desc),
            icon = Icons.Default.Info
        )
    }
}

@Composable
fun EmptyExercisesState(onAddExercise: () -> Unit) {
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
            text = stringResource(R.string.no_exercises_yet),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = stringResource(R.string.no_exercises_yet_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onAddExercise) {
            Text(stringResource(R.string.add_exercise))
        }
    }
}

@Composable
fun DeleteWorkoutDialog(
    onDismissRequest: () -> Unit, onConfirmation: () -> Unit, dialogTitle: String, dialogText: String, icon: ImageVector
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
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    )
}
