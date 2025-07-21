package at.mcbabo.corex.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.mcbabo.corex.data.models.AppTheme
import at.mcbabo.corex.data.models.WeightUnit
import at.mcbabo.corex.data.viewmodels.SettingsEvent
import at.mcbabo.corex.data.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle events
    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is SettingsEvent.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Section
            item {
                SettingsSection(title = "Appearance") {
                    ThemeSelector(
                        selectedTheme = settings.selectedTheme,
                        onThemeSelected = viewModel::setTheme
                    )
                }
            }

            // Units Section
            item {
                SettingsSection(title = "Units") {
                    WeightUnitSelector(
                        selectedUnit = settings.weightUnit,
                        onUnitSelected = viewModel::setWeightUnit
                    )
                }
            }

            // Workout Defaults Section
            item {
                SettingsSection(title = "Workout Defaults") {
                    NumberSetting(
                        title = "Default Reps",
                        subtitle = "Default number of repetitions per set",
                        value = settings.defaultReps,
                        onValueChange = viewModel::setDefaultReps,
                        range = 1..50
                    )

                    NumberSetting(
                        title = "Default Sets",
                        subtitle = "Default number of sets per exercise",
                        value = settings.defaultSets,
                        onValueChange = viewModel::setDefaultSets,
                        range = 1..10
                    )
                }
            }

            // Preferences Section
            item {
                SettingsSection(title = "Preferences") {
                    SettingsSwitch(
                        title = "Notifications",
                        subtitle = "Receive workout reminders and updates",
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { viewModel.toggleNotifications() }
                    )

                    SettingsSwitch(
                        title = "Auto Backup",
                        subtitle = "Automatically backup your data",
                        checked = settings.autoBackup,
                        onCheckedChange = { viewModel.toggleAutoBackup() }
                    )
                }
            }

            // Actions Section
            item {
                SettingsSection(title = "Actions") {
                    Button(
                        onClick = { viewModel.resetSettings() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Reset All Settings")
                    }
                }
            }

            // Debug Info (remove in production)
            item {
                SettingsSection(title = "Debug Info") {
                    Text(
                        text = "First Launch: ${settings.firstLaunch}\n" +
                                "Last Sync: ${if (settings.lastSyncTimestamp == 0L) "Never" else settings.lastSyncTimestamp}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppTheme.entries.forEach { theme ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (theme == selectedTheme),
                        onClick = { onThemeSelected(theme) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (theme == selectedTheme),
                    onClick = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = theme.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun WeightUnitSelector(
    selectedUnit: WeightUnit,
    onUnitSelected: (WeightUnit) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        WeightUnit.entries.forEach { unit ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (unit == selectedUnit),
                        onClick = { onUnitSelected(unit) },
                        role = Role.RadioButton
                    )
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (unit == selectedUnit),
                    onClick = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${unit.displayName} (${unit.symbol})",
                    style = MaterialTheme.typography.bodyLarge
                )
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
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }

    if (showDialog) {
        NumberPickerDialog(
            title = title,
            value = value,
            range = range,
            step = step,
            onValueChange = onValueChange,
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Current value: $selectedValue")
                Spacer(modifier = Modifier.height(16.dp))

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
                        style = MaterialTheme.typography.headlineMedium,
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

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() }
        )
    }
}