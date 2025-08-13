package at.mcbabo.corex.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import at.mcbabo.corex.MainActivity
import at.mcbabo.corex.R
import at.mcbabo.corex.data.repositories.SettingsRepository
import at.mcbabo.corex.data.repositories.WorkoutRepository
import at.mcbabo.corex.ui.widgets.WorkoutWidget
import at.mcbabo.corex.ui.widgets.todayWorkoutKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

class NotificationWorker(private val appContext: Context, private val workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NotificationWorkerEntryPoint {
        fun workoutRepository(): WorkoutRepository

        fun settingsRepository(): SettingsRepository
    }

    companion object {
        private const val CHANNEL_ID = "notification_channel"
        private const val NOTIFICATION_ID = 1
    }

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                appContext, NotificationWorkerEntryPoint::class.java
            )
            val workoutRepository = entryPoint.workoutRepository()
            val settingsRepository = entryPoint.settingsRepository()

            val settings = settingsRepository.settingsFlow.first()

            if (!settings.notificationsEnabled) {
                return Result.success()
            }

            val data = withContext(Dispatchers.IO) {
                val today = LocalDate.now().dayOfWeek.value

                val workouts = workoutRepository.getWorkoutsByWeekday(today)

                if (workouts.isEmpty()) {
                    null
                } else {
                    workouts.joinToString(separator = "\n") { it.name }
                }
            }

            if (!data.isNullOrEmpty()) {
                createNotificationChannel()
                showNotification(data)

                val glanceIds = GlanceAppWidgetManager(appContext)
                    .getGlanceIds(WorkoutWidget::class.java)

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(appContext, glanceId) { prefs ->
                        prefs[todayWorkoutKey] = data
                    }
                }

                WorkoutWidget().updateAll(appContext)

            } else {
                val glanceIds = GlanceAppWidgetManager(appContext)
                    .getGlanceIds(WorkoutWidget::class.java)

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(appContext, glanceId) { prefs ->
                        prefs[todayWorkoutKey] = appContext.getString(R.string.no_workouts_today)
                    }
                }

                WorkoutWidget().updateAll(appContext)
                Log.d("NotificationWorker", "No workouts for today, no notification sent")
            }

            Result.success()
        } catch (exception: Exception) {
            Log.e("NotificationWorker", "Worker failed", exception)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun createNotificationChannel() {
        val name = "Workout Reminders"
        val descriptionText = "Daily workout reminder notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification(data: String?) {
        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            appContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID).setSmallIcon(R.drawable.corex)
            .setContentText("${appContext.getString(R.string.daily_workout)}: $data")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true).setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}

@Singleton
class NotificationScheduler
@Inject constructor(private val settingsRepository: SettingsRepository) {
    companion object {
        private const val WORK_NAME = "notification_work"
    }

    suspend fun scheduleDailyWork(context: Context) {
        val settings = settingsRepository.settingsFlow.first()
        val (hour, minute) = parseReminderTime(settings.reminderTime)

        val dailyWork = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS).setInitialDelay(
            calculateInitialDelay(
                hour,
                minute
            ), TimeUnit.MILLISECONDS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, dailyWork
        )
    }

    suspend fun rescheduleWithNewTime(context: Context) {
        Log.d("NotificationScheduler", "Rescheduling work with new time")

        // Cancel existing work
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)

        // Schedule with new time
        scheduleDailyWork(context)
    }

    private fun parseReminderTime(reminderTime: String): Pair<Int, Int> = try {
        val parts = reminderTime.split(":")
        Pair(parts[0].toInt(), parts[1].toInt())
    } catch (e: Exception) {
        Pair(9, 0) // Default to 9:00 AM
    }

    private fun calculateInitialDelay(targetHour: Int, targetMinute: Int): Long {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, targetHour)
        calendar.set(Calendar.MINUTE, targetMinute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // If target time has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val delay = calendar.timeInMillis - now

        return delay
    }

    fun cancelWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_NAME)
    }
}
