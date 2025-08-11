package at.mcbabo.corex.ui.screens.settings.appearance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.mcbabo.corex.R
import at.mcbabo.corex.data.models.Language
import at.mcbabo.corex.data.viewmodels.SettingsViewModel
import at.mcbabo.corex.ui.components.BackButton
import at.mcbabo.corex.ui.components.PreferenceSingleChoiceItem
import at.mcbabo.corex.ui.components.PreferencesHintCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsScreen(onNavigateBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(),
            canScroll = { true }
        )

    val localUriHandler = LocalUriHandler.current

    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(modifier = Modifier, text = stringResource(R.string.language))
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
            PreferencesHintCard(
                title = stringResource(R.string.translate),
                description = stringResource(R.string.translate_desc),
                icon = Icons.Outlined.Translate
            ) {
                localUriHandler.openUri(
                    "https://github.com/mcbabo/CoreX/blob/main/CONTRIBUTING.md"
                )
            }

            Language.entries.forEach { language ->
                PreferenceSingleChoiceItem(
                    text = language.displayName,
                    selected = (language == settings.language),
                    onClick = {
                        viewModel.setLanguage(language)
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                )
            }
        }
    }
}
