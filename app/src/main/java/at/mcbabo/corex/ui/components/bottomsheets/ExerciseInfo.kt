package at.mcbabo.corex.ui.components.bottomsheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import at.mcbabo.corex.R
import at.mcbabo.corex.data.models.ExerciseModel
import at.mcbabo.corex.ui.components.ExerciseAvatar

@Composable
fun ExerciseInfo(exercise: ExerciseModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExerciseAvatar(exercise.name, Modifier.size(56.dp))

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = exercise.muscleGroup,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Exercise info cards
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Type badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (exercise.isBodyWeight)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (exercise.isBodyWeight)
                        Icons.Default.Person
                    else
                        Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (exercise.isBodyWeight) stringResource(R.string.bodyweight) else stringResource(
                        R.string.equipment
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Source badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (exercise.isCustom)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (exercise.isCustom)
                        Icons.Default.Edit
                    else
                        Icons.Default.Verified,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (exercise.isCustom) stringResource(R.string.custom) else stringResource(
                        R.string.built_in
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Description (if available)
    exercise.description?.let { description ->
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
