package at.mcbabo.corex.ui.screens.settings.appearance

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.mcbabo.corex.R
import at.mcbabo.corex.data.models.ThemeMode
import at.mcbabo.corex.data.viewmodels.SettingsViewModel
import at.mcbabo.corex.navigation.Screen
import at.mcbabo.corex.ui.components.BackButton
import at.mcbabo.corex.ui.components.PreferenceItem
import at.mcbabo.corex.ui.components.PreferenceSwitch
import at.mcbabo.corex.ui.components.PreferenceSwitchWithDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(),
            canScroll = { true }
        )

    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(modifier = Modifier, text = stringResource(R.string.appearance))
                },
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
            PreferenceSwitch(
                title = stringResource(R.string.dynamic_color),
                icon = Icons.Outlined.Colorize,
                description = stringResource(R.string.dynamic_color_desc),
                isChecked = settings.dynamicColors,
                onClick = {
                    viewModel.setDynamicColors(!settings.dynamicColors)
                }
            )

            PreferenceSwitchWithDivider(
                title = stringResource(R.string.dark_mode),
                icon = if (settings.selectedTheme ==
                    ThemeMode.DARK
                ) {
                    Icons.Outlined.DarkMode
                } else {
                    Icons.Outlined.LightMode
                },
                isChecked = (settings.selectedTheme == ThemeMode.DARK || (settings.selectedTheme == ThemeMode.SYSTEM && isSystemInDarkTheme())),
                description =
                    if (settings.selectedTheme.symbol ==
                        true
                    ) {
                        stringResource(R.string.on)
                    } else {
                        settings.selectedTheme.symbol?.let {
                            if (!it) {
                                stringResource(
                                    R.string.off
                                )
                            } else {
                                "System"
                            }
                        }
                    },
                onChecked = {
                    if (settings.selectedTheme == ThemeMode.LIGHT) {
                        viewModel.setTheme(ThemeMode.DARK)
                    } else {
                        viewModel.setTheme(ThemeMode.LIGHT)
                    }
                },
                onClick = { }
            )

            PreferenceItem(
                title = stringResource(R.string.language),
                description = stringResource(R.string.appearance_desc),
                icon = Icons.Outlined.Language
            ) {
                navController.navigate(route = Screen.LanguageSettings.route)
            }
        }
    }
}
