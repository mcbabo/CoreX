package at.mcbabo.corex.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TypeSpecimen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.databinding.library.BuildConfig
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import at.mcbabo.corex.R
import at.mcbabo.corex.data.viewmodels.SettingsEvent
import at.mcbabo.corex.data.viewmodels.SettingsViewModel
import at.mcbabo.corex.navigation.Screen
import at.mcbabo.corex.ui.components.SettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )

        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                SettingItem(
                    title = stringResource(R.string.general),
                    description = stringResource(R.string.general_desc),
                    icon = Icons.Outlined.Settings,
                ) {
                    navController.navigate(route = Screen.GeneralSettings.route)
                }
            }

            item {
                SettingItem(
                    title = stringResource(R.string.appearance),
                    description = stringResource(R.string.appearance_desc),
                    icon = Icons.Outlined.Palette,
                ) {
                    navController.navigate(route = Screen.AppearanceSettings.route)
                }
            }

            item {
                SettingItem(
                    title = stringResource(R.string.units),
                    description = stringResource(R.string.units_desc),
                    icon = Icons.Outlined.TypeSpecimen,
                ) {
                    navController.navigate(route = Screen.UnitsSettings.route)
                }
            }

            item {
                SettingItem(
                    title = "Debug Info",
                    description = "Version Name : ${BuildConfig.VERSION_NAME} | Version Code : ${BuildConfig.VERSION_CODE}",
                    icon = Icons.Outlined.BugReport,
                ) {

                }
            }
        }
    }
}
