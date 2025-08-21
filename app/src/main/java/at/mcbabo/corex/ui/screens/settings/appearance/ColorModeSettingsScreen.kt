package at.mcbabo.corex.ui.screens.settings.appearance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.mcbabo.corex.R
import at.mcbabo.corex.data.models.ThemeMode
import at.mcbabo.corex.data.viewmodels.SettingsViewModel
import at.mcbabo.corex.ui.components.BackButton
import at.mcbabo.corex.ui.components.PreferenceSingleChoiceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorModeSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val scrollBehavior = exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = stringResource(R.string.dark_mode)) },
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
            PreferenceSingleChoiceItem(
                text = "System",
                selected = settings.selectedTheme == ThemeMode.SYSTEM,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                viewModel.setTheme(ThemeMode.SYSTEM)
            }

            PreferenceSingleChoiceItem(
                text = stringResource(R.string.on),
                selected = settings.selectedTheme == ThemeMode.DARK,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                viewModel.setTheme(ThemeMode.DARK)
            }

            PreferenceSingleChoiceItem(
                text = stringResource(R.string.off),
                selected = settings.selectedTheme == ThemeMode.LIGHT,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                viewModel.setTheme(ThemeMode.LIGHT)
            }
        }
    }
}
