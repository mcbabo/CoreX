package at.mcbabo.corex.ui.screens.settings

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import at.mcbabo.corex.R
import at.mcbabo.corex.data.DATABASE_NAME
import at.mcbabo.corex.data.viewmodels.SettingsViewModel
import at.mcbabo.corex.ui.components.BackButton
import at.mcbabo.corex.ui.components.PreferenceItem
import at.mcbabo.corex.ui.components.PreferenceSubtitle
import at.mcbabo.corex.util.exportAppData
import at.mcbabo.corex.util.importAppData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val scrollBehavior = exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isExporting by remember { mutableStateOf(false) }
    var isImporting by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            scope.launch(Dispatchers.IO) {
                isExporting = true
                try {
                    val settingsJson = viewModel.exportSettingsToJson()

                    val result = exportAppData(
                        context = context,
                        folderUri = it,
                        databaseName = DATABASE_NAME,
                        settingsJson = settingsJson,
                        viewModel = viewModel
                    )
                    if (result.isSuccess) {
                        snackbarHostState.showSnackbar("Export completed successfully!")
                    } else {
                        snackbarHostState.showSnackbar("Export failed: ${result.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Export failed: ${e.message}")
                } finally {
                    isExporting = false
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch(Dispatchers.IO) {
                isImporting = true

                try {
                    val result = importAppData(
                        context = context,
                        selectedUris = uris,
                        databaseName = DATABASE_NAME,
                        viewModel = viewModel
                    )

                    if (result.isSuccess) {
                        snackbarHostState.showSnackbar("Import completed successfully! Please restart the app.")
                        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        context.startActivity(intent)
                        exitProcess(0)
                    } else {
                        snackbarHostState.showSnackbar("Import failed: ${result.exceptionOrNull()?.message}")
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Import failed: ${e.message}")
                } finally {
                    isImporting = false
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.developer_settings)) },
                navigationIcon = { BackButton(onNavigateBack) },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            PreferenceSubtitle(text = "${stringResource(R.string.backup)} & ${stringResource(R.string.restore)}")

            PreferenceItem(
                title = stringResource(R.string.export_database_settings),
                description = stringResource(R.string.export_desc),
                onClick = { exportLauncher.launch(null) },
                enabled = !isExporting && !isImporting
            )

            PreferenceItem(
                title = stringResource(R.string.import_database_or_settings),
                description = stringResource(R.string.import_desc),
                onClick = { importLauncher.launch(arrayOf("*/*")) },
                enabled = !isExporting && !isImporting
            )
        }
    }
}
