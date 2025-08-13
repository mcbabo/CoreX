package at.mcbabo.corex.ui.widgets

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.AppWidgetId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentSize
import androidx.glance.material3.ColorProviders
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.lifecycle.lifecycleScope
import at.mcbabo.corex.MainActivity
import at.mcbabo.corex.R
import at.mcbabo.corex.data.repositories.WorkoutRepository
import at.mcbabo.corex.ui.theme.darkScheme
import at.mcbabo.corex.ui.theme.lightScheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

object GlanceAppTheme {
    val colors = ColorProviders(
        light = lightScheme,
        dark = darkScheme
    )
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun workoutRepository(): WorkoutRepository
}

val todayWorkoutKey = stringPreferencesKey("today_workout_name")
val widgetBgTransparentKey = booleanPreferencesKey("widget_bg_transparent")
val widgetBgDarkModeKey = booleanPreferencesKey("widget_bg_color")
val widgetBgTransparency = floatPreferencesKey("widget_bg_transparency")

class WorkoutWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val workoutName = prefs[todayWorkoutKey] ?: context.getString(R.string.no_workouts_today)

            val bgTransparency = prefs[widgetBgTransparency] ?: 1f

            val bgColor = if (prefs[widgetBgTransparentKey] == true) {
                Color.Transparent.toArgb()
            } else {
                prefs[widgetBgDarkModeKey]?.let {
                    if (it) MaterialTheme.colorScheme.onSurface.copy(alpha = bgTransparency).toArgb()
                    else MaterialTheme.colorScheme.background.copy(alpha = bgTransparency).toArgb()
                } ?: MaterialTheme.colorScheme.background.copy(alpha = bgTransparency).toArgb()
            }

            val textColor = when (bgColor) {
                Color.Transparent.toArgb() -> {
                    Color.White.toArgb()
                }

                MaterialTheme.colorScheme.onSurface.copy(alpha = bgTransparency).toArgb() -> {
                    Color.White.toArgb()
                }

                else -> {
                    MaterialTheme.colorScheme.onSurface.toArgb()
                }
            }
            GlanceTheme(colors = GlanceAppTheme.colors) {
                WidgetContent(workoutName = workoutName, bgColor = bgColor, textColor = textColor)
            }
        }
    }
}

class WorkoutWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WorkoutWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        context?.let {
            CoroutineScope(Dispatchers.IO).launch {
                val entryPoint = EntryPointAccessors.fromApplication(
                    context, WidgetEntryPoint::class.java
                )
                val workoutRepo = entryPoint.workoutRepository()

                val today = LocalDate.now().dayOfWeek.value
                val data = workoutRepo.getWorkoutsByWeekday(today)
                    .joinToString("\n") { it.name }

                val glanceIds = GlanceAppWidgetManager(context)
                    .getGlanceIds(WorkoutWidget::class.java)

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[todayWorkoutKey] = data
                    }
                    WorkoutWidget().update(context, glanceId)
                }
            }
        }
    }
}

class WorkoutWidgetConfigActivity : ComponentActivity() {
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val glanceId = AppWidgetId(appWidgetId)

        lifecycleScope.launch {
            val prefs = getAppWidgetState(
                this@WorkoutWidgetConfigActivity,
                PreferencesGlanceStateDefinition,
                glanceId
            )
            val initialValue = prefs[widgetBgTransparentKey] ?: false
            val initialBgDarkMode = prefs[widgetBgDarkModeKey] ?: false
            val initialBgTransparency = prefs[widgetBgTransparency] ?: 1f

            setContent {
                WidgetSettingsScreen(
                    bgTransparent = initialValue,
                    bgDarkMode = initialBgDarkMode,
                    bgTransparency = initialBgTransparency,
                ) { bgTransparent, bgDarkMode, bgTransparency ->
                    lifecycleScope.launch {
                        updateAppWidgetState(this@WorkoutWidgetConfigActivity, glanceId) { p ->
                            p[widgetBgTransparentKey] = bgTransparent
                            p[widgetBgDarkModeKey] = bgDarkMode
                            p[widgetBgTransparency] = bgTransparency
                        }
                        WorkoutWidget().update(this@WorkoutWidgetConfigActivity, glanceId)
                    }

                    // Tell the system we’re done configuring
                    val result = Intent().apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                    }
                    setResult(RESULT_OK, result)
                    finish()
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun WidgetContent(
    workoutName: String,
    bgColor: Int,
    textColor: Int
) {
    val context = LocalContext.current

    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(bgColor))
            .clickable(onClick = actionStartActivity<MainActivity>()),
        horizontalAlignment = Alignment.Start
    ) {
        Image(
            provider = ImageProvider(
                R.drawable.corex
            ),
            modifier = GlanceModifier.wrapContentSize().padding(12.dp),
            colorFilter = ColorFilter.tint(ColorProvider(Color(textColor))),
            contentDescription = "CoreX Logo",
        )
        Column(
            modifier = GlanceModifier.fillMaxHeight().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = context.getString(R.string.todays_workouts),
                style = TextStyle(
                    color = ColorProvider(Color(textColor)),
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                ),
                maxLines = 1
            )
            Text(
                text = workoutName,
                style = TextStyle(
                    color = ColorProvider(Color(textColor)),
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                ),
                maxLines = 1
            )
        }
    }
}


@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 245 * 2, heightDp = 56 * 2) // 4x1
@Preview(widthDp = 185 * 2, heightDp = 56 * 2) // 3x1
@Suppress("Unused")
@Composable
fun ContentPreview() {
    WidgetContent(
        workoutName = "Chest and Triceps",
        bgColor = MaterialTheme.colorScheme.background.toArgb(),
        textColor = MaterialTheme.colorScheme.onBackground.toArgb()
    )
}
