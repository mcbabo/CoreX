package at.mcbabo.corex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import at.mcbabo.corex.R

@Composable
fun FilterChips(
    modifier: Modifier = Modifier,
    muscleGroups: List<String>,
    selectedGroup: String?,
    onGroupSelected: (String?) -> Unit,
    showAllItem: Boolean = true
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // "All" chip
        showAllItem.takeIf { it }?.let {
            item {
                FilterChip(
                    selected = selectedGroup == null,
                    onClick = { onGroupSelected(null) },
                    label = { Text(stringResource(R.string.all)) }
                )
            }
        }

        // Muscle group chips
        items(muscleGroups) { muscleGroup ->
            FilterChip(
                selected = selectedGroup == muscleGroup,
                onClick = {
                    onGroupSelected(
                        if (selectedGroup == muscleGroup) null else muscleGroup
                    )
                },
                label = { Text(muscleGroup) }
            )
        }
    }
}
