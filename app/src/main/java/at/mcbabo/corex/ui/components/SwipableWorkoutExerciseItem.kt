package at.mcbabo.corex.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.mcbabo.corex.R
import at.mcbabo.corex.data.entities.WorkoutExercise
import at.mcbabo.corex.ui.components.dialogs.RecordWeightDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableWorkoutExerciseCard(
    exercise: WorkoutExercise,
    arrangeMode: Boolean,
    onClick: () -> Unit,
    onMarkCompleted: (Boolean) -> Unit,
    onRecordWeight: (Float, String?) -> Unit
) {
    var showWeightDialog by remember { mutableStateOf(false) }
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart -> {
                onMarkCompleted(!exercise.workoutExercise.isCompleted)
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }

            else -> {}
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = !arrangeMode,
        enableDismissFromEndToStart = !arrangeMode,
        backgroundContent = {
            val direction = dismissState.dismissDirection

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                contentAlignment =
                    when (direction) {
                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                        else -> Alignment.Center
                    }
            ) {
                Icon(
                    imageVector = if (!exercise.workoutExercise.isCompleted) {
                        Icons.Default.Check
                    } else {
                        Icons.Default.Close
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = { showWeightDialog = true }
                    )
                    .background(
                        if (exercise.workoutExercise.isCompleted) {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .alpha(if (exercise.workoutExercise.isCompleted) 0.6f else 1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        ExerciseAvatar(exercise.exercise.name)

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = exercise.exercise.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "${exercise.workoutExercise.targetSets ?: 0} ${
                                    stringResource(
                                        R.string.sets
                                    )
                                } x ${exercise.workoutExercise.targetReps ?: 0} ${
                                    stringResource(
                                        R.string.reps
                                    )
                                }",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(Modifier.height(2.dp))

                            Text(
                                "${stringResource(R.string.weight)}: ${
                                    exercise.weightProgressions.maxOfOrNull { it.weight } ?: 0.0F
                                }kg",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }

    if (showWeightDialog) {
        RecordWeightDialog(
            onDismiss = { showWeightDialog = false },
            onRecord = { weight, notes ->
                onRecordWeight(weight, notes)
                showWeightDialog = false
            }
        )
    }
}
