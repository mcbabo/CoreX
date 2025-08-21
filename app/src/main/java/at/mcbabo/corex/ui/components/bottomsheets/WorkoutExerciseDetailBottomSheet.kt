package at.mcbabo.corex.ui.components.bottomsheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import at.mcbabo.corex.R
import at.mcbabo.corex.data.entities.WorkoutExercise
import at.mcbabo.corex.ui.components.WeightProgressionGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutExerciseDetailBottomSheet(workoutExercise: WorkoutExercise, onNavigate: () -> Unit) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
    ) {
        ExerciseInfo(workoutExercise.exercise)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExerciseInfo(
                title = stringResource(R.string.sets),
                description = (workoutExercise.workoutExercise.targetSets ?: 0).toString(),
                modifier = Modifier.weight(1f)
            )
            ExerciseInfo(
                title = stringResource(R.string.reps),
                description = (workoutExercise.workoutExercise.targetReps ?: 0).toString(),
                modifier = Modifier.weight(1f)
            )
            ExerciseInfo(
                title = stringResource(R.string.weight),
                description = (workoutExercise.workoutExercise.targetWeight ?: 0.0).toString() + " kg",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (workoutExercise.weightProgressions.size > 1) {
            Text(
                text = stringResource(R.string.weight_progression),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            WeightProgressionGraph(workoutExercise.weightProgressions, onNavigate = { onNavigate() })

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ExerciseInfo(title: String, description: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.5f
                )
            )
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}
