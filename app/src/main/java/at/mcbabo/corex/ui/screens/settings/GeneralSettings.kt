package at.mcbabo.corex.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.mcbabo.corex.R
import at.mcbabo.corex.data.viewmodels.SettingsViewModel
import at.mcbabo.corex.ui.components.BackButton
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettings(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(),
            canScroll = { true },
        )

    var showDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(modifier = Modifier, text = stringResource(R.string.general))
                },
                navigationIcon = { BackButton(onNavigateBack) },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            NumberSetting(
                title = "Default Reps",
                subtitle = "Default number of repetitions per set",
                value = settings.defaultReps,
                onValueChange = { viewModel.setDefaultReps(it) },
                range = 1..50
            )

            NumberSetting(
                title = "Default Sets",
                subtitle = "Default number of sets per exercise",
                value = settings.defaultSets,
                onValueChange = { viewModel.setDefaultSets(it) },
                range = 1..10
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { showDialog = true }) {
                    Text("Set Reminder Time")
                }

                Text(
                    text = settings.reminderTime,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = typography.bodyLarge
                )
            }
        }
    }

    if (showDialog) {
        val context = LocalContext.current

        DialWithDialogExample(onConfirm = {
            showDialog = false
            val selectedTime = "%02d:%02d".format(it.hour, it.minute)
            viewModel.setReminderTime(selectedTime, context)
        }, onDismiss = { showDialog = false })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialWithDialogExample(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TimePicker(
                state = timePickerState,
            )
            Button(onClick = { onConfirm(timePickerState) }) {
                Text("Confirm Reminder")
            }
        }
    }
}


@Composable
private fun NumberSetting(
    title: String,
    subtitle: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    step: Int = 1
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            modifier = Modifier.width(120.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    onValueChange(value - step)
                },
                enabled = value + step <= range.last
            ) {
                Icon(Icons.Default.HorizontalRule, contentDescription = "Increase")
            }

            Text(
                text = value.toString(),
                style = typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(
                onClick = {
                    onValueChange(value + step)
                },
                enabled = value + step <= range.last
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }

    if (showDialog) {
        NumberPickerDialog(
            title = title,
            value = value,
            range = range,
            step = step,
            onValueChange = { onValueChange(it) },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun NumberPickerDialog(
    title: String,
    value: Int,
    range: IntRange,
    step: Int,
    onValueChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedValue by remember { mutableIntStateOf(value) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (selectedValue - step >= range.first) {
                                selectedValue -= step
                            }
                        },
                        enabled = selectedValue - step >= range.first
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease")
                    }

                    Text(
                        text = selectedValue.toString(),
                        style = typography.headlineMedium,
                        modifier = Modifier.width(80.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = {
                            if (selectedValue + step <= range.last) {
                                selectedValue += step
                            }
                        },
                        enabled = selectedValue + step <= range.last
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onValueChange(selectedValue)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}