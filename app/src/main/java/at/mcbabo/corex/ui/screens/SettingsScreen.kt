package at.mcbabo.corex.ui.screens

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.EnergySavingsLeaf
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TypeSpecimen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.databinding.library.BuildConfig
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.mcbabo.corex.R
import at.mcbabo.corex.data.viewmodels.SettingsViewModel
import at.mcbabo.corex.navigation.Screen
import at.mcbabo.corex.ui.components.PreferencesHintCard
import at.mcbabo.corex.ui.components.SettingItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController, onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val coroutineScope = rememberCoroutineScope()

    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val debugClickCount = remember { mutableIntStateOf(0) }

    val clickThreshold = 5
    val timeLimit = 2000L // 2 seconds

    fun debugOnClick() {
        debugClickCount.intValue++

        if (debugClickCount.intValue >= clickThreshold) {
            viewModel.setDebugModeEnabled(true)
            debugClickCount.intValue = 0 // Reset the counter
        }

        coroutineScope.launch {
            delay(timeLimit)
            debugClickCount.intValue = 0
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                scrollBehavior = scrollBehavior,
                expandedHeight = TopAppBarDefaults.LargeAppBarExpandedHeight + 24.dp,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                        )
                    }
                })
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (settings.notificationsEnabled) {
                item {
                    BatteryOptimizationWarning()
                }
            }

            item {
                SettingItem(
                    title = stringResource(R.string.general),
                    description = stringResource(R.string.general_desc),
                    icon = Icons.Outlined.Settings
                ) {
                    navController.navigate(route = Screen.GeneralSettings.route)
                }
            }

            item {
                SettingItem(
                    title = stringResource(R.string.appearance),
                    description = stringResource(R.string.appearance_desc),
                    icon = Icons.Outlined.Palette
                ) {
                    navController.navigate(route = Screen.AppearanceSettings.route)
                }
            }

            item {
                SettingItem(
                    title = stringResource(R.string.units),
                    description = stringResource(R.string.units_desc),
                    icon = Icons.Outlined.TypeSpecimen
                ) {
                    navController.navigate(route = Screen.UnitsSettings.route)
                }
            }

            item {
                SettingItem(
                    title = "Debug Info",
                    description = "Version Name : ${BuildConfig.VERSION_NAME} | Version Code : ${BuildConfig.VERSION_CODE}" +
                            if (settings.debugModeEnabled) " | Dev Mode" else "",
                    icon = Icons.Outlined.BugReport
                ) {
                    debugOnClick()
                }
            }
        }
    }
}

fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}

fun requestIgnoreBatteryOptimizations(context: Context) {
    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
        data = "package:${context.packageName}".toUri()
    }
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

@Composable
fun BatteryOptimizationWarning(context: Context = LocalContext.current) {
    val isIgnoring = remember {
        mutableStateOf(isIgnoringBatteryOptimizations(context))
    }

    if (!isIgnoring.value) {
        PreferencesHintCard(
            title = stringResource(R.string.battery_warning),
            description = stringResource(R.string.battery_warning_desc),
            icon = Icons.Outlined.EnergySavingsLeaf
        ) {
            requestIgnoreBatteryOptimizations(context)
            isIgnoring.value = isIgnoringBatteryOptimizations(context)
        }
    }
}
