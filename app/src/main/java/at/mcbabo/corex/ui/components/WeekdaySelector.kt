package at.mcbabo.corex.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekdaySelector(
    selectedDays: Set<DayOfWeek> = emptySet(),
    onSelectionChanged: (Set<DayOfWeek>) -> Unit = {}
) {
    val weekdays = remember {
        DayOfWeek.entries.map { dayOfWeek ->
            dayOfWeek to dayOfWeek.getDisplayName(
                TextStyle.SHORT_STANDALONE,
                Locale.getDefault()
            )
        }
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(weekdays) { (dayOfWeek, displayName) ->
            FilterChip(
                onClick = {
                    val newSelection = if (selectedDays.contains(dayOfWeek)) {
                        selectedDays - dayOfWeek
                    } else {
                        selectedDays + dayOfWeek
                    }
                    onSelectionChanged(newSelection)
                },
                label = { Text(displayName) },
                selected = selectedDays.contains(dayOfWeek)
            )
        }
    }
}