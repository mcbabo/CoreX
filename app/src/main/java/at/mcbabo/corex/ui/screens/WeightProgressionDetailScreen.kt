package at.mcbabo.corex.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import at.mcbabo.corex.R
import at.mcbabo.corex.data.viewmodels.ExerciseViewModel
import at.mcbabo.corex.ui.components.BackButton
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightProgressionDetailScreen(
    exerciseId: Long,
    onNavigateBack: () -> Unit,
    viewModel: ExerciseViewModel = hiltViewModel()
) {
    val weightProgressions by viewModel.getWeightProgressionByExercise(exerciseId).collectAsState(emptyList())

    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.weight_progression)) },
                navigationIcon = { BackButton(onNavigateBack) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(weightProgressions.sortedBy { it.date }) { progression ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = formatter.format(progression.date))
                            Text(
                                progression.notes ?: stringResource(R.string.no_notes),
                                color =
                                    if (progression.notes.isNullOrEmpty()) {
                                        MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.6f
                                        )
                                    } else {
                                        Color.Unspecified
                                    },
                            )
                        }
                        Text(
                            text = "${progression.weight} kg",
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                        )
                    }
                }
            }
            weightProgressions.maxByOrNull { it.weight }?.let { progression ->
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = formatter.format(progression.date))
                        Text(
                            progression.notes ?: stringResource(R.string.no_notes),
                            color =
                                if (progression.notes.isNullOrEmpty()) {
                                    MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.6f
                                    )
                                } else {
                                    Color.Unspecified
                                },
                        )
                    }
                    Text(
                        text = "${progression.weight} kg",
                        fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                    )
                }
            }
        }
    }
}
