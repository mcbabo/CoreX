package at.mcbabo.corex.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import at.mcbabo.corex.R
import at.mcbabo.corex.data.entities.WorkoutSummary
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WorkoutListItem(
    workout: WorkoutSummary,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongPress: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExerciseAvatar(workout.name)
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = workout.name)
            Text(
                "${
                    DayOfWeek.of(workout.weekday)
                        .getDisplayName(TextStyle.FULL, Locale.getDefault())
                } - ${workout.exerciseCount} ${stringResource(R.string.exercises)}",
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}