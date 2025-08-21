package at.mcbabo.corex.ui.widgets

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import at.mcbabo.corex.R
import at.mcbabo.corex.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WidgetSettingsScreen(
    bgTransparent: Boolean,
    bgDarkMode: Boolean,
    bgTransparency: Float,
    showIcon: Boolean,
    onSaveSettings: (Boolean, Boolean, Float, Boolean) -> Unit
) {
    var bgTransparent by remember { mutableStateOf(bgTransparent) }
    var bgDarkMode by remember { mutableStateOf(bgDarkMode) }
    var bgTransparency by remember { mutableFloatStateOf(bgTransparency) }
    var showIcon by remember { mutableStateOf(showIcon) }

    val scrollBehavior = exitUntilCollapsedScrollBehavior()

    AppTheme(
        darkTheme = isSystemInDarkTheme(),
        dynamicColor = true
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = { Text(stringResource(R.string.widget_settings)) },
                    scrollBehavior = scrollBehavior,
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        onSaveSettings(bgTransparent, bgDarkMode, bgTransparency, showIcon)
                    },
                ) {
                    Icon(imageVector = Icons.Filled.Save, contentDescription = "")
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.transparent_background))
                    Switch(
                        checked = bgTransparent,
                        onCheckedChange = { isChecked ->
                            bgTransparent = isChecked
                        }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.background_color))
                    Switch(
                        checked = bgDarkMode,
                        enabled = !bgTransparent,
                        onCheckedChange = { isChecked ->
                            bgDarkMode = isChecked
                        }
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(stringResource(R.string.transparency))
                        Slider(
                            value = bgTransparency,
                            onValueChange = { value ->
                                bgTransparency = value
                            },
                            valueRange = 0f..1f,
                            steps = 3,
                        )
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.show_icon))
                    Switch(
                        checked = showIcon,
                        onCheckedChange = { isChecked ->
                            showIcon = isChecked
                        }
                    )
                }
            }
        }
    }
}
