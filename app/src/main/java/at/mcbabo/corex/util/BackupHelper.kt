package at.mcbabo.corex.util

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import at.mcbabo.corex.data.viewmodels.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

suspend fun exportAppData(
    context: Context,
    folderUri: Uri,
    databaseName: String,
    settingsJson: String,
    viewModel: SettingsViewModel
): Result<Unit> {
    return try {
        val resolver = context.contentResolver
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        val treeId = DocumentsContract.getTreeDocumentId(folderUri)
        val folderDocUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, treeId)

        viewModel.checkpointDatabase()

        val dbFile = context.getDatabasePath(databaseName)

        if (dbFile.exists() && dbFile.length() > 0) {
            val dbDestUri = DocumentsContract.createDocument(
                resolver,
                folderDocUri,
                "application/octet-stream",
                "fitness_database_$timestamp.db"
            )

            dbDestUri?.let { uri ->
                resolver.openOutputStream(uri)?.use { output ->
                    dbFile.inputStream().use { input ->
                        val bytesWritten = input.copyTo(output)
                        Log.d("ExportAppData", "Database exported: $bytesWritten bytes")
                        if (bytesWritten == 0L) {
                            throw Exception("Database export resulted in 0 bytes")
                        }
                    }
                }
            } ?: throw Exception("Failed to create database export file")
        } else {
            throw Exception("Database file is empty or doesn't exist")
        }

        val settingsUri = DocumentsContract.createDocument(
            resolver,
            folderDocUri,
            "application/json",
            "app_settings_$timestamp.json"
        )

        settingsUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { output ->
                output.write(settingsJson.toByteArray(Charsets.UTF_8))
                output.flush()
            }
        } ?: throw Exception("Failed to create settings export file")

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

suspend fun importAppData(
    context: Context,
    selectedUris: List<Uri>,
    databaseName: String,
    viewModel: SettingsViewModel
): Result<Unit> {
    return try {
        val resolver = context.contentResolver
        var databaseUri: Uri? = null
        var settingsUri: Uri? = null

        selectedUris.forEach { uri ->
            val fileName = getFileName(resolver, uri)
            val mimeType = resolver.getType(uri)

            when {
                fileName?.endsWith(".db") == true || mimeType == "application/octet-stream" -> {
                    databaseUri = uri
                }

                fileName?.endsWith(".json") == true || mimeType == "application/json" -> {
                    settingsUri = uri
                }
            }
        }

        databaseUri?.let { dbUri ->
            val dbFile = context.getDatabasePath(databaseName)
            val walFile = context.getDatabasePath("$databaseName-wal")
            val shmFile = context.getDatabasePath("$databaseName-shm")

            try {
                if (dbFile.exists()) {
                    dbFile.delete()
                }
                if (walFile.exists()) {
                    walFile.delete()
                }
                if (shmFile.exists()) {
                    shmFile.delete()
                }
            } catch (e: Exception) {
                Log.w("ImportAppData", "Could not delete existing database files: ${e.message}")
            }

            resolver.openInputStream(dbUri)?.use { input ->
                dbFile.outputStream().use { output ->
                    val bytesWritten = input.copyTo(output)
                    output.flush()
                    if (bytesWritten == 0L) {
                        throw Exception("Database import resulted in 0 bytes")
                    }
                }
            }
        }

        settingsUri?.let { settingsUri ->
            resolver.openInputStream(settingsUri)?.use { input ->
                val settingsJson = input.bufferedReader(Charsets.UTF_8).use { it.readText() }
                viewModel.importSettingsFromJson(settingsJson)
            }
        }

        if (databaseUri == null && settingsUri == null) {
            throw Exception("No valid backup files found. Please select .db and/or .json files.")
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

private fun getFileName(resolver: android.content.ContentResolver, uri: Uri): String? {
    return resolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) {
            cursor.getString(nameIndex)
        } else null
    }
}
