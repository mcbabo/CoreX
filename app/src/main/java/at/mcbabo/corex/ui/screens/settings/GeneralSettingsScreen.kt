package at.mcbabo.corex.ui.screens.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.mcbabo.corex.R
import at.mcbabo.corex.data.viewmodels.SettingsViewModel
import at.mcbabo.corex.ui.components.BackButton
import at.mcbabo.corex.ui.components.PreferenceNumberSetting
import at.mcbabo.corex.ui.components.PreferenceSwitchWithDivider
import at.mcbabo.corex.ui.components.dialogs.DialWithDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val scrollBehavior = exitUntilCollapsedScrollBehavior()
    var showDialog by remember { mutableStateOf(false) }

    val blurRadius by animateDpAsState(
        targetValue = if (showDialog) 6.dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "BlurAnimation"
    )

    Scaffold(
        modifier =
            Modifier
                .blur(blurRadius)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.general)) },
                navigationIcon = { BackButton(onNavigateBack) },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
        ) {
            PreferenceNumberSetting(
                title = stringResource(R.string.default_reps),
                subtitle = stringResource(R.string.default_reps_des),
                value = settings.defaultReps,
                onValueChange = { viewModel.setDefaultReps(it) },
                range = 1..50
            )

            PreferenceNumberSetting(
                title = stringResource(R.string.default_sets),
                subtitle = stringResource(R.string.default_sets_desc),
                value = settings.defaultSets,
                onValueChange = { viewModel.setDefaultSets(it) },
                range = 1..10
            )

            PreferenceSwitchWithDivider(
                title = stringResource(R.string.daily_reminder),
                description = "${stringResource(R.string.daily_reminder_desc)} (${settings.reminderTime})",
                icon = Icons.Outlined.Notifications,
                isChecked = settings.notificationsEnabled,
                onChecked = { viewModel.toggleNotifications() },
                onClick = { if (settings.notificationsEnabled) showDialog = true }
            )
        }
    }

    if (showDialog) {
        val context = LocalContext.current

        DialWithDialog(
            onConfirm = {
                showDialog = false
                val selectedTime = "%02d:%02d".format(it.hour, it.minute)
                viewModel.setReminderTime(selectedTime, context)
            }
        ) { showDialog = false }
    }
}
