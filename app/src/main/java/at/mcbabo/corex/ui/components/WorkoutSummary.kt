package at.mcbabo.corex.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.mcbabo.corex.R
import at.mcbabo.corex.data.entities.Workout

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WorkoutSummary(workout: Workout) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.workout_progress),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val completedExercises = workout.exercises.count { it.workoutExercise.isCompleted }
            val totalExercises = workout.exercises.size
            val progress = if (totalExercises > 0) completedExercises.toFloat() / totalExercises else 0f
            val animatedProgress by animateFloatAsState(
                targetValue = progress, animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress }, modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$completedExercises/$totalExercises ${stringResource(R.string.completed)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
